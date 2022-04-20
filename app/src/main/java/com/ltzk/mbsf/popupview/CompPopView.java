package com.ltzk.mbsf.popupview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.TouchImageView;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

/**
 * Created by JinJie on 2020/7/5
 */
public final class CompPopView {

    private QMUIPopup mQMUIPopup;

    public interface Callback {
        void callback(int color);
    }

    public CompPopView(final TouchImageView cv, final Callback callback) {
        final Context context = cv.getContext().getApplicationContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.ppw_comp, null);
        handleLine(view);
        view.findViewById(R.id.v_black).setOnClickListener(v -> {
            MySPUtils.putCompColor(context, Color.BLACK);
            callback.callback(Color.BLACK);
            handleLine(view);
            dismiss();
        });
        view.findViewById(R.id.v_red).setOnClickListener(v -> {
            MySPUtils.putCompColor(context, Color.RED);
            callback.callback(Color.RED);
            handleLine(view);
            dismiss();
        });
        view.findViewById(R.id.v_blue).setOnClickListener(v -> {
            MySPUtils.putCompColor(context, Color.parseColor("#0070C9"));
            callback.callback(Color.parseColor("#0070C9"));
            handleLine(view);
            dismiss();
        });

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getCompAlpha(context));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putCompAlpha(context, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cv.setPaintAlpha((255 * seekBar.getProgress() / 100));
            }
        });

        mQMUIPopup = QMUIPopups.popup(context, ViewUtil.dpToPx(260))
                .view(view)
                .bgColor(ContextCompat.getColor(context, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(context, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(10)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);
    }

    private void handleLine(View view) {
        int color = MySPUtils.getCompColor(view.getContext());
        if (color == Color.BLACK) {
            view.findViewById(R.id.v_black_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_red_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else if (color == Color.RED) {
            view.findViewById(R.id.v_black_line).setVisibility(View.GONE);
            view.findViewById(R.id.v_red_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_blue_line).setVisibility(View.GONE);
        } else {
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