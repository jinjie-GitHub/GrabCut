package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.PickPhotoHelper;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.TopBar;
import com.ltzk.mbsf.widget.zoomLayout.ZoomLayout;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JinJie on 2020/5/31
 */
public class PrinterActivity extends BaseActivity {
    private static final String TAG = "king3";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.iv_3)
    ImageView mImageView;

    @BindView(R.id.zoomLayout)
    ZoomLayout mZoomLayout;

    @BindView(R.id.frameLayout)
    FrameLayout mMainLayout;

    @BindView(R.id.recyclerView)
    GridView mGridView;

    private PrinterAdapter mAdapter;
    private String _hanzi;
    private static Bitmap sBitmap;
    private int n1 = 5, n2, n4, n3 = 1;
    private boolean isRotated;

    public static void safeStart(Context c, String _hanzi, Bitmap bitmap) {
        sBitmap = PickPhotoHelper.compressBitmap(bitmap,0.25f);
        Intent intent = new Intent(c, PrinterActivity.class);
        intent.putExtra("_hanzi", _hanzi);
        c.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (sBitmap != null) {
            sBitmap.recycle();
            sBitmap = null;
        }
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_printer;
    }

    @Override
    public void initView() {
        _hanzi = getIntent().getStringExtra("_hanzi");
        mTopBar.setTitle(_hanzi);
        mZoomLayout.setMinScale(1.5f);
        mZoomLayout.setMaxScale(6.0f);
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });

        //1.数据源 2.间距 3.列数
        n1 = MySPUtils.getPrinterN1(getApplication());
        n2 = MySPUtils.getPrinterN2(getApplication());
        n3 = MySPUtils.getPrinterN3(getApplication());
        n4 = MySPUtils.getPrinterN4(getApplication());

        mAdapter = new PrinterAdapter(sBitmap);
        mGridView.setAdapter(mAdapter);
        mMainLayout.post(() -> {
            mZoomLayout.setScale(1.5f);
            //mMainLayout.setTranslationY(getY());
            calculate();
        });

        mZoomLayout.addOnZoomListener(new ZoomLayout.OnZoomListener() {
            @Override
            public void onZoomBegin(ZoomLayout view, float scale) {

            }

            @Override
            public void onZoom(ZoomLayout view, float scale) {

            }

            @Override
            public void onZoomEnd(ZoomLayout view, float scale) {
                //mAdapter.notifyDataSetChanged();
            }
        });
    }

    private final int getY() {
        int y1 = ViewUtil.getScreenHeight(activity) - ViewUtil.dpToPx(72);
        int y2 = (ViewUtil.getScreenHeight(activity) - y1) / 2;
        return -Math.abs(y2);
    }

    @OnClick({R.id.iv_1, R.id.iv_2, R.id.iv_3, R.id.iv_4})
    public void iv_delete(View v) {
        isRotated = false;
        final int id = v.getId();
        switch (id) {
            case R.id.iv_1:
                handleAlpha(v);
                break;
            case R.id.iv_2:
                handleSpace(v);
                break;
            case R.id.iv_3:
                isRotated = true;
                mAdapter.mDegrees = mAdapter.mDegrees == 0 ? 90 : 0;
                mAdapter.notifyDataSetChanged();
                mImageView.setImageResource(mAdapter.mDegrees == 90 ? R.mipmap.ic_rotate : R.mipmap.ic_rotate_);
                break;
            case R.id.iv_4:
                ToastUtil.showToast(activity, "图片已保存到您的相册。");
                mZoomLayout.setScale(1.5f);
                mZoomLayout.postDelayed(() -> {
                    mMainLayout.setTranslationY(0);
                    savePicToSdcard(BitmapUtils.convertViewToBitmap(mZoomLayout));
                    //mMainLayout.setTranslationY(getY());
                }, 1000L);
                break;
        }
    }

    private void savePicToSdcard(Bitmap bitmap) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                BitmapUtils.savePicToSdcard(getApplication(), bitmap, String.valueOf(System.currentTimeMillis()));
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

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
                .offsetX(200)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getPrinterAlpha(activity));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putPrinterAlpha(activity, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void handleSpace(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_printer_ruler, null);
        QMUIPopups.popup(this)
                .view(view)
                .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        TextView tv_size_left = view.findViewById(R.id.tv_size_left);
        TextView tv_size_right = view.findViewById(R.id.tv_size_right);
        TextView tv_space_left = view.findViewById(R.id.tv_space_left);
        TextView tv_space_right = view.findViewById(R.id.tv_space_right);

        ImageView iv_1 = view.findViewById(R.id.iv_1);
        ImageView iv_2 = view.findViewById(R.id.iv_2);
        ImageView iv_3 = view.findViewById(R.id.iv_3);
        ImageView iv_4 = view.findViewById(R.id.iv_4);
        ImageView iv_5 = view.findViewById(R.id.iv_5);
        ImageView iv_6 = view.findViewById(R.id.iv_6);
        ImageView iv_7 = view.findViewById(R.id.iv_7);
        ImageView iv_8 = view.findViewById(R.id.iv_8);

        View.OnClickListener left = v -> {
            switch (v.getId()) {
                case R.id.iv_1:
                    if (n1 < 20) {
                        if (n1 == 19) {
                            if (n2 == 0) {
                                n1++;
                            }
                        } else {
                            n1++;
                        }
                    }
                    break;
                case R.id.iv_3:
                    if (n1 < 20) {
                        if (n2 == 9) {
                            n2 = 0;
                            n1++;
                        } else {
                            n2++;
                        }
                    }
                    break;
                case R.id.iv_2:
                    if (n1 > 1) {
                        n1--;
                    }
                    break;
                case R.id.iv_4:
                    if (n2 == 0) {
                        if (n1 > 1) {
                            n2 = 9;
                            n1--;
                        }
                    } else {
                        n2--;
                    }
                    break;
            }

            if (n1 == 20 || (n1 == 19 && n2 != 0)) {
                iv_1.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_1.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n1 == 1) {
                iv_2.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_2.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n1 == 20) {
                iv_3.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_3.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n1 == 1 && n2 == 0) {
                iv_4.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_4.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            MySPUtils.setPrinterN1(getApplication(), n1);
            MySPUtils.setPrinterN2(getApplication(), n2);
            tv_size_left.setText(String.valueOf(n1));
            tv_size_right.setText(String.valueOf(n2));
            if (v != tv_size_left) {
                calculate();
            }
        };

        View.OnClickListener right = v -> {
            switch (v.getId()) {
                case R.id.iv_5:
                    if (n3 < 5) {
                        if (n3 == 4) {
                            if (n4 == 0) {
                                n3++;
                            }
                        } else {
                            n3++;
                        }
                    }
                    break;
                case R.id.iv_7:
                    if (n3 < 5) {
                        if (n4 == 9) {
                            n4 = 0;
                            n3++;
                        } else {
                            n4++;
                        }
                    }
                    break;
                case R.id.iv_6:
                    if (n3 > 0) {
                        if (n3 != 1 || n4 != 0) {
                            n3--;
                        }
                    }
                    break;
                case R.id.iv_8:
                    if (n4 == 0) {
                        if (n3 > 0) {
                            n4 = 9;
                            n3--;
                        }
                    } else {
                        if (n3 == 0) {
                            if (n4 != 1) {
                                n4--;
                            }
                        } else {
                            n4--;
                        }
                    }
                    break;
            }

            if (n3 == 5 || (n3 == 4 && n4 != 0)) {
                iv_5.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_5.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n3 == 0 || (n3 == 1 && n4 == 0)) {
                iv_6.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_6.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n3 == 5) {
                iv_7.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_7.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            if (n4 == 1 && n3 == 0) {
                iv_8.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            } else {
                iv_8.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            }

            MySPUtils.setPrinterN3(getApplication(), n3);
            MySPUtils.setPrinterN4(getApplication(), n4);
            tv_space_left.setText(String.valueOf(n3));
            tv_space_right.setText(String.valueOf(n4));
            if (v != tv_size_left) {
                calculate();
            }
        };

        iv_1.setOnClickListener(left);
        iv_2.setOnClickListener(left);
        iv_3.setOnClickListener(left);
        iv_4.setOnClickListener(left);
        iv_5.setOnClickListener(right);
        iv_6.setOnClickListener(right);
        iv_7.setOnClickListener(right);
        iv_8.setOnClickListener(right);
        left.onClick(tv_size_left);
        right.onClick(tv_size_left);
    }

    /**
     * 计算item数量
     */
    private final void calculate() {
        //计算照片区域的总宽度
        int totalWidth = mMainLayout.getMeasuredWidth();
        //计算照片区域的总高度
        int totalHeight = mMainLayout.getMeasuredHeight();

        //计算每个照片的宽高
        int itemWidth = n1 * mAdapter.BASE_WIDTH + n2 * mAdapter.BASE_WIDTH / 10;
        int itemHeight = (int) (itemWidth / 0.9f);

        //获取间距
        int spacing = n3 * mAdapter.BASE_WIDTH + n4 * mAdapter.BASE_WIDTH / 10;
        //获取列数
        int colCount = totalWidth / itemWidth;
        //获取行数
        int rowCount = totalHeight / itemHeight;

        //获取有效的列数
        while (true) {
            if (colCount == 0) {
                colCount = 1;
                break;
            } else {
                int result = totalWidth - (itemWidth * colCount + spacing * (colCount - 1));
                if (result > 6) {
                    break;
                }
                colCount--;
            }
        }

        //获取有效的行数
        while (true) {
            if (rowCount == 0) {
                rowCount = 1;
                break;
            } else {
                int result = totalHeight - (itemHeight * rowCount + spacing * (rowCount - 1));
                if (result > 6) {
                    break;
                }
                rowCount--;
            }
        }

        mGridView.setNumColumns(colCount);
        mGridView.setHorizontalSpacing(spacing);
        mGridView.setVerticalSpacing(spacing);
        mAdapter.setData(colCount * rowCount);
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) mGridView.getLayoutParams();
        fl.width = itemWidth * colCount + spacing * (colCount - 1);
        fl.height = itemHeight * rowCount + spacing * (rowCount - 1);
        mGridView.setLayoutParams(fl);

        Log.d(TAG, "colCount=" + colCount);
        Log.d(TAG, "rowCount=" + rowCount);
    }

    /**
     * TODO 这是一个将各种单位的值转换为像素的方法
     * int unit:你想要转换的值的单位
     * float value:你想要转换的值
     * float result:转换成功的值（单位：px）
     */
    private final float convert(final float cm) {
        float result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM,
                (cm * 10),
                getResources().getDisplayMetrics());
        return result;
    }

    private class PrinterAdapter extends BaseAdapter {
        private final int BASE_WIDTH = 30;

        private int mDegrees = 0;
        private List<Integer> mData = new ArrayList<>();
        private Bitmap mBitmap;

        private PrinterAdapter(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        private void setData(final int size) {
            final List<Integer> list = new ArrayList<>();
            while (list.size() < size) {
                list.add(0);
            }
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Integer getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_printer, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.render(mBitmap);
            return convertView;
        }

        private class ViewHolder {
            ImageView photoView;
            View frameLayout;

            public ViewHolder(View view) {
                photoView = view.findViewById(R.id.photoView);
                frameLayout = view.findViewById(R.id.frameLayout);
            }

            public void render(final Bitmap bitmap) {
                final int width = n1 * mAdapter.BASE_WIDTH + n2 * mAdapter.BASE_WIDTH / 10;
                ViewGroup.LayoutParams ivParams = frameLayout.getLayoutParams();
                ivParams.width = width;
                ivParams.height = (int) (width / 0.9f);
                frameLayout.setLayoutParams(ivParams);
                photoView.setLayoutParams(ivParams);

                photoView.setImageAlpha(255 * MySPUtils.getPrinterAlpha(activity) / 100);
                photoView.setImageBitmap(bitmap);

                if (isRotated) {
                    photoView.post(() -> {
                        RotateAnimation anim = new RotateAnimation(0, mDegrees, photoView.getMeasuredWidth() / 2, photoView.getMeasuredHeight() / 2);
                        anim.setDuration(100);
                        anim.setFillAfter(true);
                        photoView.startAnimation(anim);
                    });
                }
            }
        }
    }
}