package com.ltzk.mbsf.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.AuthorFavActivity;
import com.ltzk.mbsf.activity.CacheManangeActivity;
import com.ltzk.mbsf.activity.LoginTypeActivity;
import com.ltzk.mbsf.activity.MyActivity;
import com.ltzk.mbsf.activity.MyVideosActivity;
import com.ltzk.mbsf.activity.MyWebActivity;
import com.ltzk.mbsf.activity.VideoFavActivity;
import com.ltzk.mbsf.activity.XiaoeSdkActivity;
import com.ltzk.mbsf.activity.ZiLibMyActivity;
import com.ltzk.mbsf.activity.ZitieSoucangListActivity;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.PersionPresenterImpl;
import com.ltzk.mbsf.api.view.PersionView;
import com.ltzk.mbsf.base.BaseBean;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.popupview.SharePopView;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.GlideUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：
 * 邮箱：499629556@qq.com
 */

public class PersionFragment extends MyBaseFragment<PersionView, PersionPresenterImpl> implements PersionView {
    @BindView(R2.id.swipeToLoadLayout)
    SmartRefreshLayout swipeToLoadLayout;
    @BindView(R2.id.iv_head)
    ImageView iv_head;

    @BindView(R2.id.tv_my)
    TextView tv_my;

    @OnClick(R2.id.lay_my)
    public void lay_my(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }

        if(MainApplication.getInstance().getToken().equals("")){
            startActivity(new Intent(activity,LoginTypeActivity.class));
        }else {
            startActivity(new Intent(activity,MyActivity.class));
        }
    }

    @BindView(R2.id.tv_vip_time)
    TextView tv_vip_time;
    @BindView(R2.id.tv_vip_text)
    TextView tv_vip_text;
    @BindView(R2.id.tv_vip_state)
    TextView tv_vip_state;
    @OnClick(R2.id.lay_vip)
    public void lay_vip(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }
        if(MainApplication.getInstance().getToken().equals("")){
            tipLogin("请先登录！");
        }else {
            RequestBean requestBean = new RequestBean();
            startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", RetrofitManager.RES_URL + "iap/index.php?p="+ requestBean.getParams())
                    .putExtra("title", "VIP会员续费").putExtra("qqun", ""));
        }
    }



    @OnClick(R2.id.lay_soucang)
    public void lay_soucang(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }
        if(MainApplication.getInstance().getToken().equals("")){
            tipLogin("请先登录！");
        }else {
            startActivity(new Intent(activity,ZitieSoucangListActivity.class));
        }
    }

    @BindView(R2.id.sw_light)
    SwitchCompat sw_light;

    @BindView(R2.id.tv_cache)
    TextView tv_cache;
    @OnClick(R2.id.lay_cache)
    public void lay_cache(View view){
        startActivity(new Intent(activity, CacheManangeActivity.class));
    }

    @OnClick(R2.id.lay_wxgzh)
    public void lay_wxgzh(View view) {
        final IWXAPI api = MainApplication.getInstance().api;
        // 判断当前版本是否支持拉起客服会话
        if (api.getWXAppSupportAPI() >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            WXOpenCustomerServiceChat.Req req = new WXOpenCustomerServiceChat.Req();
            req.corpId = "ww85d9054dea13b635";                                  // 企业ID
            req.url = "https://work.weixin.qq.com/kfid/kfc3827e90d8fa1e629";    // 客服URL
            api.sendReq(req);
        } else {
            ToastUtil.showToast(activity, "请更新最新版本微信");
        }
    }

    @BindView(R2.id.lay_common)
    LinearLayout lay_common;
    @OnClick(R2.id.lay_common_bg)
    public void lay_common_bg(View view) {
        try {

            TipPopView tipPopView = new TipPopView(activity, "为『" + getResources().getString(R.string.app_name) + "』评分", "如果您觉得『" + getResources().getString(R.string.app_name) + "』很好用，可否为其评一个分数？评分过程只需花费很少时间，非常感谢您的支持！", null, "以后再说", "现在评分", new TipPopView.TipListener() {
                @Override
                public void ok() {

                }
            }, new TipPopView.TipListener2() {
                @Override
                public void ok() {
                    try {
                        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        SPUtils.put(activity, "isCommon" + getVersionCode(activity), true);
                        lay_common.setVisibility(View.GONE);
                    } catch (ActivityNotFoundException e) {
                        ToastUtil.showToast(activity, "未发现安装的应用市场应用");
                    }
                }
            });
            tipPopView.showPopupWindow(tv_my);


        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "手机未安装应用市场", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R2.id.lay_share)
    public void lay_share(View view){
        final String url="https://app.ygsf.com";
        userBean = MainApplication.getInstance().getUser();

        if(sharePopView == null){
            sharePopView = new SharePopView(activity, "分享『"+getResources().getString(R.string.app_name)+"』给您的朋友");
        }
        sharePopView.setData("『"+getResources().getString(R.string.app_name)+"』 - 发现汉字之美","1、查询书法字典\n2、欣赏经典名帖\n3、辅助集字创作","webpage","",url);
        sharePopView.showPopupWindow(iv_head);
    }
    SharePopView sharePopView;

    public void shareFile(Context context) {
        File apkFile = null;
        //AppUtils.getApkFile(context);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile));
        context.startActivity(intent);
    }


    @BindView(R2.id.tv_version)
    TextView tv_version;
    @BindView(R2.id.lay_version)
    LinearLayout lay_version;
    @OnClick(R2.id.lay_version_bg)
    public void lay_version_bg(View view) {

        if(lay_version.getVisibility() != View.VISIBLE){
            TipPopView tipPopView = new TipPopView(activity, "版本更新", "『" + getResources().getString(R.string.app_name) + "』已经是最新版本了。", null, "知道了", null, new TipPopView.TipListener() {
                @Override
                public void ok() {

                }

            }, new TipPopView.TipListener2() {
                @Override
                public void ok() {

                }
            });
            tipPopView.showPopupWindow(tv_my);
        }else {
            TipPopView tipPopView = new TipPopView(activity, "版本更新", "『" + getResources().getString(R.string.app_name) + "』的最新版本为 " + SPUtils.get(MainApplication.getInstance(), "appversion", "") + "，您当前的版本为 v" + MainApplication.versionName + "，是否立即更新？", null, "以后再说", "立即更新", new TipPopView.TipListener() {
                @Override
                public void ok() {

                }
            }, new TipPopView.TipListener2() {
                @Override
                public void ok() {
                    final String url = "https://app.ygsf.com";
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            tipPopView.showPopupWindow(tv_my);
        }
    }
    @OnClick(R2.id.lay_yinsi)
    public void lay_yinsi(View view){
        String url="https://app.ygsf.com/privacy.html";
        startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", url).putExtra("title","隐私政策"));
    }

    @OnClick(R.id.lay_video)
    public void lay_video(View view) {
        if (MainApplication.getInstance().getToken().equals("")) {
            tipLogin("请先登录！");
        } else {
            MyVideosActivity.safeStart(activity);
        }
    }

    @OnClick(R.id.lay_video_fav)
    public void lay_video_fav(View view) {
        if (MainApplication.getInstance().getToken().equals("")) {
            tipLogin("请先登录！");
        } else {
            VideoFavActivity.safeStart(activity);
        }
    }

    @OnClick(R.id.lay_font_pri)
    public void lay_font_pri(View view) {
        ZiLibMyActivity.safeStart(activity, "my");
    }

    @OnClick(R.id.lay_font_fav)
    public void lay_font_fav(View view) {
        ZiLibMyActivity.safeStart(activity, "fav");
    }

    @OnClick(R.id.lay_author_fav)
    public void lay_author_fav(View view) {
        if (MainApplication.getInstance().isLogin()) {
            AuthorFavActivity.safeStart(activity);
        } else {
            tipLogin("请先登录！");
        }
    }

    @OnClick(R.id.lay_my_course)
    public void lay_my_course(View view) {
        final String url = "https://app4jbtmyne3011.h5.xiaoeknow.com/homepage/50";
        XiaoeSdkActivity.safeStart(activity, url);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_persion;
    }

    @Override
    protected void initView(View rootView) {
        EventBus.getDefault().register(this);
        swipeToLoadLayout.setEnableLoadMore(false);
        swipeToLoadLayout.setOnRefreshListener(new com.scwang.smartrefresh.layout.listener.OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                swipeToLoadLayout.finishRefresh(false);
                requestBean = new RequestBean();
                presenter.getinfo(requestBean,false);
            }
        });
        initView();
    }

    @Override
    protected void requestData() {
        super.requestData();
        getUserInfo();
    }

    private void getUserInfo() {
        if (!TextUtils.isEmpty(MainApplication.getInstance().getToken())) {
            presenter.getinfo(new RequestBean(), false);
        }
    }

    //登录被踢
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOut messageEvent) {
        initView();
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        requestBean = new RequestBean();
        requestBean.addParams("jid", "");
        initView();
    }

    //用户信息更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_UserUpdate messageEvent) {
        initView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    TipPopView tipLoginPopView;
    private void tipLogin(String msg){
        if(tipLoginPopView == null){
            tipLoginPopView = new TipPopView(activity, "请登录", msg, "登录", new TipPopView.TipListener() {
                @Override
                public void ok() {
                    startActivity(new Intent(activity, LoginTypeActivity.class));
                }
            });
        }
        tipLoginPopView.showPopupWindow(iv_head);
    }

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    UserBean userBean;
    private void initView(){

        sw_light.setChecked((Boolean) SPUtils.get(activity,"light",false));

        sw_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SPUtils.put(activity,"light",true);
                }else {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    SPUtils.put(activity,"light",false);
                }
            }
        });

        //检测用户是否对该版本评价过
        if((boolean)SPUtils.get(activity,"isCommon"+getVersionCode(activity),false)){
            lay_common.setVisibility(View.GONE);
        }else {
            lay_common.setVisibility(View.VISIBLE);
        }

        if(SPUtils.get(MainApplication.getInstance(),"appversion","").equals("")){
            lay_version.setVisibility(View.GONE);
        }else {
            lay_version.setVisibility(View.VISIBLE);
        }
        tv_version.setText("当前版本 v"+MainApplication.versionName);


        if (MainApplication.getInstance().getToken().equals("")) {
            try {
                GlideUtils.loadUserAvatar(userBean != null ? userBean.get_avatar() : "", iv_head);
            } catch (Exception e) {
                Glide.with(activity).clear(iv_head);
            }
            iv_head.setImageResource(R.mipmap.avatar);
            iv_head.setBackgroundColor(getResources().getColor(R.color.colorLine));
            tv_my.setText("登录/注册");
            tv_vip_text.setText("以观书法VIP会员");
            tv_vip_state.setText("立即开通");
            tv_vip_state.setTextColor(getResources().getColor(R.color.orange));
            tv_vip_time.setText("");
            return;
        }
        initData();
    }

    private void initData(){
        userBean = MainApplication.getInstance().getUser();

        if(userBean.get_nickname() != null && !"".equals(userBean.get_nickname())){
            tv_my.setText(""+userBean.get_nickname());
        }else if(userBean.get_name() != null && !"".equals(userBean.get_name())){
            tv_my.setText(""+userBean.get_name());
        }else if(userBean.get_phone() != null && !"".equals(userBean.get_phone())){
            tv_my.setText(""+userBean.get_phone());
        }else {
            tv_my.setText("暂未设置昵称");
        }

        //会员有效期
        long now = Calendar.getInstance().getTimeInMillis();
        long expire = userBean.get_expire()*1000;
        long yongjiu = now+200l*365l*24l*60l*60l*1000l;
        if(expire<=0){//未购买过
            tv_vip_text.setText("以观书法VIP会员");
            tv_vip_time.setText("");
            tv_vip_state.setText("立即开通");
            tv_vip_state.setTextColor(getResources().getColor(R.color.orange));
        }else if(expire<now){//已过期
            tv_vip_text.setText("VIP会员有效期至");
            tv_vip_time.setText("已过期");
            tv_vip_time.setTextColor(getResources().getColor(R.color.silver));
            tv_vip_state.setText("续费");
            tv_vip_state.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else if(expire>yongjiu){//永久有效
            tv_vip_text.setText("VIP会员有效期至");
            tv_vip_time.setText("永久会员");
            tv_vip_time.setTextColor(getResources().getColor(R.color.orange));
            tv_vip_state.setText("");
        }else if(expire>now){//未过期
            tv_vip_text.setText("VIP会员有效期至");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            tv_vip_time.setText(""+formatter.format(new Date(expire)));
            tv_vip_time.setTextColor(getResources().getColor(R.color.orange));
            tv_vip_state.setText("续费");
            tv_vip_state.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        //iv_head.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity,R.color.main)));
        iv_head.setBackgroundColor(getResources().getColor(R.color.lightCyan));
        if (!TextUtils.isEmpty(userBean.get_avatar()) && MainApplication.getInstance().isLogin()) {
            GlideUtils.loadUserAvatar(userBean.get_avatar(), iv_head);
        }

        if (activity != null) {
            if (activity.findViewById(R.id.lay_line) != null) {
                activity.findViewById(R.id.lay_line).setVisibility(userBean.get_role() > 0 ? View.VISIBLE : View.GONE);
                activity.findViewById(R.id.lay_video).setVisibility(userBean.get_role() > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }


    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    protected void cancel() {
        super.cancel();
        disimissProgress();
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        swipeToLoadLayout.finishRefresh();
    }

    @Override
    public void loadDataSuccess(BaseBean tData) {

    }

    @Override
    public void getUserInfoSuccess(UserBean userBean) {
        MainApplication.getInstance().putUser(userBean);
        initData();
    }

    @Override
    public void loadDataError(String errorMsg) {

    }

    RequestBean requestBean;
    @Override
    protected PersionPresenterImpl getPresenter() {
        return new PersionPresenterImpl();
    }
}
