package com.ltzk.mbsf.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.ActivityUtils;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 描述：
 */
public class BaseFragment extends SupportFragment {
    protected Activity activity;
    private AlertDialog alertDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startActivityFromBottom(Intent intent) {
        super.startActivity(intent);
        //注释掉activity本身的过渡动画
        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    public void finishFromTop() {
        ActivityUtils.closeSyskeyBroad(activity);
        activity.finish();
        //注释掉activity本身的过渡动画
        activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

    }

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg) {
        if(alertDialog == null){
            alertDialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialog).create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            alertDialog.setCancelable(true);
        }
        final View view = LayoutInflater.from(activity).inflate(R.layout.loading_view, null);
        if (!TextUtils.isEmpty(msg)) {
            ((TextView) view.findViewById(R.id.tv_msg)).setText(msg);
        }
        view.findViewById(R.id.ll_loading).setOnClickListener((View v) -> {
            cancel();
        });
        alertDialog.show();
        alertDialog.setContentView(view);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 主动取消订阅
     */
    protected void cancel() {
        Log.e("--->", "Fragment:Cancel the network request");
        closeProgressDialog();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
