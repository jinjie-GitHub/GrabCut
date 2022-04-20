package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.widget.CropView;
import com.ltzk.mbsf.widget.crop.BitmapLoadUtils;
import com.ltzk.mbsf.widget.crop.CropImageView;
import com.ltzk.mbsf.widget.crop.GestureCropImageView;
import com.ltzk.mbsf.widget.crop.OverlayView;
import com.ltzk.mbsf.widget.crop.TransformImageView;
import com.ltzk.mbsf.widget.crop.UCrop;
import com.ltzk.mbsf.widget.crop.UCropView;
import java.io.OutputStream;

/**
 * Created by JinJie on 2020/5/31
 */
public class PhotoCropActivity extends AppCompatActivity {
    private static final String TAG = "UCropActivity";

    private Uri mOutputUri;
    private CropView mCropView;
    private UCropView mUCropView;
    private OverlayView mOverlayView;
    private GestureCropImageView mGestureCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        setContentView(R.layout.activity_photo_crop);
        initView();
        initEvent();
        setImageData(getIntent());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    private void setImageData(Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);

        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri);
            } catch (Exception e) {
                setResultException(e);
                finish();
            }
        } else {
            setResultException(new NullPointerException("Both input and output Uri must be specified"));
            finish();
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
            float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
            } else {
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            }
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
            int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
            } else {
                Log.w(TAG, "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0");
            }
        }
    }

    private void initView() {
        mCropView = this.findViewById(R.id.cropView);
        mCropView.setPaintType(MySPUtils.getPaintType(this));
        mCropView.setPaintColor(MySPUtils.getPaintColor(this));
        mUCropView = this.findViewById(R.id.uCropView);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        // 设置允许缩放
        mGestureCropImageView.setScaleEnabled(true);
        // 设置禁止旋转
        mGestureCropImageView.setRotateEnabled(false);
        // 设置剪切后的最大宽度
        //mGestureCropImageView.setMaxResultImageSizeX(300);
        // 设置剪切后的最大高度
        //mGestureCropImageView.setMaxResultImageSizeY(300);

        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.TRANSPARENT);
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(false);
        // 设置显示裁剪边框
        mOverlayView.setShowCropFrame(false);
        // 设置不显示裁剪网格
        mOverlayView.setShowCropGrid(false);
    }

    private void initEvent() {
        mGestureCropImageView.setTransformImageListener(mImageListener);
        findViewById(R.id.sure).setOnClickListener((v) -> {
            cropAndSaveImage();
        });
        findViewById(R.id.cancel).setOnClickListener((v) -> {
            finish();
        });
    }

    private void cropAndSaveImage() {
        OutputStream outputStream = null;
        try {
            final Bitmap croppedBitmap = mGestureCropImageView.cropImage();
            if (croppedBitmap != null) {
                outputStream = getContentResolver().openOutputStream(mOutputUri);
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
                croppedBitmap.recycle();

                setResultUri(mOutputUri, mGestureCropImageView.getTargetAspectRatio());
                finish();
            } else {
                setResultException(new NullPointerException("CropImageView.cropImage() returned null."));
            }
        } catch (Exception e) {
            setResultException(e);
            finish();
        } finally {
            BitmapLoadUtils.close(outputStream);
        }
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            //setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            //setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_ucrop_fade_in);
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mGestureCropImageView.setImageToWrapCropBounds();
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mUCropView.startAnimation(fadeInAnimation);
        }

        @Override
        public void onLoadFailure(Exception e) {
            setResultException(e);
            finish();
        }
    };

    private void setResultUri(Uri uri, float resultAspectRatio) {
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio));
    }

    private void setResultException(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }
}