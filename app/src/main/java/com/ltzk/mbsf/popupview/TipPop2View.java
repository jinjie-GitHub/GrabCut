package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.base.BasePopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 *  提示
 */
public class TipPop2View extends BasePopupWindow {
    private View conentView;
    TipListener tipListener;

    @BindView(R2.id.tv_1_popview)
    TextView tv_1_popview;
    @BindView(R2.id.tv_2_popview)
    TextView tv_2_popview;
    @OnClick(R2.id.tv_pop_cancal)
    public void tv_pop_cancal(View view){
        tipListener.cannel();
        if (isShowing()) {
            dismiss();
        }
    }

    @OnClick(R2.id.tv_pop_ok)
    public void tv_pop_ok(View view){
        tipListener.ok();
        if (isShowing()) {
            dismiss();
        }
    }
    public TipPop2View(final Activity activity, String text1, String text2, TipListener tipListener) {
        this.activity = activity;
        this.tipListener = tipListener;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_tip2_popview, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        ButterKnife.bind(this, conentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        tv_1_popview.setText(text1);
        tv_2_popview.setText(text2);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackgroud(activity, 1.0f);
            }
        });
    }


    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        //   timePicker.setCityString(city);
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            //this.showAsDropDown(parent);
            darkenBackgroud(activity, 0.4f);
            this.showAtLocation(parent,Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }


    public interface TipListener{
        public void ok();
        public void cannel();
    }
}
