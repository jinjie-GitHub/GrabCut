package com.ltzk.mbsf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.base.BaseFragment;
import com.ltzk.mbsf.bean.RequestBean;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * update on 2021/06/08
 */
public class DictionaryFragment extends BaseFragment {
    public static final int REQ_SEARCH_KEY = 1;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @OnClick(R.id.iv_back)
    public void iv_back(View view) {
        this.onBackPressedSupport();
    }

    @BindView(R.id.tv_key)
    TextView tvKey;
    @OnClick(R.id.tv_key)
    public void tv_key(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZI, "", "", REQ_SEARCH_KEY);
    }

    @BindView(R.id.swipe_target)
    WebView webview;
    String url = "";

    @BindView(R.id.loading)
    ProgressBar mProgressBar;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private RequestBean requestBean = new RequestBean();
    public void search(String key) {
        tvKey.setText(key);
        requestBean.addParams("key", key);
        url = RetrofitManager.BASE_URL + "zidian/query?p=" + requestBean.getParams();
        webview.loadUrl(url);
    }

    private void initView() {
        initWebSettings();
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgressDialog("");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                closeProgressDialog();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                closeProgressDialog();
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("key"))) {
            search(getArguments().getString("key"));
        }
    }

    private final void initWebSettings() {
        WebSettings webSetting = webview.getSettings();
        webSetting.setDisplayZoomControls(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSetting.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        //webSetting.setSupportMultipleWindows(true);
        webSetting.setAllowFileAccess(true); //设置可以访问文件
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSetting.setDomStorageEnabled(true);// JS在HTML里面设置了本地存储localStorage，java中使用localStorage则必须打开
        webSetting.setUseWideViewPort(true); // 自适应屏幕
    }

    @Override
    protected void showProgressDialog(String msg) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void closeProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onBackPressedSupport() {
        if (webview.canGoBack() && !webview.getUrl().equals(url)) {
            webview.goBack();
            return true;
        }
        if (mDictionaryFragmentCallBack != null) {
            mDictionaryFragmentCallBack.onBack();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.removeAllViews();
            webview.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SEARCH_KEY && resultCode == Activity.RESULT_OK) {
            search(data.getStringExtra(SearchActivity.KEY));
        }
    }

    private DictionaryFragmentCallBack mDictionaryFragmentCallBack;
    public void setDictionaryFragmentCallBack(DictionaryFragmentCallBack mDictionaryFragmentCallBack) {
        this.mDictionaryFragmentCallBack = mDictionaryFragmentCallBack;
    }

    public interface DictionaryFragmentCallBack {
        void onBack();
    }
}