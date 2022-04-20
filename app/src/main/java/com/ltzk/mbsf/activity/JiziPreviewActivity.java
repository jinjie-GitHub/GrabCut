package com.ltzk.mbsf.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.adapter.MenuAdapter;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.JiziEditPresenterImpl;
import com.ltzk.mbsf.api.view.JiziEditView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.MenuItemBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.TmplListBean;
import com.ltzk.mbsf.utils.AppLogToFileUtils;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.GridItemDecoration;
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
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by JinJie on 2020/5/31
 */
public class JiziPreviewActivity extends MyBaseActivity<JiziEditView, JiziEditPresenterImpl> implements JiziEditView {

    private static final String JIZI_PREVIEW = RetrofitManager.BASE_URL + "jizi/preview?p=";
    private static final String CLICK_GLYPH = "jizi.open.glyph";
    private static final String SNAPSHOT_SUCCESS = "jizi.snapshot.success";
    private static final String SNAPSHOT_FAILURE = "jizi.snapshot.failure";

    @BindView(R.id.tv_zitie)
    TextView tv_zitie;

    @BindView(R.id.tv_fontlib)
    TextView tv_fontlib;

    @BindView(R.id.tv_auto)
    TextView tv_auto;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.ll_loading)
    View mProgressBar;

    private JiziBean mJiziBean;
    private RequestBean mRequestBean;

    public static void safeStart(Context c, JiziBean bean) {
        Intent intent = new Intent(c, JiziPreviewActivity.class);
        intent.putExtra("jiziBean", bean);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jizi_edit_new;
    }

    @Override
    public void initView() {
        mJiziBean = (JiziBean) getIntent().getSerializableExtra("jiziBean");
        if (mJiziBean == null) {
            finish();
            return;
        }

        initWebSettings();
        initWebClient();
        setUpView();
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

        mRequestBean = new RequestBean();
        mRequestBean.addParams("jid", mJiziBean.get_id());
        mWebView.loadUrl(getUrl());
    }

    private void setUpView() {
        findViewById(R.id.tv_design).setVisibility(View.GONE);
        findViewById(R.id.tv_preview).setVisibility(View.GONE);
        tv_auto.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_jizi_preview_color, 0, 0);
        tv_zitie.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_jizi_preview_tmpl, 0, 0);
        tv_fontlib.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_jizi_preview_snapshot, 0, 0);
        tv_auto.setText("颜色");
        tv_zitie.setText("模板");
        tv_fontlib.setText("保存");
    }

    @OnClick({R.id.iv_back, R.id.tv_auto, R.id.tv_zitie, R.id.tv_fontlib})
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_auto:
                processColor(v);
                break;
            case R.id.tv_fontlib:
                handleSnapshot();
                break;
            case R.id.tv_zitie:
                if (mBubbleDialog != null) {
                    mBubbleDialog.setClickedView(v).show();
                }
                break;
        }
    }

    private void processColor(View anchor) {
        BubbleLayout bl = new BubbleLayout(activity);
        bl.setLookLength(ViewUtil.dpToPx(10));
        bl.setLookWidth(ViewUtil.dpToPx(20));
        bl.setBubbleRadius(0);
        final BubbleDialog bubbleDialog = new BubbleDialog(activity)
                .setTransParentBackground()
                .setPosition(BubbleDialog.Position.BOTTOM)
                .setClickedView(anchor)
                .setLayout(ViewUtil.dpToPx(120), WRAP_CONTENT, ViewUtil.dpToPx(0))
                .setOffsetY(-15)
                .setBubbleLayout(bl);

        List<MenuItemBean> listMenu = new ArrayList();
        listMenu.add(new MenuItemBean(0, "原色"));
        listMenu.add(new MenuItemBean(0, "黑色"));
        MenuAdapter adapter = new MenuAdapter(activity);
        adapter.setData(listMenu);
        View rootView = LayoutInflater.from(activity).inflate(R.layout.widget_jizi_setting_popview, null);
        ListView listView = rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            bubbleDialog.dismiss();
            switch (i) {
                case 0:
                    script = "$.set_color('default')";
                    presenter.jizi_update_style(mJiziBean.get_id(), "color", "default");
                    break;
                case 1:
                    script = "$.set_color('black')";
                    presenter.jizi_update_style(mJiziBean.get_id(), "color", "black");
                    break;
            }
        });

        bubbleDialog.addContentView(rootView).show();
    }

    private final String getUrl() {
        mRequestBean.addParams("timestamp", String.valueOf(System.currentTimeMillis()));
        return JIZI_PREVIEW + mRequestBean.getParams();
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
                    if (url.contains(CLICK_GLYPH)) {
                        final String gid = uri.getQueryParameter("gid");
                        GlyphDetailActivity.safeStart(activity, gid, mGlyphs);
                        return true;
                    } else if (url.contains(SNAPSHOT_SUCCESS)) {
                        mWebView.post(() -> {
                            saveSnapshot();
                        });
                        return true;
                    } else if (url.contains(SNAPSHOT_FAILURE)) {
                        mWebView.post(() -> {
                            disimissProgress();
                            ToastUtil.showToast(activity, "获取截图失败");
                        });
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
                loadTmplList();
                presenter.jizi_glyphs(mJiziBean.get_id());
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
                    View view = LayoutInflater.from(activity).inflate(R.layout.dialog_snapshot, null);
                    final ImageView imageView = view.findViewById(R.id.image);
                    final AlertDialog dialog = new AlertDialog.Builder(activity).setView(view).create();
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                    Glide.with(getApplication())
                            .asBitmap()
                            .override(ViewUtil.getScreenWidth(activity) / 2, ViewUtil.getScreenHeight(activity) / 2)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .load(b)
                            .into(new ImageViewTarget<Bitmap>(imageView) {
                                @Override
                                protected void setResource(@Nullable final Bitmap resource) {
                                    if (resource != null && !dialog.isShowing()) {
                                        imageView.setImageBitmap(resource);
                                        dialog.show();
                                    }
                                    disimissProgress();
                                }
                            });
                    view.findViewById(R.id.btn_disagree).setOnClickListener(v -> dialog.dismiss());
                    view.findViewById(R.id.btn_agree).setOnClickListener(v -> {
                        dialog.dismiss();
                        savePicToSdcard(b);
                    });
                } catch (Throwable e) {
                    disimissProgress();
                }
            } else {
                disimissProgress();
            }
        });
    }

    private void savePicToSdcard(final byte[] b) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                ToastUtil.showToast(activity, "已保存到您的相册");
                new Thread(() -> {
                    BitmapUtils.savePicToSdcard2(getApplication(), b, String.valueOf(System.currentTimeMillis()));
                }).start();
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    private String script = "";
    public void jizi_update(boolean success) {
        mWebView.evaluateJavascript(script, (String value) -> {
        });
    }

    @Override
    public void jizi_tmpl_list(Object data) {
        final TmplListBean bean = (TmplListBean) data;
        if (bean != null) {
            mTmplAdapter.setNewData(bean.list);
        }
    }

    final List<DetailsBean> mGlyphs = new ArrayList<>();
    public void jizi_glyphs(Object data) {
        mGlyphs.clear();
        List<List<DetailsBean>> list = (List<List<DetailsBean>>) data;
        for (int i = 0; i < list.size(); i++) {
            final List<DetailsBean> temp = list.get(i);
            for (int j = 0; j < temp.size(); j++) {
                if (!TextUtils.isEmpty(temp.get(j)._id)) {
                    mGlyphs.add(temp.get(j));
                }
            }
        }
    }

    @Override
    public void loadDataSuccess(String tData) {
        //do nothing
    }

    @Override
    public void jizi_autoResult(JiziBean jiziBean) {
        //do nothing
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

    private TmplAdapter mTmplAdapter;
    private BubbleDialog mBubbleDialog;
    private void loadTmplList() {
        if (mBubbleDialog == null) {
            BubbleLayout bl = new BubbleLayout(activity);
            bl.setLookLength(ViewUtil.dpToPx(10));
            bl.setLookWidth(ViewUtil.dpToPx(20));
            bl.setBubbleRadius(0);
            mBubbleDialog = new BubbleDialog(activity)
                    .setTransParentBackground()
                    .setPosition(BubbleDialog.Position.BOTTOM)
                    //.setClickedView(tv_zitie)
                    .setLayout(ViewUtil.dpToPx(282), WRAP_CONTENT, ViewUtil.dpToPx(0))
                    .setOffsetY(-15)
                    .setBubbleLayout(bl);
            final View view = View.inflate(getApplication(), R.layout.popups_preview_tmpl, null);
            final SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
            refreshLayout.setEnableLoadMore(false);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 5));
            GridItemDecoration divider = new GridItemDecoration.Builder(this)
                    .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                    .setVerticalSpan(R.dimen.activity_vertical_margin)
                    .setColorResource(R.color.whiteSmoke)
                    .build();
            recyclerView.addItemDecoration(divider);
            mTmplAdapter = new TmplAdapter();
            recyclerView.setAdapter(mTmplAdapter);
            presenter.jizi_tmpl_list(0);
            mTmplAdapter.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mBubbleDialog.dismiss();
                    final TmplListBean.Tmpl tmpl = (TmplListBean.Tmpl) adapter.getItem(position);
                    mTmplAdapter.setTmplName(tmpl._name);
                    script = "$.set_tmpl('" + tmpl._name + "')";
                    presenter.jizi_update_style(mJiziBean.get_id(), "tmpl", tmpl._name);
                }
            });

            refreshLayout.setOnRefreshListener(refresh -> {
                refresh.finishRefresh(false);
                presenter.jizi_tmpl_list(0);
            });

            mBubbleDialog.addContentView(view);
        }
    }

    private class TmplAdapter extends BaseQuickAdapter<TmplListBean.Tmpl, BaseViewHolder> {
        public TmplAdapter() {
            super(R.layout.item_tmpl_list);
        }

        public void setTmplName(final String name) {
            mJiziBean._tmpl = name;
            notifyDataSetChanged();
        }

        @Override
        protected void convert(BaseViewHolder holder, TmplListBean.Tmpl bean) {
            holder.setText(R.id.name, bean._title);
            final ImageView imageView = holder.getView(R.id.icon);
            Glide.with(getContext()).load(bean._icon).into(imageView);
            if (bean._name.equals(mJiziBean._tmpl)) {
                imageView.setBackgroundResource(R.drawable.shape_jizi_tmpl_check);
            } else {
                imageView.setBackgroundResource(R.drawable.shape_jizi_tmpl_normal);
            }
        }
    }

    private final void initWebSettings() {
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