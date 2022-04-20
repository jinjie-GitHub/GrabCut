package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.JiziNewPresenterImpl;
import com.ltzk.mbsf.api.view.JiziNewView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.LineNoEditText;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 描述：新建集字
 * 作者： on 2017/11/29 15:14
 * 邮箱：499629556@qq.com
 */

public class JiziNewActivity extends MyBaseActivity<JiziNewView,JiziNewPresenterImpl> implements JiziNewView {
    public static final String EXTRAS_CONTENT = "extras_content";
    int MAX_LINE = 50;
    int MAX_ROW = 50;
    int MAX_COUNT = 500;

    @BindView(R2.id.et_key)
    LineNoEditText et_key;

    @BindView(R2.id.tv_format)
    TextView tv_format;

    @OnClick(R2.id.tv_clear)
    public void tv_clear(View view) {
        String text = rep(et_key.getText().toString());
        et_key.setText(text);
        et_key.setSelection(text.length());
    }

    @OnClick(R2.id.tv_format)
    public void iv_menu(View view) {
        showFormatPopup(view);
    }

    private void showFormatPopup(View v) {
        final View view = getLayoutInflater().inflate(R.layout.ppw_set_format, null);
        QMUIPopup mNormalPopup = QMUIPopups.popup(activity, (int) (ViewUtil.getWidth() * 3 / 5), (int) (ViewUtil.getWidth() * 3 / 5))
                .view(view)
                .bgColor(ContextCompat.getColor(this, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(this, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(100))
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 22);

        TextView tv_title = view.findViewById(R.id.tv_title);
        final String key = et_key.getText().toString();
        final String[] keys = key.split("\n");
        final int count = key.length() - keys.length + 1;
        tv_title.setText("请选择每行字数（总：" + count + "字）");

        GridView gv_menu = view.findViewById(R.id.gv_menu);
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            data.add(i);
        }
        ArrayAdapter adapter = new ArrayAdapter<>(activity, R.layout.adapter_item_jizi_new_menu, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int num = (int) adapter.getItem(position);
                String text = et_key.getText().toString();
                StringBuilder builder = new StringBuilder(text.replaceAll("\n", ""));
                int lenght = builder.length();
                lenght = lenght + lenght / num;
                for (int j = 0; j < lenght; ) {
                    j = j + num;
                    if (j > lenght) {
                        break;
                    }
                    if (j != lenght - 1) {
                        builder.insert(j, "\n");
                    }
                    j = j + 1;
                }
                et_key.setText(builder);
                et_key.setSelection(builder.length());
                mNormalPopup.dismiss();
            }
        };
        gv_menu.setAdapter(adapter);
        gv_menu.setOnItemClickListener(onItemClickListener);
        mNormalPopup.show(v);
    }

    RequestBean requestBean;
    public void initView() {
        requestBean = new RequestBean();
        topBar.setRightTxtListener("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.closeSyskeyBroad(activity);
                if (MainApplication.getInstance().isLogin()) {
                    submit();
                } else {
                    activity.startActivity(new Intent(activity, LoginTypeActivity.class));
                }
            }
        });

        initData();
        et_key.setSelection(et_key.getText().toString().length());
        mHandler.sendEmptyMessageDelayed(0,400);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity,et_key);
        }
    };

    /**
     * 初始化数据
     */
    JiziBean jiziBean;
    private void initData(){
        jiziBean = (JiziBean) getIntent().getSerializableExtra("jiziBean");
        String content = getIntent().getStringExtra(EXTRAS_CONTENT);
        if (!TextUtils.isEmpty(content)) {
            et_key.setText(content);
            et_key.setSelection(content.length());
        }
        if(jiziBean == null){//新建
            topBar.setTitle("新建集字");
            topBar.setLeftButtonListener(R.mipmap.close2, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            topBar.setLeftTextGone();
            topBar.setLeftButtonVISIBLE();
        }else {//编辑
            topBar.setTitle(jiziBean.get_title()+"");
            et_key.setText(jiziBean.getText()+"");
            topBar.setLeftTxtListener("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            topBar.setLeftButtonNoPic();
            topBar.setLeftTextVISIBLE();
        }
    }

    /**
     * 提交
     */
    private void submit() {
        String key = et_key.getText().toString();
        if ("".equals(key)) {
            ToastUtil.showToast(activity, "您还未输入集字内容！");
        } else {
            final String temp = stringTrimAll(key);
            if (temp.length() > MAX_COUNT) {
                ToastUtil.showToast(activity, "最多支持" + MAX_COUNT + "个汉字。");
                return;
            }

            String[] keys = key.split("\n");
            if (keys.length > MAX_LINE) {
                ToastUtil.showToast(activity, "最多支持" + MAX_LINE + "行。");
                return;
            }
            for (String key_str : keys) {
                if (key_str.length() > MAX_ROW) {
                    ToastUtil.showToast(activity, "每行最多" + MAX_ROW + "个汉字。");
                    return;
                }
            }

            requestBean.addParams("text", key);
            if (jiziBean == null) {
                presenter.jizi_add(requestBean, true);
            } else {
                requestBean.addParams("jid", jiziBean.get_id());
                presenter.jizi_update(requestBean, true);
            }
        }
    }

    private final String stringTrimAll(final String input) {
        if (null == input)
            return "";
        final String regex = "\\s*|\t|\r|\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }

    /**
     * 格式化处理复制来的内容
     * @param key
     * @return
     */
    private String rep(String key){
        key = key.replaceAll("[，。；？！,.;?!]","\n");
        //key = key.replaceAll("[^\\n\\u4e00-\\u9fa5]","\n");
        key = key.replaceAll("[^=\\n\\u4e00-\\u9fa5]","\n");
        key = key.replaceAll("\\n+","\n");

        return key;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jizi_new;
    }

    @Override
    protected JiziNewPresenterImpl getPresenter() {
        return new JiziNewPresenterImpl();
    }

    //修改集字
    @Override
    public void jizi_updateResult(JiziBean tData) {
        jiziBean.set_thumbs(tData.get_thumbs());
        jiziBean.set_json(tData.get_json());
        JiziEditNewActivity.safeStart(activity, jiziBean);
        EventBus.getDefault().post(new Bus_JiziUpdata(jiziBean));
        finish();
    }

    @Override
    public void setUp(String content) {
        if (!TextUtils.isEmpty(content)) {
            et_key.setText(content);
            et_key.setSelection(content.length());
        }
    }

    @OnClick(R2.id.tv_tradition)
    public void tv_tradition(View view) {
        final String text = rep(et_key.getText().toString());
        presenter.text_s2t(text);
    }

    @OnClick(R2.id.tv_simple)
    public void tv_simple(View view) {
        final String text = rep(et_key.getText().toString());
        presenter.text_t2s(text);
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity,errorMsg+"");
    }

    //新建集字
    @Override
    public void loadDataSuccess(JiziBean tData) {
        JiziEditNewActivity.safeStart(activity, tData);
        EventBus.getDefault().post(new Bus_JiziUpdata(tData));
        finish();
    }

    @Override
    public void finish() {
        super.finishFromTop();
    }
}
