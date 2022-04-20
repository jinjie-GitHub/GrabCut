package com.ltzk.mbsf.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ltzk.mbsf.R;

/**
 * Created by JinJie on 2020/11/15
 */
public class LoginOutDialog extends DialogFragment implements View.OnClickListener {

    //private Context mActivity;
    private View.OnClickListener mClickListener;

    public static LoginOutDialog showTip(String title, String content) {
        LoginOutDialog dialog = new LoginOutDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.widgets_tip_popview, container, false);
        view.findViewById(R.id.tv_pop_cancal).setOnClickListener(this);
        TextView tv_pop_ok = view.findViewById(R.id.tv_pop_ok);
        tv_pop_ok.setText("登录");
        tv_pop_ok.getPaint().setFakeBoldText(true);
        tv_pop_ok.setOnClickListener(this);

        TextView tv_1_popview = view.findViewById(R.id.tv_1_popview);
        TextView tv_2_popview = view.findViewById(R.id.tv_2_popview);
        if (getArguments() != null) {
            String title = getArguments().getString("title", "");
            String content = getArguments().getString("content", "");
            tv_1_popview.setText(title);
            tv_2_popview.setText(content);
        }
        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(this, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public boolean isDialogFragmentShowing() {
        if (this != null && this.getDialog() != null && this.getDialog().isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mClickListener != null) {
            mClickListener.onClick(v);
        }
    }
}