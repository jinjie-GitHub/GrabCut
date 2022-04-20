package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.content.res.ColorStateList;
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
 * update
 */
public class JiziAutoPopView extends BubbleDialog {

    private ViewHolder mViewHolder;
    private OnClickJiziAutoPopViewListener mListener;

    public void setAuthor(String author) {
        if (TextUtils.isEmpty(author)) {
            mViewHolder.tvAuthor.setText("书法家");
            mViewHolder.tvAuthor.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            mViewHolder.iv_reset.setImageResource(R.mipmap.arrow_down_small);
            mViewHolder.iv_reset.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.gray)));
            MySPUtils.setJiZiAutoAuthor(getContext(), "");
        } else {
            mViewHolder.tvAuthor.setText(author);
            mViewHolder.tvAuthor.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            mViewHolder.iv_reset.setImageResource(R.mipmap.close2);
            mViewHolder.iv_reset.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
            MySPUtils.setJiZiAutoAuthor(getContext(), author);
        }
    }

    public JiziAutoPopView(Activity context) {
        super(context);
        setTransParentBackground();
        setPosition(Position.BOTTOM);
        View rootView = LayoutInflater.from(context).inflate(R.layout.widget_jizi_auto_popview, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);

        mViewHolder.ll_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String author = mViewHolder.tvAuthor.getText().toString();
                if (author.equals("书法家")) {
                    SearchActivity.safeStart(context, Constan.Key_type.KEY_AUTHOR, "", "书法家", 3);
                    context.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                } else {
                    setAuthor("");
                }
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
    }

    @Override
    public void show() {
        final String author = MySPUtils.getJiZiAutoAuthor(getContext());
        setAuthor(author);

        final int checkIdKind = MySPUtils.getJiZiAutoKind(getContext());
        if (checkIdKind != 0) {
            mViewHolder.rgKid.check(checkIdKind);
        } else {
            mViewHolder.rgKid.check(R.id.rb_kid_1);
        }

        setBoldText(mViewHolder, R.id.rb_kid_1, R.id.rb_kid_2);
        final int checkIdFont = MySPUtils.getJiZiAutoFont(getContext());
        if (checkIdFont != 0) {
            mViewHolder.rgFont.check(checkIdFont);
        } else {
            mViewHolder.rgFont.check(R.id.rb_1);
        }

        super.show();
    }

    private static void setBoldText(ViewHolder holder, int... rbId) {
        for (int cId : rbId) {
            try {
                RadioButton rb = holder.rgKid.findViewById(cId);
                rb.getPaint().setFakeBoldText(rb.isChecked());
            } catch (Exception e) {}
        }
    }

    private void auto(boolean reset){
        RequestBean requestBean = new RequestBean();
        switch (mViewHolder.rgFont.getCheckedRadioButtonId()){
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

        switch (mViewHolder.rgKid.getCheckedRadioButtonId()){
            case R.id.rb_kid_1:
                requestBean.addParams("kind", "1");
                break;
            case R.id.rb_kid_2:
                requestBean.addParams("kind", "2");
                break;
        }

        String author = mViewHolder.tvAuthor.getText().toString();
        if (author.equals("书法家")) {
            author = "";
        }
        requestBean.addParams("author", author);

        requestBean.addParams("reset",reset);

        mListener.auto(requestBean);
    }

    public static class ViewHolder {
        @BindView(R.id.ll_author)
        View ll_author;
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
        @BindView(R.id.rb_kid_1)
        RadioButton rbKid1;
        @BindView(R.id.rb_kid_2)
        RadioButton rbKid2;
        @BindView(R.id.rg_kid)
        RadioGroup rgKid;
        @BindView(R.id.tv_supplement)
        TextView mTvSupplement;
        @BindView(R.id.tv_again)
        TextView tvAgain;

        @BindView(R.id.iv_reset)
        ImageView iv_reset;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            rgKid.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
                MySPUtils.setJiZiAutoKind(view.getContext(), checkedId);
                setBoldText(this, R.id.rb_kid_1, R.id.rb_kid_2);
            });
            rgFont.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
                MySPUtils.setJiZiAutoFont(view.getContext(), checkedId);
            });
        }
    }

    public void setClickListener(OnClickJiziAutoPopViewListener l) {
        this.mListener = l;
    }

    public interface OnClickJiziAutoPopViewListener {
        void auto(RequestBean requestBean);
    }
}
