package com.ltzk.mbsf.popupview;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.BasePopupWindow;
import com.ltzk.mbsf.utils.PicUtil;
import com.ltzk.mbsf.utils.ShareUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.Util;
import com.ltzk.mbsf.utils.ViewUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zy on 2016/3/25.
 * 分享选择
 */
public class SharePopView extends BasePopupWindow {
    private View conentView;

    @BindView(R2.id.tv_title_popview)
    TextView tv_title_popview;

    @BindView(R2.id.tv_0_popview)
    TextView tv_0_popview;
    @OnClick(R2.id.tv_0_popview)
    public void tv_0_popview(View view){
        if(TextUtils.isEmpty(url_image)){
            option = 0;
            save();
            return;
        }
        Single.just(url_image)
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        File file = PicUtil.newPicFile();
                        PicUtil.copyFile(s,file.getAbsolutePath());
                        return file;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(File file) {
                        //通知相册更新
                        //通知相册更新
                        PicUtil.sendMediaFile(activity,file);
                        ToastUtil.showToast(activity,"已保存到您的相册！");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @BindView(R2.id.tv_1_popview)
    TextView tv_1_popview;
    @OnClick(R2.id.tv_1_popview)
    public void tv_1_popview(View view){ //分享到微信朋友圈
        option = 1;
        save();
    }

    @BindView(R2.id.tv_2_popview)
    TextView tv_2_popview;
    @OnClick(R2.id.tv_2_popview)
    public void tv_2_popview(View view){//分享到微信好友
        option = 2;
        save();
    }

    @BindView(R2.id.tv_3_popview)
    TextView tv_3_popview;
    @OnClick(R2.id.tv_3_popview)
    public void tv_3_popview(View view){//分享到qq好友
        option = 3;
        share();
        dismiss();
    }

    @BindView(R2.id.tv_4_popview)
    TextView tv_4_popview;
    @OnClick(R2.id.tv_4_popview)
    public void tv_4_popview(View view){//分享到qq空间
        option = 4;
        share();
        dismiss();
    }

    @BindView(R2.id.tv_cannel)
    TextView tv_cannel;
    @OnClick(R2.id.tv_cannel)
    public void tv_cannel(View view){
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    int option = 0;
    private void save(){
        if (this.isShowing()) {
            this.dismiss();
        }

        if(type.equals("img")){
            if(!TextUtils.isEmpty(url_image)) {
                Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
                    @Override
                    public void onGranted() {
                        ((BaseActivity)activity).showProgressDialog("");
                        Glide.with(activity)
                                .asBitmap()
                                .override(480,800)
                                .load(url_image)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        //加载成功，resource为加载到的bitmap
                                        bitmap = resource;
                                        bitmapToShare();
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        ((BaseActivity)activity).closeProgressDialog();
                                        ToastUtil.showToast(activity, "分享失败！");
                                    }
                                });
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });
            }else {
                ((BaseActivity)activity).showProgressDialog("");
                bitmapToShare();
            }
        }else {
            share();
        }
    }

    private void bitmapToShare(){
        new PicUtil(activity, new PicUtil.SavePicListener() {
            @Override
            public void sussces(String path) {
                url_image = path;
                share();
                ((BaseActivity)activity).closeProgressDialog();
            }

            @Override
            public void fail() {
                ((BaseActivity)activity).closeProgressDialog();
                ToastUtil.showToast(activity, "分享失败！");
            }
        }).execute(bitmap);
    }

    private void share(){
        switch (option){
            case 0://保存到相册
                File file = new File(url_image);
                //通知相册更新
                PicUtil.sendMediaFile(activity,file);
                ToastUtil.showToast(activity,"已保存到您的相册！");
                break;
            case 1://分享到微信朋友圈
                shareWechat(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case 2://分享到微信好友
                shareWechat(SendMessageToWX.Req.WXSceneSession);
                break;
            case 3://分享到qq好友
                ShareUtils.shareQQ(activity, title, des, targ_url);
                break;
            case 4://分享到qq空间
                ShareUtils.shareQQZone(activity, title, des, targ_url);
                break;
        }
    }

    public SharePopView(final Activity activity, String title) {
        init(activity,title);
    }

    public SharePopView(final Activity activity, String title,int view) {
        init(activity,title);
        tv_0_popview.setVisibility(view);
    }

    private void init(final Activity activity, String title){
        api = MainApplication.getInstance().api;
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_share_popview, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        ButterKnife.bind(this, conentView);
        setWidth(Math.round(ViewUtil.getWidth()));
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackgroud(activity, 1.0f);
            }
        });
        tv_title_popview.setText(title);
    }

    String title;
    String des;
    String type;
    String url_image;
    String targ_url;
    public void setData(String title,String des,String type,Bitmap btimap,String targ_url){
        this.title = title;
        this.des = des;
        this.type = type;
        this.bitmap = btimap;
        this.url_image = "";
        this.targ_url = targ_url;
    }

    public void setData(String title,String des,String type,String url_image,String targ_url){
        this.title = title;
        this.des = des;
        this.type = type;
        this.url_image = url_image;
        this.targ_url = targ_url;
    }

    private Bitmap bitmap;
    private IWXAPI api;
    /**
     * 分享微信
     * @param toType
     */
    private void shareWechat(int toType) {
        if (api != null &&
                !api.isWXAppInstalled()) {
            ToastUtil.showToast(activity, "您需要安装微信客户端。");
            return;
        }
        WXMediaMessage msg = null;
        if(type.equals("img")){
            WXImageObject imageObject = new WXImageObject();
            imageObject.imagePath = url_image;
            msg = new WXMediaMessage(imageObject);
        }else {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = targ_url;
            msg = new WXMediaMessage(webpage);
        }
        msg.title = title;
        msg.description = des;
        Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher_share);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //分享类型 链接
        req.transaction = (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
        req.message = msg;
        req.scene = toType;
        api.sendReq(req);
    }

    /**
     * qq分享
     */
    /*private Bundle getMyBundle(){
        // 创建一个QQ实例，用于QQ分享、QQ空间分享
        Bundle bundle = new Bundle();
        if(type.equals("img")){
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                    QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        }
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targ_url);

        ArrayList<String> path_arr = new ArrayList<>();
        path_arr.add(targ_url);
        bundle.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, path_arr);
        return bundle;
    }*/

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        //   timePicker.setCityString(city);
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            //this.showAsDropDown(parent);
            darkenBackgroud(activity, 0.4f);
            this.showAtLocation(parent,Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
