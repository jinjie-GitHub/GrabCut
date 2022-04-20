package com.ltzk.mbsf.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.bean.Bus_wechatLogin;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * 描述：
 * 作者： on 2018/10/9 22:24
 * 邮箱：499629556@qq.com
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果没回调onResp，八成是这句没有写
        MainApplication.getInstance().api.handleIntent(getIntent(), this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }
    @Override
    public void onResp(BaseResp resp) {
        //Log.e("WXEntryActivity","微信回调:"+resp.errCode+"=="+resp.errStr);
        switch(resp.errCode){
            case BaseResp.ErrCode.ERR_OK://成功
                if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//登录
                    SendAuth.Resp newResp = (SendAuth.Resp) resp;
                    EventBus.getDefault().post(new Bus_wechatLogin(newResp.code));
                }else if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
                    Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        finish();
    }
}
