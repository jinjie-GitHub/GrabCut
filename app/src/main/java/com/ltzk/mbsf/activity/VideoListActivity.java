package com.ltzk.mbsf.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.FileRequestBody;
import com.ltzk.mbsf.api.presenter.VideoListPresenter;
import com.ltzk.mbsf.api.view.VideoListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.SimilarBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.VideoListBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.FileUtil;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.OpenVideoDialog;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JinJie on 2021/4/13
 */
public class VideoListActivity extends MyBaseActivity<VideoListView, VideoListPresenter> implements VideoListView {
    private static final int RC_CHOOSE_PHOTO = 0x001;
    private static final int RC_VIDEO_CLIP = 0x002;

    @OnClick(R.id.left_button_)
    public void left_button_(View view) {
        finish();
    }

    @OnClick(R.id.right_button_)
    public void right_button_(View view) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "选择视频"), RC_CHOOSE_PHOTO);
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    @BindView(R.id.title_)
    TextView mTitle;

    @BindView(R.id.left_txt_)
    TextView mTvLeft;

    @BindView(R.id.right_txt_)
    TextView mTvRight;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private int mPosition = 0;
    private VideoListAdapter mAdapter;
    private List<DetailsBean> mGlyphs = new ArrayList<>();

    private String gid;
    private String zid;
    private String fid;
    private String uid;
    private String mPath = FileUtil.PATH;
    private String mOrderBy = Sort.DATE.orderBy();
    private DetailsBean mBean;
    private UserBean mUserBean;

    public static void safeStart(Context c, String gid, String zid, String fid, String uid, DetailsBean bean, List<DetailsBean> list) {
        Intent intent = new Intent(c, VideoListActivity.class);
        intent.putExtra("gid", gid);//字形id
        intent.putExtra("zid", zid);//字帖id
        intent.putExtra("fid", fid);//字库id
        intent.putExtra("uid", uid);//用户id
        intent.putExtra("glyph_detail", bean);

        Bundle bundle = new Bundle();
        bundle.putSerializable("glyphs", (Serializable) list);
        intent.putExtras(bundle);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        mUserBean = MainApplication.getInstance().getUser();
        return R.layout.activity_video_list;
    }

    @Override
    public void initView() {
        gid = getIntent().getStringExtra("gid");
        zid = getIntent().getStringExtra("zid");
        fid = getIntent().getStringExtra("fid");
        uid = getIntent().getStringExtra("uid");
        mBean = (DetailsBean) getIntent().getSerializableExtra("glyph_detail");
        List<DetailsBean> list = (List<DetailsBean>) getIntent().getSerializableExtra("glyphs");
        if (mBean == null || TextUtils.isEmpty(mBean._id)) {
            mTitle.setText((mBean == null || TextUtils.isEmpty(mBean._name)) ? mUserBean.get_nickname() : mBean._name);
            findViewById(R.id.right_button_).setVisibility(View.GONE);
        } else {
            mTitle.setText(mBean._hanzi);
            findViewById(R.id.right_button_).setVisibility(View.VISIBLE);
        }

        if (list != null && list.size() > 0) {
            glyphs(list);
        }

        mAdapter = new VideoListAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, 3));
        mRecyclerView.setAdapter(mAdapter);

        GridItemDecoration divider = new GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build();
        mRecyclerView.addItemDecoration(divider);

        /*final DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(400);
        defaultItemAnimator.setRemoveDuration(400);
        mRecyclerView.setItemAnimator(defaultItemAnimator);*/

        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setRunnable(() -> {
            presenter.glyph_video_query(gid, zid, fid, uid, "", mRefreshLayout.getLoaded());
        });
    }

    /*private void handleSelectTab() {
        int checkedId;
        if (mBean == null || TextUtils.isEmpty(mBean._id)) {
            checkedId = (int) SPUtils.get(getApplication(), "video_list_my", -1);
        } else {
            checkedId = (int) SPUtils.get(getApplication(), "video_list_glyph", -1);
        }
        if (checkedId == -1) {
            checkedId = R.id.rb_date;
        }
        if (checkedId > 0) {
            final View checkView = mRadioGroup.findViewById(checkedId);
            if (checkView instanceof RadioButton) {
                ((RadioButton) checkView).setChecked(true);
            } else {
                mRadioGroup.check(R.id.rb_date);
            }
        }
    }*/

    private final OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.setLoaded(0);
        mAdapter.setNewData(null);
        presenter.glyph_video_query(gid, zid, fid, uid, "", 0);
    };

    private RadioGroup.OnCheckedChangeListener mChangeListener = (RadioGroup group, int checkedId) -> {
        if (mBean == null || TextUtils.isEmpty(mBean._id)) {
            SPUtils.put(activity, "video_list_my", checkedId);
        } else {
            SPUtils.put(activity, "video_list_glyph", checkedId);
        }
        if (checkedId == R.id.rb_date) {
            mOrderBy = Sort.DATE.orderBy();
        } else if (checkedId == R.id.rb_hot) {
            mOrderBy = Sort.HOT.orderBy();
        } else if (checkedId == R.id.rb_author) {
            mOrderBy = Sort.AUTHOR.orderBy();
        }
        mRefreshListener.onRefresh(mRefreshLayout);
    };

    @Override
    public void showProgress() {
        if (mDialog == null) {
            showProgressDialog("");
        }
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        hideDialog();
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    private Dialog mDialog;
    private CircularProgressBar circularProgressBar;
    private void showDialog() {
        final View adView = LayoutInflater.from(activity).inflate(R.layout.dialog_upload_video, null);
        circularProgressBar = adView.findViewById(R.id.circularProgressBar);
        mDialog = new Dialog(activity, R.style.AlertDialog);
        mDialog.setCancelable(false);
        final Window window = mDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0f);
        mDialog.setContentView(adView);
        mDialog.show();
    }

    private void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void loadDataSuccess(VideoListBean data) {
        if (data == null) {
            return;
        }
        mRefreshLayout.setTotal(data.total);
        if (mRefreshLayout.getLoaded() == 0) {
            mAdapter.setNewData(data.list);
            mRefreshLayout.finishRefresh(true);
            mAdapter.showEmptyView(data.list);
        } else {
            mRefreshLayout.finishLoadMore(true);
            if (mRefreshLayout.getLoaded() < data.total) {
                mAdapter.addData(data.list);
            }
        }
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < data.total);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    @Override
    protected VideoListPresenter getPresenter() {
        return new VideoListPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_CHOOSE_PHOTO == requestCode) {
            if (data != null) {
                final Uri selectedVideo = data.getData();
                final Cursor cursor = getContentResolver().query(selectedVideo, null, null, null, null);
                try {
                    if (cursor != null) {
                        cursor.moveToFirst();
                        final long size = (cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))) / 1024 / 1024;
                        final String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                        cursor.close();
                        if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
                            if (size < 100) {
                                final InputStream inStream = getContentResolver().openInputStream(selectedVideo);
                                final int len = inStream.available();
                                FileUtil.copyFile(inStream, name);
                                mPath = String.format("%s%s", FileUtil.PATH, name);
                                if (len / 1024 / 1024 < 100) {
                                    //VideoClipActivity.safeStart(activity, mPath);
                                } else {
                                    ToastUtil.showToast(activity, "视频文件过大!");
                                }
                            } else {
                                ToastUtil.showToast(activity, "视频文件过大!");
                            }
                        } else {
                            ToastUtil.showToast(activity, "请选择视频文件!");
                        }
                    }
                } catch (Throwable e) {
                    Logger.d("pick video:" + e.toString());
                }
            }
        } else if (RC_VIDEO_CLIP == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                showDialog();
                mPath = data.getStringExtra("path");
                presenter.glyph_video_upload(gid, getDisplayName(mPath), mPath, mUploadCallback);
            }
        }
    }

    private FileRequestBody.Callback mUploadCallback = (long total, long progress) -> {
        mRefreshLayout.post(() -> {
            if (total <= progress) {
                circularProgressBar.setVisibility(View.GONE);
            } else {
                circularProgressBar.setVisibility(View.VISIBLE);
                circularProgressBar.setProgressMax(total);
                circularProgressBar.setProgress(progress);
            }
        });
    };

    static String getDisplayName(final String name) {
        String str = name;
        if (!TextUtils.isEmpty(str) && str.contains(".")) {
            String[] arr = str.split("\\.");
            return arr[1];
        }
        return "mp4";
    }

    /*static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }*/

    @OnClick(R.id.left_txt_)
    public void left_txt_(View view) {
        if (mGlyphs != null && mPosition > 0) {
            mPosition--;
            gid = mGlyphs.get(mPosition)._id;
            mTitle.setText(mGlyphs.get(mPosition)._hanzi);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGlyphs.size() - 1 ? R.color.silver : R.color.colorPrimary));
            mRefreshListener.onRefresh(mRefreshLayout);
        }
    }

    @OnClick(R.id.right_txt_)
    public void right_txt_(View view) {
        if (mGlyphs != null && mPosition < mGlyphs.size() - 1) {
            mPosition++;
            gid = mGlyphs.get(mPosition)._id;
            mTitle.setText(mGlyphs.get(mPosition)._hanzi);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == mGlyphs.size() - 1 ? R.color.silver : R.color.colorPrimary));
            mRefreshListener.onRefresh(mRefreshLayout);
        }
    }

    @Override
    public void glyphs(List<DetailsBean> data) {
        if (data == null) {
            return;
        }

        mGlyphs = data;
        mTvLeft.setVisibility(data == null || data.size() <= 1 ? View.GONE : View.VISIBLE);
        mTvRight.setVisibility(data == null || data.size() <= 1 ? View.GONE : View.VISIBLE);
        if (data == null || data.size() <= 1) {
            mTvLeft.setClickable(false);
            mTvLeft.setTextColor(ContextCompat.getColor(activity, R.color.silver));
            mTvRight.setClickable(false);
            mTvRight.setTextColor(ContextCompat.getColor(activity, R.color.silver));
        } else {
            mTvLeft.setClickable(true);
            mTvRight.setClickable(true);
            for (int pos = 0; pos < data.size(); pos++) {
                if (gid.equals(data.get(pos)._id)) {
                    mPosition = pos;
                    break;
                }
            }
            mTvLeft.setTextColor(ContextCompat.getColor(activity, mPosition == 0 ? R.color.silver : R.color.colorPrimary));
            mTvRight.setTextColor(ContextCompat.getColor(activity, mPosition == data.size() - 1 ? R.color.silver : R.color.colorPrimary));
        }
    }

    @Override
    public void upload(VideoListBean.Videos data) {
        if (data == null) {
            return;
        }

        try {
            mAdapter.getData().add(0, data);
            mAdapter.notifyItemInserted(0);
            mAdapter.notifyItemRangeChanged(0, mAdapter.getData().size());
            FileUtil.deleteAll(new File(FileUtil.PATH));
        } catch (Exception e) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class VideoListAdapter extends BaseQuickAdapter<VideoListBean.Videos, BaseViewHolder> {
        public VideoListAdapter() {
            super(R.layout.item_video_list);
        }

        private void showEmptyView(List<?> data) {
            if (data != null && data.size() != 0) {
                return;
            }
            View emptyView = getLayoutInflater().inflate(R.layout.empty_data, null);
            mAdapter.setEmptyView(emptyView);
            mAdapter.notifyDataSetChanged();
            emptyView.findViewById(R.id.empty_retry_view).setOnClickListener(view -> {
                mRefreshListener.onRefresh(mRefreshLayout);
            });
        }

        @Override
        protected void convert(BaseViewHolder holder, VideoListBean.Videos bean) {
            final ImageView imageView = holder.getView(R.id.imageView);
            Glide.with(getContext()).load(bean._cover).into(imageView);
            imageView.post(() -> {
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.height = imageView.getMeasuredWidth();
                imageView.setLayoutParams(lp);
            });

            final ImageView avatar = holder.getView(R.id.avatar);
            String logo = (bean._glyph != null) ? bean._glyph._color_image : mBean._color_image;
            Glide.with(getContext()).load(logo).into(avatar);

            final TextView nickname = holder.getView(R.id.nickname);
            nickname.setText((bean._glyph != null) ? bean._glyph._hanzi : mBean._hanzi);

            final ImageView edit = holder.getView(R.id.edit);
            final boolean isGone = (bean._locked == 1 || !bean._author._id.equals(mUserBean.get_id()));
            holder.setGone(R.id.edit, isGone);

            final TextView vip = holder.getView(R.id.vip);
            holder.setVisible(R.id.vip, bean._free == 0 || bean._disabled == 1);
            if (bean._disabled == 1) {
                vip.setText("封禁");
                vip.setTextColor(ContextCompat.getColor(activity, R.color.orange));
                //vip.setBackgroundTintList(null);
            } else {
                vip.setText("VIP");
                vip.setTextColor(ContextCompat.getColor(activity, R.color.orange));
                //vip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.white)));
            }

            final TextView state = holder.getView(R.id.state);
            renderState(state, bean);
            final View.OnClickListener clickListener = (v) -> {
                switch (v.getId()) {
                    case R.id.imageView:
                        if (MainApplication.getInstance().isCanGo(activity, v, String.valueOf(bean._free), new RequestBean().getParams())) {
                            OpenVideoDialog.openVideo(bean._id, "").show(getSupportFragmentManager(), null);
                        }
                        break;
                    case R.id.avatar:
                    case R.id.nickname:
                        GlyphDetailActivity.safeStart(activity, (bean._glyph != null) ? bean._glyph._id : mBean._id, new ArrayList<>());
                        break;
                    case R.id.edit:
                        processEdit(v, holder.getLayoutPosition(), bean);
                        break;
                    default:
                        break;
                }
            };

            edit.setOnClickListener(clickListener);
            avatar.setOnClickListener(clickListener);
            nickname.setOnClickListener(clickListener);
            imageView.setOnClickListener(clickListener);
        }

        private void processEdit(final View v, final int pos, final VideoListBean.Videos bean) {
            final View view = LayoutInflater.from(activity).inflate(R.layout.ppw_edit_action, null);
            final QMUIPopup popup = QMUIPopups.popup(activity, ViewUtil.dpToPx(100))
                    .view(view)
                    .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                    .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                    .borderWidth(ViewUtil.dpToPx(1))
                    .radius(ViewUtil.dpToPx(3))
                    .offsetYIfBottom(0)
                    .offsetX(0)
                    .preferredDirection(QMUIPopup.DIRECTION_TOP)
                    .shadow(true)
                    .shadowElevation(10, 1f)
                    .arrow(true)
                    .arrowSize(40, 22)
                    .show(v);

            final TextView tv_1 = view.findViewById(R.id.tv_1);
            final TextView tv_2 = view.findViewById(R.id.tv_2);
            switch (bean._review_stat) {
                case -2:
                    tv_1.setText("发布");
                    break;
                case -1:
                    tv_1.setText("撤回");
                    break;
                case 0:
                    tv_1.setVisibility(View.GONE);
                    break;
                case 1:
                    if (bean._published == 0) {
                        tv_1.setText("公开");
                    } else {
                        tv_1.setText("私密");
                    }
                    break;
            }

            tv_2.setOnClickListener((v2) -> {//删除
                popup.dismiss();
                TipPopView tipPopView = new TipPopView(activity, "", "确定要删除该视频？", "删除", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        if (pos != -1) {
                            presenter.glyph_video_delete(bean._id);
                            try {
                                getData().remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, getData().size() - pos);
                            } catch (Exception e) {
                                mAdapter.notifyDataSetChanged();
                            }

                            if (mAdapter.getData().size() == 0) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
                tipPopView.showPopupWindow(v2);
            });

            tv_1.setOnClickListener((v1) -> {
                popup.dismiss();
                final String msg = tv_1.getText().toString();
                if ("发布".equals(msg)) {
                    bean._review_stat = -1;
                    presenter.glyph_video_publish(bean._id);
                } else if ("撤回".equals(msg)) {
                    bean._review_stat = -2;
                    presenter.glyph_video_unpublish(bean._id);
                } else if ("公开".equals(msg)) {
                    bean._published = 1;
                    presenter.glyph_video_public(bean._id);
                } else if ("私密".equals(msg)) {
                    bean._published = 0;
                    presenter.glyph_video_private(bean._id);
                }
                notifyItemChanged(pos);
            });
        }

        private void renderState(final TextView state, final VideoListBean.Videos bean) {
            int roundRadius = ViewUtil.dpToPx(4);
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(roundRadius);
            int fillColor = ContextCompat.getColor(activity, R.color.colorPrimary);
            String name = "待发布";
            switch (bean._review_stat) {
                case -2:
                    name = "待发布";
                    fillColor = ContextCompat.getColor(activity, R.color.gray);
                    break;
                case -1:
                    name = "待审核";
                    fillColor = ContextCompat.getColor(activity, R.color.limeGreen);
                    break;
                case 0:
                    name = "被拒绝";
                    fillColor = ContextCompat.getColor(activity, R.color.darkRed);
                    break;
                case 1:
                    name = "";
                    fillColor = ContextCompat.getColor(activity, R.color.transparent);
                    if (bean._published == 0) {
                        name = "私密";
                        fillColor = ContextCompat.getColor(activity, R.color.gray);
                    }
                    break;
                default:
                    break;
            }
            state.setText(name);
            gd.setColor(fillColor);
            state.setBackground(gd);
        }
    }

    private enum Status {
        DFB("待发布", -2), DSH("待审核", -1), BJJ("被拒绝", 0), SUC("审核成功", 1);

        public String name;
        public int review_stat;

        Status(String name, int review_stat) {
            this.name = name;
            this.review_stat = review_stat;
        }

        public static String getName(int _review_stat) {
            for (Status c : Status.values()) {
                if (c.review_stat == _review_stat) {
                    return c.name;
                }
            }
            return "";
        }
    }

    private enum Sort {
        HOT {
            public String orderBy() {
                return "hot";
            }
        },

        DATE {
            public String orderBy() {
                return "date";
            }
        },

        AUTHOR {
            public String orderBy() {
                return "author";
            }
        };

        public abstract String orderBy();
    }
}