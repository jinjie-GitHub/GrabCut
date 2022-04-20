package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 */
public class TopBar extends LinearLayout {
    @BindView(R2.id.btn_close)
    ImageView btn_close;
    @BindView(R2.id.left_button)
    ImageView left_button;
    @BindView(R2.id.left_txt)
    TextView leftTxt;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.small)
    TextView smallTitle;
    @BindView(R2.id.right_button)
    ImageView rightButton;
    @BindView(R2.id.right_txt)
    TextView rightTxt;
    @BindView(R2.id.topbarlayout)
    RelativeLayout topbarlayout;
    private Context context;

    public TopBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.widget_topbar, this);
        ButterKnife.bind(this, view);
    }


    /**
     * 设置topBar的背景颜色
     *
     * @param colorid
     */
    public void setBackGround(int colorid) {
        topbarlayout.setBackgroundResource(colorid);
    }

    /**
     * 直接传入title
     *
     * @param name
     */
    public void setTitle(String name) {
        title.setText(name);
        title.getPaint().setFakeBoldText(true);
    }

    public void setSmallTitle(String title) {
        smallTitle.setVisibility(VISIBLE);
        smallTitle.setText(title);
    }

    public void setTitleColor(int color) {
        title.setTextColor(color);
    }

    public String getTitle(){
        return title.getText().toString();
    }

    /**
     * 传入source id String.xml 中对应的
     *
     * @param resouce
     */
    public void setTitle(int resouce) {
        title.setText(context.getResources().getString(resouce));
    }


    /**
     * 设置左边按钮显示图片和点击事件
     *
     * @param resourceid 图片id
     * @param listener   点击事件
     */
    public void setLeftButtonListener(@NonNull int resourceid, @NonNull OnClickListener listener) {
        left_button.setImageResource(resourceid);
        left_button.setVisibility(View.VISIBLE);
        leftTxt.setVisibility(View.GONE);
        left_button.setOnClickListener(listener);
    }

    /**
     * 设置左边按钮无图片
     *
     */
    public void setLeftButtonNoPic() {
        left_button.setVisibility(View.GONE);
    }
    /**
     * 设置左边按钮无图片
     *
     */
    public void setLeftButtonVISIBLE() {
        left_button.setVisibility(View.VISIBLE);
    }

    /**
     * 设置左边文字按钮消失
     */
    public void setLeftTextGone(){
        leftTxt.setVisibility(View.GONE);
    }

    /**
     * 设置左边文字按钮消失
     */
    public void setLeftTextVISIBLE(){
        leftTxt.setVisibility(View.VISIBLE);
    }

    public void setTitleListener(@NonNull OnClickListener listener) {
        if (title != null) {
            title.setOnClickListener(listener);
        }
    }

    /**
     * 设置左边按钮显示文本和点击事件
     *
     * @param txt
     * @param listener 点击事件
     */
    public void setLeftTxtListener(@NonNull String txt, @NonNull OnClickListener listener) {
        leftTxt.setText(txt);
        leftTxt.setVisibility(View.VISIBLE);
        left_button.setVisibility(View.GONE);
        leftTxt.setOnClickListener(listener);
    }

    public void setLeftTxtColor(int color) {
        leftTxt.setTextColor(color);
    }

    /**
     * 设置右边按钮显示图片和点击事件
     *
     * @param resourceid 图片id
     * @param listener   点击事件
     */
    public void setRightButtonListener(@NonNull int resourceid, @NonNull OnClickListener listener) {
        rightButton.setImageResource(resourceid);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(listener);
    }

    public void setRightButtonTint(int color) {
        rightButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), color)));
    }

    public View getRightView(){
        return rightButton;
    }

    /**
     * 设置右边按钮消失
     */
    public void setRightButtonGone(){
        rightButton.setVisibility(View.GONE);
    }

    /**
     * 设置右边文字按钮消失
     */
    public void setRightTextGone(){
        rightTxt.setVisibility(View.GONE);
    }

    public void setRightTextVisible(){
        rightTxt.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边按钮显示文本和点击事件
     *
     * @param txt
     * @param listener 点击事件
     */
    public void setRightTxtListener(@NonNull String txt, @NonNull OnClickListener listener) {
        rightTxt.setText(txt);
        rightTxt.setVisibility(View.VISIBLE);
        rightTxt.setOnClickListener(listener);
    }
    public void setRightTxt(@NonNull String txt) {
        rightTxt.setText(txt);
    }
    public void setPaddingStatusBar(int height) {
        topbarlayout.setPadding(0, height, 0, 0);
    }

    public void setRightButton(@NonNull int resourceid) {
        rightButton.setImageResource(resourceid);
        rightButton.setVisibility(View.VISIBLE);
    }

    public void setCloseButtonListener(@NonNull OnClickListener listener) {
        btn_close.setOnClickListener(listener);
    }

    public void setCloseButtonState(boolean show) {
        btn_close.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
