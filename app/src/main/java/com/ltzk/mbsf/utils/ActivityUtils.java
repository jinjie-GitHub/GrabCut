package com.ltzk.mbsf.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.ltzk.mbsf.MainApplication;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.File;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * by y on 2016/4/29.
 */
@SuppressWarnings("ALL")
public class ActivityUtils {

    /****************
     *
     * 发起添加群流程。群号：以观书法(20654673) 的 key 为： emGWrX2DPO8XOKuwm5pOhP6fM3qOmi2Z
     * 调用 joinQQGroup(emGWrX2DPO8XOKuwm5pOhP6fM3qOmi2Z) 即可发起手Q客户端申请加群 以观书法(20654673)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public static boolean joinQQGroup(Activity activity,String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            ToastUtil.showToast(activity,"手机未安装QQ");
            return false;
        }
    }

    public static void openWeChatService(Context context) {
        final IWXAPI api = MainApplication.getInstance().api;
        // 判断当前版本是否支持拉起客服会话
        if (api.getWXAppSupportAPI() >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            WXOpenCustomerServiceChat.Req req = new WXOpenCustomerServiceChat.Req();
            req.corpId = "ww85d9054dea13b635";                                  // 企业ID
            req.url = "https://work.weixin.qq.com/kfid/kfc3827e90d8fa1e629";    // 客服URL
            api.sendReq(req);
        } else {
            ToastUtil.showToast(context, "请更新最新版本微信");
        }
    }

    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public static void callPhone(final Activity activity,final String phoneNum){

        //跳转到拨号界面
        Intent dialIntent =  new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNum));//跳转到拨号界面，同时传递电话号码
        activity.startActivity(dialIntent);

        //直接拨打电话
        /*Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(
                Manifest.permission.CALL_PHONE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + phoneNum);
                intent.setData(data);
                activity.startActivity(intent);
            }

            @Override
            public void onDenied(List<String> permissions) {

            }
        });*/
    }


    public static boolean editTextValueIsNull(TextView text){
        if(text.getText()!=null && text.getText().toString()!=null && !text.getText().toString().equals("")){
            return true;
        }
        return false;
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public static void showInput(Activity activity, final EditText et) {
        try {
            et.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {

        }
    }

    // 收起软键盘
    public static void closeSyskeyBroad(Activity activity) {
        try {
            if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
                ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {

        }
    }

    // 收起软键盘
    public static void closeSyskeyBroad(Activity activity, View view) {
        try {
            ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    //屏幕高度
    public static int getTop(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        return height;
    }

    //toolbar高度
    public static int getToolBarTop(Toolbar toolbar) {
        return toolbar.getTop();
    }


    //状态栏高度
    public static int getRectTop(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int i = outRect.top;
        return i;
    }
    
    //获取图库路径
    public static File ImagePath(){
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/DCIM");
        if (!file.exists()) {
            file.mkdirs();
        }
        File mFile = new File(file + "/Demo");
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        return mFile.getAbsoluteFile();
    }

    //获取音频路径
    public static File getVoicePath(){
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/DCIM");
        if (!file.exists()) {
            file.mkdirs();
        }
        File mFile = new File(file + "/voice");
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        return mFile.getAbsoluteFile();
    }


}
