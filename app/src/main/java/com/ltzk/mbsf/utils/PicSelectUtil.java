package com.ltzk.mbsf.utils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;


import com.ltzk.mbsf.MainApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.core.content.FileProvider;

public class PicSelectUtil {

    public final static int TAKE_IMAGE_REQUEST_CODE = 1001;
    public final static int CROP_IMAGE_REQUEST_CODE2 = 1012;
    public final static String IMAGE_STORE_FILE_NAME = "IMG_%s.jpg";

    public static File mImageStoreDir;
    /**
     * 设置路径
     */
    public static File getmageStoreDir() {
        if(mImageStoreDir == null){
            mImageStoreDir = new File(Environment.getExternalStorageDirectory(), "/DCIM" + File.separator + "fangtian" + File.separator);
        }
        return mImageStoreDir;
    }

    public static void cropPic(Activity context, String path, Uri uri){

        File file = new File(path);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainApplication.getInstance(), MainApplication.getInstance().getPackageName()+".fileProvider", file);
            intent.setDataAndType(contentUri, "image/*");

        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 29);
        intent.putExtra("aspectY", 30);
        intent.putExtra("outputX", 145);
        intent.putExtra("outputY", 150);
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        context.startActivityForResult(intent, PicSelectUtil.CROP_IMAGE_REQUEST_CODE2);
    }

    public static String encodeImage(Bitmap bitmap, int sizeLimit){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //读取图片到ByteArrayOutputStream
        int quality = 40;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos); //参数如果为100那么就不压缩
        while (baos.toByteArray().length / 1024 > sizeLimit) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }

        byte[] bytes = baos.toByteArray();
        String strbm = Base64.encodeToString(bytes, Base64.DEFAULT);
        return strbm;
    }
}
