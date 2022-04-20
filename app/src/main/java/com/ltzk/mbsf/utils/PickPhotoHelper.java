package com.ltzk.mbsf.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by JinJie on 2020/11/19
 */
public class PickPhotoHelper {
    /**
     * 质量压缩
     */
    public static Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length / 1024 > sizeLimit) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }
        final Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
        return newBitmap;
    }

    /**
     * 宽高压缩
     */
    public static Bitmap decodeSampledBitmapFromResource(File f, int reqWidth, int reqHeight) throws Exception {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 先将inJustDecodeBounds设置为true不会申请内存去创建Bitmap，返回的是一个空的Bitmap，但是可以获取
        //图片的一些属性，例如图片宽高，图片类型等等。
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 加载压缩版图片
        options.inJustDecodeBounds = false;
        // 根据具体情况选择具体的解码方法
        return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 计算inSampleSize值
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 宽高压缩-createScaledBitmap
     */
    public static Bitmap compressBitmap(Bitmap src) {
        return Bitmap.createScaledBitmap(src, 150, 150, true);
    }

    /**
     * 宽高压缩-setScale
     */
    public static Bitmap compressBitmap(Bitmap src, float scale) {
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    public static Bitmap compressBitmap(Bitmap src, float sx, float sy) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}