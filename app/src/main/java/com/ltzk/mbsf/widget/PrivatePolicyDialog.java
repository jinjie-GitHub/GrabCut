package com.ltzk.mbsf.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.WebCollectQueryActivity;
import com.ltzk.mbsf.utils.MySPUtils;

/**
 * Created by JinJie on 2020/6/7
 */
public class PrivatePolicyDialog extends DialogFragment implements View.OnClickListener {
    private static final String URL = "https://app.ygsf.com/privacy.html";
    private static final String CONTENT = "感谢您使用以观书法产品！为帮助您安全使用产品和服务，请您在使用前仔细阅读并透彻理解《以观书法隐私政策》。如您同意此政策，请点击“同意”并开始使用我们的产品和服务，我们尽全力保护您的个人信息安全。";

    private TextView mContent;
    private Button mAgree, mDisAgree;
    private FragmentActivity mActivity;

    public static void showTip(FragmentActivity fa) {
        final boolean isAgree = MySPUtils.getPrivatePolicy(fa);
        if (!isAgree) {
            PrivatePolicyDialog dialog = new PrivatePolicyDialog();
            dialog.setCancelable(false);
            dialog.show(fa.getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_private_policy, container, false);
        mAgree = view.findViewById(R.id.btn_agree);
        mAgree.setOnClickListener(this);
        mDisAgree = view.findViewById(R.id.btn_disagree);
        mDisAgree.setOnClickListener(this);
        mContent = view.findViewById(R.id.tv_content);
        setSpannableString();
        return view;
    }

    private void setSpannableString() {
        final SpannableString spannableString = new SpannableString(CONTENT);
        final ClickableSpan clickSpan = new ClickableSpan() {
            public void onClick(View widget) {
                WebCollectQueryActivity.safeStart(mActivity, URL);
            }

            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        int start = 41, end = 51;
        spannableString.setSpan(clickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.darkRed));
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mContent.setMovementMethod(LinkMovementMethod.getInstance());
        mContent.setText(spannableString);
        mContent.setHighlightColor(getResources().getColor(R.color.transparentWhiteSmoke));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(this, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_disagree:
                dismiss();
                mActivity.finish();
                Process.killProcess(Process.myPid());
                break;
            case R.id.btn_agree:
                dismiss();
                MySPUtils.setPrivatePolicy(mActivity, true);
                MainApplication.getInstance().initSDK();
                break;
        }
    }
}