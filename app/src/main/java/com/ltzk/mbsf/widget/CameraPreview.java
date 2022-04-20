package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ltzk.mbsf.utils.APIUtils;
import com.ltzk.mbsf.utils.CameraPreviewUtils;
import com.ltzk.mbsf.utils.CameraUtils;

/**
 * Created by JinJie on 2020/7/16
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.ErrorCallback {

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mCameraParam;
    private Rect mPreviewRect = new Rect();

    private int mCameraId;
    private int mPreviewWidth;
    private int mPreviewHight;
    private int mDisplayWidth;
    private int mDisplayHeight;

    public CameraPreview(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;

        mContext = context;
        mHolder = getHolder();
        mHolder.setSizeFromLayout();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    public void takePicture(Camera.PictureCallback callback) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, callback);
        }
    }

    public void startPreview() {
        if (this != null && this.getHolder() != null) {
            mHolder = this.getHolder();
            mHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            return;
        }

        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(mContext);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        int previewDegree = degree;

        mCameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));

        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432

        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);

        final Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        setCameraPreviewSize(mDisplayWidth, mDisplayHeight, previewSize.width, previewSize.height);

        //mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCameraParam.setJpegQuality(80);

        try {
            mCamera.setParameters(mCameraParam);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }

    private void setCameraPreviewSize(int viewWidth, int viewHeight, int cameraWidth, int cameraHeight) {
        boolean bIsPortrait = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);    // 判断水平/垂直状态

        if (bIsPortrait) {
            int tmp = cameraWidth;
            cameraWidth = cameraHeight;
            cameraHeight = tmp;
        }

        int destWidth = viewWidth;
        int destHeight = viewHeight;

        if (bIsPortrait) {
            destHeight = (int) (((float) cameraHeight / cameraWidth) * destWidth);
        } else {
            destWidth = (int) (((float) cameraWidth / cameraHeight) * destHeight);
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = destWidth;
        layoutParams.height = destHeight;
        setLayoutParams(layoutParams);
    }

    public void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mHolder != null) {
            mHolder.removeCallback(this);
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onError(int error, Camera camera) {

    }
}