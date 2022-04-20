package com.ltzk.mbsf.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.HistoryActivity;
import com.ltzk.mbsf.activity.JiziNewActivity;
import com.ltzk.mbsf.base.BaseFragment;
import com.ltzk.mbsf.bean.TodayBean;
import cn.tseeey.justtext.JustTextView;

/**
 * Created by JinJie on 2020/5/31
 */
public class HistoryDetailFragment extends BaseFragment {
    private JustTextView detail;
    private TodayBean bean;

    public static HistoryDetailFragment newInstance(TodayBean bean) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("today", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        ((HistoryActivity) _mActivity).mCurrent = "HistoryDetailFragment";
        ((HistoryActivity) _mActivity).mTopBar.setRightTextVisible();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_detail, container, false);
        detail = view.findViewById(R.id.detail);

        if (getArguments() != null) {
            bean = (TodayBean) getArguments().getSerializable("today");
            if (bean != null) {
                detail.setText(bean._content);
                ((HistoryActivity) _mActivity).mTopBar.setTitle("《" + bean._src + "》" + bean._dynasty + "・" + bean._author);
            }
        }

        return view;
    }

    /*public void copy() {
        ToastUtil.showToast(_mActivity, "书论内容已复制到剪贴板。");
        ClipboardManager cm = (ClipboardManager) _mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Label", detail.getText().toString());
        cm.setPrimaryClip(clipData);
    }*/

    /**
     * 去集字
     */
    public void copy() {
        StringBuilder sb = new StringBuilder();
        sb.append(detail.getText().toString());
        sb.append("\n");
        sb.append("《" + bean._src + "》" + bean._dynasty + "・" + bean._author);
        final Intent intent = new Intent(activity, JiziNewActivity.class);
        intent.putExtra(JiziNewActivity.EXTRAS_CONTENT, sb.toString());
        startActivityFromBottom(intent);
    }
}