package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.BigZitiePresenterImpl;
import com.ltzk.mbsf.api.view.BigZitieView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.SoucangCannelBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.popupview.BrushPopView;
import com.ltzk.mbsf.popupview.GeXianPopView;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.DialogUtils;
import com.ltzk.mbsf.utils.MirrorRotateHelper;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MarkView;
import com.ltzk.mbsf.widget.MySeekBar;
import com.ltzk.mbsf.widget.MyWebView;
import com.ltzk.mbsf.widget.pen.PaintView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import notchtools.geek.com.notchtools.NotchTools;

/**
 * update on 2021/5/31
 */
public class BigZitieActivity extends AppCompatActivity implements BigZitieView {

    @BindView(R.id.swipe_target)
    MyWebView webview;

    @BindView(R.id.iv_rotate)
    ImageView ivRotate;

    @BindView(R.id.ll_loading)
    View mProgressBar;

    @BindView(R.id.markView)
    MarkView mMarkView;

    @BindView(R.id.paintView)
    PaintView mPaintView;

    @BindView(R.id.iv_undo)
    ImageView mUndoView;

    @BindView(R.id.iv_delete)
    ImageView mClearView;

    @BindView(R.id.tv_move)
    TextView mViewMove;

    @BindView(R.id.sd)
    TextView mSdView;

    @BindView(R.id.sc)
    TextView mScView;

    @BindView(R.id.zd)
    TextView mZdView;

    @BindView(R.id.alphaView)
    View alphaView;

    private String zid = "";
    private String url_loac = "file:///android_asset/iaperr.html";
    private AppCompatActivity activity;
    private int degrees = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((Boolean) SPUtils.get(this, "light", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.midnightBlue));
        NotchTools.getFullScreenTools().fullScreenUseStatusForActivityOnCreate(this);
        setContentView(R.layout.activity_big_zitie);
        ButterKnife.bind(this);
        initPresenter();
        initView();
    }

    private BigZitiePresenterImpl presenter;
    private RequestBean requestBean;
    private void initPresenter() {
        presenter = new BigZitiePresenterImpl();
        presenter.subscribe(this);
        requestBean = new RequestBean();
    }

    public void initView() {
        activity = this;
        mPaintView.setStepCallback(mStepCallback);
        mPaintView.init(ViewUtil.getScreenWidth(activity), ViewUtil.getScreenHeight(activity) + ViewUtil.dpToPx(80), "");
        mMarkView.setPaintType(MySPUtils.getGXPaintType(this));
        mMarkView.setPaintColor(MySPUtils.getGXPaintColor(this));

        zid = getIntent().getStringExtra("zid");
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
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                closeProgressDialog();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                webview.loadUrl(url_loac);
                closeProgressDialog();
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        requestBean.addParams("zid", zid);
        requestBean.addParams("page", "1");
        presenter.zitie_hd_url(requestBean, false);

        webview.setClick(() -> {
            clickWebView();
        });
    }

    private final PaintView.StepCallback mStepCallback = () -> {
        mUndoView.setEnabled(mPaintView.canUndo());
        mClearView.setEnabled(mPaintView.canUndo());
        mUndoView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.gray)));
        mClearView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.gray)));
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
    }

    /**
     * 显示提示框
     */
    public void showProgressDialog(String msg) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭提示框
     */
    public void closeProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.unSubscribe();
        }
        if (webview != null) {
            webview.removeAllViews();
            webview.destroy();
        }
        super.onDestroy();
    }

    /*private static void hideBottomNav(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        int vis = decorView.getSystemUiVisibility();
        vis |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(vis);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        hideBottomNav(this);
        super.onWindowFocusChanged(hasWindowFocus);
    }*/

    @OnClick({R.id.iv_close, R.id.zd, R.id.gx, R.id.sx, R.id.sd, R.id.sc, R.id.iv_rotate, R.id.tv_exit, R.id.iv_delete, R.id.iv_undo, R.id.iv_brush, R.id.iv_alpha, R.id.iv_action, R.id.tv_move})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.zd:
                processMaskView();
                break;
            case R.id.gx:
                final GeXianPopView popView = new GeXianPopView(mMarkView);
                popView.showAt2(v);
                break;
            case R.id.sx:
                mMarkView.setEnabled(false);
                processWrite(true);
                break;
            case R.id.sd:
                webview.setEnabled(!webview.isEnabled());
                mSdView.setText(webview.isEnabled() ? "固定" : "移动");
                mSdView.setCompoundDrawablesWithIntrinsicBounds(0, webview.isEnabled() ? R.mipmap.ic_lock : R.mipmap.ic_move, 0, 0);
                break;
            case R.id.sc:
                if (!MainApplication.getInstance().isLogin()) {
                    startActivity(new Intent(BigZitieActivity.this, LoginTypeActivity.class));
                    return;
                }
                if (isChecked) {
                    List<SoucangCannelBean> list_select = new ArrayList<>();
                    SoucangCannelBean bean = new SoucangCannelBean();
                    bean.setPage(1);
                    bean.setZid(zid);
                    list_select.add(bean);
                    requestBean.addParams("pages", list_select);
                    presenter.zitie_page_unfav(requestBean, false);
                } else {
                    presenter.zitie_page_fav(requestBean, false);
                }
                break;
            case R.id.iv_rotate:
                degrees = degrees == 0 ? degrees = 180 : 0;
                MirrorRotateHelper.postTranslate(ivRotate, degrees);
                mZdView.setClickable(degrees == 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mZdView.setTextColor(ContextCompat.getColor(activity, degrees == 0 ? R.color.colorPrimary : R.color.gray));
                    mZdView.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, degrees == 0 ? R.color.colorPrimary : R.color.gray)));
                }
                setRequestedOrientation(degrees == 180 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                final int w = ViewUtil.getScreenWidth(activity) + ViewUtil.dpToPx(80);
                final int h = ViewUtil.getScreenHeight(activity);
                mPaintView.onSizeChange(h, w);
                break;
            case R.id.tv_exit:
                mMarkView.setEnabled(true);
                processWrite(false);
                break;
            case R.id.iv_delete:
                mPaintView.reset();
                break;
            case R.id.iv_undo:
                mPaintView.undo();
                break;
            case R.id.iv_brush:
                final BrushPopView brushPop = new BrushPopView(mPaintView, (int color) -> {
                    mPaintView.setPaintColor(color);
                });
                brushPop.showAt2(v);
                break;
            case R.id.iv_alpha:
                handleAlpha(v);
                break;
            case R.id.iv_action:
                showDownload(v);
                break;
            case R.id.tv_move:
                mMarkView.setEnabled(mPaintView.isEnabled());
                mPaintView.setEnabled(!mPaintView.isEnabled());
                mViewMove.setText(mPaintView.isEnabled() ? "移动" : "手写");
                mViewMove.setCompoundDrawablesWithIntrinsicBounds(0, mPaintView.isEnabled() ? R.mipmap.ic_move : R.mipmap.ic_write, 0, 0);
                webview.setEnabled(!mPaintView.isEnabled());
                break;
        }
    }

    private void processWrite(boolean isShow) {
        mPaintView.reset();
        mPaintView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        alphaView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        alphaView.setAlpha(MySPUtils.getWriterAlpha(activity) / 100.0f);
        findViewById(R.id.rl_ready).setVisibility(isShow ? View.GONE : View.VISIBLE);
        findViewById(R.id.rl_write).setVisibility(isShow ? View.VISIBLE : View.GONE);
        webview.setEnabled(!isShow);
        mSdView.setText("固定");
        mSdView.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_lock, 0, 0);
    }

    private void processMaskView() {
        boolean isShow = findViewById(R.id.maskView).getVisibility() == View.VISIBLE;
        findViewById(R.id.maskView).setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    private void handleAlpha(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_printer_alpha, null);
        QMUIPopup popup = QMUIPopups.popup(this, ViewUtil.dpToPx(260))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(-40)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        DialogUtils.hideBottomNav(popup);
        final MySeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setProgress(MySPUtils.getWriterAlpha(activity));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MySPUtils.putWriterAlpha(activity, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                alphaView.setAlpha(seekBar.getProgress() / 100.0f);
            }
        });
    }

    private void showDownload(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_contrast, null);
        final TextView first = view.findViewById(R.id.tv_1);
        first.setVisibility(View.GONE);
        final TextView second = view.findViewById(R.id.tv_2);
        second.setVisibility(View.GONE);
        final QMUIPopup popup = QMUIPopups.popup(this, ViewUtil.dpToPx(140))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(-40)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);

        DialogUtils.hideBottomNav(popup);
        final TextView third = view.findViewById(R.id.tv_3);
        third.setText("保存手写图片");
        third.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(activity, mPaintView.canUndo() ? R.color.colorPrimary : R.color.gray)));
        view.findViewById(R.id.tv_3).setOnClickListener(v -> {
            popup.dismiss();
            if (mPaintView.canUndo()) {
                savePicToSdcard("手写图片已保存到您的相册。", BitmapUtils.convertViewToBitmap(mPaintView));
            }
        });
    }

    private void savePicToSdcard(String msg, Bitmap bitmap) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                ToastUtil.showToast(activity, msg);
                BitmapUtils.savePicToSdcard(getApplication(), bitmap, String.valueOf(System.currentTimeMillis()));
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    private void clickWebView() {
        if (mPaintView.getVisibility() == View.GONE) {
            boolean isShow = findViewById(R.id.rl_ready).getVisibility() == View.VISIBLE;
            findViewById(R.id.rl_ready).setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void showProgress() {
        //showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        //closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(String _hd_url) {
        webview.loadUrl(_hd_url);
        presenter.getDetail(requestBean, false);
    }

    @Override
    public void loadDataError(String errorMsg) {
        closeProgressDialog();
        ToastUtil.showToast(this, errorMsg);
    }

    @Override
    public void favAndUnFav(String errorMsg) {
        loadDataError(errorMsg);
    }

    @Override
    public void favResult(String bean) {
        closeProgressDialog();
        isChecked = true;
        mScView.setCompoundDrawablesWithIntrinsicBounds(0, isChecked ? R.mipmap.ic_zitie_faved_bold : R.mipmap.ic_zitie_fav_bold, 0, 0);
        //ToastUtil.showToast(this, "收藏成功！");
    }

    @Override
    public void unfavResult(String bean) {
        closeProgressDialog();
        isChecked = false;
        mScView.setCompoundDrawablesWithIntrinsicBounds(0, isChecked ? R.mipmap.ic_zitie_faved_bold : R.mipmap.ic_zitie_fav_bold, 0, 0);
        //ToastUtil.showToast(this, "取消收藏成功！");
    }

    private boolean isChecked;
    public void getDetailSuccess(ZitieBean bean) {
        isChecked = !(bean.get_favs() == null || bean.get_favs().size() == 0);
        mScView.setCompoundDrawablesWithIntrinsicBounds(0, isChecked ? R.mipmap.ic_zitie_faved_bold : R.mipmap.ic_zitie_fav_bold, 0, 0);
    }

    @Override
    public void getDetailFail(String bean) {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}