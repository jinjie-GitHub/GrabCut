package com.ltzk.mbsf.popupview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.SimpleDraw;
import com.ltzk.mbsf.widget.bigScaleImage.MyImageView;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

/**
 * Created by JinJie on 2020/7/5
 */
public final class ZiTieSettingPopView {
    private static final int BRUSH_RED = Color.parseColor("#c91f37");
    private static final int BRUSH_BLU = Color.parseColor("#ff0070C9");
    private static final int BRUSH_GRY = Color.parseColor("#ffc0c0c0");

    private static final String KEY_ZITIE_SETTING_ALPHA = "zitie_setting_alpha";
    private static final String KEY_ZITIE_SETTING_COLOR = "zitie_setting_color";
    private static final String KEY_ZITIE_SETTING_TYPE = "zitie_setting_type";
    private static final String KEY_ZITIE_SETTING_SIZE = "zitie_setting_size";

    private Context mContext;
    private QMUIPopup mQMUIPopup;

    private void handleLine(final View view) {
        final int color = getColor(mContext);
        if (color == Color.BLACK) {
            view.findViewById(R.id.v_black_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_red_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else if (color == BRUSH_RED) {
            view.findViewById(R.id.v_black_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.v_black_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.VISIBLE);
        }
    }

    private void handleStyle(final View view) {
        ImageView s1 = view.findViewById(R.id.v_black_);
        ImageView s2 = view.findViewById(R.id.v_red_);
        ImageView s3 = view.findViewById(R.id.v_blue_);
        final int style = getStyle(mContext);
        if (style == SimpleDraw.STYLE_IN) {
            s1.setImageTintList(ColorStateList.valueOf(BRUSH_BLU));
            s2.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            s3.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            view.findViewById(R.id.v_black_line_).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_red_line_).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line_).setVisibility(View.GONE);
        } else if (style == SimpleDraw.STYLE_INTERSECT) {
            s1.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            s2.setImageTintList(ColorStateList.valueOf(BRUSH_BLU));
            s3.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            view.findViewById(R.id.v_black_line_).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line_).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_blue_line_).setVisibility(View.GONE);
        } else {
            s1.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            s2.setImageTintList(ColorStateList.valueOf(BRUSH_GRY));
            s3.setImageTintList(ColorStateList.valueOf(BRUSH_BLU));
            view.findViewById(R.id.v_black_line_).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line_).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line_).setVisibility(View.VISIBLE);
        }
    }

    public ZiTieSettingPopView(final Context ctx, final MyImageView imageView) {
        mContext = ctx.getApplicationContext();
        final View view = LayoutInflater.from(mContext).inflate(R.layout.ppw_zitie_setting, null);

        handleLine(view);
        View.OnClickListener clickListener = (View v) -> {
            final int id = v.getId();
            if (R.id.v_black == id) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_COLOR, Color.BLACK);
                imageView.setPaintColor(Color.BLACK);
            } else if (R.id.v_red == id) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_COLOR, BRUSH_RED);
                imageView.setPaintColor(BRUSH_RED);
            } else {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_COLOR, BRUSH_BLU);
                imageView.setPaintColor(BRUSH_BLU);
            }
            handleLine(view);
            dismiss();
            return;
        };
        view.findViewById(R.id.v_black).setOnClickListener(clickListener);
        view.findViewById(R.id.v_red).setOnClickListener(clickListener);
        view.findViewById(R.id.v_blue).setOnClickListener(clickListener);

        handleStyle(view);
        View.OnClickListener clickListener2 = (View v) -> {
            final int id = v.getId();
            if (R.id.v_black_ == id) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_TYPE, SimpleDraw.STYLE_IN);
                imageView.setPaintType(SimpleDraw.STYLE_IN);
            } else if (R.id.v_red_ == id) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_TYPE, SimpleDraw.STYLE_INTERSECT);
                imageView.setPaintType(SimpleDraw.STYLE_INTERSECT);
            } else {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_TYPE, SimpleDraw.STYLE_OUT);
                imageView.setPaintType(SimpleDraw.STYLE_OUT);
            }
            handleStyle(view);
            dismiss();
            return;
        };
        view.findViewById(R.id.v_black_).setOnClickListener(clickListener2);
        view.findViewById(R.id.v_red_).setOnClickListener(clickListener2);
        view.findViewById(R.id.v_blue_).setOnClickListener(clickListener2);

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(100 * getAlpha(mContext) / 255);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_ALPHA, 255 * progress / 100);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imageView.setPaintAlpha(255 * seekBar.getProgress() / 100);
            }
        });

        final MySeekBar seekBar2 = view.findViewById(R.id.seekBar2);
        seekBar2.setProgress(getSize(mContext, true));
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SPUtils.put(mContext, KEY_ZITIE_SETTING_SIZE, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float size = (seekBar.getProgress() <= 10) ? 10 : seekBar.getProgress();
                imageView.setPaintSize(size / 100);
            }
        });

        mQMUIPopup = QMUIPopups.popup(mContext, ViewUtil.dpToPx(260))
                .view(view)
                .bgColor(ContextCompat.getColor(mContext, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(mContext, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(6)
                .offsetX(ViewUtil.dpToPx(110))
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);
    }

    public static int getAlpha(Context context) {
        return (int) SPUtils.get(context, KEY_ZITIE_SETTING_ALPHA, 153);
    }

    public static int getSize(Context context, boolean isFirst) {
        final int progress = (int) SPUtils.get(context, KEY_ZITIE_SETTING_SIZE, 100);
        if (isFirst) {
            return progress;
        } else {
            return (progress <= 10) ? 10 : progress;
        }
    }

    public static int getColor(Context context) {
        return (int) SPUtils.get(context, KEY_ZITIE_SETTING_COLOR, BRUSH_BLU);
    }

    public static int getStyle(Context context) {
        return (int) SPUtils.get(context, KEY_ZITIE_SETTING_TYPE, 0);
    }

    public void showAt(View anchor) {
        if (mQMUIPopup != null) {
            mQMUIPopup.show(anchor);
        }
    }

    public void dismiss() {
        if (mQMUIPopup != null) {
            mQMUIPopup.dismiss();
        }
    }
}