package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.GlyphDetailActivity;
import com.ltzk.mbsf.activity.MyWebActivity;
import com.ltzk.mbsf.activity.XiaoeSdkActivity;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.FontQueryPresenter;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by JinJie on 2021/4/13
 */
public class OpenVideoDialog extends DialogFragment implements IBaseView {
    public static final String URL = RetrofitManager.BASE_URL + "glyph/video/play?p=";

    private Context mContext;
    private X5WebView mWebView;
    private View mRelativeLayout;
    private FontQueryPresenter mPresenter;

    public static OpenVideoDialog openVideo(String vid, String gid) {
        OpenVideoDialog dialog = new OpenVideoDialog();
        Bundle args = new Bundle();
        args.putString("vid", vid);
        args.putString("gid", gid);
        dialog.setArguments(args);
        return dialog;
    }

    private final String getUrl(String vid, String gid) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);//视频id
        requestBean.addParams("gid", gid);//字形id
        return URL + requestBean.getParams();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.OpenVideoDialog);
        mContext = getActivity();
        mPresenter = new FontQueryPresenter();
        mPresenter.subscribe(OpenVideoDialog.this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final int width = ViewUtil.getScreenWidth(mContext) - ViewUtil.dpToPx(20);
        final Window window = getDialog().getWindow();
        window.setLayout(width, width);
        window.getDecorView().setPadding(0, 0, 0, 0);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.dialog_open_video, null);
        mRelativeLayout = view.findViewById(R.id.relativeLayout);
        final ProgressBar loading = view.findViewById(R.id.loading);
        mWebView = view.findViewById(R.id.webView);
        initWebChromeClient();
        mWebView.setX5WebViewClient(new X5WebView.X5WebViewClient() {
            @Override
            void onPageFinished(WebView view, String url) {
                loading.setVisibility(View.GONE);
            }

            @Override
            boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    final Uri uri = Uri.parse(url);
                    final String path = uri.getLastPathSegment();
                    final String vid = uri.getQueryParameter("vid");
                    if ("play".equals(path)) {
                        return true;
                    } else if ("show".equals(path)) {
                        final String gid = uri.getQueryParameter("gid");
                        GlyphDetailActivity.safeStart((BaseActivity) getActivity(), gid, new ArrayList<>(), true);
                        return true;
                    } else if ("purchase".equals(path)) {
                        mContext.startActivity(new Intent(mContext, MyWebActivity.class).putExtra("url", RetrofitManager.RES_URL + "iap/index.php?p=" + new RequestBean().getParams()).putExtra("title", "VIP会员续费"));
                        return true;
                    } else if ("fav".equals(path)) {
                        mPresenter.glyph_video_fav(vid);
                        return true;
                    } else if ("unfav".equals(path)) {
                        mPresenter.glyph_video_unfav(Arrays.asList(vid));
                        return true;
                    } else if (url.contains("/goods_detail/") || url.contains("https://app4jbtmyne3011.h5.xiaoeknow.com/")) {
                        XiaoeSdkActivity.safeStart(mContext, url);
                        return true;
                    } else {//返回值是true的时候WebView打开，为false调用系统浏览器或第三方浏览器
                        openUrl(url);
                        return true;
                    }
                } catch (Exception e) {}
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        if (getArguments() != null) {
            final String url = getArguments().getString("url", "");
            final String vid = getArguments().getString("vid", "");
            final String gid = getArguments().getString("gid", "");
            if (!TextUtils.isEmpty(vid) || !TextUtils.isEmpty(gid)) {
                mWebView.loadUrl(getUrl(vid, gid));
            } else {
                mWebView.loadUrl(url);
            }
        }
        return view;
    }

    private void openUrl(String url) {
        mWebView.post(() -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mContext.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        });
    }

    @Override
    public void onStart() {
        final Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        window.setNavigationBarColor(ContextCompat.getColor(mContext, R.color.black));
        window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.black));
        super.onStart();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (!isDialogFragmentShowing()) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    public boolean isDialogFragmentShowing() {
        if (this != null && this.getDialog() != null && this.getDialog().isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        if (mPresenter != null) {
            mPresenter.unSubscribe();
            mPresenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null) {
            try {
                //解决Dialog内存泄漏
                //getDialog().setOnDismissListener(null);
                //getDialog().setOnCancelListener(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            private View myView;

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                super.onShowCustomView(view, customViewCallback);
                myView = view;
                view.setBackgroundColor(Color.BLACK);
                RelativeLayout parent = (RelativeLayout) mWebView.getParent();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                parent.addView(view, params);
                parent.removeView(mWebView);
                setFullScreen();
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (myView != null) {
                    RelativeLayout parent = (RelativeLayout) myView.getParent();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    parent.addView(mWebView, params);
                    parent.removeView(myView);
                    quitFullScreen();
                    myView = null;
                }
            }
        });
    }

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRelativeLayout.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        final Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        final int margins = ViewUtil.dpToPx(10);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRelativeLayout.getLayoutParams();
        layoutParams.setMargins(margins, margins, margins, margins);
        final int width = ViewUtil.getScreenWidth(mContext) - ViewUtil.dpToPx(20);
        final Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(width, width);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Object tData) {

    }

    @Override
    public void loadDataError(String errorMsg) {

    }
}