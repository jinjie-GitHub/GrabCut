package com.ltzk.mbsf.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.PromotePresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.TopBar;
import com.ltzk.mbsf.widget.photoview2.PhotoView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import butterknife.BindView;

/**
 * update on 2021/03/09
 */
public class PhotosActivity extends MyBaseActivity<IBaseView, PromotePresenter> implements IBaseView<String> {
    private static final int REQUEST_CODE_ALBUM = 0x01;

    static {
        System.loadLibrary("native-lib");
    }

    @BindView(R.id.root)
    View mRoot;

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.imageView1)
    PhotoView imageView1;

    @BindView(R.id.imageGrabCut)
    ImageView imageView;

    @BindView(R.id.imageView2)
    PhotoView imageView2;

    @BindView(R.id.confirm)
    View confirm;

    private boolean isOk;
    private int     type = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_photos;
    }

    @Override
    protected PromotePresenter getPresenter() {
        return new PromotePresenter();
    }

    @Override
    public void initView() {
        imageView1.enable();
        imageView1.disableRotate();

        imageView2.setRatio(0.8f);
        imageView2.enable();
        imageView2.enableRotate();

        mTopBar.setTitle("合成图片");
        mTopBar.setLeftTxtListener("选择图片", v -> {
            showBottomDialog();
        });

        mTopBar.setRightTxtListener("合成图片", v -> {
            if (isOk) {
                showProgress();
                mTopBar.postDelayed(() -> {
                    savePicToSdcard(mergeBitmap(BitmapUtils.convertViewToBitmap(imageView1),
                            createBitmap(imageView2)));
                    ToastUtil.showToast(activity, "操作成功");
                    disimissProgress();
                    isOk = false;
                }, 2000L);
            }
        });

        init();
    }

    private Scalar GREEN = new Scalar(0, 255, 0);
    private Bitmap bitmap;
    private Bitmap bm;
    private Mat    image = new Mat();
    private Mat    imgC3 = new Mat();
    private int    downX, downY;
    private Rect rect;

    private void init() {
        imageView.setOnTouchListener((v, event) -> {
            final int x = (int) (event.getX());
            final int y = (int) (event.getY());

            int type = event.getAction();
            switch (type) {
                case MotionEvent.ACTION_DOWN:
                    downX = x;
                    downY = y;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    rect = new Rect(new Point(downX, downY), new Point(x, y));
                    Mat res = new Mat();
                    image.copyTo(res);
                    Imgproc.rectangle(res, new Point(downX, downY), new Point(x, y), GREEN, 1);
                    Utils.matToBitmap(res, bm);
                    imageView.setImageBitmap(bm);
                    break;
            }
            return true;
        });
    }

    public void onGrabCut(View view) {
        if (rect == null) {
            Toast.makeText(this, "请用手指标注裁剪区域!", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread() {
            public void run() {
                super.run();
                Mat firstMask = new Mat();
                Mat bgModel   = new Mat();
                Mat fgModel   = new Mat();
                Imgproc.grabCut(imgC3, firstMask, rect, bgModel, fgModel, 1, Imgproc.GC_INIT_WITH_RECT);

                Mat source = new Mat(firstMask.rows(), firstMask.cols(), CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
                Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

                //This is important. You must use Scalar(255,255, 255,255), not Scalar(255,255,255)
                Mat res = new Mat(image.size(), CvType.CV_8UC3, new Scalar(0,
                        0, 0, 0));
                image.copyTo(res, firstMask);
                Utils.matToBitmap(res, bm);

                runOnUiThread(() -> {
                    //进行合并
                    confirm.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    imageView2.setVisibility(View.VISIBLE);
                    imageView2.setImageBitmap(bm);
                    isOk = true;
                });
            }
        }.start();
    }

    //把白色转换成透明
    public static Bitmap getImageToChange(Bitmap mBitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        if (mBitmap != null) {
            int   mWidth  = mBitmap.getWidth();
            int   mHeight = mBitmap.getHeight();
            int[] pixels  = new int[mWidth * mHeight];
            for (int i = 0; i < mWidth * mHeight; i++) {
                int color = pixels[i];
                int g     = Color.green(color);
                int r     = Color.red(color);
                int b     = Color.blue(color);
                int a     = Color.alpha(color);
                if (g >= 250 && r >= 250 && b >= 250) {
                    a = 0;
                }
                pixels[i] = Color.argb(a, r, g, b);
            }
            createBitmap.setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
        }
        return createBitmap;
    }

    public Bitmap removeBackground(Bitmap bitmap) {
        //GrabCut part
        Mat img = new Mat();
        Utils.bitmapToMat(bitmap, img);

        Mat mask     = new Mat();
        Mat fgdModel = new Mat();
        Mat bgdModel = new Mat();

        Mat imgC3 = new Mat();
        Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);

        Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 5, Imgproc.
                GC_INIT_WITH_RECT);

        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
        Core.compare(mask, source/* GC_PR_FGD */, mask, Core.CMP_EQ);

        //This is important. You must use Scalar(255,255, 255,255), not Scalar(255,255,255)
        Mat foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255,
                255, 255, 255));
        img.copyTo(foreground, mask);

        // convert matrix to output bitmap
        bitmap = Bitmap.createBitmap((int) foreground.size().width,
                (int) foreground.size().height,
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(foreground, bitmap);
        return bitmap;
    }

    private Bitmap createBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {
        Canvas      canvas = new Canvas(backBitmap);
        final Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(frontBitmap, new Matrix(), paint);
        return backBitmap;
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        View         view   = View.inflate(this, R.layout.dialog_custom_layout, null);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                type = 0;
                uploadPicture();
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                type = 1;
                uploadPicture();
            }
        });
    }

    private void savePicToSdcard(Bitmap bitmap) {
        Acp.getInstance(activity).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                BitmapUtils.savePicToSdcard(getApplication(), bitmap, String.valueOf(System.currentTimeMillis()));
            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtil.showToast(activity, "没有相册权限！");
            }
        });
    }

    public void uploadPicture() {
        Acp.getInstance(activity)
                .request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .build(), new AcpListener() {
                    @Override
                    public void onGranted() {
                        chooseAlbum();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        ToastUtil.showToast(activity, "没有相机权限！");
                    }
                });
    }

    /**
     * 选择相册照片
     */
    private void chooseAlbum() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM) {

            //取消拍照或者图片选择时
            if (resultCode != RESULT_OK) {
                ToastUtil.showToast(activity, "取消图片选择");
                return;
            }

            //拍照成功和选取照片时
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {
                        if (type == 0) {
                            Glide.with(activity).clear(imageView1);
                            Glide.with(getApplication())
                                    .asBitmap()
                                    .dontAnimate()
                                    .load(data.getData())
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            imageView1.setVisibility(View.VISIBLE);
                                            imageView1.setImageBitmap(resource);
                                        }
                                    });
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                            imageView2.setVisibility(View.GONE);
                            Glide.with(activity).clear(imageView);
                            Glide.with(getApplication())
                                    .asBitmap()
                                    .dontAnimate()
                                    .load(data.getData())
                                    .override((int) (ViewUtil.getScreenWidth(activity) * 0.7f), (int) (ViewUtil.getScreenHeight(activity) * 0.7))
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            if (resource != null) {
                                                //进行裁剪
                                                bitmap = resource;
                                                bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                                Utils.bitmapToMat(bitmap, image);
                                                Imgproc.cvtColor(image, imgC3, Imgproc.COLOR_RGBA2RGB);
                                                confirm.setVisibility(View.VISIBLE);
                                                imageView.setImageBitmap(bitmap);

                                                //isOk = true;
                                                //imageView2.setImageBitmap(resource);
                                                //imageView2.setImageBitmap(BitmapHelper.INSTANCE.grayToBinaryNew(resource));
                                            }
                                        }
                                    });
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(String tData) {

    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    private native void reset(long img);
}