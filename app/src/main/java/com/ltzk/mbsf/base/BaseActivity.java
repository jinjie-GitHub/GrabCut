package com.ltzk.mbsf.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.LoginTypeActivity;
import com.ltzk.mbsf.bean.Bus_LoginOutTip;
import com.ltzk.mbsf.utils.ActivityCollector;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.StatusBarUtil;
import com.ltzk.mbsf.widget.LoginOutDialog;
import com.ltzk.mbsf.widget.TopBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;


/**
 * 描述：
 */
public abstract class BaseActivity extends SupportActivity {


    public String Tag = getClass().toString();
    public Activity activity;
    private AlertDialog alertDialog;
    @BindView(R2.id.top_bar)
    public TopBar topBar;

    protected boolean isPaused;

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //注释掉activity本身的过渡动画
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startActivityFromBottom(Intent intent) {
        super.startActivity(intent);
        //注释掉activity本身的过渡动画
        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    public void finishFromTop() {
        ActivityUtils.closeSyskeyBroad(activity);
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

    }

    @Override
    public void finish() {
        ActivityUtils.closeSyskeyBroad(activity);
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();
        activity = this;
        if((Boolean) SPUtils.get(activity,"light",false)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setSize();
        setContentView(getLayoutId());
        getWindow().setBackgroundDrawable(null);
        setStatusBar();//设置沉浸式
        ButterKnife.bind(this);
        //ViewServer.get(this).addWindow(activity);
        ActivityCollector.addActivity(activity);
        initView();
    }

    // 登录被踢
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOutTip messageEvent) {
        synchronized (this) {
            if (tipPopView == null && !isPaused) {// 弹窗提示
                tipPopView = LoginOutDialog.showTip(messageEvent.getTitle(), messageEvent.getMessage());
                final boolean isShowing = tipPopView.isDialogFragmentShowing();
                if (tipPopView != null && !isShowing) {
                    tipPopView.show(getSupportFragmentManager(), null);
                    tipPopView.setOnClickListener((view) -> {
                        final int id = view.getId();
                        if (id == R.id.tv_pop_ok) {
                            activity.startActivity(new Intent(activity, LoginTypeActivity.class));
                        }
                        disPopupWindow();
                    });
                }
            }
        }
    }

    /**
     * 非首页检测到为登录
     * @param msg
     */
    public void logout(String msg){

    }

    private LoginOutDialog tipPopView;
    public void disPopupWindow() {
        if (tipPopView != null && tipPopView.isDialogFragmentShowing()) {
            tipPopView.dismiss();
            tipPopView = null;
            isPaused = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        disPopupWindow();
        ActivityCollector.removeActivity(activity);
        super.onDestroy();
    }

    private void setSize(){
        Configuration configuration = activity.getResources().getConfiguration();
        configuration.fontScale = (float) SPUtils.get(activity,"size",1.0f);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);//更新全局的配置
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        //ViewServer.get(this).setFocusedWindow(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    /**
     * 初始化界面
     */
    public abstract void initView();

    public abstract int getLayoutId();

    public void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(activity, getResources().getColor(R.color.transparentWhiteSmoke));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
    }

    /**
     * 显示提示框
     */
    public void showProgressDialog(String msg) {
        if(alertDialog == null){
            alertDialog = new AlertDialog.Builder(activity,R.style.AlertDialog).create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            alertDialog.setCancelable(true);
        }
        alertDialog.show();
        View view = getLayoutInflater().inflate(R.layout.loading_view,null);
        if(!TextUtils.isEmpty(msg)){
            ((TextView)view.findViewById(R.id.tv_msg)).setText(msg);
        }
        if (msg == null) {
            view.findViewById(R.id.tv_msg).setVisibility(View.GONE);
        }
        view.findViewById(R.id.ll_loading).setOnClickListener((View v) -> {
            cancel();
        });
        alertDialog.setContentView(view);
        alertDialog.setCanceledOnTouchOutside(false);

    }

    /**
     * 主动取消订阅
     */
    protected void cancel() {
        Log.e("--->", "Activity:Cancel the network request");
        closeProgressDialog();
    }

    /**
     * 关闭提示框
     */
    public void closeProgressDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void initWindows() {
        Window window = getWindow();
        int color = getResources().getColor(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置导航栏颜色
            window.setNavigationBarColor(ContextCompat.getColor(this, android.R.color.white));
            //设置状态栏颜色
            window.setStatusBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarUtil.getStatusBarHeight(activity)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }


}
