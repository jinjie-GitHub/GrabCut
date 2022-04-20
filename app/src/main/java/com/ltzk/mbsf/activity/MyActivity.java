package com.ltzk.mbsf.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.MyPresenterImpl;
import com.ltzk.mbsf.api.view.MyView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.Bus_wechatLogin;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.popupview.PhoneBindSelectPopView;
import com.ltzk.mbsf.popupview.PicSelectPopView;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ActivityCollector;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.PicSelectUtil;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.crop.UCrop;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：我的信息
 * 作者： on 2017/11/26 17:38
 * 邮箱：499629556@qq.com
 */

public class MyActivity extends MyBaseActivity<MyView,MyPresenterImpl> implements MyView {
    @BindView(R2.id.tv_head)
    TextView tv_head;
    @BindView(R2.id.iv_head)
    ImageView iv_head;
    @OnClick(R2.id.lay_head)
    public void lay_head(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }
        if(picSelectPopView == null){
            picSelectPopView = new PicSelectPopView(activity,View.VISIBLE,View.VISIBLE ,"拍照", "从相册选择", new PicSelectPopView.OptionOnClick() {
                @Override
                public void onClick1() {
                    Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).build(), new AcpListener() {
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

                        }
                    });
                }

                @Override
                public void onClick2() {
                    Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).build(), new AcpListener() {
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

                        }
                    });
                }
            });
        }
        picSelectPopView.showPopupWindow(iv_head);
    }
    PicSelectPopView picSelectPopView;
    Uri uritempFile;
    private static final int RC_CHOOSE_PHOTO = 2;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        requestBean.addParams("avatar", PicSelectUtil.encodeImage(bitmap, 500));
                        presenter.update_avatar(requestBean,true);
                    } catch (Exception e) {
                        ToastUtil.showToast(activity, "无法剪切选择图片！");
                    }
                }
            }
        }
    }

    @BindView(R2.id.tv_nick)
    TextView tv_nick;
    @OnClick(R2.id.lay_nick)
    public void lay_nick(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }
        startActivity(new Intent(activity,NickUpdateActivity.class));
    }

    @BindView(R2.id.tv_phone)
    TextView tv_phone;
    @OnClick(R2.id.lay_phone)
    public void lay_phone(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }
        final UserBean userBean = MainApplication.getInstance().getUser();
        if(userBean.get_phone()!=null && !userBean.get_phone().equals("")){
            if(phoneBindSelectPopView == null){
                phoneBindSelectPopView = new PhoneBindSelectPopView(activity, new PhoneBindSelectPopView.OptionOnClick() {
                    @Override
                    public void onClick1() {
                        //解绑手机号
                        if((userBean.get_name() == null || userBean.get_phone().equals("")) && userBean.get_wx_bind() !=1){
                            //微信唯一
                            ToastUtil.showToast(activity,"手机号是该账号唯一登录方式，无法解绑，如果该账号不再使用，您可以选择注销账号。");
                        }else {
                            TipPopView tipPopView = new TipPopView(activity, "解绑手机号", "解绑后无法通过手机号登录该账号。","解绑", new TipPopView.TipListener() {
                                @Override
                                public void ok() {
                                    presenter.unbind_phone(requestBean,true);
                                }
                            });
                            tipPopView.showPopupWindow(topBar);
                        }
                    }

                    @Override
                    public void onClick2() {
                        //换绑手机号
                        LoginByCodeActivity.safeStart(activity,true);
                    }
                });
            }
            phoneBindSelectPopView.showPopupWindow(topBar);
        }else {
            LoginByCodeActivity.safeStart(activity,true);
        }
    }
    PhoneBindSelectPopView phoneBindSelectPopView;

    @BindView(R2.id.tv_wechat)
    TextView tv_wechat;
    @OnClick(R2.id.lay_wechat)
    public void lay_wechat(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }

        if(tv_wechat.getText().equals("已绑定")){

            if((MainApplication.getInstance().getUser().get_phone() == null || MainApplication.getInstance().getUser().get_phone().equals("")) && (MainApplication.getInstance().getUser().get_name()==null || MainApplication.getInstance().getUser().get_name().equals(""))){
                //微信唯一
                ToastUtil.showToast(activity,"微信是该账号唯一登录方式，无法解绑，如果该账号不再使用，您可以选择注销账号。");
            }else {
                TipPopView tipPopView = new TipPopView(activity, "解绑微信", "解绑后无法通过微信登录该账号。","解绑", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        presenter.unbind_weixin(requestBean,true);
                    }
                });
                tipPopView.showPopupWindow(topBar);
            }
        }else {
            TipPopView tipPopView = new TipPopView(activity, "绑定微信", "绑定微信后可通过微信登录该账号。","绑定", new TipPopView.TipListener() {
                @Override
                public void ok() {
                    if (!MainApplication.getInstance().api.isWXAppInstalled()) {
                        ToastUtil.showToast(activity,"您还未安装微信客户端");
                        return;
                    }
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "mbsf_wx_login";
                    MainApplication.getInstance().api.sendReq(req);
                }
            });
            tipPopView.showPopupWindow(topBar);
        }

    }

    @BindView(R2.id.tv_pwd)
    TextView tv_pwd;
    @OnClick(R2.id.lay_pwd)
    public void lay_pwd(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }

        startActivity(new Intent(activity,LoginByPwdActivity.class).putExtra("isSetting",true));
    }

    @OnClick(R2.id.lay_zhuxiao)
    public void lay_zhuxiao(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }

        startActivity(new Intent(activity,UserDelActivity.class));
    }

    @OnClick(R2.id.tv_quit)
    public void tv_quit(View view){
        if(XClickUtil.isFastDoubleClick(view)){
            return;
        }

        TipPopView tipPopView = new TipPopView(activity, "", "确定要退出当前账号？", "退出", "取消", null, new TipPopView.TipListener2() {
            @Override
            public void ok() {
                //presenter.logout(requestBean, true);
                presenter.xiaoe_sdk_logout();
            }
        });
        tipPopView.showPopupWindow(topBar);

    }

    @Override
    public void xiaoe_sdk_logout() {
        presenter.logout(requestBean, true);
    }

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @Override
    public void initView() {
        topBar.setTitle("用户中心");
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*topBar.setRightTxtListener("刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getinfo(requestBean,true);
            }
        });*/

        setUserData();
        initRefreshLayout();
        requestBean = new RequestBean();
        presenter.getinfo(requestBean,true);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(true);
        mRefreshLayout.setEnableOverScrollDrag(true);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mRefreshLayout.finishRefresh(false);
            presenter.getinfo(requestBean, true);
        });
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_my;
    }

    //微信登录授权成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_wechatLogin messageEvent) {
        Logger.e("--->bind");
        synchronized (this) {
            if (!requestBean.containsKey("code")) {
                EventBus.getDefault().post(new Integer(0x1000));
                requestBean.addParams("code", messageEvent.getCode());
                presenter.login_weixin(requestBean, true);
            }
        }
    }

    //刷新页面数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_UserUpdate messageEvent) {
        setUserData();
    }

    //刷新页面数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        presenter.getinfo(requestBean,true);
    }

    //退出登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOut messageEvent) {
        if(!ActivityCollector.getTopActivity().getClass().equals(this.getClass())){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected MyPresenterImpl getPresenter() {
        return new MyPresenterImpl();
    }

    RequestBean requestBean;

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(UserBean userBean) {
        MainApplication.getInstance().putUser(userBean);
        setUserData();
    }

    private void setUserData(){
        UserBean userBean = MainApplication.getInstance().getUser();

        //头像
        if(userBean.get_avatar() == null || userBean.get_avatar().equals("")){
            //未设置
            tv_head.setVisibility(View.VISIBLE);
            iv_head.setVisibility(View.GONE);
        }else {
            tv_head.setVisibility(View.GONE);
            iv_head.setVisibility(View.VISIBLE);
            Glide.with(activity).load(userBean.get_avatar()).centerCrop().dontAnimate().placeholder(R.mipmap.avatar)
                    .error(R.mipmap.avatar).into(iv_head);
        }
        //昵称
        if(userBean.get_nickname()!=null && !"".equals(userBean.get_nickname())){
            tv_nick.setText(""+userBean.get_nickname());
        }else {
            tv_nick.setText("未设置");
        }

        //手机号
        if(userBean.get_phone()==null || userBean.get_phone().equals("")){//未绑手机号
            tv_phone.setText("未绑定");
        }else {//已绑定微信
            tv_phone.setText(userBean.get_phone());
        }
        //微信
        if(userBean.get_wx_bind() != 1){//未绑定微信
            tv_wechat.setText("未绑定");
        }else {//已绑定微信
            tv_wechat.setText("已绑定");
        }

        //info_promote_text.setText(TextUtils.isEmpty(userBean._ad_title) ? "未设置" : userBean._ad_title);
        //info_promote_link.setText(TextUtils.isEmpty(userBean._ad_link) ? "未设置" : userBean._ad_link);

        //积分
        //tv_jifen.setText(userBean.get_point()+"");
    }


    /**
     * 退出登录成功
     */
    private void logout(){
        MainApplication.getInstance().quit();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new Bus_LoginOut(""));
        finish();
    }

    @Override
    public void logoutResult(String bean) {
        logout();
    }


    @Override
    public void logoutError(String bean) {
        logout();
    }


    @Override
    public void checkinResult(JSONObject bean) {
        ToastUtil.showToast(activity,"签到成功！");
        EventBus.getDefault().unregister(this);
        presenter.getinfo(requestBean,true);
        finish();
    }

    @Override
    public void unbind_weixinResult(String bean) {
        ToastUtil.showToast(activity,"解绑成功！");
        UserBean userBean = MainApplication.getInstance().getUser();
        userBean.set_wx_bind(0);
        MainApplication.getInstance().putUser(userBean);
        tv_wechat.setText("未绑定");
    }

    @Override
    public void unbind_phoneResult(String bean) {
        ToastUtil.showToast(activity,"解绑成功！");
        UserBean userBean = MainApplication.getInstance().getUser();
        userBean.set_phone("");
        MainApplication.getInstance().putUser(userBean);
        tv_phone.setText("未绑定");
    }

    @Override
    public void update_avatarResult(UserBean bean) {
        UserBean userBean = MainApplication.getInstance().getUser();
        userBean.set_avatar(bean.getAvatar());
        Glide.with(activity).load(bean.get_avatar()).centerCrop().dontAnimate().placeholder(R.mipmap.avatar)
                .error(R.mipmap.avatar).into(iv_head);
        EventBus.getDefault().post(new Bus_UserUpdate());
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity,""+errorMsg);
    }

    /**
     * 推广文案
     */
    /*@OnClick(R.id.info_promote_1)
    public void info_promote_1(View view) {
        if (XClickUtil.isFastDoubleClick(view)) {
            return;
        }

        PromoteActivity.safeStart(this, false);
    }*/

    /**
     * 推广链接
     */
    /*@OnClick(R.id.info_promote_2)
    public void info_promote_2(View view) {
        if (XClickUtil.isFastDoubleClick(view)) {
            return;
        }

        PromoteActivity.safeStart(this, true);
    }*/
}