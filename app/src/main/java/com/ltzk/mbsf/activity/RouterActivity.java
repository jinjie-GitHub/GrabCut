package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by JinJie on 2020/8/19
 */
public class RouterActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.post(() -> {
            gotoActivity(this);
            finish();
        });
    }

    public static void gotoActivity(AppCompatActivity activity) {
        /*String action = getIntent().getAction();
        if (!TextUtils.isEmpty(action) && Intent.ACTION_VIEW.equal(action)) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                String id = uri.getQueryParameter("id");
                String name = uri.getQueryParameter("name");
            }
        }*/
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}