/*
package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.DateTimeUtil;
import com.ltzk.mbsf.utils.FileUtil;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ToastUtil;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.spx.library.ThumbExoPlayerView;
import com.spx.library.Util_extKt;
import com.spx.library.player.VideoPlayTimeController;
import com.spx.library.player.VideoPlayer;
import com.spx.library.player.VideoPlayerOfMediaPlayer;
import com.spx.library.util.BackgroundTasks;
import com.spx.library.util.Config;
import com.spx.library.util.ViewUtil;
import com.spx.library.view.ClipContainer;
import com.spx.library.view.CropView;
import com.spx.library.view.ZoomLayout;

import java.io.File;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import kotlin.jvm.functions.Function2;

*/
/**
 * Created by JinJie on 2021/09/14
 *//*

public final class VideoClipActivity extends AppCompatActivity implements ClipContainer.Callback, View.OnClickListener {
    private static final float DEF_WIDTH = 630;
    private static final float DEF_HEIGHT = 1120;

    private int millsPerThumbnail = 1000;//每个缩略图的时间
    private int thumbnailCount;//缩略图的个数
    private long mediaDuration;
    private long startMillSec;
    private long endMillSec;

    private String finalVideoPath;
    private VideoPlayer videoPlayer;
    private VideoPlayTimeController videoPlayTimeController;
    //private DecimalFormat secFormat = new DecimalFormat("#0.0");
    private float width, height, x, y;

    private ThumbExoPlayerView thumbExoPlayerView;
    private SurfaceView surfaceView;
    private CropView cropView;
    private ClipContainer clipContainer;
    private TextView toast_msg_tv;
    private TextView tv_location;
    private ZoomLayout zoomLayout;
    private float width2, height2, x2, y2;

    private int defWidth = 630;
    private int defHeight = 1120;
    private String mVideoRotation = "0";

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            VideoClipActivity.this.updatePlayPosition();
        }
    };

    public static void safeStart(Activity c, String video_path) {
        Intent intent = new Intent(c, VideoClipActivity.class);
        intent.putExtra("video_path", video_path);
        c.startActivityForResult(intent, 0x002);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        setContentView(R.layout.activity_video_clip);
        finalVideoPath = getIntent().getStringExtra("video_path");
        initView();
        initSetParam();
        initPlayer();
    }

    private void initView() {
        thumbExoPlayerView = findViewById(R.id.player_view_exo_thumbnail);
        surfaceView = findViewById(R.id.player_view_mp);
        clipContainer = findViewById(R.id.clipContainer);
        toast_msg_tv = findViewById(R.id.toast_msg_tv);
        tv_location = findViewById(R.id.tv_location);
        cropView = findViewById(R.id.cropView);
        zoomLayout = findViewById(R.id.zoomLayout);
        zoomLayout.setMinScale(1.0f);
        zoomLayout.setMaxScale(6.0f);

        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.iv_crop).setOnClickListener(this);
        findViewById(R.id.iv_square).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
    }

    private void initSetParam() {
        final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(getApplication(), Uri.parse(finalVideoPath));
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        float videoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        float videoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        Logger.d("--->rotation:" + mVideoRotation);
        Logger.d("--->srcWidth:" + videoWidth);
        Logger.d("--->srcHeight:" + videoHeight);

        defHeight = (int) (defWidth / (videoWidth / videoHeight));
        if (mVideoRotation.equals("270") || mVideoRotation.equals("90")) {
            swap(defWidth, defHeight);
        }
        final float s = DEF_WIDTH / defWidth;
        defWidth = (int) (defWidth * s);
        //defHeight = (int) (defHeight * s);
        defHeight = (defHeight * s) > DEF_HEIGHT ? (int) DEF_HEIGHT : (int) (defHeight * s);
        Logger.d("--->defWidth:" + defWidth);
        Logger.d("--->defHeight:" + defHeight);

        if (mVideoRotation.equals("0") && videoWidth > videoHeight) {//本地视频竖屏
            Logger.e("initSetParam: 0");
            //surfaceView.setRotation(90);
            setPortraitParam();
        } else if (mVideoRotation.equals("90") && videoWidth > videoHeight) {//本地视频竖屏
            setPortraitParam();
        } else if (mVideoRotation.equals("0") && videoWidth < videoHeight) {//本地视频竖屏
            setPortraitParam();
        } else if (mVideoRotation.equals("180") && videoWidth > videoHeight) {//本地视频竖屏
            Logger.e("initSetParam: 180");
            setPortraitParam();
        } else {
            setPortraitParam();
        }
    }

    private void setPortraitParam() {
        ViewGroup.LayoutParams layoutParams1 = surfaceView.getLayoutParams();
        layoutParams1.width = defWidth;
        layoutParams1.height = defHeight;
        surfaceView.setLayoutParams(layoutParams1);
        surfaceView.requestLayout();
        ///createNewVideo(layoutParams1.width, layoutParams1.height);
    }

    private void setLandScapeParam() {
        ViewGroup.LayoutParams layoutParams1 = surfaceView.getLayoutParams();
        layoutParams1.width = defHeight;
        layoutParams1.height = defWidth;
        surfaceView.setLayoutParams(layoutParams1);
        surfaceView.requestLayout();
    }

    private final void initPlayer() {
        this.mediaDuration = Util_extKt.getVideoDuration(getApplication(), finalVideoPath);
        this.endMillSec = this.mediaDuration > Config.Companion.getMaxSelection() ? Config.Companion.getMaxSelection() : this.mediaDuration;
        this.millsPerThumbnail = this.mediaDuration > Config.Companion.getMaxSelection() ? Config.Companion.getMAX_FRAME_INTERVAL_MS() : (int) (this.mediaDuration / Config.Companion.getDEFAULT_FRAME_COUNT());
        this.thumbnailCount = this.mediaDuration > Config.Companion.getMaxSelection() ? (int) Math.ceil(this.mediaDuration * 1.0F / this.millsPerThumbnail) : Config.Companion.getDEFAULT_FRAME_COUNT();

        clipContainer.initRecyclerList(thumbnailCount);
        videoPlayer = new VideoPlayerOfMediaPlayer(surfaceView);
        videoPlayer.initPlayer(getApplication(), finalVideoPath);
        videoPlayTimeController = new VideoPlayTimeController(videoPlayer);
        thumbExoPlayerView.setDataSource(finalVideoPath, millsPerThumbnail, thumbnailCount, new Function2() {
            public Object invoke(Object var1, Object var2) {
                return this.invoke((Bitmap) var1, (int) var2);
            }

            public final boolean invoke(final Bitmap bitmap, final int index) {
                return clipContainer.post(() -> clipContainer.addThumbnail(index, bitmap));
            }
        });

        clipContainer.setCallback(this);
        clipContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                clipContainer.updateInfo(mediaDuration, clipContainer.getList().size());
                clipContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final String toast_msg = "[" + DateTimeUtil.formatTime(startMillSec) + " - " + DateTimeUtil.formatTime(mediaDuration) + "]      " +
                DateTimeUtil.formatTime(startMillSec) + " / " + DateTimeUtil.formatTime(mediaDuration);
        toast_msg_tv.setText(toast_msg);
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        final String[] info = new String[]{String.valueOf(params.width), String.valueOf(params.height)};
        cropView.addOnCropListener((float width, float height, float x, float y) -> {
            x2 = x;
            y2 = y;
            width2 = width;
            height2 = height;

            */
/*this.width = (width * p1);
            this.height = (height * p2);
            this.x = (x * p1 + Math.abs(rectF.left) * p1);
            this.y = (y * p2 + Math.abs(rectF.top) * p2);*//*


            final RectF rectF = zoomLayout.getDrawRect();
            float p1 = Float.parseFloat(info[0]) / rectF.width();
            float p2 = Float.parseFloat(info[1]) / rectF.height();

            float dx = x * p1 - rectF.left * p1;
            float dy = y * p2 - rectF.top * p2;

            if (dx >= 0) {
                this.x = dx;
                this.width = (width * p1);
            } else {
                this.x = 0;
                this.width = ((width * p1) + dx) > 0 ? ((width * p1) + dx) : 0;
            }

            if (dy >= 0) {
                this.y = dy;
                this.height = (height * p2);
            } else {
                this.y = 0;
                this.height = ((height * p2) + dy) > 0 ? ((height * p2) + dy) : 0;
            }

            //修正右边
            float dx1 = defWidth - (this.x + this.width);
            float dy2 = defHeight - (this.y + this.height);
            if (dx1 < 0) {
                this.width = (this.width + dx1) > 0 ? (this.width + dx1) : 0;
            }
            if (dy2 < 0) {
                this.height = (this.height + dy2) > 0 ? (this.height + dy2) : 0;
            }
            clipContainer.post(() -> {
                tv_location.setText("[" + (int) (this.x) + ", " + (int) (this.y) + ", " + (int) (this.width) + " x " + (int) (this.height) + "]" + "    " +
                        info[0] + " x " + info[1]);
            });
        });

        zoomLayout.addOnZoomListener(new ZoomLayout.OnZoomListener() {
            @Override
            public void onZoomBegin(ZoomLayout view, float scale) {
                //Logger.d("onZoomBegin:" + scale);
            }

            @Override
            public void onZoom(ZoomLayout view, float scale) {
                if (1.0f <= scale && scale <= 6.0f) {
                    */
/*width = (width2 * p1);
                    height = (height2 * p2);
                    x = (x2 * p1 + Math.abs(rectF.left) * p1);
                    y = (y2 * p2 + Math.abs(rectF.top) * p2);*//*


                    final RectF rectF = zoomLayout.getDrawRect();
                    float p1 = Float.parseFloat(info[0]) / rectF.width();
                    float p2 = Float.parseFloat(info[1]) / rectF.height();

                    float dx = x2 * p1 - rectF.left * p1;
                    float dy = y2 * p2 - rectF.top * p2;

                    if (dx >= 0) {
                        x = dx;
                        width = (width2 * p1);
                    } else {
                        x = 0;
                        width = ((width2 * p1) + dx) > 0 ? ((width2 * p1) + dx) : 0;
                    }

                    if (dy >= 0) {
                        y = dy;
                        height = (height2 * p2);
                    } else {
                        y = 0;
                        height = ((height2 * p2) + dy) > 0 ? ((height2 * p2) + dy) : 0;
                    }

                    //修正右边
                    float dx1 = defWidth - (x + width);
                    float dy2 = defHeight - (y + height);
                    if (dx1 < 0) {
                        width = (width + dx1) > 0 ? (width + dx1) : 0;
                    }
                    if (dy2 < 0) {
                        height = (height + dy2) > 0 ? (height + dy2) : 0;
                    }
                    clipContainer.post(() -> {
                        tv_location.setText("[" + (int) (x) + ", " + (int) (y) + ", " + (int) (width) + " x " + (int) (height) + "]" + "    " +
                                info[0] + " x " + info[1]);
                    });
                }
            }

            @Override
            public void onZoomEnd(ZoomLayout view, float scale) {
                */
/*if (scale <= 1.0f) {
                    cx = cy = 0;
                }*//*

            }
        });

        zoomLayout.addOnPanListener(new ZoomLayout.OnPanListener() {
            @Override
            public void onPanBegin(ZoomLayout view) {

            }

            @Override
            public void onPan(ZoomLayout view) {
                */
/*width = (width2 * p1);
                height = (height2 * p2);
                x = (x2 * p1 + Math.abs(rectF.left) * p1);
                y = (y2 * p2 + Math.abs(rectF.top) * p2);*//*


                final RectF rectF = zoomLayout.getDrawRect();
                float p1 = Float.parseFloat(info[0]) / rectF.width();
                float p2 = Float.parseFloat(info[1]) / rectF.height();

                float dx = x2 * p1 - rectF.left * p1;
                float dy = y2 * p2 - rectF.top * p2;

                if (dx >= 0) {
                    x = dx;
                    width = (width2 * p1);
                } else {
                    x = 0;
                    width = ((width2 * p1) + dx) > 0 ? ((width2 * p1) + dx) : 0;
                }

                if (dy >= 0) {
                    y = dy;
                    height = (height2 * p2);
                } else {
                    y = 0;
                    height = ((height2 * p2) + dy) > 0 ? ((height2 * p2) + dy) : 0;
                }

                //修正右边
                float dx1 = defWidth - (x + width);
                float dy2 = defHeight - (y + height);
                if (dx1 < 0) {
                    width = (width + dx1) > 0 ? (width + dx1) : 0;
                }
                if (dy2 < 0) {
                    height = (height + dy2) > 0 ? (height + dy2) : 0;
                }
                clipContainer.post(() -> {
                    tv_location.setText("[" + (int) (x) + ", " + (int) (y) + ", " + (int) (width) + " x " + (int) (height) + "]" + "    " +
                            info[0] + " x " + info[1]);
                });
            }

            @Override
            public void onPanEnd(ZoomLayout view) {

            }
        });

        zoomLayout.addOnTapListener(new ZoomLayout.OnTapListener() {
            public boolean onTap(ZoomLayout view, ZoomLayout.TapInfo info) {
                if (videoPlayer.isPlaying()) {
                    pausePlayer();
                } else {
                    startPlayer();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_cancel:
                cropView.setAlpha(0);
                cropView.setCropEnabled(false);
                findViewById(R.id.iv_square).setVisibility(View.GONE);
                findViewById(R.id.tv_cancel).setVisibility(View.GONE);
                findViewById(R.id.iv_crop).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_close).setVisibility(View.VISIBLE);
                break;
            case R.id.iv_crop:
                cropView.setAlpha(1);
                cropView.setCropEnabled(true);
                findViewById(R.id.iv_square).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);
                findViewById(R.id.iv_crop).setVisibility(View.GONE);
                findViewById(R.id.iv_close).setVisibility(View.GONE);
                break;
            case R.id.iv_square:
                cropView.setCropMode(CropView.CropModeEnum.FREE);
                break;
            case R.id.confirm:
                try {
                    execVideo();
                } catch (Throwable e) {
                    ToastUtil.showToast(VideoClipActivity.this, e.getMessage());
                    Logger.d("--->" + e.toString());
                }
                break;
        }
    }

    private void createNewVideo(final int w, final int h) {
        EpVideo epVideo = new EpVideo(String.valueOf(finalVideoPath));
        final String outPath = String.format("%s%s", FileUtil.PATH, "temp.mp4");
        EpEditor.OutputOption option = new EpEditor.OutputOption(outPath);
        option.setWidth(w);
        option.setHeight(h);
        //String cmd = "-y -i " + finalVideoPath + " -s wxh -vcodec libx264 -preset fast -b 80000 -r 25 " + outPath;
        EpEditor.exec(epVideo, option, new OnEditorListener() {
            public void onSuccess() {}
            public void onFailure() {}
            public void onProgress(float progress) {}
        });
    }

    private final void swap(int w, int h) {
        w = w ^ h;
        h = w ^ h;
        w = w ^ h;
        defWidth = w;
        defHeight = h;
    }

    */
/**
     * 开始编辑
     *//*

    private void execVideo() {
        showDialog();
        EpVideo epVideo = new EpVideo(finalVideoPath);
        epVideo.clip(startMillSec / 1000f, (endMillSec - startMillSec) / 1000f);
        if (cropView.getAlpha() == 1) {
            boolean state = (width == 0 || height == 0 || x >= defWidth || y >= defHeight);
            if (!state) {
                final String[] info = ViewUtil.getMediaInfo(finalVideoPath);
                float srcW = Float.parseFloat(info[0]);
                float srcH = Float.parseFloat(info[1]);

                if (mVideoRotation.equals("270") || mVideoRotation.equals("90")) {//倒方向
                    swap(defWidth, defHeight);
                }

                x = (srcW / defWidth) * x;
                y = (srcH / defHeight) * y;
                if ((srcW / srcH) == (defWidth * 1.0f / defHeight)) {
                    width = (srcW / defWidth) * width;
                    height = (srcH / defHeight) * height;
                } else {
                    //final float p = Math.min((srcW / defWidth), (srcH / defHeight));
                    final float p = ((srcW / defWidth) + (srcH / defHeight)) / 2;
                    width = p * width;
                    height = p * height;
                }
                epVideo.crop(width, height, x, y);
            }
        }
        final String outPath = String.format("%s%s", FileUtil.PATH, "out.mp4");
        EpEditor.exec(epVideo, new EpEditor.OutputOption(outPath), new OnEditorListener() {
            @Override
            public void onSuccess() {
                BackgroundTasks.getInstance().runOnUiThread(() -> {
                    hideDialog();
                    final Intent intent = new Intent();
                    intent.putExtra("path", outPath);
                    setResult(RESULT_OK, intent);
                    finish();
                });
            }

            @Override
            public void onFailure() {
                BackgroundTasks.getInstance().runOnUiThread(() -> {
                    hideDialog();
                });
            }

            @Override
            public void onProgress(final float progress) {
                BackgroundTasks.getInstance().runOnUiThread(() -> {
                    if (progress < 1.0f) {
                        circularProgressBar.setProgress(progress * 100);
                    }
                });
            }
        });
    }

    private Dialog mDialog;
    private CircularProgressBar circularProgressBar;
    private void showDialog() {
        final View adView = LayoutInflater.from(this).inflate(R.layout.dialog_upload_video, null);
        adView.findViewById(R.id.loading).setVisibility(View.GONE);
        circularProgressBar = adView.findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgress(0);
        circularProgressBar.setProgressMax(100);
        mDialog = new Dialog(this, R.style.AlertDialog);
        mDialog.setCancelable(false);
        final Window window = mDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0f);
        mDialog.setContentView(adView);
        mDialog.show();
    }

    private void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            FileUtil.deleteAll(new File(FileUtil.PATH));
        }
    }

    @Override
    public void onSelectionChang(int totalCount, long _startMillSec, long _endMillSec, boolean finished, boolean isRight) {
        if (!finished) {
            String toast_msg = "[" + DateTimeUtil.formatTime(_startMillSec) + " - " + DateTimeUtil.formatTime(_endMillSec) + "]      " +
                    DateTimeUtil.formatTime(isRight ? _endMillSec : _startMillSec) + " / " + DateTimeUtil.formatTime(mediaDuration);
            toast_msg_tv.setText(toast_msg);
        }

        this.startMillSec = _startMillSec;
        this.endMillSec = _endMillSec;
        this.adjustSelection();
        this.pausePlayer();
        this.seekToPosition(isRight ? _endMillSec : _startMillSec);
    }

    @Override
    public void onPreviewChang(long previewMillSec, boolean finished) {
        if (!finished) {
            String toast_msg = "[" + DateTimeUtil.formatTime(startMillSec) + " - " + DateTimeUtil.formatTime(endMillSec) + "]      " +
                    DateTimeUtil.formatTime(previewMillSec) + " / " + DateTimeUtil.formatTime(mediaDuration);
            toast_msg_tv.setText(toast_msg);
        }

        this.pausePlayer();
        this.seekToPosition(previewMillSec);
    }

    @Deprecated
    private final void adjustSelection() {
        if (this.startMillSec < 0L) {
            this.startMillSec = 0L;
        }
        if (this.endMillSec > this.mediaDuration) {
            this.endMillSec = this.mediaDuration;
        }
        if (this.startMillSec + Config.Companion.getMinSelection() > this.endMillSec && this.startMillSec > 0L) {
            this.startMillSec = Math.max(0L, this.endMillSec - Config.Companion.getMinSelection());
        }
        if (this.startMillSec + Config.Companion.getMinSelection() > this.endMillSec && this.endMillSec < this.mediaDuration) {
            this.endMillSec = Math.min(this.startMillSec + Config.Companion.getMinSelection(), this.mediaDuration);
        }
    }

    public final void updatePlayPosition() {
        final int position = videoPlayer != null ? videoPlayer.getPlayerCurrentPosition() : 0;
        if (position > this.endMillSec) {
            this.seekToPosition(0);
        } else {
            clipContainer.setProgress(position, 0);
        }
        this.handler.sendEmptyMessageDelayed(Config.Companion.getMSG_UPDATE(), 20L);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    @Override
    protected void onDestroy() {
        this.pausePlayer();
        this.releasePlayer();
        this.videoPlayTimeController.stop();
        this.handler.removeCallbacksAndMessages(null);
        hideDialog();
        super.onDestroy();
    }

    private final void seekToPosition(long millSec) {
        if (videoPlayer != null) {
            clipContainer.setProgress(millSec, 0);
            videoPlayer.seekToPosition(millSec);
        }
    }

    private final void startPlayer() {
        if (videoPlayer != null) {
            videoPlayer.startPlayer();
            videoPlayTimeController.setPlayTimeRange(startMillSec, endMillSec);
            VideoClipActivity.this.updatePlayPosition();
            videoPlayTimeController.start();
        }
    }

    private final void pausePlayer() {
        if (videoPlayer != null) {
            this.handler.removeCallbacksAndMessages(null);
            if (videoPlayer.isPlaying()) {
                videoPlayer.pausePlayer();
                videoPlayTimeController.stop();
            }
        }
    }

    private final void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.releasePlayer();
        }
    }
}*/
