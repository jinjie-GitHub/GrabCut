package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.JavascriptInterface;
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
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by JinJie on 2020/10/22
 * Web集字详情界面
 */
public class WebCollectDetailActivity extends BaseActivity {
    private static final String KEY_EXTRAS = "url";
    private static final String RIGHT_TEXT = "集字";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.loading)
    View mProgressBar;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private String mUrl;
    private List<String> mList = new ArrayList<>();

    public static void safeStart(Context c, String url) {
        Intent intent = new Intent(c, WebCollectDetailActivity.class);
        intent.putExtra(KEY_EXTRAS, url);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_web_collect_detail;
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

        mTopBar.setRightTxtListener(RIGHT_TEXT, (v -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mList.size(); i++) {
                sb.append(mList.get(i));
                if (i != mList.size() - 1) {
                    sb.append("\n");
                }
            }
            final Intent intent = new Intent(activity, JiziNewActivity.class);
            intent.putExtra(JiziNewActivity.EXTRAS_CONTENT, sb.toString());
            startActivityFromBottom(intent);
        }));
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
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissProgress();
                view.loadUrl("javascript:window.java_obj.showContent("
                        + "document.getElementById('content').textContent);");
                view.loadUrl("javascript:window.java_obj.showContent("
                        + "document.getElementById('title').textContent);");
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

    private void initWebSettings() {
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
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

    private final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showContent(String content) {
            mList.add(content);
        }
    }
}