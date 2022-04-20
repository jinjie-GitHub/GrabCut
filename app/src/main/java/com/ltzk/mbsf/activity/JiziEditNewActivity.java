package com.ltzk.mbsf.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.JiziEditPresenterImpl;
import com.ltzk.mbsf.api.view.JiziEditView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.JiZiSettingPopView;
import com.ltzk.mbsf.popupview.JiziAutoPopView;
import com.ltzk.mbsf.popupview.JiziLibPopView;
import com.ltzk.mbsf.popupview.JiziSelectPopView;
import com.ltzk.mbsf.popupview.JiziZiTiePopView;
import com.ltzk.mbsf.popupview.TipPop2View;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.AppLogToFileUtils;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xujiaji.happybubble.BubbleLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by JinJie on 2020/5/31
 */
public class JiziEditNewActivity extends MyBaseActivity<JiziEditView, JiziEditPresenterImpl> implements JiziEditView {

    public static final int REQ_SEARCH_AUTHOR_SELECT = 2;
    public static final int REQ_SEARCH_AUTHOR_AUTO = 3;
    public static final int REQ_SEARCH_AUTHOR_ZITIE = 6;
    public static final int REQ_SEARCH_JIZI_ZITIE = 7;
    public static final String EXTRA_ZITIE_PICK = "zitie_pick";

    private static final String JIZI_SHOW = RetrofitManager.BASE_URL + "jizi/show?p=";
    private static final String CLICK_PICKER = "jizi.open.picker";
    private static final String CLICK_GLYPH = "jizi.open.glyph";
    private static final String SNAPSHOT_SUCCESS = "jizi.snapshot.success";
    private static final String SNAPSHOT_FAILURE = "jizi.snapshot.failure";

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.ll_loading)
    View mProgressBar;

    private JiziBean jiziBean;
    private RequestBean requestBean;
    private volatile String mPrompt;
    private JiziAutoPopView mJiziAutoPopView;
    private JiziLibPopView mJiziLibPopView;

    public static void safeStart(Context c, JiziBean bean) {
        Intent intent = new Intent(c, JiziEditNewActivity.class);
        intent.putExtra("jiziBean", bean);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jizi_edit_new;
    }

    @Override
    public void initView() {
        jiziBean = (JiziBean) getIntent().getSerializableExtra("jiziBean");
        if (jiziBean == null) {
            finish();
            return;
        }

        initWebSettings();
        initWebClient();
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

        requestBean = new RequestBean();
        requestBean.addParams("jid", jiziBean.get_id());
        mWebView.loadUrl(getUrl());
    }

    @OnClick({R.id.iv_back, R.id.tv_design, R.id.tv_auto, R.id.tv_zitie, R.id.tv_fontlib, R.id.tv_preview})
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_design:
                onJiZiDesign(v);
                break;
            case R.id.tv_auto:
                if (mJiziAutoPopView == null) {
                    BubbleLayout bl = new BubbleLayout(activity);
                    bl.setLookLength(ViewUtil.dpToPx(10));
                    bl.setLookWidth(ViewUtil.dpToPx(20));
                    bl.setBubbleRadius(0);
                    mJiziAutoPopView = new JiziAutoPopView(activity)
                            .setClickedView(v)
                            .setLayout(MATCH_PARENT, WRAP_CONTENT, ViewUtil.dpToPx(20))
                            .setOffsetY(-15)
                            .setBubbleLayout(bl);
                    mJiziAutoPopView.setClickListener(new JiziAutoPopView.OnClickJiziAutoPopViewListener() {
                        @Override
                        public void auto(RequestBean requestBean) {
                            mJiziAutoPopView.dismiss();
                            requestBean.addParams("jid", jiziBean.get_id());
                            presenter.jizi_auto(requestBean, true);
                        }
                    });
                }
                mJiziAutoPopView.show();
                break;
            case R.id.tv_fontlib:
                if (mJiziLibPopView == null) {
                    BubbleLayout bl = new BubbleLayout(activity);
                    bl.setLookLength(ViewUtil.dpToPx(10));
                    bl.setLookWidth(ViewUtil.dpToPx(20));
                    bl.setBubbleRadius(0);
                    mJiziLibPopView = new JiziLibPopView(activity)
                            .setClickedView(v)
                            .setLayout(MATCH_PARENT, WRAP_CONTENT, ViewUtil.dpToPx(20))
                            .setOffsetY(-15)
                            .setBubbleLayout(bl);
                    mJiziLibPopView.setClickListener(new JiziLibPopView.OnClickJiziAutoPopViewListener() {
                        @Override
                        public void auto(RequestBean requestBean) {
                            mJiziLibPopView.dismiss();
                            requestBean.addParams("jid", jiziBean.get_id());
                            presenter.jizi_auto(requestBean, true);
                        }
                    });
                }
                mJiziLibPopView.show();
                break;
            case R.id.tv_zitie:
                onZiTieCollection(v);
                break;
            case R.id.tv_preview:
                JiziPreviewActivity.safeStart(activity, jiziBean);
                break;
        }
    }

    private void onJiZiDesign(View anchor) {
        BubbleLayout bl = new BubbleLayout(activity);
        bl.setLookLength(ViewUtil.dpToPx(10));
        bl.setLookWidth(ViewUtil.dpToPx(20));
        bl.setBubbleRadius(0);
        JiZiSettingPopView mJiZiSettingPopView = new JiZiSettingPopView(activity, jiziBean)
                .setClickedView(anchor)
                .setLayout(ViewUtil.dpToPx(180), WRAP_CONTENT, ViewUtil.dpToPx(0))
                .setOffsetY(-15)
                .setBubbleLayout(bl);
        mJiZiSettingPopView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            switch (i) {
                case 0:
                    presenter.getJiZiText(jiziBean.get_id());
                    break;
                case 1:
                    jiziBean._layout = "V".equals(jiziBean._layout) ? "H" : "V";
                    script = "$.set_layout('" + jiziBean._layout + "')";
                    presenter.jizi_update_style(jiziBean.get_id(), "layout", jiziBean._layout);
                    break;
                case 2:
                    jiziBean._direction = "R2L".equals(jiziBean._direction) ? "L2R" : "R2L";
                    script = "$.set_direction('" + jiziBean._direction + "')";
                    presenter.jizi_update_style(jiziBean.get_id(), "direction", jiziBean._direction);
                    break;
                case 3:
                    showTipClean();
                    break;
            }
        });
        mJiZiSettingPopView.show();
    }

    @Override
    protected void onResume() {
        if (mZiTiePopView != null) {
            mZiTiePopView.setZiTieInfo();
        }
        super.onResume();
    }

    private final String getUrl() {
        requestBean.addParams("timestamp", String.valueOf(System.currentTimeMillis()));
        return JIZI_SHOW + requestBean.getParams();
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mWebView.loadUrl(getUrl());
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
                    final String key = uri.getQueryParameter("key");
                    mPrompt = uri.getQueryParameter("prompt");
                    if (url.contains(CLICK_PICKER)) {
                        final int x = Integer.parseInt(uri.getQueryParameter("x"));
                        final int y = Integer.parseInt(uri.getQueryParameter("y"));
                        Log.d("king", "--->" + ("key:" + key + ",prompt:" + mPrompt + ",x:" + x + ",y:" + y));
                        showSelectPop(key, x, y);
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("king", "e:" + e.getMessage());
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                disimissProgress();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                disimissProgress();
                AppLogToFileUtils.getInstance().write(error.getDescription());
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void handleSnapshot() {
        showProgress();
        mWebView.evaluateJavascript("$.gen_snapshot()", (String value) -> {
        });
    }

    private void saveSnapshot() {
        mWebView.evaluateJavascript("$.get_snapshot()", (String value) -> {
            if (!TextUtils.isEmpty(value)) {
                try {
                    value = value.substring(1, value.length() - 1).replace("\\", "");
                    byte[] b = Base64.decode(value.getBytes(), Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    savePicToSdcard(bitmap);
                } catch (Exception e) {
                    Log.d("king", "e:" + e.getMessage());
                }
            }
        });
    }

    private void savePicToSdcard(Bitmap bitmap) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                BitmapUtils.savePicToSdcard(getApplication(), bitmap, String.valueOf(System.currentTimeMillis()));
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    /**
     * TODO 点击右上角自动集字-->Result
     */
    public void jizi_autoResult(JiziBean bean) {
        if (bean != null) {
            mRefreshListener.onRefresh(mRefreshLayout);
        }
        EventBus.getDefault().post(new Bus_JiziUpdata(jiziBean));
    }

    private String script = "";
    public void jizi_update(boolean success) {
        mWebView.evaluateJavascript(script, (String value) -> {
        });
    }

    @Override
    public void jizi_tmpl_list(Object data) {

    }

    @Override
    public void jizi_glyphs(Object data) {

    }

    /**
     * 字帖集字
     */
    private JiziZiTiePopView mZiTiePopView;
    private void onZiTieCollection(View view) {
        if (mZiTiePopView != null) {
            mZiTiePopView.show();
            return;
        }
        BubbleLayout bl = new BubbleLayout(activity);
        bl.setLookLength(ViewUtil.dpToPx(10));
        bl.setLookWidth(ViewUtil.dpToPx(20));
        bl.setBubbleRadius(0);
        mZiTiePopView = new JiziZiTiePopView(activity)
                .setClickedView(view)
                .setLayout(MATCH_PARENT, WRAP_CONTENT, ViewUtil.dpToPx(20))
                .setOffsetY(-15)
                .setBubbleLayout(bl);
        mZiTiePopView.show();
        mZiTiePopView.setClickListener(new JiziZiTiePopView.OnClickJiziAutoPopViewListener() {
            @Override
            public void auto(RequestBean requestBean) {
                mZiTiePopView.dismiss();
                requestBean.addParams("jid", jiziBean.get_id());
                presenter.jizi_auto(requestBean, true);
            }
        });
    }

    /**
     * 清除选字
     */
    private TipPopView tipPopViewClean;
    private void showTipClean() {
        if (tipPopViewClean == null) {
            tipPopViewClean = new TipPopView(activity, "", "清除所有已选择的字形？", "清除", new TipPopView.TipListener() {
                @Override
                public void ok() {
                    presenter.jiZiUnset(jiziBean.get_id(), 0, 0);
                }
            });
        }
        tipPopViewClean.showPopupWindow(topBar);
    }

    /**
     * 点击GridView弹出选字窗
     */
    private JiziSelectPopView jiziSelectPopView;
    private void showSelectPop(final String key, final int x, final int y) {
        final ZiBean bean = new ZiBean();
        bean.set_key(key);
        bean.set_hanzi(key);
        /*if (!TextUtils.isEmpty(bean.get_key()) && Util.isChineseChar(bean.get_key().charAt(0))) {
        } else if (!TextUtils.isEmpty(bean.get_hanzi()) && Util.isChineseChar(bean.get_hanzi().charAt(0))) {
        } else {
            ToastUtil.showToast(activity, "所选的不是汉字！");
            return;
        }*/
        if (jiziSelectPopView == null) {
            jiziSelectPopView = new JiziSelectPopView(activity, new JiziSelectPopView.TipListener() {
                @Override
                public void ok(ZiBean ziBean, int index) {
                    try {
                        final String json = new Gson().toJson(ziBean);
                        final String strBase64 = Base64.encodeToString(json.getBytes("UTF-8"), Base64.NO_WRAP);
                        requestBean.addParams("json", strBase64);
                        mWebView.evaluateJavascript("$.set_glyph('" + strBase64 + "')", (String value) -> {
                            Log.d("king", "---->" + value);
                            if (!TextUtils.isEmpty(value) && value.contains(",")) {
                                autoJiZi("1".equals(mPrompt));
                                try {
                                    value = value.substring(1, value.length() - 1).replace("\\", "");
                                    final String[] args = value.split(",");
                                    RequestBean requestBean = new RequestBean();
                                    requestBean.addParams("jid", jiziBean.get_id());
                                    requestBean.addParams("gid", ziBean.get_id());
                                    requestBean.addParams("row", Integer.parseInt(args[0]));
                                    requestBean.addParams("col", Integer.parseInt(args[1]));
                                    presenter.updateGid(requestBean);
                                } catch (Exception e) {}
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
            jiziSelectPopView.arrow(true).radius(0);
        }

        final int[] locationInScreen = {ViewUtil.dpToPx(x), ViewUtil.dpToPx(y)};
        int left = locationInScreen[0];
        int top = locationInScreen[1];

        final Rect rect = new Rect();
        rect.left = left;
        //rect.top = top + getStatusBarHeight(activity) + ViewUtil.dpToPx(40);
        rect.top = top;
        rect.right = (int) (rect.left + 0.1);
        rect.bottom = (int) (rect.top + 0.1);
        jiziSelectPopView.setData(bean, 0);
        jiziSelectPopView.showPopupWindow2(topBar, rect);
        /*jiziSelectPopView.onDismiss(() -> {
            jiziSelectPopView.dismiss();
            jiziSelectPopView = null;
        });*/
    }

    private void autoJiZi(final boolean isAuto) {
        if (isAuto) {
            TipPop2View tipPopView = new TipPop2View(activity, "", "是否参考该字风格自动集字？", new TipPop2View.TipListener() {
                @Override
                public void ok() {
                    requestBean.addParams("reset", false);
                    presenter.jizi_auto(requestBean, true);
                }

                @Override
                public void cannel() {

                }
            });
            tipPopView.showPopupWindow(mWebView);
        }
    }

    @Override
    public void loadDataSuccess(String tData) {
        if (!TextUtils.isEmpty(tData)) {
            jiziBean.setText(tData);
            startActivity(new Intent(activity, JiziNewActivity.class).putExtra("jiziBean", jiziBean));
        } else {
            jizi_autoResult(tData == null ? null : jiziBean);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQ_SEARCH_AUTHOR_SELECT) {
            jiziSelectPopView.setAuthor(data.getStringExtra(SearchActivity.KEY));
        } else if (requestCode == REQ_SEARCH_AUTHOR_AUTO) {
            mJiziAutoPopView.setAuthor(data.getStringExtra(SearchActivity.KEY));
        } else if (requestCode == 4) {
            mJiziLibPopView.setZilibBean((ZilibBean) data.getSerializableExtra("zilibBean"));
        } else if (requestCode == REQ_SEARCH_AUTHOR_ZITIE) {
            if (mZiTiePopView != null) {
                mZiTiePopView.setAuthor(data.getStringExtra(SearchActivity.KEY));
            }
        } else if (requestCode == REQ_SEARCH_JIZI_ZITIE) {
            if (mZiTiePopView != null) {
                String key = data.getStringExtra(SearchActivity.KEY);
                Intent intent = new Intent(activity, ZitieZuopingListActivity.class);
                intent.putExtra("type", 0).putExtra("key", key);
                intent.putExtra(EXTRA_ZITIE_PICK, true);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            jiziBean = (JiziBean) intent.getSerializableExtra("jiziBean");
            if (jiziBean != null) {
                mWebView.loadUrl(getUrl());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected JiziEditPresenterImpl getPresenter() {
        return new JiziEditPresenterImpl();
    }

    @Override
    public void showProgress() {
        //showProgressDialog("");
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void disimissProgress() {
        //closeProgressDialog();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
    }

    private void initWebSettings() {
        WebSettings webSetting = mWebView.getSettings();
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
}