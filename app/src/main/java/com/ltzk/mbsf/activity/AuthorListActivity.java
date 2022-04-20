package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ChaodaiAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.AuthorListPresenterImpl;
import com.ltzk.mbsf.api.view.AuthorListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.AuthorBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.TagLayout;
import com.ltzk.mbsf.widget.UnderlineTextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：
 * 作者： on 2018/9/15 14:32
 * 邮箱：499629556@qq.com
 */

public class AuthorListActivity extends MyBaseActivity<AuthorListView, AuthorListPresenterImpl> implements AuthorListView {

    public static final int REQ_SEARCH_KEY = 1;
    public static final int REQ_SEARCH = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_SEARCH_KEY) {
                et_key.setText(data.getStringExtra(SearchActivity.KEY));
                search();
            }
        }
    }

    private void search() {
        if (!"".equals(et_key.getText().toString())) {
            requestBean = new RequestBean();
            requestBean.addParams("name", et_key.getText().toString());
            presenter.getAuthorList(requestBean, true);
            adapter.setIndexSelect(-1);
            adapter.notifyDataSetChanged();
        }
    }

    @BindView(R2.id.iv_back)
    ImageView iv_back;
    @OnClick(R2.id.iv_back)
    public void iv_back(View view) {
        finish();
    }

    @BindView(R2.id.et_key)
    EditText et_key;
    @OnClick(R2.id.et_key)
    public void et_key(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_AUTHOR, "", "书法家", REQ_SEARCH_KEY);
    }

    @BindView(R2.id.lv_caodai)
    ListView lv;
    ChaodaiAdapter adapter;

    @BindView(R2.id.lay_search)
    LinearLayout lay_search;

    @BindView(R2.id.tl_author)
    TagLayout tl_author;

    String dynasty = "晋";

    public static void safeStart(Context c, String dynasty) {
        Intent intent = new Intent(c, AuthorListActivity.class);
        intent.putExtra("dynasty", dynasty);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_author_list;
    }

    @Override
    public void initView() {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("dynasty"))) {
            dynasty = getIntent().getStringExtra("dynasty");
        }
        requestBean = new RequestBean();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != adapter.getIndexSelect()) {
                    String chaodai = (String) parent.getItemAtPosition(position);
                    getAuthorByChaodai(position, chaodai);
                }
            }
        });
        adapter = new ChaodaiAdapter(activity);
        lv.setAdapter(adapter);
        presenter.dylist(requestBean, true);
        getAuthorByChaodai(0, dynasty);
    }

    private void getAuthorByChaodai(int position, String chaodai) {
        adapter.setIndexSelect(position);
        adapter.notifyDataSetChanged();
        requestBean = new RequestBean();
        requestBean.addParams("dynasty", "" + chaodai);
        presenter.getAuthorList(requestBean, true);
    }

    @Override
    protected AuthorListPresenterImpl getPresenter() {
        return new AuthorListPresenterImpl();
    }

    private RequestBean requestBean;
    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(List<AuthorBean> tData) {
        tl_author.removeAllViews();

        if (tData != null && tData.size() > 0) {
            for (int i = 0; i < tData.size(); i++) {
                AuthorBean authorBean = tData.get(i);
                if (authorBean.get_hi() != null && authorBean.get_hi().equals("1")) {
                    //TextView textView = (TextView) getLayoutInflater().inflate(R.layout.adapter_author_hi,null);
                    TextView textView = new TextView(this);
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_border_stroke));
                    textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView.setPadding(ViewUtil.dpToPx(5), ViewUtil.dpToPx(3), ViewUtil.dpToPx(5), ViewUtil.dpToPx(3));
                    textView.setText("" + authorBean.get_name());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type", 1).putExtra("name", authorBean.get_name()).putExtra("title", authorBean.get_name()));
                        }
                    });

                    tl_author.addView(textView);
                } else {
                    //UnderlineTextView textView = (UnderlineTextView)getLayoutInflater().inflate(R.layout.adapter_author,null);
                    UnderlineTextView textView = new UnderlineTextView(this);
                    textView.setTextColor(getResources().getColor(R.color.gray));
                    textView.setPadding(ViewUtil.dpToPx(5), ViewUtil.dpToPx(3), ViewUtil.dpToPx(5), ViewUtil.dpToPx(3));

                    textView.setText("" + authorBean.get_name());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type", 1).putExtra("name", authorBean.get_name()).putExtra("title", authorBean.get_name()));
                        }
                    });

                    tl_author.addView(textView);
                }
            }
        } else {

        }
    }

    @Override
    public void dylist(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(dynasty)) {
                adapter.setIndexSelect(i);
            }
        }
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
    }

    @Override
    public void dylistFail(String s) {
        String[] chaodai = getResources().getStringArray(R.array.chaodai);
        List<String> list = Arrays.asList(chaodai);
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }
}