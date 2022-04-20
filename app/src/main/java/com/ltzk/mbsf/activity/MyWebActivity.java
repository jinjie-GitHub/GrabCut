package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.ltzk.mbsf.BuildConfig;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.WebViewPresenterImpl;
import com.ltzk.mbsf.api.view.WebViewView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.PayWeichatBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.PicUtil;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.UrlUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * update on 2021/6/9
 */
public class MyWebActivity extends MyBaseActivity<WebViewView, WebViewPresenterImpl> implements WebViewView {

    @BindView(R.id.swipe_target)
    WebView webview;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private String url_def = "https://www.ygsf.com";
    //private String url_loc = "file:///android_asset/iaperr.html";
    private String title = "";
    private String pid = "", msg = "", qqun = "";
    private RequestBean requestBean = new RequestBean();

    public static void safeStart(Context c, String url) {
        Intent intent = new Intent(c, MyWebActivity.class);
        intent.putExtra("url", url);
        c.startActivity(intent);
    }

    @Override
    public void initView() {
        api = MainApplication.getInstance().api;
        qqun = getIntent().getStringExtra("qqun");
        title = getIntent().getStringExtra("title");
        title = "";
        topBar.setTitle("" + title);
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack() && !webview.getUrl().equals(url_def)) {
                    topBar.setRightTxt("刷新");
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });

        topBar.setRightTxtListener("刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.setVisibility(View.INVISIBLE);
                webview.postDelayed(() -> {
                    webview.loadUrl(url_def);
                    webview.setVisibility(View.VISIBLE);
                }, 100);
            }
        });

        url_def = getIntent().getStringExtra("url");
        initWebSettings();

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                final Uri uri = Uri.parse(url);
                final String path = uri.getLastPathSegment();
                UrlUtil.UrlEntity entity = UrlUtil.parse(url);
                if (url.startsWith("https://action.purchase")) {//购买
                    if (!api.isWXAppInstalled()) {
                        ToastUtil.showToast(activity, "您的手机未安装微信App");
                        return true;
                    }

                    pid = entity.params.get("pid");
                    msg = entity.params.get("msg");
                    TipPopView tipPopView = new TipPopView(activity, entity.params.get("title") + "", msg + "", new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            requestBean.addParams("pid", pid);
                            presenter.pay(requestBean, true);
                        }
                    });
                    tipPopView.showPopupWindow(webview);
                    return true;
                } else if (url.startsWith("https://action.alert")) {//提示
                    msg = entity.params.get("msg");
                    TipPopView tipPopView = new TipPopView(activity, entity.params.get("title") + "", msg + "", new TipPopView.TipListener() {
                        @Override
                        public void ok() {

                        }
                    });
                    tipPopView.showPopupWindow(webview);
                    return true;
                } else if (url.startsWith("https://action.qun")) {//加群
                    //ActivityUtils.joinQQGroup(activity, qqun);
                    ActivityUtils.openWeChatService(activity);
                    return true;
                } else if (url.startsWith("https://action.wxkf")) {//微信客服
                    ActivityUtils.openWeChatService(activity);
                    return true;
                } else if (url.startsWith("https://action.tel")) {//打电话
                    final String tel = entity.params.get("phone");
                    TipPopView tipPopView = new TipPopView(activity, "", "" + tel, "呼叫", new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            ActivityUtils.callPhone(activity, tel);
                        }
                    });
                    tipPopView.showPopupWindow(webview);
                    return true;
                } else if ("authorize".equals(path)) {//授权
                    redirect_uri = Uri.decode(uri.getQueryParameter("redirect_uri"));
                    if (MainApplication.getInstance().isLogin()) {
                        view.loadUrl(getRedirect_url());
                    } else {
                        activity.startActivity(new Intent(activity, LoginTypeActivity.class));
                    }
                    return true;
                } else if (url.startsWith("https://quanzi.ygsf.com/")) {//书友圈
                    url_def = url;
                    view.loadUrl(url);
                    return true;
                } else if (url.startsWith("https://action.refresh")) {//刷新
                    loadUlr(view, entity, url_def);
                } else if (url.startsWith("https://action.redeem")) {//确认兑换
                    final String gift = entity.params.get("gift");
                    TipPopView tipPopView = new TipPopView(activity, "兑换提示", "您将花费10000积分兑换与1个月VIP会员", new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            requestBean.addParams("gift", gift);
                            presenter.redeem(requestBean, true);
                        }
                    });
                    tipPopView.showPopupWindow(webview);
                } else if (url.startsWith(RetrofitManager.BASE_URL + "iap/redeem-help.html")) {//积分获取说明
                    loadUlr(view, entity, url);
                    topBar.setRightTxt("");
                    //topBar.setTitle("如何获取积分");
                } else {
                    loadUlr(view, entity, url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                topBar.setTitle(view.getTitle() + "");
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    topBar.setTitle(title);
                }
            }
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessageAboveL = filePathCallback;
                uploadPicture();
                return true;
            }
        });

        webview.loadUrl(url_def);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        //mRefreshLayout.setEnableOverScrollBounce(true);
        //mRefreshLayout.setEnableOverScrollDrag(true);
        //mRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        webview.loadUrl(url_def);
    };

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

        webSetting.setUserAgent("yiguanshufa");
    }

    private void loadUlr(WebView view, UrlUtil.UrlEntity entity, String url) {
        if (entity != null && entity.params != null && !TextUtils.isEmpty(entity.params.get("target"))) {
            if (entity.params.get("target").equals("browser")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else if (entity.params.get("target").equals("blank")) {
                startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", url));
            } else {
                view.loadUrl(url);
            }
        } else {
            view.loadUrl(url);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mywebview;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //支付结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(BaseResp baseResp) {
        /*0	成功	展示成功页面
		-1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
		-2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。*/
        switch (baseResp.errCode) {
            case 0:
                ToastUtil.showToast(activity, "支付成功！");
                webview.loadUrl(url_def);
                break;
            case -1:
                ToastUtil.showToast(activity, "支付失败！");
                break;
            case -2:
                ToastUtil.showToast(activity, "用户取消！");
                break;
        }
    }

    //登录成功
    private String redirect_uri;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Integer code) {
        if (code == 0x1000) {
            if (!TextUtils.isEmpty(redirect_uri)) {
                webview.loadUrl(getRedirect_url());
            }
        }
    }

    private String getRedirect_url() {
        return redirect_uri + "?access_token=" + MainApplication.getInstance().getToken();
    }

    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.removeAllViews();
            webview.destroy();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected WebViewPresenterImpl getPresenter() {
        return new WebViewPresenterImpl();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
    }

    @Override
    public void redeemResult(UserBean string) {
        ToastUtil.showToast(activity, "操作成功！");
        EventBus.getDefault().post(new Bus_UserUpdate());
    }

    private IWXAPI api;
    public void loadDataSuccess(PayWeichatBean tData) {
        PayReq req = new PayReq();
        req.appId			= tData.getAppid();
        req.partnerId		= tData.getPartnerid();
        req.prepayId		= tData.getPrepayid();
        req.nonceStr		= tData.getNoncestr();
        req.timeStamp		= tData.getTimestamp();
        req.packageValue	= tData.getPackagevalue();
        req.sign			= tData.getSign();
        req.extData			= "app data"; // optional
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }

    //TODO H5访问本地文件的时候，使用的<input type="file">，WebView出于安全性的考虑，限制了以上操作。
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int                  REQUEST_CODE_ALBUM  = 0x01;
    private static final int                  REQUEST_CODE_CAMERA = 0x02;
    private              String               mCurrentPhotoPath;
    private              ValueCallback<Uri>   mUploadMessage;
    private              ValueCallback<Uri[]> mUploadMessageAboveL;

    private void onReceiveValue(final Uri imageUri) {
        //一定要返回null,否则<input file>没有反应
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(imageUri);
            mUploadMessage = null;
        }
        if (mUploadMessageAboveL != null) {
            mUploadMessageAboveL.onReceiveValue(imageUri != null ? new Uri[] {imageUri} : null);
            mUploadMessageAboveL = null;
        }
    }

    /**
     * 选择相机或者相册
     */
    public void uploadPicture() {
        Acp.getInstance(activity)
            .request(new AcpOptions.Builder()
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .build(), new AcpListener() {
                @Override
                public void onGranted() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("请选择图片上传方式");
                    builder.setOnCancelListener(dialog -> onReceiveValue(null));
                    builder.setPositiveButton("相机", (dialog, which) -> takePhoto());
                    builder.setNegativeButton("相册", (dialog, which) -> chooseAlbum());
                    builder.create().show();
                }

                @Override
                public void onDenied(List<String> permissions) {
                    ToastUtil.showToast(activity, "没有相机权限！");
                }
            });
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(System.currentTimeMillis()).append(".png");
        File tempFile =
            new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri =
                FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider",
                    tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mCurrentPhotoPath = tempFile.getAbsolutePath();
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 选择相册照片
     */
    private void chooseAlbum() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_CAMERA) {

            if ((mUploadMessage == null && mUploadMessageAboveL == null)) {
                return;
            }

            //取消拍照或者图片选择时
            if (resultCode != RESULT_OK) {
                onReceiveValue(null);
                return;
            }

            //拍照成功和选取照片时
            Uri imageUri = null;
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {
                        imageUri = data.getData();
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                        final File file = new File(mCurrentPhotoPath);
                        imageUri = Uri.fromFile(file);
                        PicUtil.sendMediaFile(activity, file);
                    }
                    break;
            }

            //上传文件
            onReceiveValue(imageUri);
        }
    }
}