package com.ltzk.mbsf.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {

    /**
     * 利用正确视角（和预览视角相同）的裁剪图片
     *
     * @param bitmap    原始图片
     * @param preWidth  预览视图宽
     * @param preHeight 预览视图高
     * @param frameRect 裁剪框
     **/
    public static Bitmap getCropPicture(Bitmap bitmap, float preWidth, float preHeight, RectF frameRect) {

        //原始照片的宽高
        float picWidth = bitmap.getWidth();
        float picHeight = bitmap.getHeight();

        //预览界面和照片的比例
        float preRW = picWidth / preWidth;
        float preRH = picHeight / preHeight;

        //裁剪框的位置和宽高
        float frameLeft = frameRect.left;
        float frameTop = frameRect.top;
        float frameWidth = frameRect.width();
        float frameHeight = frameRect.height();

        int cropLeft = (int) (frameLeft * preRW);
        int cropTop = (int) (frameTop * preRH);
        int cropWidth = (int) (frameWidth * preRW);
        int cropHeight = (int) (frameHeight * preRH);

        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight);
        return cropBitmap;
    }

    /**
     * 获取圆形图片
     * 只有图片是正方形的才能完美匹配
     * 如果图片尺寸不是1:1，最终的截图会存在在高度或者宽度上部分截取不到的情况
     * 这是因为圆形图片需要裁剪对应的矩形，半径只能以矩形最小的边
     */
    public static Bitmap getCircularBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_4444);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        int halfWidth = square.getWidth() / 2;
        int halfHeight = square.getHeight() / 2;

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawCircle(halfWidth, halfHeight, Math.min(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }

    /**
     * 获取椭圆图片
     */
    public static Bitmap getOvalBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_4444);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawOval(new RectF(rect.left, rect.top, rect.right, rect.bottom), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }

    /**
     * 保存图片到相册
     */
    public static void savePicToSdcard(Context ctx, Bitmap bitmap, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, fileName + ".jpg");
        if (file != null && file.exists()) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Throwable e) {
            Logger.e(e.toString());
        }

        PicUtil.sendMediaFile(ctx.getApplicationContext(), file);

        /*try {
            MediaStore.Images.Media.insertImage(ctx.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "")));
        }*/
    }

    public static void savePicToSdcard2(Context ctx, byte[] bytes, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, fileName + ".jpg");
        if (file != null && file.exists()) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            bos.flush();
            closeSilently(bos);
        } catch (Throwable e) {
            return;
        }

        PicUtil.sendMediaFile(ctx.getApplicationContext(), file);
    }

    /**
     * View转为Bitmap
     */
    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) {
            return null;
        }

        //view.clearFocus();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            view.draw(canvas);
        }
        return bitmap;
    }

    public static Bitmap convertViewToBitmap(int width, int height, View view) {
        if (view == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap getWhiteBgBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(square, new Matrix(), paint);
        return output;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Throwable e) {
        }
    }
}