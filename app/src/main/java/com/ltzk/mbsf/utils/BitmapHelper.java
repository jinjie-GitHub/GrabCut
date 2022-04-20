package com.ltzk.mbsf.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public enum BitmapHelper {
    INSTANCE;

    public static final int THRESH_NORMAL = 95;
    public static final int THRESH_HIGH = 160;
    public int mThresh = THRESH_HIGH;
    final JNIUtils mUtils = new JNIUtils();

    public interface Callback {
        void callback(Bitmap bitmap);
    }

    private Callback mCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                if (mCallback != null) {
                    mCallback.callback(bitmap);
                }
            }
        }
    };

    public void setThresh(int thresh) {
        mThresh = thresh;
    }

    public void onReleased() {
        mCallback = null;
    }

    public void processForeground(Bitmap bmSrc, int fore, Callback callback) {
        processBinary(bmSrc, callback, fore, Color.TRANSPARENT);
    }

    public void processBackground(Bitmap bmSrc, int back, Callback callback) {
        processBinary(bmSrc, callback, Color.BLACK, back);
    }

    private void processBinary(Bitmap bmSrc, Callback callback, final int fore, final int color) {
        this.mCallback = callback;
        final Bitmap binaryBitmap = bmSrc.copy(Bitmap.Config.ARGB_8888, true);
        final Bitmap revertBitmap = bmSrc.copy(Bitmap.Config.ARGB_8888, true);
        new Thread(() -> {
            final Bitmap bmp = grayToBinaryNew(binaryBitmap, revertBitmap, fore, color);
            Message msg = Message.obtain(mHandler, 0, bmp);
            msg.sendToTarget();
        }).start();
    }

    public Bitmap invertBitmap(Bitmap bitmap) {
        int sWidth = bitmap.getWidth();
        int sHeight = bitmap.getHeight();
        int[] sPixels = new int[sWidth * sHeight];
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);

        int sIndex = 0;
        for (int sRow = 0; sRow < sHeight; sRow++) {
            sIndex = sRow * sWidth;
            for (int sCol = 0; sCol < sWidth; sCol++) {
                int sPixel = sPixels[sIndex];
                int sA = (sPixel >> 24) & 0xff;
                int sR = (sPixel >> 16) & 0xff;
                int sG = (sPixel >> 8) & 0xff;
                int sB = sPixel & 0xff;

                sR = 255 - sR;
                sG = 255 - sG;
                sB = 255 - sB;

                sPixel = ((sA & 0xff) << 24 | (sR & 0xff) << 16 | (sG & 0xff) << 8 | sB & 0xff);
                sPixels[sIndex] = sPixel;
                sIndex++;
            }
        }
        bitmap.setPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);
        return bitmap;
    }

    private boolean isBlackBack(final Bitmap bmSrc) {
        int n1 = 0, n2 = 0;
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        int[] pixels = new int[width * height];
        bmSrc.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            final int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int gray = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
            if (gray <= mThresh) {
                n1++;
            } else {
                n2++;
            }
        }
        return n1 > n2;
    }

    public Bitmap bitmap2Gray(Bitmap bitmap) {
        final Bitmap bmSrc = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (isBlackBack(bitmap)) {
            invertBitmap(bmSrc);
        }
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        Bitmap bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        if (bmSrc != null) {
            bmSrc.recycle();
        }
        return bmpGray;
    }

    /**
     * 灰度图
     */
    public Bitmap bitmap2Gray2(Bitmap bmSrc) {
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        Bitmap bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        return bmpGray;
    }

    /**
     * 二值化图片
     */
    private Bitmap grayToBinaryNew(final Bitmap binaryBitmap, final Bitmap revertBitmap, final int fore, final int back) {
        int n1 = 0, n2 = 0;
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();

        int[] pixels1 = new int[width * height];
        int[] pixels2 = new int[width * height];
        binaryBitmap.getPixels(pixels1, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            final int color = pixels1[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            int gray = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
            if (gray <= mThresh) {//黑底
                pixels1[i] = fore;
                pixels2[i] = back;
                n1++;
            } else {//白底
                pixels1[i] = back;
                pixels2[i] = fore;
                n2++;
            }
        }
        binaryBitmap.setPixels(pixels1, 0, width, 0, 0, width, height);
        revertBitmap.setPixels(pixels2, 0, width, 0, 0, width, height);
        return n1 < n2 ? binaryBitmap : revertBitmap;
    }

    public Bitmap grayToBinaryNew(final Bitmap binaryBitmap) {
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();

        int[] pixels1 = new int[width * height];
        binaryBitmap.getPixels(pixels1, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            final int color = pixels1[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            int gray = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
            if (gray <= mThresh) {//背景
                pixels1[i] = Color.TRANSPARENT;
            } else {//TODO 不处理
               // pixels1[i] = Color.TRANSPARENT;
            }
        }
        binaryBitmap.setPixels(pixels1, 0, width, 0, 0, width, height);
        return binaryBitmap;
    }

    @Deprecated
    private Bitmap grayToBinary(final Bitmap binaryBitmap, final Bitmap revertBitmap, final int fore, final int back) {
        int n1 = 0, n2 = 0;
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int col = binaryBitmap.getPixel(x, y);
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                if (gray <= mThresh) {//黑底
                    binaryBitmap.setPixel(x, y, fore);
                    revertBitmap.setPixel(x, y, back);
                    n1++;
                } else {//白底
                    binaryBitmap.setPixel(x, y, back);
                    revertBitmap.setPixel(x, y, fore);
                    n2++;
                }
            }
        }
        return n1 < n2 ? binaryBitmap : revertBitmap;
    }

    @Deprecated
    private Bitmap gray2Binary(final Bitmap binaryBitmap, final int fore, final int color) {
        int n1 = 0, n2 = 0;
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int col = binaryBitmap.getPixel(x, y);
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                if (gray <= mThresh) {//黑色
                    binaryBitmap.setPixel(x, y, fore);
                    n1++;
                } else {//透明
                    binaryBitmap.setPixel(x, y, color);
                    n2++;
                }
            }
        }
        return n1 < n2 ? binaryBitmap : null;
    }

    @Deprecated
    private Bitmap revertBinary(final Bitmap revertBitmap, final int fore, final int color) {
        int n1 = 0, n2 = 0;
        int width = revertBitmap.getWidth();
        int height = revertBitmap.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int col = revertBitmap.getPixel(x, y);
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                if (gray <= mThresh) {
                    revertBitmap.setPixel(x, y, color);
                    n1++;
                } else {
                    revertBitmap.setPixel(x, y, fore);
                    n2++;
                }
            }
        }
        return n1 < n2 ? null : revertBitmap;
    }
}