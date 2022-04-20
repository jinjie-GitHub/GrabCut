package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.MySPUtils;
import com.xujiaji.happybubble.BubbleDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * update on 2021/6/6
 */
public class JiziZiTiePopView extends BubbleDialog {

    private ViewHolder mViewHolder;
    private OnClickJiziAutoPopViewListener mListener;
    private String mZiTieId = "";

    public void setAuthor(String author) {
        if (TextUtils.isEmpty(author)) {
            mViewHolder.tvAuthor.setText("书法家");
            mViewHolder.tvAuthor.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            mViewHolder.iv_icon.setImageResource(R.mipmap.arrow_down_small);
            mViewHolder.iv_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.gray)));
            MySPUtils.setJiZiZiTieAuthor(getContext(), "");
        } else {
            mViewHolder.tvAuthor.setText(author);
            mViewHolder.tvAuthor.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            mViewHolder.iv_icon.setImageResource(R.mipmap.close2);
            mViewHolder.iv_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
            MySPUtils.setJiZiZiTieAuthor(getContext(), author);
        }
    }

    public void setZiTieInfo() {
        String name = MySPUtils.getZiTieName(getContext());
        mZiTieId = MySPUtils.getZiTieId(getContext());
        if (TextUtils.isEmpty(name)) {
            mZiTieId = "";
            mViewHolder.ziTieName.setText("请选择字帖");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int c = ContextCompat.getColor(getContext(), R.color.gray);
                mViewHolder.ziTieName.setTextColor(c);
                mViewHolder.ziTieName.setCompoundDrawableTintList(ColorStateList.valueOf(c));
            }
        } else {
            mViewHolder.ziTieName.setText(name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final int c = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                mViewHolder.ziTieName.setTextColor(c);
                mViewHolder.ziTieName.setCompoundDrawableTintList(ColorStateList.valueOf(c));
            }
        }

        final String author = MySPUtils.getJiZiZiTieAuthor(getContext());
        setAuthor(author);
    }

    public JiziZiTiePopView(Activity context) {
        super(context);
        setTransParentBackground();
        setPosition(Position.BOTTOM);
        View rootView = LayoutInflater.from(context).inflate(R.layout.widget_jizi_zitie_popview, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);

        mViewHolder.llAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String author = mViewHolder.tvAuthor.getText().toString();
                if (author.equals("书法家")) {
                    author = "";
                    SearchActivity.safeStart(context, Constan.Key_type.KEY_AUTHOR, author, "书法家", 6);
                    context.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                } else {
                    setAuthor("");
                }
            }
        });

        mViewHolder.ziTieName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mViewHolder.ziTieName.getText().toString();
                if (name.equals("请选择字帖")) {
                    name = "";
                }
                SearchActivity.safeStart(context, Constan.Key_type.KEY_ZITIE, name, "字帖查询", 7);
                context.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
        });

        mViewHolder.tvAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto(true);
            }
        });

        mViewHolder.mTvSupplement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto(false);
            }
        });

        setZiTieInfo();
    }

    private void auto(boolean reset) {
        RequestBean requestBean = new RequestBean();
        switch (mViewHolder.rgFont.getCheckedRadioButtonId()) {
            case R.id.rb_0:
                requestBean.addParams("font", "");
                break;
            case R.id.rb_1:
                requestBean.addParams("font", "楷");
                break;
            case R.id.rb_2:
                requestBean.addParams("font", "行");
                break;
            case R.id.rb_3:
                requestBean.addParams("font", "草");
                break;
            case R.id.rb_4:
                requestBean.addParams("font", "隶");
                break;
            case R.id.rb_5:
                requestBean.addParams("font", "篆");
                break;
        }

        String author = mViewHolder.tvAuthor.getText().toString();
        if (author.equals("书法家")) {
            author = "";
        }
        requestBean.addParams("author", author);
        requestBean.addParams("zid", mZiTieId);
        requestBean.addParams("reset", reset);
        mListener.auto(requestBean);
    }

    public static class ViewHolder {
        @BindView(R.id.tv_author)
        TextView tvAuthor;
        @BindView(R.id.rb_1)
        RadioButton rb1;
        @BindView(R.id.rb_2)
        RadioButton rb2;
        @BindView(R.id.rb_3)
        RadioButton rb3;
        @BindView(R.id.rb_4)
        RadioButton rb4;
        @BindView(R.id.rb_5)
        RadioButton rb5;
        @BindView(R.id.rg_font)
        RadioGroup rgFont;
        @BindView(R.id.tv_supplement)
        TextView mTvSupplement;
        @BindView(R.id.tv_again)
        TextView tvAgain;
        @BindView(R.id.ziTieName)
        TextView ziTieName;
        @BindView(R.id.iv_icon)
        ImageView iv_icon;
        @BindView(R.id.llAuthor)
        View llAuthor;

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