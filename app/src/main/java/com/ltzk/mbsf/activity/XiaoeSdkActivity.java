package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginCannel;
import com.ltzk.mbsf.bean.XeLoginBean;
import com.ltzk.mbsf.utils.Const;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xiaoe.shop.webcore.core.XEToken;
import com.xiaoe.shop.webcore.core.XiaoEWeb;
import com.xiaoe.shop.webcore.core.bridge.JsBridgeListener;
import com.xiaoe.shop.webcore.core.bridge.JsCallbackResponse;
import com.xiaoe.shop.webcore.core.bridge.JsInteractType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/03/09
 */
public class XiaoeSdkActivity extends BaseActivity {

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.web_layout)
    RelativeLayout mWebLayout;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.ll_loading)
    View mProgressBar;

    private String mUrl;
    private XiaoEWeb xiaoEWeb;
    public static void safeStart(Context activity, String url) {
        Intent intent = new Intent(activity, XiaoeSdkActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_youzan;
    }

    @Override
    public void initView() {
        setupViews();
        initEvent();
    }

    private void setupViews() {
        mTopBar.setTitle("");
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            if (!xiaoEWeb.handlerBack()) {
                finish();
            }
        });

        mTopBar.setRightTxtListener("刷新", v -> {
            xiaoEWeb.reload();
        });

        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mRefreshLayout.finishRefresh(false);
            xiaoEWeb.reload();
        });

        mUrl = getIntent().getStringExtra("url");
        xiaoEWeb = XiaoEWeb.with(this)
                .setWebParent(mWebLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultUI()
                .useDefaultTopIndicator(getResources().getColor(R.color.colorPrimary))
                .buildWeb()
                .loadUrl(TextUtils.isEmpty(mUrl) ? Const.SHOP_URL : mUrl);

        user_sdk_login();
        //fixWebViewByRefresh();
    }

    private final void fixWebViewByRefresh() {
        final View webView = xiaoEWeb.getRealWebView();
        if (webView == null) {
            return;
        }

        final boolean isX5WebView = (webView instanceof com.tencent.smtt.sdk.WebView);

        //WebView与下拉刷新控件滑动冲突
        webView.setOnTouchListener((v, event) -> {
            if (isX5WebView) {
                com.tencent.smtt.sdk.WebView webView1 = (com.tencent.smtt.sdk.WebView) webView;
                mRefreshLayout.setEnableRefresh(webView1.getView().getScrollY() <= 0);
            } else {
                android.webkit.WebView webView2 = (android.webkit.WebView) webView;
                mRefreshLayout.setEnableRefresh(webView2.getScrollY() <= 0);
            }
            return false;
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY < 100) {
                    if (isX5WebView) {
                        com.tencent.smtt.sdk.WebView webView1 = (com.tencent.smtt.sdk.WebView) webView;
                        mRefreshLayout.setEnableRefresh(webView1.getView().getScrollY() <= 0);
                    } else {
                        android.webkit.WebView webView2 = (android.webkit.WebView) webView;
                        mRefreshLayout.setEnableRefresh(webView2.getScrollY() <= 0);
                    }
                }
            });
        }*/
    }

    private void initEvent() {
        xiaoEWeb.setJsBridgeListener(new JsBridgeListener() {
            @Override
            public void onJsInteract(int actionType, JsCallbackResponse response) {
                switch (actionType) {
                    case JsInteractType.LOGIN_ACTION:
                        //H5登录态请求 这里三方 APP 应该调用登陆接口，获取到token_key, token_value
                        //如果登录成功，通过XiaoEWeb.sync(XEToken)方法同步登录态到H5页面
                        //如果登录失败，通过XiaoEWeb.syncNot()清除登录态（注意用户需要实现自己的SDK的登录）
                        if (MainApplication.getInstance().isLogin()) {
                            user_sdk_login();
                        } else {
                            activity.startActivity(new Intent(activity, LoginTypeActivity.class));
                        }
                        break;

                    case JsInteractType.SHARE_ACTION:
                        //H5分享请求回调，通过 response.getResponseData() 获取分享的数据，这里三方 APP 自行分享登录方法
                        //  Toast.makeText(XeSdkDemoActivity.this, response.getResponseData(), Toast.LENGTH_SHORT).show();
                        xiaoEWeb.share();
                        break;

                    case JsInteractType.TITLE_RECEIVE:
                        //H5标题回调，通过 response.getResponseData() 获取标题
                        mTopBar.setTitle(String.valueOf(response.getResponseData()));
                        break;

                    case JsInteractType.LOGOUT_ACTION:
                        xiaoEWeb.syncNot();
                        break;

                    case JsInteractType.NOTICE_OUT_LINK_ACTION:
                        //sdk通知需要外部打开的链接回调，通过 response.getResponseData() 获取外链
                        //原来后台自定义链接：
                        //1.本来不带参数的  http://www.baidu.com
                        //2.本来带参数的  http://www.baidu.com?xxx=xxx
                        //需要sdk通知外部打开，拼装带参数needoutlink=1
                        //1.本来不带参数的 http://www.baidu.com?needoutlink=1
                        //2.本来带参数的  http://www.baidu.com?xxx=xxx&needoutlink=1
                        openUrl(String.valueOf(response.getResponseData()));
                        break;
                }
            }
        });
    }

    private void openUrl(String url) {
        mWebLayout.post(() -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        });
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Integer code) {
        if (code == 0x1000) {
            user_sdk_login();
        }
    }

    //登录取消
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginCannel messageEvent) {
        //xiaoEWeb.loginCancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        xiaoEWeb.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressedSupport() {
        if (!xiaoEWeb.handlerBack()) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        xiaoEWeb.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        xiaoEWeb.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        xiaoEWeb.onDestroy();
    }

    private void user_sdk_login() {
        if (MainApplication.getInstance().mXeLoginBean != null) {
            final XeLoginBean loginEntity = MainApplication.getInstance().mXeLoginBean;
            XEToken token = new XEToken(loginEntity.token_key, loginEntity.token_value);
            xiaoEWeb.sync(token);
        }
    }
}