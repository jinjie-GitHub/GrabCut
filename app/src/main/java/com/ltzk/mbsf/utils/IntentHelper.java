package com.ltzk.mbsf.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.activity.MyWebActivity;
import com.ltzk.mbsf.activity.WebCollectMainActivity;
import com.ltzk.mbsf.activity.XiaoeSdkActivity;
import com.ltzk.mbsf.activity.ZiLibActivity;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * Created by JinJie on 2021/8/24
 */
public class IntentHelper {
    private static final String TARGET_XET = "xet";
    private static final String TARGET_SELF = "self";
    private static final String TARGET_WXMP = "wxmp";
    private static final String TARGET_WEBVIEW = "webview";
    private static final String TARGET_BROWSER = "browser";

    private static final String TAG_SFZK = "sfzk";
    private static final String TAG_SCWF = "scwf";
    private static final String TAG_DLGY = "dlgy";

    public static void processAction(Context ctx, BannerViewsInfo info) {
        if (info == null) {
            Logger.d("--->processAction: Banner Info is null");
            return;
        }
        switch (String.valueOf(info.target)) {
            case TARGET_XET:
                XiaoeSdkActivity.safeStart(ctx, info.url);
                break;
            case TARGET_SELF:
                if (info.params != null) {
                    if (TAG_SFZK.equals(info.params.tag)) {
                        ZiLibActivity.safeStart(ctx);
                    } else if (TAG_SCWF.equals(info.params.tag)) {
                        WebCollectMainActivity.safeStart(ctx, 1);
                    } else if (TAG_DLGY.equals(info.params.tag)) {
                        WebCollectMainActivity.safeStart(ctx, 2);
                    }
                }
                break;
            case TARGET_WXMP:
                if (info.params != null) {
                    openWxApplet(ctx, info.params.username, info.params.path);
                }
                break;
            case TARGET_WEBVIEW:
                MyWebActivity.safeStart(ctx, info.url);
                break;
            case TARGET_BROWSER:
                openUrl(ctx, info.url);
                break;
        }
    }

    private static void openUrl(Context ctx, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        ctx.startActivity(Intent.createChooser(intent, "请选择浏览器"));
    }

    private static void openWxApplet(Context ctx, String userName, String path) {
        IWXAPI api = MainApplication.getInstance().api;

        if (!api.isWXAppInstalled()) {
            ToastUtil.showToast(ctx, "请安装最新版本微信。");
            return;
        }

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        if (!TextUtils.isEmpty(userName)) {
            req.userName = userName; //填小程序原始id
        }
        if (!TextUtils.isEmpty(path)) {
            req.path = path; //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        }
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;//可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }
}