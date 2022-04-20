package com.ltzk.mbsf.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 描述：
 * 作者： on 2018/6/25 22:13
 * 邮箱：499629556@qq.com
 */

public class PicUtil extends AsyncTask<Bitmap,Integer,String> {
    private Context context;
    SavePicListener savePicListener;
    public PicUtil(Context context, SavePicListener savePicListener) {
        this.context = context;
        this.savePicListener = savePicListener;
    }

    /**
     * 运行在UI线程中，在调用doInBackground()之前执行
     */
    @Override
    protected void onPreExecute() {
    }

    /**
     * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
     */


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     *         <code>false</code> otherwise
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }
 
            /* 如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            */

            FileInputStream fileInputStream = new FileInputStream(oldPath);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通知相册更新
     * @param content
     * @param file
     */
    public static void sendMediaFile(Context content,File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        content.sendBroadcast(intent);
    }
    
    @Override
    protected String doInBackground(Bitmap... params) {
        try{
            File file = newPicFile();

            FileOutputStream os = new FileOutputStream(file);
            params[0].compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static File newPicFile(){
        try{
            //系统相册目录
            String galleryPath = Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "YGSF" + File.separator;
            //输出到sd卡
            File dirFirstFolder = new File(galleryPath);
            if(!dirFirstFolder.exists()) { //如果该文件夹不存在，则进行创建
                dirFirstFolder.mkdirs();//创建文件夹
            }
            String fileName = System.currentTimeMillis()+ ".png";
            File file = new File(galleryPath,fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 运行在ui线程中，在doInBackground()执行完毕后执行
     */
    @Override
    protected void onPostExecute(String path) {
        if(path==null){
            savePicListener.fail();
        }else {
            savePicListener.sussces(path);
        }
    }


    @Override
    protected void onCancelled() {
        savePicListener.fail();
        super.onCancelled();
    }

    /**
     * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    public interface SavePicListener{
        void sussces(String path);
        void fail();
    }
    
    
}
