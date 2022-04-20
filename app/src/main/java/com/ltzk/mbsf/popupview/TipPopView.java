package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
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
 * Created by zy on 2016/3/25.
 *  提示
 */
public class TipPopView extends BasePopupWindow {
    private View conentView;
    TipListener tipListener;
    TipListener2 tipListener2;

    @BindView(R2.id.tv_1_popview)
    TextView tv_1_popview;
    @BindView(R2.id.tv_2_popview)
    TextView tv_2_popview;
    @BindView(R2.id.tv_pop_cancal)
    TextView tv_pop_cancal;
    @OnClick(R2.id.tv_pop_cancal)
    public void tv_pop_cancal(View view){
        if (isShowing()) {
            dismiss();
        }
        if (tipListener2 != null) {
            tipListener2.ok();
        }
    }

    @BindView(R2.id.v_line_1)
    View v_line_1;
    @BindView(R2.id.tv_pop_ok)
    TextView tv_pop_ok;
    @OnClick(R2.id.tv_pop_ok)
    public void tv_pop_ok(View view){
        if (isShowing()) {
            dismiss();
        }
        tipListener.ok();
    }

    @BindView(R2.id.v_line_2)
    View v_line_2;
    @BindView(R2.id.tv_pop_ok2)
    TextView tv_pop_ok2;
    @OnClick(R2.id.tv_pop_ok2)
    public void tv_pop_ok2(View view){
        if (isShowing()) {
            dismiss();
        }
        tipListener2.ok();
    }

    public TipPopView(final Activity activity, String text1, String text2, TipListener tipListener) {
        init(activity,text1,text2,tipListener);
    }

    public TipPopView(final Activity activity, String text1, String text2,String btn_text2,TipListener tipListener) {
        init(activity,text1,text2,tipListener);
        tv_pop_ok.setText(btn_text2);
        if (btn_text2.equals("删除") || btn_text2.equals("清除") || btn_text2.equals("注销")) {
            tv_pop_ok.setTextColor(activity.getResources().getColor(R.color.red));
        }
    }

    //new
    public TipPopView(final Activity activity, String text1, String text2, String btn_text1, String btn_text2, TipListener tipListener, TipListener2 tipListener2) {
        init(activity, text1, text2, tipListener != null ? tipListener : mCallback);
        this.tipListener2 = tipListener2;
        tv_pop_cancal.setText(btn_text1);
        tv_pop_ok.setText(btn_text2);
    }

    public TipPopView(final Activity activity, String text1, String text2,String cannel,String btn_ok_1,String btn_ok_2,TipListener tipListener,TipListener2 tipListener2) {
        init(activity,text1,text2,tipListener);
        this.tipListener2 = tipListener2;

        if(TextUtils.isEmpty(cannel)){
            tv_pop_cancal.setVisibility(View.GONE);
        }else {
            tv_pop_cancal.setText(cannel);
        }

        tv_pop_ok.setText(btn_ok_1);

        if(!TextUtils.isEmpty(btn_ok_2)){
            v_line_2.setVisibility(View.VISIBLE);
            tv_pop_ok2.setVisibility(View.VISIBLE);
            tv_pop_ok2.setText(btn_ok_2);
        }
    }

    private void init(final Activity activity, String text1, String text2, TipListener tipListener){
        this.activity = activity;
        this.tipListener = tipListener;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_tip_popview, null);
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
        tv_pop_ok.getPaint().setFakeBoldText(true);
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

    public void showAtView(View parent) {
        if (!this.isShowing()) {
            darkenBackgroud(activity, 0.4f);
            this.showAtLocation(parent,Gravity.CENTER, 0, 0);
        }
    }

    private final TipListener mCallback = () -> {

    };

    public interface TipListener{
        public void ok();
    }
    public interface TipListener2{
        public void ok();
    }
}
