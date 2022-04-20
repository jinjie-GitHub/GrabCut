package com.ltzk.mbsf.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.util.List;

/**
 * Created by JinJie on 2021/3/30
 */
public class ShareUtils {

    private static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
    private static final String PACKAGE_QZONE = "com.qzone";

    public static boolean isAppInstalled(Context context, String app_package) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 直接分享纯文本内容至QQ好友
     */
    public static void shareQQ(Context ctx, String title, String desc, String url) {
        if (isAppInstalled(ctx, PACKAGE_MOBILE_QQ)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            sb.append(title).append(System.lineSeparator());
            sb.append(desc).append(System.lineSeparator());
            sb.append(url);
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName(PACKAGE_MOBILE_QQ, "com.tencent.mobileqq.activity.JumpActivity"));
            ctx.startActivity(Intent.createChooser(intent, "Share"));
        } else {
            ToastUtil.showToast(ctx, "您需要安装QQ客户端。");
        }
    }

    /**
     * 直接分享纯文本内容至QQ空间
     */
    public static void shareQQZone(Context ctx, String title, String desc, String url) {
        if (isAppInstalled(ctx, PACKAGE_QZONE)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            sb.append(title).append(System.lineSeparator());
            sb.append(desc).append(System.lineSeparator());
            sb.append(url);
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName(PACKAGE_QZONE, "com.qzonex.module.operation.ui.QZonePublishMoodActivity"));
            ctx.startActivity(Intent.createChooser(intent, "Share"));
        } else {
            ToastUtil.showToast(ctx, "您需要安装QQ空间客户端。");
        }
    }
}