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
import com.ltzk.mbsf.utils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zy on 2016/3/25.
 * 绑定手机号选择
 */
public class PhoneBindSelectPopView extends BasePopupWindow {
    private View conentView;
    @BindView(R2.id.tv_1_popview)
    TextView tv_1_popview;
    @OnClick(R2.id.tv_1_popview)
    public void tv_1_popview(View view){
        optionOnClick.onClick1();
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    @BindView(R2.id.tv_2_popview)
    TextView tv_2_popview;
    @OnClick(R2.id.tv_2_popview)
    public void tv_2_popview(View view){
        optionOnClick.onClick2();
        if (this.isShowing()) {
            this.dismiss();
        }
    }


    @BindView(R2.id.tv_cannel)
    TextView tv_cannel;
    @OnClick(R2.id.tv_cannel)
    public void tv_cannel(View view){
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    public PhoneBindSelectPopView(final Activity activity, OptionOnClick optionOnClick) {

        this.activity = activity;
        this.optionOnClick = optionOnClick;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_phone_bind_select_popview, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        ButterKnife.bind(this, conentView);
        setWidth(Math.round(ViewUtil.getWidth()));
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
            this.showAtLocation(parent,Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }
    OptionOnClick optionOnClick;
    public interface OptionOnClick{
        public void onClick1();
        public void onClick2();
    }
}
