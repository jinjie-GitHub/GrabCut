package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.bean.RequestBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by JinJie on 2020/10/22
 * Web集字主页界面
 */
public class WebCollectMainActivity extends BaseActivity {
    private static final String PATH_GUSHIWEN = RetrofitManager.BASE_URL + "gushiwen/homepage?p=";
    private static final String PATH_GUSHIWEN_QUERY = RetrofitManager.BASE_URL + "gushiwen/query?p=";
    private static final String PATH_DUILIAN = RetrofitManager.BASE_URL + "duilian/homepage?p=";
    private static final String PATH_DUILIAN_QUERY = RetrofitManager.BASE_URL + "duilian/query?p=";

    private static final String PARAMS_KEY = "key";
    private static final String PARAMS_STATE = "state";
    private static final String GUSHIWEN = "诗句 / 标题 / 作者";
    private static final String DUILIAN = "查询";

    @BindView(R.id.loading)
    View mProgressBar;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.tv_search)
    TextView mTextView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mState;
    private String mUrl;
    private RequestBean mRequestBean = new RequestBean();

    public static void safeStart(Context c, int state) {
        Intent intent = new Intent(c, WebCollectMainActivity.class);
        intent.putExtra(PARAMS_STATE, state);
        c.startActivity(intent);
    }

    public static void safeStart(BaseActivity c, int state) {
        Intent intent = new Intent(c, WebCollectMainActivity.class);
        intent.putExtra(PARAMS_STATE, state);
        c.startActivityFromBottom(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_web_collect_main;
    }

    @OnClick(R2.id.iv_close)
    public void iv_close(View view) {
        finish();
    }

    @OnClick(R2.id.tv_search)
    public void tv_search(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_WEB_JIZI, "", mTextView.getText().toString(), 1);
    }

    @Override
    public void initView() {
        initWebSettings();
        initWebClient();
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mWebView.setBackgroundColor(ContextCompat.getColor(activity, R.color.whiteSmoke));
        mState = getIntent().getIntExtra(PARAMS_STATE, 1);
        mTextView.setText(mState == 1 ? GUSHIWEN : DUILIAN);
        mUrl = (mState == 1 ? PATH_GUSHIWEN : PATH_DUILIAN) + mRequestBean.getParams();
        mWebView.loadUrl(mUrl);
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mRequestBean.addParams(PARAMS_KEY, "");
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
                mWebView.post(() -> {
                    WebCollectQueryActivity.safeStart(activity, url);
                });
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
                /*if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error);*/
            }
        });
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

    /*@Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                search(data.getStringExtra(SearchActivity.KEY));
            }
        }
    }

    private void search(String key) {
        mRequestBean.addParams(PARAMS_KEY, key);
        final String url = (mState == 1 ? PATH_GUSHIWEN_QUERY : PATH_DUILIAN_QUERY) + mRequestBean.getParams();
        WebCollectQueryActivity.safeStart(activity, url);
    }

    //关闭页面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_JiziUpdata messageEvent) {
        finish();
    }
}