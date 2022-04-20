package com.ltzk.mbsf.popupview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import androidx.core.content.ContextCompat;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.DialogUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.pen.PaintView;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

/**
 * Created by JinJie on 2020/7/5
 */
public final class BrushPopView {
    private static final int BRUSH_RED         = Color.parseColor("#c91f37");

    private QMUIPopup mQMUIPopup;

    public interface Callback {
        void callback(int color);
    }

    private void handleLine(final View view) {
        int color = MySPUtils.getBrushColor(view.getContext());
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

    public BrushPopView(final PaintView cv, final Callback callback) {
        final Context context = cv.getContext().getApplicationContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.ppw_brush, null);
        handleLine(view);

        View.OnClickListener clickListener = (View v) -> {
            final int id = v.getId();
            if (R.id.v_black == id) {
                MySPUtils.putBrushColor(context, Color.BLACK);
                callback.callback(Color.BLACK);
            } else if (R.id.v_red == id) {
                MySPUtils.putBrushColor(context, BRUSH_RED);
                callback.callback(BRUSH_RED);
            } else {
                MySPUtils.putBrushColor(context, ContextCompat.getColor(context, R.color.colorPrimary));
                callback.callback(ContextCompat.getColor(context, R.color.colorPrimary));
            }
            handleLine(view);
            dismiss();
        };

        view.findViewById(R.id.v_black).setOnClickListener(clickListener);
        view.findViewById(R.id.v_red).setOnClickListener(clickListener);
        view.findViewById(R.id.v_blue).setOnClickListener(clickListener);

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getBrushWidth(context));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putBrushWidth(context, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cv.setPaintWidth(seekBar.getProgress());
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
                .offsetX(-50)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);
    }

    public void showAt(View anchor) {
        if (mQMUIPopup != null) {
            mQMUIPopup.show(anchor);
        }
    }

    public void showAt2(View anchor) {
        if (mQMUIPopup != null) {
            mQMUIPopup.show(anchor);
            DialogUtils.hideBottomNav(mQMUIPopup);
        }
    }

    public void dismiss() {
        if (mQMUIPopup != null) {
            mQMUIPopup.dismiss();
        }
    }
}