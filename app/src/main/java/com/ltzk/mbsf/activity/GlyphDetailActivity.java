package com.ltzk.mbsf.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.GlyphDetailPresenter;
import com.ltzk.mbsf.api.view.GlyphDetailView;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_ZilibAdd;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.FontListBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.BrushPopView;
import com.ltzk.mbsf.popupview.CompPopView;
import com.ltzk.mbsf.popupview.EditPopView;
import com.ltzk.mbsf.popupview.MarkPopView;
import com.ltzk.mbsf.utils.BitmapHelper;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.CropUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.CropView;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.OpenVideoDialog;
import com.ltzk.mbsf.widget.TouchImageView;
import com.ltzk.mbsf.widget.crop.UCrop;
import com.ltzk.mbsf.widget.pen.PaintView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JinJie on 2020/6/7
 */
public class GlyphDetailActivity extends MyBaseActivity<GlyphDetailView, GlyphDetailPresenter> implements GlyphDetailView, View.OnClickListener, OnItemClickListener {
    private static final int RC_CHOOSE_PHOTO = 2;
    private static final String FONT = "印";

    @BindView(R.id.title_)
    TextView mTitle;

    @BindView(R.id.left_txt_)
    TextView mTvLeft;

    @BindView(R.id.right_txt_)
    TextView mTvRight;

    @BindView(R.id.ll_translate)
    LinearLayout mTranslate;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.photo_view)
    TouchImageView mPhotoView;

    @BindView(R.id.touch_view)
    TouchImageView mTouchView;

    @BindView(R.id.crop_view)
    CropView mCropView;

    @BindView(R.id.fl_render)
    View mRender;

    @BindView(R.id.loading)
    ProgressBar mProgressBar;

    @BindView(R.id.draw_pen_view)
    PaintView mHw;

    @BindView(R.id.rel_write)
    RelativeLayout mRelWrite;

    @BindView(R.id.iv_undo)
    ImageView mUndoView;

    @BindView(R.id.iv_delete)
    ImageView mClearView;

    @BindView(R.id.cb_ishow)
    CheckBox mCbIshow;

    //截图保存
    @BindView(R.id.iv_action)
    ImageView iv_action;

    @BindView(R.id.right_button_)
    ImageView video_upload;

    @OnClick(R.id.iv_action)
    public void iv_action(View view) {
        if (mHw.canUndo()) {
            final Bitmap bmp_new = BitmapUtils.convertViewToBitmap(mHw);
            savePicToSdcard("手写图片已保存到您的相册。", bmp_new);
        }
    }

    @OnClick(R.id.iv_undo)
    public void onUndoClick(View view) {
        mHw.undo();
    }

    //刷新界面
    @OnClick(R.id.iv_delete)
    public void iv_delete(View view) {
        mHw.reset();
    }

    @OnClick(R.id.tv_exit_write)
    public void tv_exit_write(View view) {
        mCropView.setLocked(false);
        mHw.setVisibility(View.GONE);
        mRelWrite.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        translation(false);
    }

    private String gid;
    private int mPosition = 0;
    private boolean isOnTouch;
    private DetailsBean bean;
    private List<DetailsBean> mGlyphs;

    private QMUIPopup mFontsPopups;
    private FontsAdapter mFontsAdapter;
    private MySmartRefreshLayout mRefreshLayout;
    private boolean isFromBottom;
    private ZilibBean mZiLibBean;

    public static void safeStart(Context c, String id, List<DetailsBean> list) {
        Intent intent = new Intent(c, GlyphDetailActivity.class);
        intent.putExtra("id", id);
        sList = list;
        c.startActivity(intent);
    }

    public static void safeStart(Context c, String id, List<DetailsBean> list, ZilibBean zilibBean) {
        Intent intent = new Intent(c, GlyphDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("zilibBean", zilibBean);
        sList = list;
        c.startActivity(intent);
    }

    public static void safeStart(BaseActivity c, String id, List<DetailsBean> list, boolean fromBottom) {
        Intent intent = new Intent(c, GlyphDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("fromBottom", fromBottom);
        sList = list;
        c.startActivityFromBottom(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_glyph_detail;
    }

    private static List<DetailsBean> sList;

    @Override
    public void initView() {
        final Intent intent = getIntent();
        gid = intent.getStringExtra("id");
        //List<DetailsBean> list = (List<DetailsBean>) getIntent().getSerializableExtra("glyphs");

        if (sList != null && sList.size() > 0) {
            glyphs(sList);
        }

        if (intent.hasExtra("zilibBean")) {
            mZiLibBean = (ZilibBean) intent.getSerializableExtra("zilibBean");
        }

        isFromBottom = getIntent().getBooleanExtra("fromBottom", false);
        if (isFromBottom) {
            ((ImageView) findViewById(R.id.left_button_)).setImageResource(R.mipmap.close2);
        }

        final UserBean userBean = MainApplication.getInstance().getUser();
        final boolean isTablet = MainApplication.isTablet;
        video_upload.setVisibility((userBean.get_role() > 0 && !isTablet) ? View.VISIBLE : View.GONE);
        mCropView.setPaintType(MySPUtils.getPaintType(this));
        mCropView.setPaintColor(MySPUtils.getPaintColor(this));
        mHw.setPaintColor(MySPUtils.getBrushColor(this));
        mHw.setPaintWidth(MySPUtils.getBrushWidth(this));
        mHw.setStepCallback(mStepCallback);

        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 6));
        GlyphDetailAdapter glyphAdapter = new GlyphDetailAdapter();
        glyphAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(glyphAdapter);

        setViewCenterVertical();
        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.left_button_).setOnClickListener(this);
        findViewById(R.id.left_txt_).setOnClickListener(this);
        findViewById(R.id.right_txt_).setOnClickListener(this);
        findViewById(R.id.tv_exit).setOnClickListener(this);
        findViewById(R.id.iv_adjuster).setOnClickListener(this);
        findViewById(R.id.iv_download).setOnClickListener(this);
        presenter.details(gid, true);

        mTouchView.setClick(() -> {
            translation(!isOnTouch);
        });

        mCbIshow.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                mTouchView.setVisibility(View.INVISIBLE);
            } else {
                mTouchView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setViewCenterVertical() {
        mCropView.post(() -> {
            int y1 = ViewUtil.dpToPx(40);
            int y2 = CropUtils.getScreenHeight(getApplication()) - y1 * 2 - mRecyclerView.getMeasuredHeight();
            int y3 = (y2 - mTranslate.getMeasuredHeight()) / 2 - y1;
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) mTranslate.getLayoutParams();
            rl.topMargin = Math.abs(y3);
            mTranslate.setLayoutParams(rl);
        });
    }

    private void translation(boolean isOnTouch) {
        this.isOnTouch = isOnTouch;
        findViewById(R.id.action_bar).setVisibility(isOnTouch ? View.INVISIBLE : View.VISIBLE);
        if (mRelWrite.getVisibility() == View.GONE) {//判断是否是手写状态
            mRecyclerView.setVisibility(isOnTouch ? View.GONE : View.VISIBLE);
        }
        mCropView.post(() -> {
            int y1 = ViewUtil.dpToPx(40);
            int y2 = CropUtils.getScreenHeight(getApplication()) - y1 * 2 - mRecyclerView.getMeasuredHeight();
            int y3 = (CropUtils.getScreenHeight(getApplication()) - y2) / 2 - y1;
            ObjectAnimator.ofFloat(mTranslate, "translationY", isOnTouch ? y3 : 0).start();
        });
    }

    private final PaintView.StepCallback mStepCallback = () -> {
        mUndoView.setEnabled(mHw.canUndo());
        mClearView.setEnabled(mHw.canUndo());
        mUndoView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mHw.canUndo() ? R.color.colorPrimary : R.color.silver)));
        mClearView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mHw.canUndo() ? R.color.colorPrimary : R.color.silver)));
        iv_action.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mHw.canUndo() ? R.color.colorPrimary : R.color.silver)));
    };

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.left_button_) {
            finish();
        } else if (id == R.id.tv_exit) {
            contrast(false);
        } else if (id == R.id.iv_adjuster) {
            final CompPopView popView = new CompPopView(mPhotoView, new CompPopView.Callback() {
                public void callback(int color) {
                    mPhotoView.setPaintColor(color);
                    contrast(true);
                }
            });
            popView.showAt(view);
        } else if (id == R.id.iv_download) {
            savePicToSdcard("临摹对比图片已保存到您的相册。", BitmapUtils.convertViewToBitmap(mRender));
        } else if (id == R.id.root) {
            translation(!isOnTouch);
        } else if (id == R.id.left_txt_) {
            final int state = mProgressBar.getVisibility();
            if (state != View.VISIBLE && mGlyphs != null && mPosition > 0) {
                mHw.reset();
                mPosition--;
                mTitle.setText(mGlyphs.get(mPosition)._hanzi);
                presenter.details(mGlyphs.get(mPosition)._id, true);
                mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
                mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGlyphs.size() - 1 ? R.color.silver : R.color.colorPrimary));
            }
        } else if (id == R.id.right_txt_) {
            final int state = mProgressBar.getVisibility();
            if (state != View.VISIBLE && mGlyphs != null && mPosition < mGlyphs.size() - 1) {
                mHw.reset();
                mPosition++;
                mTitle.setText(mGlyphs.get(mPosition)._hanzi);
                presenter.details(mGlyphs.get(mPosition)._id, true);
                mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
                mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGlyphs.size() - 1 ? R.color.silver : R.color.colorPrimary));
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 2: {
                mPhotoView.enableRotate();
                contrast(view);
            }
            break;
            case 3: {
                mHw.post(() -> {
                    mCropView.setLocked(true);
                    ViewGroup.LayoutParams lp = mHw.getLayoutParams();
                    lp.width = mCropView.getMeasuredWidth();
                    lp.height = mCropView.getMeasuredHeight();
                    mHw.setPaintColor(mCropView.getPaintColor());
                    mHw.init(lp.width, lp.height, "");
                    mHw.setVisibility(View.VISIBLE);
                    mRelWrite.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                });
            }
            break;
            case 4: {
                final MarkPopView popView = new MarkPopView(mCropView, mTouchView);
                popView.showAt(view);
            }
            break;
            case 6: {
                final EditPopView popView = new EditPopView(mTouchView, new EditPopView.Callback() {
                    public void callback(int state) {
                        processEdit(mCropView.getBitmap(), state);
                    }
                });
                popView.showAt(view);
            }
            break;
            case 7: {
                mCropView.setLocked(mCropView.isEnabled());
                mTouchView.setEnabled(mCropView.isEnabled());
                final ImageView iv = view.findViewById(R.id.icon);
                final TextView tv = view.findViewById(R.id.name);
                tv.setText(mCropView.isEnabled() ? "锁定" : "解锁");
                iv.setImageResource(mCropView.isEnabled() ? R.mipmap.ic_unlock : R.mipmap.ic_lock);
            }
            break;
            case 0: {
                if (bean != null && !TextUtils.isEmpty(bean._zitie_id)) {
                    Intent intent = new Intent(activity, ZitieActivity.class);
                    intent.putExtra("zid", bean._zitie_id);
                    intent.putExtra("frame", bean._frame);
                    intent.putExtra("index", bean._page);
                    intent.putExtra("hasAnim", false);
                    startActivity(intent);
                } else {
                    if (mZiLibBean != null) {
                        Intent intent = new Intent(activity, ZiLibZiListActivity.class);
                        intent.putExtra("zilibBean", mZiLibBean);
                        activity.startActivity(intent);
                    }
                }
            }
            break;
            case 1: {
                if (bean != null && bean._video_count > 0) {
                    OpenVideoDialog.openVideo("", gid).show(getSupportFragmentManager(), null);
                }
            }
            break;
            case 5: {
                if (bean != null && !FONT.equals(bean._font)) {
                    SimilarActivity.safeStart(activity, gid, String.valueOf(bean._hanzi));
                }
            }
            break;
            case 8: {
                loadFavList(view);
            }
            break;
            case 9: {
                DictionaryActivity.safeStart(activity, String.valueOf(bean._hanzi));
            }
            break;
            case 10: {
                showDownload(view);
            }
            break;
            case 11: {
                mCropView.setLocked(true);
                mCropView.setBackgroundResource(R.drawable.shap_border_none);
                PrinterActivity.safeStart(activity, String.valueOf(bean._hanzi), BitmapUtils.convertViewToBitmap(mRender));
                mCropView.setLocked(!mTouchView.isEnabled());
                mCropView.setBackgroundResource(R.drawable.shap_crop_border);
            }
            break;
        }
    }

    @OnClick(R.id.right_button_)
    public void video_upload(View view) {
        if (bean != null) {
            VideoListActivity.safeStart(activity, gid, bean._zitie_id, "", "", bean, mGlyphs);
        }
    }

    @Override
    protected GlyphDetailPresenter getPresenter() {
        return new GlyphDetailPresenter();
    }

    @Override
    public void showProgress() {
        mCropView.post(() -> {
            mProgressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void disimissProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    @Override
    protected void onDestroy() {
        /*if (sList != null) {
            sList.clear();
            sList = null;
        }*/
        BitmapHelper.INSTANCE.onReleased();
        EventBus.getDefault().unregister(this);
        if (mHw != null)
            mHw.release();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromBottom) {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }

    @Override
    public void loadDataSuccess(Object object) {
        bean = (DetailsBean) object;
        if (bean == null) {
            return;
        }

        gid = bean._id;
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mTitle.setText(String.valueOf(bean._hanzi));
        BitmapHelper.INSTANCE.setThresh(FONT.equals(bean._font) ? BitmapHelper.THRESH_HIGH : BitmapHelper.THRESH_NORMAL);
        Glide.with(getApplication())
                .asBitmap()
                .load(!TextUtils.isEmpty(bean._color_image) ? bean._color_image : bean._clear_image)
                .into(new ImageViewTarget<Bitmap>(mTouchView) {
                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        if (!isCanceled() && resource != null) {
                            mTouchView.setColorImage(!TextUtils.isEmpty(bean._color_image));
                            mCropView.setSourceBitmap(resource);
                            processEdit(resource, mTouchView.getStatus());
                        }
                    }
                });
    }

    private void processEdit(Bitmap resource, int state) {
        if (resource == null) {
            return;
        }
        final boolean flag = (TextUtils.isEmpty(bean._color_image)) && (!TextUtils.isEmpty(bean._clear_image));
        if (state == EditPopView.CODE_YT) {
            mTouchView.setImageBitmap(resource);
        } else if (state == EditPopView.CODE_HD) {
            if (flag) {
                mTouchView.setImageBitmap3(resource);
            } else {
                mTouchView.setImageBitmap(resource);
            }
        } else if (state == EditPopView.CODE_HB) {
            if (flag) {
                mTouchView.setImageBitmap(resource);
            } else {
                //showProgress();
                BitmapHelper.INSTANCE.processBackground(resource, Color.WHITE, (Bitmap binary) -> {
                    if (!isCanceled()) {
                        disimissProgress();
                        mTouchView.setImageBitmap(binary);
                    }
                });
            }
        }
    }

    private void downloadBitmap(int state) {
        final String msg = "字帖图片已保存到您的相册。";
        Bitmap resource = mCropView.getBitmap();
        if (resource == null) {
            return;
        }

        if (mTouchView.isRotate()) {
            resource = mTouchView.convertBitmap2(mCropView.getBitmap());
        }

        final boolean flag = (TextUtils.isEmpty(bean._color_image)) && (!TextUtils.isEmpty(bean._clear_image));
        if (flag) {
            resource = BitmapUtils.getWhiteBgBitmap(resource);
        }
        if (state == EditPopView.CODE_YT) {
            savePicToSdcard(msg, resource);
        } else if (state == EditPopView.CODE_HD) {
            if (flag) {
                savePicToSdcard(msg, resource);
            } else {
                savePicToSdcard(msg, BitmapHelper.INSTANCE.bitmap2Gray(resource));
            }
        } else if (state == EditPopView.CODE_HB) {
            if (flag) {
                savePicToSdcard(msg, resource);
            } else {
                BitmapHelper.INSTANCE.processBackground(resource, Color.WHITE, (Bitmap binary) -> {
                    savePicToSdcard(msg, binary);
                });
            }
        } else {
            savePicToSdcard(msg, resource);
        }
    }

    @Override
    public void glyphs(List<DetailsBean> data) {
        mGlyphs = data;
        mTvLeft.setVisibility(data == null || data.size() <= 1 ? View.GONE : View.VISIBLE);
        mTvRight.setVisibility(data == null || data.size() <= 1 ? View.GONE : View.VISIBLE);
        if (data == null || data.size() <= 1) {
            mTvLeft.setClickable(false);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, R.color.silver));
            mTvRight.setClickable(false);
            mTvRight.setTextColor(ContextCompat.getColor(activity, R.color.silver));
        } else {
            mTvLeft.setClickable(true);
            mTvRight.setClickable(true);
            for (int pos = 0; pos < data.size(); pos++) {
                if (gid.equals(data.get(pos)._id)) {
                    mPosition = pos;
                    break;
                }
            }
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == data.size() - 1 ? R.color.silver : R.color.colorPrimary));
        }
    }

    @Override
    public void fontList(FontListBean tData) {
        mRefreshLayout.setTotal(tData.total);
        if (mRefreshLayout.getLoaded() == 0) {
            mFontsAdapter.setNewData(tData.list);
            mRefreshLayout.finishRefresh(true);
        } else {
            mRefreshLayout.finishLoadMore(true);
            if (mRefreshLayout.getLoaded() < tData.total) {
                mFontsAdapter.addData(tData.list);
            }
        }
        mRefreshLayout.setEnableLoadMore(mFontsAdapter.getItemCount() < tData.total);
    }

    @Override
    public void add(int stat) {
        disimissProgress();
        ToastUtil.showToast(activity, stat == 0 ? "添加成功" : "添加失败");
    }

    @OnClick(R.id.iv_brush)
    public void onBrushClick(View view) {
        final BrushPopView popView = new BrushPopView(mHw, (int color) -> {
            mHw.setPaintColor(color);
        });
        popView.showAt(view);
    }

    //字库有新增
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_ZilibAdd messageEvent) {
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    private void loadFavList(View anchor) {
        if (bean != null && !FONT.equals(bean._font) && mFontsPopups != null) {
            mFontsPopups.show(anchor);
            return;
        }
        final View view = View.inflate(getApplication(), R.layout.popups_fonts, null);
        mFontsPopups = QMUIPopups.popup(getApplication(), ViewUtil.dpToPx(234), ViewUtil.dpToPx(250))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        mFontsAdapter = new FontsAdapter();
        recyclerView.setAdapter(mFontsAdapter);
        presenter.query(mRefreshLayout.getLoaded());
        mFontsAdapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mFontsPopups.dismiss();
                showProgress();
                final FontListBean.FontList fav = (FontListBean.FontList) adapter.getItem(position);
                presenter.add(fav._id, new String[]{bean._hanzi}, new String[]{gid});
            }
        });

        mRefreshLayout.setOnRefreshListener(refresh -> {
            mRefreshLayout.finishRefresh(false);
            mRefreshLayout.setLoaded(0);
            mFontsAdapter.setNewData(null);
            presenter.query(mRefreshLayout.getLoaded());
        });

        mRefreshLayout.setRunnable(() -> {
            presenter.query(mRefreshLayout.getLoaded());
        });

        view.findViewById(R.id.add_lib).setOnClickListener((v) -> {
            mFontsPopups.dismiss();
            startActivityFromBottom(new Intent(activity, ZiLibAddActivity.class).putExtra("canStart", false));
        });
    }

    private void showDownload(View anchor) {
        final View view = LayoutInflater.from(activity).inflate(R.layout.ppw_glyph_download, null);
        final QMUIPopup popup = QMUIPopups.popup(activity, ViewUtil.dpToPx(160), ViewUtil.dpToPx(88))
                .view(view)
                .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                .borderWidth(2)
                .radius(8)
                .offsetYIfBottom(ViewUtil.dpToPx(-1))
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setChecked((boolean) SPUtils.get(activity, "key_glyph_download", false));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtils.put(activity, "key_glyph_download", isChecked);
        });
        final TextView tv_1 = view.findViewById(R.id.tv_1);
        tv_1.setOnClickListener((v1) -> {
            popup.dismiss();
            if (checkBox.isChecked()) {
                mCropView.setLocked(true);
                mCropView.setBackgroundResource(R.drawable.shap_border_none);
                savePicToSdcard("字帖图片已保存到您的相册。", BitmapUtils.convertViewToBitmap(mRender));
                mCropView.setLocked(false);
                mCropView.setBackgroundResource(R.drawable.shap_crop_border);
            } else {
                downloadBitmap(mTouchView.getStatus());
            }
        });
    }

    private void contrast(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_contrast, null);
        final QMUIPopup popup = QMUIPopups.popup(this, ViewUtil.dpToPx(120))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        final TextView first = view.findViewById(R.id.tv_1);
        if (mPhotoView.isCompared()) {
            first.setEnabled(true);
            first.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        } else {
            first.setEnabled(false);
            first.setTextColor(ContextCompat.getColor(activity, R.color.silver));
        }
        view.findViewById(R.id.tv_1).setOnClickListener(v -> {
            contrast(true);
            popup.dismiss();
        });
        view.findViewById(R.id.tv_2).setOnClickListener(v -> {
            choosePhoto();
            popup.dismiss();
        });
        view.findViewById(R.id.tv_3).setOnClickListener(v -> {
            takePhoto();
            popup.dismiss();
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

    private void takePhoto() {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
            @Override
            public void onGranted() {
                final Uri sourceUri = Uri.fromFile(new File(getCacheDir(), "takeImage.png"));
                final Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.png"));
                UCrop.of(sourceUri, destinationUri)
                        .withTargetActivity(CameraCropActivity.class)
                        .withAspectRatio(1, 1)
                        .start(GlyphDetailActivity.this);
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相机权限！");
            }
        });
    }

    private void choosePhoto() {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "选择图片"), RC_CHOOSE_PHOTO);
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    public void handleCropResult(final Bitmap bitmap) {
        if (bitmap != null) {
            showProgress();
            final int color = MySPUtils.getCompColor(activity);
            BitmapHelper.INSTANCE.processForeground(bitmap, color, (Bitmap binary) -> {
                if (!isCanceled()) {
                    disimissProgress();
                    contrast(true);
                    mPhotoView.setImageBitmap2(binary);
                    mPhotoView.setPaintAlpha((255 * MySPUtils.getCompAlpha(activity) / 100));
                }
            });
        }
    }

    private boolean isCanceled() {
        return (activity == null || activity.isFinishing() || activity.isDestroyed());
    }

    private void contrast(boolean isCon) {
        mCropView.setLocked(isCon);
        mPhotoView.setVisibility(isCon ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isCon ? View.GONE : View.VISIBLE);
        findViewById(R.id.root).setOnClickListener(isCon ? null : this);
        findViewById(R.id.contrast).setVisibility(isCon ? View.VISIBLE : View.GONE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_CHOOSE_PHOTO == requestCode) {
            if (data != null) {
                final Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.png"));
                UCrop.of(data.getData(), destinationUri)
                        .withTargetActivity(PhotoCropActivity.class)
                        .withAspectRatio(1, 1)
                        .start(this);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        handleCropResult(bmp);
                    } catch (Exception e) {
                        ToastUtil.showToast(activity, "无法剪切选择图片！");
                    }
                }
            }
        }
    }

    private class GlyphDetailAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {
        private final List<Map<String, Object>> mData = new ArrayList<>();
        private final String[] mName = new String[]{"字帖", "视频", "对比", "手写", "格线", "相似", "编辑", "锁定", "收藏", "字典", "下载", "打印"};
        private final int[] mIcon = new int[]{R.mipmap.ic_zitie, R.mipmap.ic_video, R.mipmap.ic_vs, R.mipmap.ic_write, R.mipmap.ic_small, R.mipmap.ic_link, R.mipmap.ic_flip, R.mipmap.ic_unlock, R.mipmap.ic_plus, R.mipmap.ic_book, R.mipmap.ic_download, R.mipmap.ic_printer};

        private GlyphDetailAdapter() {
            super(R.layout.item_glyph_detail);
            for (int pos = 0; pos < mName.length; pos++) {
                final Map<String, Object> map = new HashMap<>();
                map.put("name", mName[pos]);
                map.put("icon", mIcon[pos]);
                mData.add(map);
            }
            setNewData(mData);
        }

        @Override
        protected void convert(BaseViewHolder holder, Map<String, Object> item) {
            final String name = (String) item.get("name");
            holder.setText(R.id.name, name);
            final ImageView icon = holder.getView(R.id.icon);
            icon.setImageResource((Integer) item.get("icon"));
            final String zId = GlyphDetailActivity.this.bean != null ? GlyphDetailActivity.this.bean._zitie_id : null;
            final String font = GlyphDetailActivity.this.bean != null ? GlyphDetailActivity.this.bean._font : null;
            if (name.equals("观帖")) {
                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, TextUtils.isEmpty(zId) ? R.color.silver : R.color.colorPrimary)));
            } else if (name.equals("相似") || name.equals("字库")) {
                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, FONT.equals(font) ? R.color.silver : R.color.colorPrimary)));
            } else if (name.equals("视频")) {
                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, (bean != null && bean._video_count == 0) ? R.color.silver : R.color.colorPrimary)));
            } else {
                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorPrimary)));
            }

            final int pos = holder.getLayoutPosition();
            if (pos == 0) {
                if (bean != null && !TextUtils.isEmpty(bean._zitie_id)) {
                    holder.setText(R.id.name, "字帖");
                    icon.setImageResource(R.mipmap.ic_zitie);
                } else {
                    holder.setText(R.id.name, "字库");
                    icon.setImageResource(R.mipmap.tab_font);
                }
            }
        }
    }

    private static class FontsAdapter extends BaseQuickAdapter<FontListBean.FontList, BaseViewHolder> {
        private static final String MB = "毛笔 / ";
        private static final String YB = "硬笔 / ";

        public FontsAdapter() {
            super(R.layout.item_popups_fonts);
        }

        @Override
        protected void convert(BaseViewHolder holder, FontListBean.FontList bean) {
            holder.setText(R.id.font, bean._title);
            holder.setText(R.id.desc, ("1".equals(bean._own) ? MB : YB) + bean._font + "书 · " + bean._author);
        }
    }
}