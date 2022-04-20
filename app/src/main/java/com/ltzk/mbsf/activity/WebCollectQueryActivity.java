package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by JinJie on 2020/10/22
 * Web集字查询界面
 */
public class WebCollectQueryActivity extends BaseActivity {
    private static final String KEY_EXTRAS = "url";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.loading)
    View mProgressBar;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private String mUrl;

    public static void safeStart(Context c, String url) {
        Intent intent = new Intent(c, WebCollectQueryActivity.class);
        intent.putExtra(KEY_EXTRAS, url);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_web_collect_query;
    }

    @Override
    public void initView() {
        initWebSettings();
        initWebClient();
        mUrl = getIntent().getStringExtra(KEY_EXTRAS);
        mWebView.loadUrl(mUrl);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        //mProgressBar.setVisibility(View.VISIBLE);
        mRefreshLayout.finishRefresh(false);
        mWebView.loadUrl(mUrl);
    };

    private void initWebClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgress();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    final Uri uri = Uri.parse(url);
                    final String content = uri.getQueryParameter("content");
                    final String path = uri.getLastPathSegment();

                    if ("jizi".equals(path)) {
                        processContent(content);
                        return true;
                    } else if ("details".equals(path)) {
                        WebCollectDetailActivity.safeStart(activity, url);
                        return true;
                    }

                } catch (Exception e) {}

                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissProgress();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                dismissProgress();
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTopBar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void processContent(String content) {
        String[] arr = content.split("，");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1) {
                sb.append("\n");
            }
        }
        final Intent intent = new Intent(activity, JiziNewActivity.class);
        intent.putExtra(JiziNewActivity.EXTRAS_CONTENT, sb.toString());
        startActivityFromBottom(intent);
    }

    private void initWebSettings() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setMediaPlaybackRequiresUserGesture(false);

        webSetting.setUseWideViewPort(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setGeolocationEnabled(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);
        webSetting.setLoadWithOverviewMode(true);
        //webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }

    //关闭页面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_JiziUpdata messageEvent) {
        finish();
    }
}