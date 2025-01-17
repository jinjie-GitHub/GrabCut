package com.ltzk.mbsf.widget.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import com.ltzk.mbsf.R;

public class UCropView extends FrameLayout {

    private final GestureCropImageView mGestureCropImageView;
    private final OverlayView mViewOverlay;

    public UCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_crop_view, this, true);
        mGestureCropImageView = findViewById(R.id.image_view_crop);
        mViewOverlay = findViewById(R.id.view_overlay);

        mGestureCropImageView.setCropBoundsChangeListener(new CropImageView.CropBoundsChangeListener() {
            @Override
            public void onCropBoundsChangedRotate(float cropRatio) {
                if (mViewOverlay != null) {
                    mViewOverlay.setTargetAspectRatio(cropRatio);
                    mViewOverlay.postInvalidate();
                }
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView);
        mViewOverlay.processStyledAttributes(a);
        mGestureCropImageView.processStyledAttributes(a);
        a.recycle();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @NonNull
    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    @NonNull
    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

}