package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.classic.common.MultipleStatusView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ZiLibDetailListAdapter;
import com.ltzk.mbsf.api.presenter.ZiLibDetailListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibDetailListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibEdit;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.JiziSelectPopView;
import com.ltzk.mbsf.utils.PicSelectUtil;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.crop.UCrop;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 上传字形图片 update on 2021/06/10
 */
public class ZiLibDetailListActivity extends MyBaseActivity<ZiLibDetailListView, ZiLibDetailListPresenterImpl> implements OnRefreshListener, ZiLibDetailListView {
    public static final int RC_CHOOSE_PHOTO = 2;

    //字典列表
    @BindView(R2.id.status_view)
    MultipleStatusView mStatus_view;

    @BindView(R2.id.refresh_layout)
    MySmartRefreshLayout mRefresh_layout;

    @BindView(R2.id.lay_bottom)
    LinearLayout lay_bottom;

    @BindView(R2.id.recyclerView)
    RecyclerView mRv_zi;
    ZiLibDetailListAdapter mAdapter;

    @OnClick(R2.id.iv_sdcard)
    public void iv_sdcard(View view) {
        choosePhoto();
    }

    @OnClick(R2.id.iv_camera)
    public void iv_camera(View view) {
        takePhoto();
    }

    @BindView(R2.id.iv_font)
    ImageView iv_font;

    @OnClick(R2.id.iv_font)
    public void iv_font(View view) {
        showSelectPop(iv_font);
    }

    @BindView(R.id.title_)
    TextView mTitle;
    @BindView(R.id.left_txt_)
    TextView mTvLeft;
    @BindView(R.id.right_txt_)
    TextView mTvRight;
    @OnClick(R.id.left_button_)
    public void left_button_(View view) {
        finish();
    }
    @OnClick(R.id.right_button_)
    public void right_button_(View view) {
        DictionaryActivity.safeStart(activity, key);
    }
    private List<ZiLibDetailBean> mGids;
    private int mPosition = 0;

    private void takePhoto() {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
            @Override
            public void onGranted() {
                final Uri sourceUri = Uri.fromFile(new File(getCacheDir(), "takeImage.png"));
                final Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.png"));
                UCrop.of(sourceUri, destinationUri)
                        .withTargetActivity(CameraCropActivity.class)
                        .withAspectRatio(1, 1)
                        .start(activity);
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相机权限！");
            }
        });
    }

    private void choosePhoto() {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "选择图片"), RC_CHOOSE_PHOTO);
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_CHOOSE_PHOTO == requestCode) {
            if (data != null) {
                final Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.png"));
                UCrop.of(data.getData(), destinationUri)
                        .withTargetActivity(PhotoCropActivity.class)
                        .withAspectRatio(1, 1)
                        .start(this);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        if (bitmap != null) {
                            requestBean.addParams("img", PicSelectUtil.encodeImage(bitmap, 800));
                            presenter.font_glyphs_upload(requestBean, true);
                        }
                    } catch (Exception e) {
                        ToastUtil.showToast(activity, "无法剪切选择图片！");
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //数据改变
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_ZilibEdit messageEvent) {
        onRefresh(null);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zi_lib_detail_list;
    }

    private String key;
    public void initView() {
        ZilibBean zilibBean = (ZilibBean) getIntent().getSerializableExtra("zilibBean");
        key = getIntent().getStringExtra("key");

        mPosition = getIntent().getIntExtra("pos", 0);
        mGids = (List<ZiLibDetailBean>) getIntent().getSerializableExtra("gids");
        handlePrevAndNext();

        requestBean.addParams("char", key);
        requestBean.addParams("fid", zilibBean.get_id());
        mTitle.setText(key);
        mRefresh_layout.setOnRefreshListener(this);
        mAdapter = new ZiLibDetailListAdapter(this);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (XClickUtil.isFastDoubleClick(mRv_zi)) {
                    return;
                }
                ZiBean bean = mAdapter.getData().get(position);
                final ZiLibDetailBean old = mGids.get(mPosition);
                mGids.set(mPosition, ZiLibDetailBean.newZiLibDetailBean(old, bean));

                final List<DetailsBean> list = new ArrayList<>();
                for (ZiLibDetailBean ziLibDetailBean : mGids) {
                    if (ziLibDetailBean.getGlyph() != null && !TextUtils.isEmpty(ziLibDetailBean.getGlyph().get_id())) {
                        list.add(DetailsBean.newDetails(ziLibDetailBean.getGlyph().get_id(), ziLibDetailBean.getGlyph().get_hanzi()));
                    }
                }
                GlyphDetailActivity.safeStart(activity, bean.get_id(), list);
            }
        });
        int bottom = 0;
        if (zilibBean.get_own() == ZilibBean.OWN_ME) {
            lay_bottom.setVisibility(View.VISIBLE);
            bottom = 88;
            mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    ZiBean bean = mAdapter.getData().get(position);
                    requestBean.addParams("gid", bean.get_id());
                    initMenu(view.findViewById(R.id.iv_thumb_url));
                    return true;
                }
            });
        } else {
            lay_bottom.setVisibility(View.GONE);
            bottom = 20;
        }
        mRv_zi.setAdapter(mAdapter);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity, mAdapter.calculate(activity)));
        mRv_zi.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(4), ViewUtil.dpToPx(8), ViewUtil.dpToPx(8), ViewUtil.dpToPx(8), ViewUtil.dpToPx(bottom), 0, SpaceItemDecoration.GRIDLAYOUT));
        mStatus_view.setOnRetryClickListener(mRetryClickListener);
        mRefresh_layout.setRunnable(mFetchRunnable);

        onRefresh(null);
    }

    private void handlePrevAndNext() {
        mTvLeft.setVisibility(View.VISIBLE);
        mTvRight.setVisibility(View.VISIBLE);
        if (mGids == null || mGids.size() <= 1) {
            mTvLeft.setClickable(false);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, R.color.silver));
            mTvRight.setClickable(false);
            mTvRight.setTextColor(ContextCompat.getColor(activity, R.color.silver));
        } else {
            mTvLeft.setClickable(true);
            mTvRight.setClickable(true);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGids.size() - 1 ? R.color.silver : R.color.colorPrimary));
        }
    }

    @OnClick(R.id.left_txt_)
    public void left_txt_(View view) {
        if (mGids != null && mPosition > 0) {
            mPosition--;
            key = mGids.get(mPosition).getGlyph().get_key();
            requestBean.addParams("char", key);
            mTitle.setText(key);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGids.size() - 1 ? R.color.silver : R.color.colorPrimary));
            onRefresh(mRefresh_layout);
        }
    }

    @OnClick(R.id.right_txt_)
    public void right_txt_(View view) {
        if (mGids != null && mPosition < mGids.size() - 1) {
            mPosition++;
            key = mGids.get(mPosition).getGlyph().get_key();
            requestBean.addParams("char", key);
            mTitle.setText(key);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGids.size() - 1 ? R.color.silver : R.color.colorPrimary));
            onRefresh(mRefresh_layout);
        }
    }

    /**
     * 点击"空空如也"重新发送请求
     */
    private View.OnClickListener mRetryClickListener = (View v) -> {
        onRefresh(null);
    };

    private QMUIPopup mNormalPopup;
    private void initMenu(View viewH) {
        if (mNormalPopup == null) {
            final View view = LayoutInflater.from(this).inflate(R.layout.ppw_zilib_option, null);
            mNormalPopup = QMUIPopups.popup(activity, ViewUtil.dpToPx(100))
                    .view(view)
                    .bgColor(getResources().getColor(R.color.whiteSmoke))
                    .borderColor(getResources().getColor(R.color.colorLine))
                    .borderWidth(ViewUtil.dpToPx(1))
                    .radius(0)
                    .offsetYIfBottom(ViewUtil.dpToPx(-10))
                    .offsetX(ViewUtil.dpToPx(-8))
                    .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                    .shadow(true)
                    .arrow(true);
            view.findViewById(R.id.tv_2).setOnClickListener(v -> {
                presenter.font_glyphs_prefer(requestBean, false);
                mNormalPopup.dismiss();
            });
            view.findViewById(R.id.tv_3).setOnClickListener(v -> {
                presenter.font_glyphs_delete(requestBean, false);
                mNormalPopup.dismiss();
            });
        }
        mNormalPopup.show(viewH);
    }

    private int loaded = 0;
    private RequestBean requestBean = new RequestBean();
    private final Runnable mFetchRunnable = () -> {
        loaded = mAdapter.getData().size();
        requestBean.addParams("loaded", loaded);
        presenter.font_glyphs_char(requestBean, false);
    };

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mRefresh_layout.finishRefresh(false);
        mAdapter.setNewData(null);

        loaded = 0;
        mRefresh_layout.setLoaded(0);
        requestBean.addParams("loaded", loaded);
        presenter.font_glyphs_char(requestBean, true);
    }

    @Override
    protected ZiLibDetailListPresenterImpl getPresenter() {
        return new ZiLibDetailListPresenterImpl();
    }

    @Override
    public void showProgress() {
        if (mAdapter.getItemCount() == 0) {
            mStatus_view.showLoading();
            if (null != mStatus_view.findViewById(R.id.ll_loading)) {
                mStatus_view.findViewById(R.id.ll_loading).setOnClickListener(mCancelClick);
            }
        }
    }

    /**
     * 允许用户取消api请求
     */
    private final View.OnClickListener mCancelClick = (View v) -> {
        cancel();
        mStatus_view.showEmpty();
        mRefresh_layout.finishRefresh();
    };

    @Override
    public void disimissProgress() {
        mRefresh_layout.finishRefresh();
        mRefresh_layout.finishLoadMore();
    }

    private int total = 0;
    @Override
    public void loadDataSuccess(RowBean<ZiBean> tData) {
        if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
            total = tData.getTotal();
            mRefresh_layout.setTotal(tData.getTotal());
            mStatus_view.showContent();
            mAdapter.setNewData(tData.getList());
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getData().size() >= total) {
                mRefresh_layout.setEnableLoadMore(false);
            } else {
                mRefresh_layout.setEnableLoadMore(true);
            }
        } else {
            if (loaded == 0) {//刷新
                mStatus_view.showEmpty();
                showRefreshByEmpty();
            }
        }
    }

    /**
     * 1.MultipleStatusView导致SmartRefreshLayout隐藏了无法刷新
     * 2.空空如也无法点击
     */
    private void showRefreshByEmpty() {
        mRefresh_layout.setVisibility(View.VISIBLE);
        mRefresh_layout.setEnableLoadMore(false);
    }

    @Override
    public void loadDataError(String errorMsg) {
        mStatus_view.showError();
        ToastUtil.showToast(activity, errorMsg + "");
    }

    @Override
    public void uploadSuccess(ZiBean ziBean) {
        onRefresh(null);
    }

    @Override
    public void uploadFail(String msg) {

    }

    @Override
    public void deleteSuccess(String msg) {
        onRefresh(null);
    }

    @Override
    public void deleteFail(String msg) {

    }

    @Override
    public void preferSuccess(String msg) {
        onRefresh(null);
    }

    @Override
    public void preferFail(String msg) {

    }

    @Override
    public void addSuccess(String msg) {
        onRefresh(null);
    }

    @Override
    public void addFail(String msg) {

    }

    /**
     * 弹出选字窗
     */
    private JiziSelectPopView jiziSelectPopView;
    private void showSelectPop(View view) {
        final ZiBean ziBean = new ZiBean();
        if (jiziSelectPopView == null) {
            jiziSelectPopView = new JiziSelectPopView(activity, new JiziSelectPopView.TipListener() {
                @Override
                public void ok(ZiBean ziBean, int index) {
                    List<String> chars = new ArrayList<>();
                    chars.add(key);
                    List<String> gids = new ArrayList<>();
                    gids.add(ziBean.get_id());
                    requestBean.addParams("chars", chars);
                    requestBean.addParams("gids", gids);
                    presenter.font_glyphs_add(requestBean, true);
                }
            });
            jiziSelectPopView.arrow(true).radius(0);
        }
        final Rect rect = new Rect();
        rect.left = ViewUtil.getScreenWidth(activity) - ViewUtil.dpToPx(96);
        rect.top = ViewUtil.getScreenHeight(activity);
        rect.right = (int) (rect.left + 0.1);
        rect.bottom = (int) (rect.top + 0.1);
        jiziSelectPopView.showPopupWindow2(iv_font, rect);
        ziBean.set_key(key);
        jiziSelectPopView.setData(ziBean, 0);
        /*jiziSelectPopView.onDismiss(() -> {
            jiziSelectPopView.dismiss();
            jiziSelectPopView = null;
        });*/
    }
}