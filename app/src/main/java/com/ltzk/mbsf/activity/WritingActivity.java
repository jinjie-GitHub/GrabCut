package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.popupview.BrushPopView;
import com.ltzk.mbsf.popupview.GeXianPopView;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MarkView;
import com.ltzk.mbsf.widget.MyScrollView;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.pen.PaintView;
import com.ltzk.mbsf.widget.photoview.OnMatrixChangedListener;
import com.ltzk.mbsf.widget.photoview.OnPhotoTapListener;
import com.ltzk.mbsf.widget.photoview.OnSingleFlingListener;
import com.ltzk.mbsf.widget.photoview.PhotoView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import notchtools.geek.com.notchtools.NotchTools;

/**
 * Created by JinJie on 2020/11/12
 */
public class WritingActivity extends BaseActivity {

    @BindView(R.id.rl_root)
    View mRoot;

    @BindView(R.id.scrollView)
    MyScrollView mScrollView;

    @BindView(R.id.photoView)
    PhotoView mPhotoView;

    @BindView(R.id.markView)
    MarkView mMarkView;

    @BindView(R.id.paintView)
    PaintView mPaintView;

    @BindView(R.id.iv_undo)
    ImageView mUndoView;

    @BindView(R.id.iv_delete)
    ImageView mClearView;

    @BindView(R.id.tv_move)
    TextView mViewMove;

    @BindView(R.id.sd)
    TextView mSdView;

    public static void safeStart(BaseActivity a, String url) {
        Intent intent = new Intent(a, WritingActivity.class);
        intent.putExtra("url", url);
        a.startActivityFromBottom(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) return true;
        mPhotoView.getAttacher().getScaleGestureDetector().onTouchEvent(ev);
        mPhotoView.getAttacher().getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public int getLayoutId() {
        NotchTools.getFullScreenTools().fullScreenUseStatusForActivityOnCreate(this);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.midnightBlue));
        return R.layout.activity_writing_copy;
    }

    @Override
    public void initView() {
        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.midnightBlue));
        mPaintView.setStepCallback(mStepCallback);
        mPaintView.init(ViewUtil.getScreenWidth(activity), ViewUtil.getScreenHeight(activity) + ViewUtil.dpToPx(80), "");
        mMarkView.setPaintType(MySPUtils.getGXPaintType(this));
        mMarkView.setPaintColor(MySPUtils.getGXPaintColor(this));
        String url = getIntent().getStringExtra("url");
        Glide.with(getApplication())
                .asBitmap()
                .load(url)
                .override(800, 800)
                .into(new ImageViewTarget<Bitmap>(mPhotoView) {
                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        if (resource != null) {
                            mPhotoView.setMinimumScale(0.8f);
                            mPhotoView.setMaximumScale(10.0f);
                            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            mPhotoView.setImageBitmap(resource);
                        }
                    }
                });

        initGestureDetector();
        setOnClickListener(mClickListener);
        mPhotoView.setOnMatrixChangeListener(mMatrixChangedListener);
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
    }

    private View.OnClickListener mClickListener = (View view) -> {
        processRootView();
    };

    private RectF mRectF;
    private boolean isFirst = true;
    private OnMatrixChangedListener mMatrixChangedListener = (final RectF rect) -> {
        post(() -> {
            this.mRectF = rect;
            //processMatrixChanged(mPaintView.getVisibility() == View.VISIBLE);
            if (isFirst) {
                isFirst = false;
                moveToCenter(rect);
            }
        });
    };

    private float mP = MainApplication.isTablet ? 2.3f : 3.8f;
    private final int mPaddingTop = ViewUtil.getScreenHeight(MainApplication.getInstance()) / 18;
    private final int mPaddingLeft = (int) (ViewUtil.getScreenWidth(MainApplication.getInstance()) / mP);
    private void processMatrixChanged(boolean show) {
        final RectF rect = mRectF;
        if (rect == null || !show) {
            return;
        }
        final int w = (int) rect.width();
        final int h = (int) rect.height();
        mPaintView.onSizeChange(w, h);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPaintView.getLayoutParams();
        params.width = w;
        params.height = h;
        params.topMargin = rect.top <= 0 ?
                (int) (ViewUtil.dpToPx(mPaddingTop) - Math.abs(rect.top)) :
                (ViewUtil.getScreenHeight(getApplication()) - h) / 2;
        params.leftMargin = rect.left <= 0 ?
                (int) (ViewUtil.dpToPx(mPaddingLeft) - Math.abs(rect.left)) :
                (int) (Math.abs(rect.left) + ViewUtil.dpToPx(mPaddingLeft));
        mPaintView.setLayoutParams(params);
    }

    private void moveToCenter(final RectF rect) {
        mPhotoView.setPadding(ViewUtil.dpToPx(mPaddingLeft),
                ViewUtil.dpToPx(mPaddingTop),
                (int) ((ViewUtil.getScreenWidth(activity) - rect.width()) / 2),
                ViewUtil.dpToPx(mPaddingTop));
        mPhotoView.post(() -> {
            mScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    @OnClick({R.id.zd, R.id.gx, R.id.sx, R.id.sd, R.id.tv_exit, R.id.iv_delete, R.id.iv_undo,
            R.id.iv_alpha, R.id.iv_action, R.id.iv_brush, R.id.tv_move, R.id.iv_close})
    public void onClick(View v) {
        isButtonClick = true;
        final int id = v.getId();
        switch (id) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.zd:
                processMaskView();
                break;
            case R.id.gx:
                final GeXianPopView popView = new GeXianPopView(mMarkView);
                popView.showAt(v);
                break;
            case R.id.sx:
                mMarkView.setEnabled(false);
                processWrite(true);
                //processMatrixChanged(true);
                break;
            case R.id.sd:
                mPhotoView.setEnabled(!mPhotoView.isEnabled());
                mSdView.setText(mPhotoView.isEnabled() ? "固定" : "移动");
                mSdView.setCompoundDrawablesWithIntrinsicBounds(0, mPhotoView.isEnabled() ? R.mipmap.ic_lock : R.mipmap.ic_move, 0, 0);
                mScrollView.setScroll(mPhotoView.isEnabled());
                break;
            case R.id.tv_exit:
                mMarkView.setEnabled(true);
                processWrite(false);
                break;
            case R.id.iv_delete:
                mPaintView.reset();
                break;
            case R.id.iv_undo:
                mPaintView.undo();
                break;
            case R.id.iv_brush:
                final BrushPopView brushPop = new BrushPopView(mPaintView, (int color) -> {
                    mPaintView.setPaintColor(color);
                });
                brushPop.showAt(v);
                break;
            case R.id.iv_alpha:
                handleAlpha(v);
                break;
            case R.id.iv_action:
                showDownload(v);
                break;
            case R.id.tv_move:
                mMarkView.setEnabled(mPaintView.isEnabled());
                mPaintView.setEnabled(!mPaintView.isEnabled());
                mViewMove.setText(mPaintView.isEnabled() ? "移动" : "手写");
                mViewMove.setCompoundDrawablesWithIntrinsicBounds(0, mPaintView.isEnabled() ? R.mipmap.ic_move : R.mipmap.ic_write, 0, 0);
                mScrollView.setScroll(!mPaintView.isEnabled());
                mPhotoView.setEnabled(!mPaintView.isEnabled());
                break;
        }
    }

    private void processWrite(boolean isShow) {
        mScrollView.setScroll(!isShow);
        mPaintView.reset();
        mPaintView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.rl_ready).setVisibility(isShow ? View.GONE : View.VISIBLE);
        findViewById(R.id.rl_write).setVisibility(isShow ? View.VISIBLE : View.GONE);
        mPhotoView.setEnabled(!isShow);
        mPhotoView.setImageAlpha(isShow ? (255 - 255 * MySPUtils.getWriterAlpha(activity) / 100) : 255);
        mSdView.setText("固定");
        mSdView.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_lock, 0, 0);
    }

    private void processMaskView() {
        boolean isShow = findViewById(R.id.maskView).getVisibility() == View.VISIBLE;
        findViewById(R.id.maskView).setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    private void processRootView() {
        if (mPaintView.getVisibility() == View.GONE) {
            boolean isShow = findViewById(R.id.rl_ready).getVisibility() == View.VISIBLE;
            findViewById(R.id.rl_ready).setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }

    private final PaintView.StepCallback mStepCallback = () -> {
        mUndoView.setEnabled(mPaintView.canUndo());
        mClearView.setEnabled(mPaintView.canUndo());
        mUndoView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.silver)));
        mClearView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.silver)));
    };

    private void handleAlpha(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_printer_alpha, null);
        QMUIPopups.popup(this, ViewUtil.dpToPx(260))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(-40)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getWriterAlpha(activity));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putWriterAlpha(activity, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPhotoView.setImageAlpha(255 - 255 * seekBar.getProgress() / 100);
            }
        });
    }

    private void showDownload(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_contrast, null);
        final TextView first = view.findViewById(R.id.tv_1);
        first.setVisibility(View.GONE);
        final TextView second = view.findViewById(R.id.tv_2);
        second.setVisibility(View.GONE);
        final QMUIPopup popup = QMUIPopups.popup(this, ViewUtil.dpToPx(140))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(-40)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);
        final TextView third = view.findViewById(R.id.tv_3);
        third.setText("保存手写图片");
        third.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.silver)));
        view.findViewById(R.id.tv_3).setOnClickListener(v -> {
            popup.dismiss();
            if (mPaintView.canUndo()) {
                final Bitmap newBitmap = Bitmap.createBitmap(ViewUtil.getScreenWidth(activity), ViewUtil.getScreenHeight(activity), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(newBitmap);
                canvas.drawColor(Color.WHITE);
                Matrix matrix = new Matrix();
                matrix.postScale(0.8f, 0.8f);
                matrix.postTranslate(newBitmap.getWidth() / 2 - 0.8f * mPhotoView.getDrawable().getIntrinsicWidth() / 2,
                        newBitmap.getHeight() / 2 - 0.8f * mPhotoView.getDrawable().getIntrinsicHeight() / 2);
                canvas.drawBitmap(((BitmapDrawable) mPhotoView.getDrawable()).getBitmap(), matrix, null);
                canvas.drawBitmap(mPaintView.getBitmap(), new Matrix(), null);
                savePicToSdcard("手写图片已保存到您的相册。", newBitmap);
            }
        });
    }

    private void savePicToSdcard(String msg, Bitmap bitmap) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                ToastUtil.showToast(activity, msg);
                BitmapUtils.savePicToSdcard(getApplication(), bitmap, String.valueOf(System.currentTimeMillis()));
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    private boolean isButtonClick;
    private GestureDetector mGestureDetector;
    private final void initGestureDetector() {
        mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    public void setOnClickListener(final View.OnClickListener l) {
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (l != null && !isButtonClick) {
                    l.onClick(mRoot);
                }
                isButtonClick = false;
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }

    private class PhotoTapListener implements OnPhotoTapListener {
        public void onPhotoTap(ImageView view, float x, float y) {
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }
}