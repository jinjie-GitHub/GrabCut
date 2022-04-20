package com.ltzk.mbsf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.request.target.ViewTarget;
import com.google.gson.Gson;
import com.ltzk.mbsf.activity.LoginTypeActivity;
import com.ltzk.mbsf.activity.MyWebActivity;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.XeLoginBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.Const;
import com.ltzk.mbsf.utils.CrashHandler;
import com.ltzk.mbsf.utils.EncryptUtil;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.Util;
import com.ltzk.mbsf.widget.MyClassicsFooter;
import com.ltzk.mbsf.widget.MyClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.WebStorage;
import com.xiaoe.shop.webcore.core.XiaoEWeb;

import java.util.Calendar;

import me.yokeyword.fragmentation.Fragmentation;

/**
 * Created by Administrator on 2017-04-28.
 */
public class MainApplication extends MultiDexApplication {
    //打印调试开关
    public static boolean IS_DEBUG = false;

    //广点通广告
    public final static String APPID = "1110935028";

    public final static String SplashPosID = "3061120734545875";

    //渠道
    public static String channel = "";
    public static String brand = "模拟器";
    public static String versionName = "";
    public static boolean isTablet = false;

    private static MainApplication instance;
    public static MainApplication getInstance() {
        return instance;
    }

    public IWXAPI api;
    public final static String seed = "K*8#6Mygsf10&yP+";
    private UserBean userBean;
    private String token;
    public XeLoginBean mXeLoginBean;

    private String getMyProcessName() {
        String processName = "";
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == pid) {
                processName = runningAppProcessInfo.processName;
                break;
            }
        }
        return processName;
    }

    @Override
    protected void attachBaseContext(Context base) {
        ViewTarget.setTagId(R.id.glideIndexTag);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        brand = Build.MANUFACTURER;
        isTablet = Util.isPad(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            final String processName = getMyProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        initSDK();
    }

    public final void initSDK() {
        if (getMyProcessName().equals(getPackageName())) {
            api = WXAPIFactory.createWXAPI(instance, Constan.wechat_APPID);
            api.registerApp(Constan.wechat_APPID);
            initFragmentation();
            initX5Environment();
            CrashHandler.getInstance().init(instance);
            XiaoEWeb.init(instance, Const.APP_ID, Const.CLIENT_ID, XiaoEWeb.WebViewType.X5);
        }
    }

    private void initX5Environment() {
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Logger.d("onDownloadFinish --->下载X5内核完成：" + i);
            }

            @Override
            public void onInstallFinish(int i) {
                Logger.d("onInstallFinish --->安装X5内核进度：" + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                Logger.d("onDownloadProgress --->下载X5内核进度：" + i);
            }
        });

        final QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                Logger.d("--->QbSdk：" + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initFragmentation() {
        Fragmentation.builder()
                .stackViewMode(Fragmentation.NONE)
                .debug(true)
                .handleException(e -> {
                }).install();
    }

    /**
     * 获取版本名
     */
    public static String getVersionName() {
        if (TextUtils.isEmpty(versionName)) {
            try {
                PackageManager packageManager = instance.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        instance.getPackageName(), 0);
                versionName = packageInfo.versionName;
            } catch (Exception e) {
                Logger.d("getVersionName:" + e.toString());
            }
        }
        return versionName;
    }

    /**
     * 获取渠道标识
     */
    public static String getChannel() {
        if (TextUtils.isEmpty(channel)) {
            try {
                ApplicationInfo var2 = instance.getPackageManager().getApplicationInfo(instance.getPackageName(), PackageManager.GET_META_DATA);
                if (var2 != null) {
                    Object var3 = var2.metaData.get("CHANNEL");
                    if (var3 != null) {
                        channel = var3.toString();
                    }
                }
            } catch (Exception e) {
                channel = "mysrv";
            }
        }
        return channel;
    }

    /**
     * 退出
     */
    public void quit() {
        try {
            token = null;
            userBean = null;
            SPUtils.remove(instance, "user");
            SPUtils.remove(instance, "token");
            SPUtils.remove(instance, "ad_setting");
            XiaoEWeb.userLogout(getApplicationContext());

            //WebView清除缓存
            WebStorage.getInstance().deleteAllData();
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        } catch (Exception e) {

        }
    }

    public void putToken(String token) {
        this.token = token;
        SPUtils.put(instance, "token", token);
    }

    public String getToken() {
        if (TextUtils.isEmpty(token)) {
            token = (String) SPUtils.get(instance, "token", "");
        }
        return token;
    }

    public final boolean isLogin() {
        return !TextUtils.isEmpty(getToken()) && !TextUtils.isEmpty(getUser().get_id());
    }

    public UserBean getUser() {
        if (userBean == null) {
            try {
                String userStr = (String) SPUtils.get(instance, "user", "");
                userStr = EncryptUtil.decrypt(userStr, seed.replaceAll("10", "27"));
                userBean = new Gson().fromJson(userStr, UserBean.class);
            } catch (Exception e) {
                Logger.d("getUser:" + e.toString());
            } finally {
                if (userBean == null) {
                    SPUtils.remove(instance, "token");
                    userBean = new UserBean();
                }
            }
        }
        return userBean;
    }

    public void putUser(UserBean userBean) {
        this.userBean = userBean;
        try {
            String userStr = EncryptUtil.encrypt(new Gson().toJson(userBean), seed.replaceAll("10", "27"));
            SPUtils.put(instance, "user", userStr);
        } catch (Exception e) {
            Logger.d("putUser:" + e.toString());
        }
        //混淆视听
        SPUtils.put(instance, "userInfo", new Gson().toJson(userBean));
    }

    public boolean isCanGo(Activity activity, View view, String free, String params) {
        if (TextUtils.isEmpty(free) || "1".equals(free)) {//非精品
            return true;
        } else {
            if (TextUtils.isEmpty(getToken())) {
                TipPopView tipPopView = new TipPopView(activity, "请您先登录", "仅VIP会员可查看精品字帖。", "登录", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        activity.startActivity(new Intent(activity, LoginTypeActivity.class));
                    }
                });
                tipPopView.showPopupWindow(view);
            } else {
                //会员有效期
                long now = Calendar.getInstance().getTimeInMillis();
                UserBean userBean = MainApplication.getInstance().getUser();
                long expire = userBean.get_expire() * 1000;
                if (expire < now) {//非vip用户看精品
                    activity.startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", RetrofitManager.RES_URL + "iap/index.php?p=" + params).putExtra("title", "VIP会员续费"));
                } else {//vip用户看精品
                    return true;
                }
            }
        }
        return false;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //layout.setPrimaryColorsId(Color.alpha(#4545445) ,R.color.text_color);//全局设置主题颜色
                return new MyClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new MyClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
}