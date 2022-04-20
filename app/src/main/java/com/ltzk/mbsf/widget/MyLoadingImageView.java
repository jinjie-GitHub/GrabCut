package com.ltzk.mbsf.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * update on 2021/12/14
 */
public class MyLoadingImageView extends RelativeLayout {

    @BindView(R2.id.tv_text)
    TextView tv_text;

    @BindView(R2.id.iv)
    ImageView iv;

    @BindView(R2.id.lay_loading)
    LinearLayout lay_loading;

    @BindView(R2.id.loading)
    ProgressBar loading;

    private Context context;

    public MyLoadingImageView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public MyLoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attr) {
        if (attr != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.MyLoadingImageView);
            //padding = typedArray.getDimension(R.styleable.MyLoadingImageView_padding, 0.0f);
            typedArray.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_image_loading, this);
        ButterKnife.bind(this, view);
    }

    public void clear() {
        lay_loading.setVisibility(GONE);
        iv.setImageBitmap(null);
    }

    public void setBitmap(Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public void setData(FragmentActivity activity, String url, int errorRes, ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            iv.setScaleType(scaleType);
        }
        setData(activity, url, errorRes);
    }

    public void setData(Activity activity, String url, int errorRes, ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            iv.setScaleType(scaleType);
        }
        setData(activity, url, errorRes);
    }

    public void setData(Activity activity, String url, int errorRes, int def, ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            iv.setScaleType(scaleType);
        }
        setData(activity, url, errorRes, def);
    }

    public void setData(android.app.Fragment fragment, String url, int errorRes, ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            iv.setScaleType(scaleType);
        }
        setData(fragment, url, errorRes);
    }

    public void setData(Fragment fragment, String url, int errorRes, ImageView.ScaleType scaleType) {
        if (scaleType != null) {
            iv.setScaleType(scaleType);
        }
        setData(fragment, url, errorRes);
    }

    public void setData(FragmentActivity activity, String url, int errorRes) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(activity);
            load(requestManager, url, errorRes);
        } else {
            iv.setImageResource(R.color.transparent);
        }
    }

    public void setData(Activity activity, String url, int errorRes) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(activity);
            load(requestManager, url, errorRes);
        } else {
            iv.setImageResource(R.color.transparent);
        }
    }

    public void setData(Activity activity, String url, int errorRes, int def) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(activity);
            load(requestManager, url, errorRes);
        } else {
            if (def > 0) {
                iv.setImageResource(def);
            } else {
                iv.setImageResource(R.color.transparent);
            }
        }
    }

    public void setData(Activity activity, String url, int errorRes, int def, int width, int height) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(activity);
            load(requestManager, url, errorRes);
        } else {
            if (def > 0) {
                iv.setImageResource(def);
            } else {
                iv.setImageResource(R.color.transparent);
            }
        }
    }

    public void setData(android.app.Fragment fragment, String url, int errorRes) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(fragment);
            load(requestManager, url, errorRes);
        } else {
            iv.setImageResource(R.color.transparent);
        }
    }

    public void setData(Fragment fragment, String url, int errorRes) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(fragment);
            load(requestManager, url, errorRes);
        } else {
            iv.setImageResource(R.color.transparent);
        }
    }

    public void setData(Fragment fragment, String url, int errorRes, int def) {
        if (checkUrl(url)) {
            RequestManager requestManager = Glide.with(fragment);
            load(requestManager, url, errorRes);
        } else {
            if (def > 0) {
                iv.setImageResource(def);
            } else {
                iv.setImageResource(R.color.transparent);
            }
        }
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        iv.setScaleType(scaleType);
    }

    public void setTextData(String text) {
        tv_text.setVisibility(VISIBLE);
        tv_text.setText(text);
        iv.setVisibility(GONE);
        lay_loading.setVisibility(GONE);
    }

    /**
     * 核对url
     */
    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url) || !url.contains("/")) {
            lay_loading.setVisibility(GONE);
            iv.setVisibility(GONE);
            return false;
        } else {
            lay_loading.setVisibility(VISIBLE);
            iv.setVisibility(VISIBLE);
            return true;
        }
    }

    /**
     * 加载图片
     */
    private void load(RequestManager requestManager, String url, int errorRes) {
        try {
            load(requestManager, url, errorRes, -1, -1);
        } catch (Exception e) {}
    }

    /**
     * 加载图片
     */
    private void load(RequestManager requestManager, String url, int errorRes, int width, int height) {
        requestManager.asBitmap()
                .load(url)
                .override(width, height)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        lay_loading.setVisibility(GONE);
                        if (errorRes > 0) {
                            iv.setImageResource(errorRes);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        lay_loading.setVisibility(GONE);
                        iv.setImageBitmap(resource);
                        return false;
                    }
                }).into(iv);
    }
}