package com.ltzk.mbsf.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.AuthorDetailPresenterImpl;
import com.ltzk.mbsf.api.view.AuthorDetailView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.AuthorDetailBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.UrlUtil;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.net.URLEncoder;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * update on 2021/6/9
 */
public class AuthorDetailActivity extends MyBaseActivity<AuthorDetailView, AuthorDetailPresenterImpl> implements AuthorDetailView {

    @BindView(R.id.tv_title)
    TextView titleTxt;

    @BindView(R.id.swipe_target)
    WebView webview;

    @BindView(R.id.iv_left)
    ImageView leftButton;
    @OnClick(R.id.iv_left)
    public void left_button(View v) {
        if (tData == null) {
            finish();
            return;
        }

        if (!webview.canGoBack()) {
            finish();
        } else {
            if (webview.getUrl().startsWith(tData.get_baike())) {
                rightTxt.setText("百科");
                rightTxt2.setVisibility(View.GONE);
                leftButton.setImageResource(R.mipmap.close2);
            }
            webview.goBack();
        }
    }

    @BindView(R.id.tv_right)
    TextView rightTxt;
    @OnClick(R.id.tv_right)
    public void right_txt(View v) {
        if (tData == null) {
            finish();
            return;
        }
        if (rightTxt.getText().toString().equals("关闭")) {
            finish();
        } else {
            leftButton.setImageResource(R.mipmap.back);
            rightTxt.setText("关闭");
            rightTxt2.setVisibility(View.VISIBLE);
            webview.loadUrl(tData.get_baike());
        }
    }

    @BindView(R.id.tv_right2)
    TextView rightTxt2;
    @OnClick(R.id.tv_right2)
    public void right_txt2(View v) {
        webview.reload();
    }

    private RequestBean requestBean;
    @Override
    public void initView() {
        initWebSettings();
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgressDialog("");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//拦截处理，调用WebView自身打开
                return true;
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
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError error) {
                handler.proceed();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    titleTxt.setText(title);
                }
            }
        });

        requestBean = new RequestBean();
        requestBean.addParams("name", "" + getIntent().getStringExtra("name"));
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        presenter.author_details(requestBean, true);
    }

    private void initWebSettings() {
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
    public int getLayoutId() {
        return R.layout.activity_author_detail;
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
    protected AuthorDetailPresenterImpl getPresenter() {
        return new AuthorDetailPresenterImpl();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    private AuthorDetailBean tData;
    @Override
    public void loadDataSuccess(AuthorDetailBean data) {
        tData = data;
        webview.loadUrl(data.get_profile());
        tData.set_profile(parse(data.get_profile()));
        tData.set_baike(parse(data.get_baike()));
        right_txt(null);
    }

    private String parse(String url) {
        UrlUtil.UrlEntity entity = UrlUtil.parse(url);
        if (url == null) {
            return url;
        }
        url = url.trim();
        if (url.equals("")) {
            return url;
        }
        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //编码host 和 path
        String[] baseArray = entity.baseUrl.split("//");
        if (baseArray != null && baseArray.length > 1) {
            String host = baseArray[1];
            String hostEncode = URLEncoder.encode(host);
            hostEncode = hostEncode.replace("%2F", "/");
            //hostEncode = hostEncode.replace("%26", "&");
            entity.baseUrl = baseArray[0] + "//" + hostEncode;
        }
        //没有参数
        if (urlParts.length == 1) {
            return entity.baseUrl;
        }
        for (Map.Entry<String, String> entry : entity.params.entrySet()) {
            entity.params.put(entry.getKey(), URLEncoder.encode(entry.getValue()));
        }
        String url_new = UrlUtil.getUrl(entity);
        return url_new;
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
        finish();
    }
}