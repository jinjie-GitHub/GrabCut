package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.ZilibSelectActivity;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.xujiaji.happybubble.BubbleDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * update on 2021-6-8
 */
public class JiziLibPopView extends BubbleDialog {
    public static final String JIZI_LIB_TITLE = "jizi_lib_title";
    public static final String JIZI_LIB_ID = "jizi_lib_id";

    private ViewHolder mViewHolder;
    private OnClickJiziAutoPopViewListener mListener;
    private ZilibBean mZilibBean;

    public void setZilibBean(ZilibBean zilibBean) {
        this.mZilibBean = zilibBean;
        if (zilibBean != null && !TextUtils.isEmpty(zilibBean.get_id())) {
            mViewHolder.tvAuthor.setText(zilibBean.get_title());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int c = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                mViewHolder.tvAuthor.setTextColor(c);
                mViewHolder.tvAuthor.setCompoundDrawableTintList(ColorStateList.valueOf(c));
            }
            SPUtils.put(getContext(), JIZI_LIB_TITLE, zilibBean.get_title());
            SPUtils.put(getContext(), JIZI_LIB_ID, zilibBean.get_id());
        } else {
            mViewHolder.tvAuthor.setText("请选择字库");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int c = ContextCompat.getColor(getContext(), R.color.gray);
                mViewHolder.tvAuthor.setTextColor(c);
                mViewHolder.tvAuthor.setCompoundDrawableTintList(ColorStateList.valueOf(c));
            }
            SPUtils.put(getContext(), JIZI_LIB_TITLE, "");
            SPUtils.put(getContext(), JIZI_LIB_ID, "");
        }
    }

    public JiziLibPopView(Activity context) {
        super(context);
        setTransParentBackground();
        setPosition(Position.BOTTOM);
        View rootView = LayoutInflater.from(context).inflate(R.layout.widget_jizi_lib_popview, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);

        final String title = (String) SPUtils.get(getContext(), JIZI_LIB_TITLE, "");
        final String id = (String) SPUtils.get(getContext(), JIZI_LIB_ID, "");
        final ZilibBean bean = new ZilibBean();
        bean.set_title(title);
        bean.set_id(id);
        setZilibBean(bean);

        mViewHolder.tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivityForResult(new Intent(context, ZilibSelectActivity.class), 4);
                context.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
        });

        mViewHolder.tvAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mZilibBean == null) {
                    ToastUtil.showToast(context, "请选择字库。");
                    return;
                }
                RequestBean requestBean = new RequestBean();
                requestBean.addParams("fid", mZilibBean.get_id());
                requestBean.addParams("reset", true);
                mListener.auto(requestBean);
            }
        });

        mViewHolder.mTvSupplement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mZilibBean == null) {
                    ToastUtil.showToast(context, "请选择字库。");
                    return;
                }
                RequestBean requestBean = new RequestBean();
                requestBean.addParams("fid", mZilibBean.get_id());
                requestBean.addParams("reset", false);
                mListener.auto(requestBean);
            }
        });
    }

    public static class ViewHolder {
        @BindView(R.id.tv_author)
        TextView tvAuthor;
        @BindView(R.id.tv_supplement)
        TextView mTvSupplement;
        @BindView(R.id.tv_again)
        TextView tvAgain;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setClickListener(OnClickJiziAutoPopViewListener l) {
        this.mListener = l;
    }

    public interface OnClickJiziAutoPopViewListener {
        void auto(RequestBean requestBean);
    }
}