package com.ltzk.mbsf.popupview;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.TouchImageView;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

/**
 * Created by JinJie on 2020/7/5
 */
public final class EditPopView {
    public static final int CODE_YT = 0;
    public static final int CODE_HD = 1;
    public static final int CODE_HB = 2;
    public static final int CODE_ZX = 3;
    public static final int CODE_FZ = 4;

    private QMUIPopup mQMUIPopup;

    public interface Callback {
        void callback(int state);
    }

    public EditPopView(final TouchImageView cv, final Callback callback) {
        final Context context = cv.getContext().getApplicationContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.ppw_edit, null);
        handleLine(view, cv);
        view.findViewById(R.id.v_black).setOnClickListener(v -> {
            cv.setStatus(0);
            callback.callback(CODE_YT);
            handleLine(view, cv);
            dismiss();
        });
        view.findViewById(R.id.v_red).setOnClickListener(v -> {
            cv.setStatus(1);
            callback.callback(CODE_HD);
            handleLine(view, cv);
            dismiss();
        });
        view.findViewById(R.id.v_blue).setOnClickListener(v -> {
            cv.setStatus(2);
            callback.callback(CODE_HB);
            handleLine(view, cv);
            dismiss();
        });

        CheckBox zx = view.findViewById(R.id.cb_zx);
        zx.setChecked(MySPUtils.getGravityState(context));
        zx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MySPUtils.putGravityState(context, isChecked);
                cv.setGravity(isChecked);
            }
        });

        CheckBox fz = view.findViewById(R.id.cb_fz);
        fz.setChecked(cv.isRotate());
        fz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cv.setMirrorRotate(isChecked);
            }
        });

        mQMUIPopup = QMUIPopups.popup(context, ViewUtil.dpToPx(180), ViewUtil.dpToPx(95))
                .view(view)
                .bgColor(ContextCompat.getColor(context, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(context, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                //.animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                //.offsetYIfTop(ViewUtil.dpToPx(100))
                .offsetX(ViewUtil.dpToPx(52))
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);
    }

    private void handleLine(View view, TouchImageView cv) {
        int color = cv.getStatus();
        TextView tv1 = view.findViewById(R.id.v_black);
        TextView tv2 = view.findViewById(R.id.v_red);
        TextView tv3 = view.findViewById(R.id.v_blue);
        if (color == 0) {
            tv1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tv2.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv3.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv1.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            tv2.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            tv3.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            view.findViewById(R.id.v_black_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_red_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else if (color == 1) {
            tv1.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tv3.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv1.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            tv2.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            tv3.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            view.findViewById(R.id.v_black_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else {
            tv1.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv2.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tv1.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            tv2.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
            tv3.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            view.findViewById(R.id.v_black_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.VISIBLE);
        }
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