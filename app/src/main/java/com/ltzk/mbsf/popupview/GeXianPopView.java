package com.ltzk.mbsf.popupview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.DialogUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.CropView;
import com.ltzk.mbsf.widget.MarkView;
import com.ltzk.mbsf.widget.MySeekBar;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JinJie on 2020/7/5
 */
public final class GeXianPopView {

    private QMUIPopup mQMUIPopup;

    public GeXianPopView(final MarkView cv) {
        final Context context = cv.getContext().getApplicationContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.ppw_mark, null);
        handleLine(view);
        view.findViewById(R.id.v_black).setOnClickListener(v -> {
            MySPUtils.putGXPaintColor(context, Color.BLACK);
            cv.setPaintColor(Color.BLACK);
            handleLine(view);
            dismiss();
        });
        view.findViewById(R.id.v_red).setOnClickListener(v -> {
            MySPUtils.putGXPaintColor(context, Color.RED);
            cv.setPaintColor(Color.RED);
            handleLine(view);
            dismiss();
        });
        view.findViewById(R.id.v_blue).setOnClickListener(v -> {
            MySPUtils.putGXPaintColor(context, ContextCompat.getColor(context, R.color.colorPrimary));
            cv.setPaintColor(ContextCompat.getColor(context, R.color.colorPrimary));
            handleLine(view);
            dismiss();
        });

        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getGXPaintAlpha(context));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putGXPaintAlpha(context, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cv.setEnabled(seekBar.getProgress() > 0);
                cv.setPaintAlpha((255 * seekBar.getProgress() / 100));
            }
        });

        final GridView gridView = view.findViewById(R.id.gridView);
        MarkAdapter adapter = new MarkAdapter(context);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((AdapterView<?> parent, View item, int position, long id) -> {
            MySPUtils.putGXPaintType(context, position);
            cv.setPaintType(position);
            dismiss();
        });

        mQMUIPopup = QMUIPopups.popup(context, ViewUtil.dpToPx(260), ViewUtil.dpToPx(210))
                .view(view)
                .bgColor(ContextCompat.getColor(context, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(context, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(80))
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);
    }

    private void handleLine(View view) {
        int color = MySPUtils.getGXPaintColor(view.getContext());
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

    private final class MarkAdapter extends BaseAdapter {
        private final List<Integer> mData = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        private Context mContext;

        private MarkAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Integer getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ppw_mark, null);
            }
            CropView cropView = convertView.findViewById(R.id.crop_view);
            cropView.setLocked(true);
            cropView.setPaintType(mData.get(position));
            int pos = MySPUtils.getGXPaintType(mContext);
            if (pos == position) {
                cropView.setPaintColor2(MySPUtils.getGXPaintColor(mContext));
            } else {
                cropView.setPaintColor2(ContextCompat.getColor(mContext, R.color.silver));
            }
            return convertView;
        }
    }
}