package com.ltzk.mbsf.activity;

import android.view.View;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.CleanDataUtils;
import com.ltzk.mbsf.widget.TopBar;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * update on 2021/5/31
 */
public class CacheManangeActivity extends BaseActivity {

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_length)
    TextView tvLength;

    @Override
    public void initView() {
        topBar.setTitle("缓存管理");
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        showProgressDialog("根据缓存多少，耗时时间可能稍长……");
        Observable.create(new ObservableOnSubscribe<HashMap<String, String>>() {
            @Override
            public void subscribe(ObservableEmitter<HashMap<String, String>> map) throws Exception {
                String length = CleanDataUtils.getTotalCacheSize(activity);
                String count = CleanDataUtils.getTotalCacheFileSize(activity) + "个";
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("length", length);
                map1.put("count", count);
                map.onNext(map1);
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Consumer<HashMap<String, String>>() {
                    @Override
                    public void accept(HashMap<String, String> map) throws Exception {
                        tvLength.setText(map.get("length"));
                        tvCount.setText(map.get("count"));
                        closeProgressDialog();
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cache_manage;
    }

    @OnClick(R.id.tv_clear)
    public void onViewClicked() {
        TipPopView tipPopView = new TipPopView(activity, "", "确定要清理应用缓存？", "清理", new TipPopView.TipListener() {
            @Override
            public void ok() {
                CleanDataUtils.clearAllCache(activity);
                initData();
            }
        });
        tipPopView.showPopupWindow(tvLength);
    }
}