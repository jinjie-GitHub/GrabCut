package com.ltzk.mbsf.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.VersionPresenterImpl;
import com.ltzk.mbsf.api.view.VersionView;
import com.ltzk.mbsf.base.BaseFragment;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.KindType;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.VersionBean;
import com.ltzk.mbsf.bean.XeLoginBean;
import com.ltzk.mbsf.fragment.DictionaryFragment;
import com.ltzk.mbsf.fragment.FindFragment;
import com.ltzk.mbsf.fragment.HomeFragment;
import com.ltzk.mbsf.fragment.JiziListFragment;
import com.ltzk.mbsf.fragment.PersionFragment;
import com.ltzk.mbsf.fragment.ZiListFragment;
import com.ltzk.mbsf.fragment.ZiTiejiListFragment;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.widget.PrivatePolicyDialog;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;


/**
 * 描述：首页
 * 作者： on 2017/11/26 17:38
 * 邮箱：499629556@qq.com
 */

public class MainActivity extends MyBaseActivity<VersionView, VersionPresenterImpl> implements VersionView {

    @BindView(R.id.lay)
    LinearLayout lay;
    @BindView(R.id.rg_tab)
    RadioGroup rg_tab;
    @BindView(R.id.msg_tip1)
    View msg_tip1;
    @BindView(R.id.msg_tip2)
    View msg_tip2;
    @BindView(R.id.msg_tip3)
    View msg_tip3;
    @BindView(R.id.msg_tip4)
    View msg_tip4;

    HomeFragment homeFragment;
    DictionaryFragment dictionaryFragment;
    ZiListFragment ziListFragment1;// 字典界面 毛笔
    ZiListFragment ziListFragment2;// 字典界面 硬笔
    ZiListFragment ziListFragment3;// 字典界面 篆刻

    ZiTiejiListFragment ziTieListFragment;
    JiziListFragment jiziListFragment;
    FindFragment findFragment;
    PersionFragment persionFragment;

    private int mQueryType = KindType.MAOBI.getKind();
    private FragmentType mShowFragmentType = FragmentType.home;
    private FragmentType mShowFragmentTypeOld = FragmentType.home;

    private BaseFragment getFragmentByType(FragmentType fragmentType) {
        switch (fragmentType) {
            case zilist1://字列表
                return ziListFragment1;
            case zilist2://字列表
                return ziListFragment2;
            case zilist3://字列表
                return ziListFragment3;
            case dictionary: //字典
                return dictionaryFragment;
            default://首页
                return homeFragment;
        }
    }

    private void processFirstTab(FragmentTransaction ft) {
        ft.hide(getFragmentByType(mShowFragmentTypeOld));
        ft.show(getFragmentByType(mShowFragmentType));
    }

    /**
     * 切换Fragment
     * @param kindType
     * @param type
     */
    private void change(int kindType, FragmentType type) {
        mShowFragmentTypeOld = mShowFragmentType;
        mShowFragmentType = type;
        mQueryType = kindType;
        int checkId = rg_tab.getCheckedRadioButtonId();
        rg_tab.clearCheck();
        rg_tab.check(checkId);
    }

    @Override
    public void initView() {
        topBar.setVisibility(View.GONE);
        topBar.setLeftButtonNoPic();

        requestBean = new RequestBean();
        homeFragment = new HomeFragment();
        homeFragment.setHomeSearchCallBack(new HomeFragment.HomeSearchCallBack() {
            @Override
            public void onSearch(int kindType, String key) {
                if (kindType == KindType.ZIDIAN.getKind()) {
                    dictionaryFragment.search(key);
                    change(kindType, FragmentType.dictionary);
                } else if (kindType == KindType.ZHUANGKE.getKind()) {
                    ziListFragment3.search(key, kindType, true);
                    change(kindType, FragmentType.zilist3);
                } else if (kindType == KindType.YINGBI.getKind()) {
                    ziListFragment2.search(key, kindType, true);
                    change(kindType, FragmentType.zilist2);
                } else {
                    ziListFragment1.search(key, kindType, true);
                    change(kindType, FragmentType.zilist1);
                }
            }
        });

        dictionaryFragment = new DictionaryFragment();
        dictionaryFragment.setDictionaryFragmentCallBack(new DictionaryFragment.DictionaryFragmentCallBack() {
            @Override
            public void onBack() {
                change(mQueryType, FragmentType.home);
            }
        });

        ziListFragment1 = new ZiListFragment();
        ziListFragment1.setZiListFragmentCallBack(new ZiListFragment.ZiListFragmentCallBack() {
            @Override
            public void onBack() {
                change(mQueryType, FragmentType.home);
            }
        });

        ziListFragment2 = new ZiListFragment();
        ziListFragment2.setZiListFragmentCallBack(new ZiListFragment.ZiListFragmentCallBack() {
            @Override
            public void onBack() {
                change(mQueryType, FragmentType.home);
            }
        });

        ziListFragment3 = new ZiListFragment();
        ziListFragment3.setZiListFragmentCallBack(new ZiListFragment.ZiListFragmentCallBack() {
            @Override
            public void onBack() {
                change(mQueryType, FragmentType.home);
            }
        });

        ziTieListFragment = new ZiTiejiListFragment();
        jiziListFragment = new JiziListFragment();
        findFragment = new FindFragment();
        persionFragment = new PersionFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, homeFragment);
        fragmentTransaction.add(R.id.fragment_container, dictionaryFragment);
        fragmentTransaction.add(R.id.fragment_container, ziListFragment1);
        fragmentTransaction.add(R.id.fragment_container, ziListFragment2);
        fragmentTransaction.add(R.id.fragment_container, ziListFragment3);
        fragmentTransaction.add(R.id.fragment_container, ziTieListFragment);
        fragmentTransaction.add(R.id.fragment_container, jiziListFragment);
        fragmentTransaction.add(R.id.fragment_container, findFragment);
        fragmentTransaction.add(R.id.fragment_container, persionFragment);
        fragmentTransaction.show(homeFragment);
        fragmentTransaction.hide(ziListFragment1);
        fragmentTransaction.hide(ziListFragment2);
        fragmentTransaction.hide(ziListFragment3);
        fragmentTransaction.hide(dictionaryFragment);
        fragmentTransaction.hide(ziTieListFragment);
        fragmentTransaction.hide(jiziListFragment);
        fragmentTransaction.hide(findFragment);
        fragmentTransaction.hide(persionFragment);
        fragmentTransaction.commit();
        for (int i = 0; i < 4; i++) {
            if ((Boolean) SPUtils.get(MainApplication.getInstance(), "msgtip_tabindex" + i, false)) {
                initMsgTip(i + "");
            }
        }
        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    return;
                }
                SPUtils.put(getApplication(), KEY_SELECT_TAB, checkedId);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (checkedId == R.id.rb_1) {
                    msg_tip1.setVisibility(View.INVISIBLE);
                    SPUtils.put(MainApplication.getInstance(), "msgtip_tabindex" + 0, false);
                    topBar.setVisibility(View.GONE);
                    processFirstTab(fragmentTransaction);
                    fragmentTransaction.hide(ziTieListFragment);
                    fragmentTransaction.hide(jiziListFragment);
                    fragmentTransaction.hide(findFragment);
                    fragmentTransaction.hide(persionFragment);
                } else if (checkedId == R.id.rb_2) {
                    msg_tip2.setVisibility(View.INVISIBLE);
                    SPUtils.put(MainApplication.getInstance(), "msgtip_tabindex" + 1, false);
                    topBar.setVisibility(View.GONE);
                    fragmentTransaction.show(ziTieListFragment);
                    fragmentTransaction.hide(getFragmentByType(mShowFragmentType));
                    fragmentTransaction.hide(jiziListFragment);
                    fragmentTransaction.hide(findFragment);
                    fragmentTransaction.hide(persionFragment);
                } else if (checkedId == R.id.rb_3) {
                    msg_tip3.setVisibility(View.INVISIBLE);
                    SPUtils.put(MainApplication.getInstance(), "msgtip_tabindex" + 2, false);
                    topBar.setVisibility(View.GONE);
                    fragmentTransaction.show(jiziListFragment);
                    fragmentTransaction.hide(getFragmentByType(mShowFragmentType));
                    fragmentTransaction.hide(ziTieListFragment);
                    fragmentTransaction.hide(findFragment);
                    fragmentTransaction.hide(persionFragment);
                } else if (checkedId == R.id.rb_4) {
                    msg_tip2.setVisibility(View.INVISIBLE);
                    SPUtils.put(MainApplication.getInstance(), "msgtip_tabindex" + 4, false);
                    topBar.setVisibility(View.VISIBLE);
                    topBar.setTitle("发现");
                    fragmentTransaction.show(findFragment);
                    fragmentTransaction.hide(getFragmentByType(mShowFragmentType));
                    fragmentTransaction.hide(ziTieListFragment);
                    fragmentTransaction.hide(jiziListFragment);
                    fragmentTransaction.hide(persionFragment);
                } else {
                    msg_tip4.setVisibility(View.INVISIBLE);
                    SPUtils.put(MainApplication.getInstance(), "msgtip_tabindex" + 3, false);
                    topBar.setVisibility(View.VISIBLE);
                    topBar.setTitle("我的");
                    fragmentTransaction.hide(getFragmentByType(mShowFragmentType));
                    fragmentTransaction.hide(ziTieListFragment);
                    fragmentTransaction.hide(jiziListFragment);
                    fragmentTransaction.hide(findFragment);
                    fragmentTransaction.show(persionFragment);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_main_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrivatePolicyDialog.showTip(this);
        setRadioBoldText();
        handleSelectTab();
        if (MainApplication.getInstance().isLogin()) {
            presenter.user_sdk_login();
        }
    }

    private final void setRadioBoldText() {
        final int count = rg_tab.getChildCount();
        for (int i = 0; i < count; i++) {
            if (rg_tab.getChildAt(i) instanceof RadioButton) {
                RadioButton rb = (RadioButton) rg_tab.getChildAt(i);
                if (rb != null && rb.getPaint() != null) {
                    rb.getPaint().setFakeBoldText(true);
                }
            }
        }
    }

    static final String KEY_SELECT_TAB = "key_select_tab";
    private void handleSelectTab() {
        final int checkedId = (int) SPUtils.get(getApplication(), KEY_SELECT_TAB, R.id.rb_1);
        rg_tab.clearCheck();
        if (checkedId > 0) {
            final View checkView = rg_tab.findViewById(checkedId);
            if (checkView instanceof RadioButton) {
                ((RadioButton) checkView).setChecked(true);//回调一次
            } else {
                rg_tab.check(R.id.rb_1);//直接执行RadioGroup.check会回调多次
            }
        } else {
            rg_tab.check(R.id.rb_1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        presenter.user_sdk_login();
    }

    private void initMsgTip(String tabindex) {
        switch (tabindex) {
            case "0":
                msg_tip1.setVisibility(View.VISIBLE);
                break;
            case "1":
                msg_tip2.setVisibility(View.VISIBLE);
                break;
            case "2":
                msg_tip3.setVisibility(View.VISIBLE);
                break;
            case "3":
                msg_tip4.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SPUtils.put(MainApplication.getInstance(), "height", lay.getHeight());
        super.onPause();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(VersionBean tData) {
        if (tData == null) {
            return;
        }
        TipPopView tipPopView;
        if (tData.isForce()) {//强制更新
            tipPopView = new TipPopView(activity, "发现新版本", tData.getWhatsnew(), "", "　去官网　", "去应用市场",
                    new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            Uri uri = Uri.parse(tData.getUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }, new TipPopView.TipListener2() {
                @Override
                public void ok() {
                    try {
                        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtil.showToast(activity, "未发现安装的应用市场应用");
                    }
                }
            });
            tipPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    finish();
                }
            });
        } else {
            tipPopView = new TipPopView(activity, "发现新版本", tData.getWhatsnew(), " 暂不更新 ", "　去官网　", "去应用市场",
                    new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            Uri uri = Uri.parse(tData.getUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }, new TipPopView.TipListener2() {
                @Override
                public void ok() {
                    try {
                        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtil.showToast(activity, "未发现安装的应用市场应用");
                    }
                }
            });
        }
        tipPopView.showPopupWindow(topBar);
    }

    @Override
    public void loadDataError(String errorMsg) {

    }

    @Override
    public void getUserInfoSuccess(UserBean userBean) {
        MainApplication.getInstance().putUser(userBean);
        EventBus.getDefault().post(new Bus_UserUpdate());
    }

    //支付结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(BaseResp baseResp) {
        /*0	成功	展示成功页面
		-1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
		-2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。*/
        switch (baseResp.errCode) {
            case 0:
                presenter.getinfo(requestBean, true);
                break;
            case -1:
                break;
            case -2:
                break;
        }
    }

    @Override
    public void user_sdk_login(XeLoginBean bean) {
        MainApplication.getInstance().mXeLoginBean = bean;
        EventBus.getDefault().post(new Integer(0x1000));
    }

    private RequestBean requestBean;
    protected VersionPresenterImpl getPresenter() {
        return new VersionPresenterImpl();
    }

    public enum FragmentType {
        home, zilist1, zilist2, zilist3, dictionary;
    }

    private long mExitTime;
    public void onBackPressedSupport() {
        if (mShowFragmentType != FragmentType.home) {
            change(mQueryType, FragmentType.home);
        } else {
            if (System.currentTimeMillis() - mExitTime < 2000) {
                super.onBackPressedSupport();
            } else {
                mExitTime = System.currentTimeMillis();
                ToastUtil.showToast(this, "再按一次退出应用");
            }
        }
    }
}