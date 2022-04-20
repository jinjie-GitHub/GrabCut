package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by JinJie on 2021/4/12
 */
public class X5WebView extends WebView {

    private X5WebViewClient mWebViewClient;

    public X5WebView(Context context) {
        super(context);
        init();
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public X5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        Drawable bg = getBackground();
        if (null != bg) {
            bg.setAlpha(0);
        }

        initWebSettings();
        initWebViewClient();
    }

    private void initWebViewClient() {
        super.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mWebViewClient != null) {
                    mWebViewClient.onPageStarted(view, url, favicon);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mWebViewClient != null) {
                    return mWebViewClient.shouldOverrideUrlLoading(view, url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mWebViewClient != null) {
                    mWebViewClient.onPageFinished(view, url);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (mWebViewClient != null) {
                    mWebViewClient.onReceivedError(view, request, error);
                }
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                sslErrorHandler.proceed();
            }
        });
    }

    private void initWebSettings() {
        WebSettings webSetting = getSettings();
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

    public void setX5WebViewClient(X5WebViewClient client) {
        this.mWebViewClient = client;
    }

    abstract static class X5WebViewClient {
        void onPageStarted(WebView view, String url, Bitmap favicon) {}
        boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
        void onPageFinished(WebView view, String url) {}
        void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {}
    }
}