package com.ltzk.mbsf.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;

public class GlideUtils {
    public static void loadCircleImage1(String url, ImageView imageView) {
        Glide.with(MainApplication.getInstance())
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void loadCircleImage2(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .circleCrop();
        Glide.with(MainApplication.getInstance())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    public static void loadUserAvatar(final String url, final ImageView imageView) {
        /*Glide.with(MainApplication.getInstance())
                .asBitmap()
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .placeholder(R.mipmap.avatar).error(R.mipmap.avatar)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                });*/
        Glide.with(MainApplication.getInstance())
                .load(url)
                .dontAnimate()
                .placeholder(R.mipmap.avatar)
                .error(R.mipmap.avatar)
                .into(imageView);
    }
}