package com.ltzk.mbsf.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.MyPagerAdapter;
import com.ltzk.mbsf.adapter.ZitieDetailItemAdapter;
import com.ltzk.mbsf.api.presenter.ZitieDetailPresenterImpl;
import com.ltzk.mbsf.api.view.ZitieDetailView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.SoucangCannelBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZitieDetailitemBean;
import com.ltzk.mbsf.bean.ZitieShiwenBean;
import com.ltzk.mbsf.fragment.ZitieDetailFragment;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.popupview.ZiTieSettingPopView;
import com.ltzk.mbsf.utils.BitmapHelper;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.CenterLayoutManager;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.ltzk.mbsf.api.RetrofitManager.RES_URL;


/**
 * update on 2021/05/08
 */

public class ZitieActivity extends MyBaseActivity<ZitieDetailView,ZitieDetailPresenterImpl> implements ZitieDetailView{
    ZitieDetailFragment mCurZitieDetailFragment;
    @BindView(R2.id.render)
    ImageView render;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    MyPagerAdapter adapter_fragment;

    @BindView(R2.id.tv_vip)
    TextView tv_vip;
    private TipPopView tipPopView;
    @OnClick(R2.id.tv_vip)
    public void tv_vip(View view) {
        if (MainApplication.getInstance().getToken() == null || MainApplication.getInstance().getToken().equals("")) {
            if (tipPopView == null) {
                tipPopView = new TipPopView(activity, "请您先登录", "仅VIP会员可查看精品字帖。", "登录", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        startActivity(new Intent(activity, LoginTypeActivity.class));
                    }
                });
            }
            tipPopView.showPopupWindow(tv_vip);
        } else {
            RequestBean requestBean = new RequestBean();
            startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", RES_URL + "iap/index.php?p=" + requestBean.getParams()).putExtra("title", "VIP会员续费"));
        }
    }

    //定位
    @BindView(R2.id.iv_1)
    TextView iv_1;
    @OnClick(R2.id.iv_1)
    public void iv_1(View v) {
        if (list == null || list.isEmpty()) {
            return;
        }

        if (viewpager.getCurrentItem() == index_init) {
            mCurZitieDetailFragment.dingweiShow(false);
        } else {
            viewpager.setCurrentItem(index_init);
            mCurZitieDetailFragment.dingweiShow(true);
        }

        resetLocation();
    }

    //注解
    @BindView(R2.id.cb_2)
    CheckBox cb_2;
    //释文
    @BindView(R2.id.cb_3)
    CheckBox cb_3;
    //下载
    @BindView(R2.id.iv_4)
    TextView iv_4;
    @OnClick(R2.id.iv_4)
    public void iv_4(View view) {
        if (MainApplication.getInstance().isCanGo(activity, topBar, zitieBean.get_free(), new RequestBean().getParams())) {
            showDownload(view);
        }
    }

    //收藏
    @BindView(R2.id.cb_5)
    CheckBox cb_5;
    @OnClick(R2.id.cb_5)
    public void cb_5(View view) {
        if (cb_5.isChecked()) {
            cb_5.setChecked(false);
            presenter.zitie_page_fav(requestBean, true);
        } else {
            cb_5.setChecked(true);
            List<SoucangCannelBean> list = new ArrayList<>();
            SoucangCannelBean bean = new SoucangCannelBean();
            bean.setPage(mPage);
            bean.setZid(zid);
            list.add(bean);
            requestBean.addParams("pages", list);
            presenter.zitie_page_unfav(requestBean, true);
        }
    }

    @BindView(R2.id.lay_bottom)
    View lay_bottom;
    @BindView(R.id.scrollView)
    View scrollView;
    @BindView(R2.id.tv_shiwen)
    TextView tv_shiwen;
    @BindView(R2.id.recycler_view)
    RecyclerView recycler_view;
    //下部缩率图适配器
    private ZitieDetailItemAdapter adapter;
    private CenterLayoutManager centerLayoutManager;

    private String zid;
    //字典过来，字的位置信息
    private String frame;
    //初始化过来选中字帖在列表中的下标
    private int index_init = 0;
    //当前选中字帖的序号，与列表下标是相反的
    private int mPage = 0;
    private RequestBean requestBean;
    public boolean isInit = true;
    public boolean isAll = false;

    public boolean hasAnim = true;

    @BindView(R.id.iv_setting)
    View iv_setting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zitie;
    }

    @Override
    public void initView() {
        hasAnim = getIntent().getBooleanExtra("hasAnim", true);

        //字的id
        zid = getIntent().getStringExtra("zid");
        //字的定位区域
        frame = getIntent().getStringExtra("frame");
        //字帖中的页码
        mPage = getIntent().getIntExtra("index", -1);

        ObjectAnimator.ofFloat(viewpager, "translationY", ViewUtil.dpToPx(-60)).setDuration(1).start();

        setBoldText(cb_2, cb_3, cb_5);
        topBar.setLeftButtonListener(hasAnim ? R.mipmap.close2 : R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasAnim) {
                    finishFromTop();
                } else {
                    finish();
                }
            }
        });
        topBar.setRightButtonListener(R.mipmap.ic_zuopin_list, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZuoPin(v);
            }
        });
        topBar.setTitleListener((v) -> {
            if (zitieBean != null) {
                final String name = zitieBean.get_name();
                String title = name.contains("-") ? name.split("-")[0] : name;
                startActivity(new Intent(activity, ZitieListActivity.class).putExtra("zid", zitieBean._zuopin_id).putExtra("title", title));
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurZitieDetailFragment = (ZitieDetailFragment) adapter_fragment.instantiateItem(viewpager, position);
                mPage = list.size() - position;
                //显示新一页数据
                resetView();
                recycler_view.scrollToPosition(position);
                //更新选择项到中间位置
                if (centerLayoutManager == null) {
                    mHandler.sendEmptyMessageDelayed(position, 0);
                } else {
                    centerLayoutManager.smoothScrollToPosition(recycler_view, new RecyclerView.State(), (position));
                }
                //设置下方红框下标
                adapter.setSelect(position);
                if (position == index_init) {
                    mCurZitieDetailFragment.setFrame(frame);
                }
                vipExpire();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (frame != null) {//定位区域不为空，字典过来的
            iv_1.setEnabled(true);
            iv_1.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorPrimary)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iv_1.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_zitie_postion_focus, 0, 0);
                iv_1.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorPrimary)));
            }
        } else {//无定位区域，字帖过来的
            iv_1.setEnabled(false);
            iv_1.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iv_1.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_zitie_postion_unfocus, 0, 0);
                iv_1.setCompoundDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.silver)));
            }
        }

        //底部缩率图控件初始化
        adapter = new ZitieDetailItemAdapter(activity);
        //切换下面字帖的页码
        adapter.setOntiemmClickListener(new ZitieDetailItemAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                viewpager.setCurrentItem(position);
            }
        });

        recycler_view.setAdapter(adapter);
        if (centerLayoutManager == null) {
            centerLayoutManager = new CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recycler_view.setLayoutManager(centerLayoutManager);
        }

        //注解点击
        cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean isLogin = MainApplication.getInstance().isLogin();
                if (mCurZitieDetailFragment == null || !isLogin) {
                    return;
                }
                iv_setting.setVisibility(isChecked?View.VISIBLE:View.GONE);
                mCurZitieDetailFragment.showZhujie(isChecked);
            }
        });

        //释文显示
        cb_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    scrollView.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.GONE);
                } else {
                    scrollView.setVisibility(View.GONE);
                    recycler_view.setVisibility(View.VISIBLE);
                }
            }
        });

        //开始获取数据
        requestBean = new RequestBean();
        requestBean.addParams("zid", zid);
        presenter.getDetail(requestBean, true);
    }

    /**
     * 切换是否显示底部和顶部
     */
    public void change() {
        mCurZitieDetailFragment.showBigOrTop(!isAll, viewpager);
        //是否全屏显示
        if (isAll) {//是
            isAll = false;
            final Animation topIn = AnimationUtils.loadAnimation(this, R.anim.anim_top_view_in);
            final Animation bottomIn = AnimationUtils.loadAnimation(this, R.anim.anim_bottom_view_in);
            topBar.startAnimation(topIn);
            lay_bottom.startAnimation(bottomIn);

            viewpager.setPadding(0, ViewUtil.dpToPx(100), 0, ViewUtil.dpToPx(100));
            mHandler.postDelayed(() -> {
                topBar.setVisibility(View.VISIBLE);
                lay_bottom.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.VISIBLE);
                if (cb_2.isChecked()) iv_setting.setVisibility(View.VISIBLE);
            }, 0);
        } else {//否
            isAll = true;
            final Animation topOut = AnimationUtils.loadAnimation(this, R.anim.anim_top_view_out);
            final Animation bottomOut = AnimationUtils.loadAnimation(this, R.anim.anim_bottom_view_out);
            topBar.startAnimation(topOut);
            lay_bottom.startAnimation(bottomOut);

            viewpager.setPadding(0, 0, 0, 0);
            mHandler.postDelayed(() -> {
                topBar.setVisibility(View.GONE);
                lay_bottom.setVisibility(View.GONE);
                recycler_view.setVisibility(View.GONE);
                iv_setting.setVisibility(View.GONE);
            }, 400);
        }
    }

    /**
     * 显示标题
     */
    private void resetView() {
        //显示标题
        topBar.setTitleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        topBar.setTitle(zitieBean.get_name());
        topBar.setSmallTitle("作者: " + zitieBean.get_author());

        //获取释文
        requestBean.addParams("page", mPage);
        presenter.zitie_page_text(requestBean, false);

        //标注复位
        if (list_annotations != null && list_annotations.size() > mPage && list_annotations.get(mPage)) {
            cb_2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            cb_2.setEnabled(true);
        } else {
            cb_2.setTextColor(ContextCompat.getColor(this, R.color.silver));
            cb_2.setEnabled(false);
        }

        //释文复位
        cb_3.setChecked(false);
        cb_2.setChecked(false);

        //设置是否被收藏
        if (list_favs.contains(mPage)) {
            cb_5.setChecked(true);
        } else {
            cb_5.setChecked(false);
        }
    }

    private final void setBoldText(CheckBox... cbs) {
        for (CheckBox cb : cbs) {
            if (cb.getPaint() != null) {
                cb.getPaint().setFakeBoldText(true);
            }
        }
    }

    /**
     * 验证是否是vip会员
     */
    public boolean vipExpire() {
        if (mCurZitieDetailFragment == null) {
            return false;
        }

        if (zitieBean.get_free() == null || zitieBean.get_free().equals("1")) {//非精品
            mCurZitieDetailFragment.setLay_vip(View.GONE);
            tv_vip.setVisibility(View.GONE);
            return true;
        }

        UserBean userBean = MainApplication.getInstance().getUser();
        if (userBean == null) {//未登录用户看精品
            mCurZitieDetailFragment.setLay_vip(View.VISIBLE);
            tv_vip.setVisibility(View.VISIBLE);
            return false;
        }

        //会员有效期
        long now = Calendar.getInstance().getTimeInMillis();
        long expire = userBean.get_expire() * 1000;
        if (expire < now) {//非vip用户看精品
            mCurZitieDetailFragment.setLay_vip(View.VISIBLE);
            tv_vip.setVisibility(View.VISIBLE);
            return false;
        } else {//vip用户看精品
            mCurZitieDetailFragment.setLay_vip(View.GONE);
            tv_vip.setVisibility(View.GONE);
            return true;
        }
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        vipExpire();
    }

    //用户信息变更
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_UserUpdate bus_userUpdate) {
        vipExpire();
    }

    @Override
    protected ZitieDetailPresenterImpl getPresenter() {
        return new ZitieDetailPresenterImpl();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    //底部列表
    private List<ZitieDetailitemBean> list = new ArrayList<>();
    //注解列表
    private List<Boolean> list_annotations = new ArrayList<>();
    private ZitieBean zitieBean;

    @Override
    public void loadDataSuccess(ZitieBean tData) {//获取到字帖数据
        if (tData == null) {
            return;
        }

        zitieBean = tData;

        list_annotations = tData.get_annotations();

        if (zitieBean.get_favs() != null) {
            list_favs = tData.get_favs();
        }

        resetView();
        list.clear();
        //单页详细数据bean
        ZitieDetailitemBean zitieDetailitemBean;
        //缩略图
        List<String> thumbs = tData.get_thumbs();
        //大图
        List<String> images = tData.get_images();
        //页码大图尺寸
        List<String> sizes = tData.get_sizes();
        for (int i = 0; i < thumbs.size(); i++) {
            zitieDetailitemBean = new ZitieDetailitemBean();
            zitieDetailitemBean.set_thumb(thumbs.get(thumbs.size() - i - 1));
            zitieDetailitemBean.set_image(images.get(thumbs.size() - i - 1));
            if (sizes.get(thumbs.size() - i - 1) != null) {
                String[] size = sizes.get(thumbs.size() - i - 1).split(",");
                zitieDetailitemBean.setHeight(Integer.valueOf(size[1]));
                zitieDetailitemBean.setWidth(Integer.valueOf(size[0]));
            }
            zitieDetailitemBean.setZid(zid);
            zitieDetailitemBean.setPage(thumbs.size() - i);

            //加入新列表，并倒序，保证从右边开始读文
            list.add(zitieDetailitemBean);
        }

        //记住初始的页码下标
        index_init = list.size() - mPage;
        Logger.d("[index_init]:" + index_init);

        //初始大图适配器，并赋值
        adapter_fragment = new MyPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, list);
        viewpager.setAdapter(adapter_fragment);
        adapter_fragment.notifyDataSetChanged();

        //对缩略图赋值
        adapter.setData(list);
        adapter.setFavs(list_favs);
        recycler_view.setHasFixedSize(true);
        //recycler_view.setItemViewCacheSize(list.size());
        adapter.notifyDataSetChanged();
        viewpager.setCurrentItem(index_init);
        if (index_init == 0) {
            mCurZitieDetailFragment = (ZitieDetailFragment) adapter_fragment.instantiateItem(viewpager, 0);
            vipExpire();
            mCurZitieDetailFragment.setFrame(frame);
            mCurZitieDetailFragment.dingweiShow(true);
        }

        showZuoPinIcon();
    }

    /**
     * 滚动到选中的字帖
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 注释文
     */
    @Override
    public void getShiwenResult(ZitieShiwenBean zitieShiwenBean) {
        if (zitieShiwenBean != null && zitieShiwenBean.get_page() == mPage) {
            tv_shiwen.setText(zitieShiwenBean.get_text() + "");
        }
    }

    //收藏id列表
    private List<Integer> list_favs = new ArrayList<>();

    @Override
    public void favResult(String bean) {//收藏成功
        cb_5.setChecked(true);
        list_favs.add(mPage);
        adapter.setChecked(mPage, true);
    }

    @Override
    public void unfavResult(String bean) {//取消收藏成功
        cb_5.setChecked(false);
        list_favs.remove(Integer.valueOf(mPage));
        adapter.setChecked(mPage, false);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        if (hasAnim) {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }

    @OnClick(R.id.iv_setting)
    public void iv_setting(View view) {
        if(!isAll){
            final ZiTieSettingPopView brushPop = new ZiTieSettingPopView(activity, mCurZitieDetailFragment.getIv_ziTie());
            brushPop.showAt(view);
        }
    }

    public final int getPosition() {
        return viewpager != null ? viewpager.getCurrentItem() : 0;
    }

    public final void resetLocation() {
        if (iv_1 != null && mCurZitieDetailFragment != null) {
            iv_1.setCompoundDrawablesWithIntrinsicBounds(0, mCurZitieDetailFragment.isShowZhegai() ?
                    R.mipmap.ic_zitie_postion_focus : R.mipmap.ic_zitie_postion_unfocus, 0, 0);
        }
    }

    @OnClick(R2.id.iv_8)
    public void iv_8(View view) {
        if (zitieBean != null) {
            GlyphsListActivity.safeStart(this, zitieBean.get_zitie_id());
        }
    }

    @OnClick(R2.id.iv_9)
    public void iv_9(View view) {
        if (mCurZitieDetailFragment != null) {
            if (mCurZitieDetailFragment.getmZitieDetailitemBean() != null) {
                if (MainApplication.getInstance().isCanGo(activity, topBar, zitieBean.get_free(), new RequestBean().getParams())) {
                    String url_image = mCurZitieDetailFragment.getmZitieDetailitemBean().get_image();
                    WritingActivity.safeStart(this, url_image);
                }
            }
        }
    }

    private void savePicToSdcard(int pos) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                final String url_image = mCurZitieDetailFragment.getmZitieDetailitemBean().get_image();
                if (pos == 0) {
                    Glide.with(getApplication())
                            .asBitmap()
                            .load(url_image)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(new ImageViewTarget<Bitmap>(render) {
                                @Override
                                protected void setResource(@Nullable Bitmap bmp) {
                                    if (bmp != null) {
                                        ToastUtil.showToast(activity, "字帖图片已保存到您的相册。");
                                        BitmapUtils.savePicToSdcard(getApplication(), bmp, String.valueOf(System.currentTimeMillis()));
                                    }
                                }
                            });
                } else if (pos == 1) {
                    Glide.with(getApplication())
                            .asBitmap()
                            .load(url_image)
                            //.override(800, 800)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(new ImageViewTarget<Bitmap>(render) {
                                @Override
                                protected void setResource(@Nullable Bitmap resource) {
                                    if (resource != null) {
                                        ToastUtil.showToast(activity, "字帖图片已保存到您的相册。");
                                        final Bitmap bmp = BitmapHelper.INSTANCE.bitmap2Gray2(resource);
                                        BitmapUtils.savePicToSdcard(getApplication(), bmp, String.valueOf(System.currentTimeMillis()));
                                    }
                                }
                            });
                } else {
                    Glide.with(getApplication())
                            .asBitmap()
                            .load(url_image)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(new ImageViewTarget<Bitmap>(render) {
                                @Override
                                protected void setResource(@Nullable Bitmap resource) {
                                    if (resource != null) {
                                        ToastUtil.showToast(activity, "字帖图片已保存到您的相册。");
                                        final Bitmap bmp = BitmapHelper.INSTANCE.invertBitmap(resource);
                                        BitmapUtils.savePicToSdcard(getApplication(), bmp, String.valueOf(System.currentTimeMillis()));
                                    }
                                }
                            });
                }
            }
            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "权限被拒绝！");
            }
        });
    }

    private void showDownload(View anchor) {
        final View view = LayoutInflater.from(this).inflate(R.layout.ppw_contrast, null);
        final QMUIPopup popup = QMUIPopups.popup(this, ViewUtil.dpToPx(140))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(20)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22)
                .show(anchor);
        final TextView first = view.findViewById(R.id.tv_1);
        final TextView second = view.findViewById(R.id.tv_2);
        final TextView third = view.findViewById(R.id.tv_3);
        first.setEnabled(true);
        first.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        first.setText("保存原图");
        second.setText("保存灰度图");
        third.setText("保存反色图");
        view.findViewById(R.id.tv_1).setOnClickListener(v -> {
            popup.dismiss();
            savePicToSdcard(0);
        });
        view.findViewById(R.id.tv_2).setOnClickListener(v -> {
            popup.dismiss();
            savePicToSdcard(1);
        });
        view.findViewById(R.id.tv_3).setOnClickListener(v -> {
            popup.dismiss();
            savePicToSdcard(2);
        });
    }

    private QMUIPopup mZuoPinPopups;
    private void showZuoPin(View anchor) {
        if (zitieBean == null || zitieBean._zuopin_list == null ||
                zitieBean._zuopin_list.size() == 0) {
            return;
        }

        if (mZuoPinPopups != null) {
            mZuoPinPopups.show(anchor);
            return;
        }

        final View view = View.inflate(getApplication(), R.layout.popups_zuopin, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        recyclerView.setAdapter(new ZuoPinAdapter(zitieBean._zuopin_list));
        mZuoPinPopups = QMUIPopups.popup(getApplication(), ViewUtil.dpToPx(260), ViewUtil.dpToPx(300))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_RIGHT)
                .offsetYIfBottom(ViewUtil.dpToPx(-20))
                .offsetX(ViewUtil.dpToPx(-110))
                .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                .shadow(true)
                .arrow(true)
                .arrowSize(50, 30)
                .show(anchor);
    }

    private void showZuoPinIcon() {
        if (zitieBean == null || zitieBean._zuopin_list == null ||
                zitieBean._zuopin_list.size() == 0) {
            topBar.setRightButtonTint(R.color.silver);
        } else {
            topBar.setRightButtonTint(R.color.colorPrimary);
        }
    }

    private class ZuoPinAdapter extends BaseQuickAdapter<ZitieBean.ZuoPin, BaseViewHolder> {
        public ZuoPinAdapter(List<ZitieBean.ZuoPin> data) {
            super(R.layout.item_popups_zuopin, data);
            setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
                mZuoPinPopups.dismiss();
                ZitieBean.ZuoPin zuoPin = (ZitieBean.ZuoPin) adapter.getItem(position);
                viewpager.setCurrentItem(list.size() - zuoPin._focus_page);
            });
        }

        @Override
        protected void convert(BaseViewHolder holder, ZitieBean.ZuoPin bean) {
            holder.setText(R.id.name, bean._name);
            holder.setText(R.id.desc, " · " + bean._author);
            holder.getView(R.id.detail).setOnClickListener((v) -> {
                if (bean != null) {
                    mZuoPinPopups.dismiss();
                    final String name = bean._name;
                    String title = name.contains("-") ? name.split("-")[0] : name;
                    startActivity(new Intent(activity, ZitieListActivity.class).putExtra("zid", bean._zuopin_id).putExtra("title", title));
                }
            });
        }
    }
}