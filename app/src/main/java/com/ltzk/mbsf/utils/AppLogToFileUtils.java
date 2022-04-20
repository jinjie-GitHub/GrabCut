package com.ltzk.mbsf.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JinJie on 2020/08/28
 */
public class AppLogToFileUtils {

    private static final String LOG_DIR_NAME = "YGSF/Log";
    private static final String FILE_NAME = "/logs.csv";
    private static ThreadPoolManager sLogService = ThreadPoolManager.newInstance();

    private static Context sContext;
    private static AppLogToFileUtils sInstance;
    private static File sLogFile;
    private static SimpleDateFormat sLogSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static long LOG_MAX_SIZE = 5 * 1024 * 1024; //5mb
    private boolean mUseSdCard = true;

    private AppLogToFileUtils() {

    }

    /**
     * init
     *
     * @param context
     */
    public static void init(Context context) {
        if (null == sContext || null == sInstance || null == sLogFile || !sLogFile.exists()) {
            sContext = context.getApplicationContext();
            sInstance = getInstance();
            sLogService.addExecuteTask(() -> {
                sLogFile = sInstance.getLogFile();
                if (sLogFile != null) {
                    // 获取当前日志文件大小
                    long logFileSize = getLogFileSize(sLogFile);
                    // 若日志文件超出了预设大小，则重置日志文件
                    if (LOG_MAX_SIZE < logFileSize) {
                        sInstance.resetLogFile();
                    }
                }
            });
        }
    }

    public static AppLogToFileUtils getInstance() {
        if (sInstance == null) {
            synchronized (AppLogToFileUtils.class) {
                if (sInstance == null) {
                    sInstance = new AppLogToFileUtils();
                }
            }
        }
        return sInstance;
    }

    public static void reset() {
        sContext = null;
        sInstance = null;
        sLogFile = null;
    }

    /**
     * file length
     *
     * @param file 文件
     * @return
     */
    public static long getLogFileSize(File file) {
        long size = 0;
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * log size
     *
     * @return
     */
    public static long getLocalLogFileSize() {
        return getLogFileSize(sLogFile);
    }

    private long readSDCardSpace() {
        long sdCardSize = 0;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            sdCardSize = availCount * blockSize;
        }
        return sdCardSize;
    }

    private long readSystemSpace() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        long systemSpaceSize = availCount * blockSize / 1024;
        return systemSpaceSize;
    }

    public void setUseSdCard(boolean useSdCard) {
        this.mUseSdCard = useSdCard;
    }

    /**
     * set log file
     */
    public void resetLogFile() {
        // 创建log.csv，若存在则删除
        if (!sLogFile.getParentFile().exists()) {
            sLogFile.getParentFile().mkdirs();
        }
        File logFile = new File(sLogFile.getParent() + FILE_NAME);
        if (logFile.exists()) {
            logFile.delete();
        }
        // 新建日志文件
        createNewFile(logFile);
    }

    public void deleteLogFile() {
        // 创建log.csv，若存在则删除
        File logFile = new File(sLogFile.getParent() + FILE_NAME);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    public void deleteLogFileDir() {
        // 创建log.csv，若存在则删除
        deleteLogFile();
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + LOG_DIR_NAME);
        if (dir.exists()) {
            dir.delete();
        }
    }

    /**
     * get log file
     *
     * @return APP日志文件
     */
    private File getLogFile() {
        File file;
        boolean canStorage;
        // 判断是否有SD卡或者外部存储器
        if (mUseSdCard && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            canStorage = readSDCardSpace() > LOG_MAX_SIZE / 1024;
            // 有SD卡则使用SD - PS:没SD卡但是有外部存储器，会使用外部存储器
            file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + LOG_DIR_NAME);
        } else {
            // 没有SD卡或者外部存储器，使用内部存储器
            canStorage = readSystemSpace() > LOG_MAX_SIZE / 1024;
            file = new File(sContext.getFilesDir().getPath() + File.separator + LOG_DIR_NAME);
        }
        File logFile = null;
        // 若目录不存在则创建目录
        if (canStorage) {
            if (!file.exists()) {
                file.mkdirs();
            }
            logFile = new File(file.getPath() + FILE_NAME);
            if (!logFile.exists()) {
                createNewFile(logFile);
            }
        }
        return logFile;
    }

    public void createNewFile(File logFile) {
        try {
            logFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFunctionInfo(StackTraceElement[] ste) {
        String msg = null;
        if (ste == null) {
            msg = "[" + sLogSDF.format(new Date()) + "]";
        }
        return msg;
    }

    public synchronized void write(Object str) {
        // 判断是否初始化或者初始化是否成功
        if (null == sContext || null == sInstance || null == sLogFile) {
            return;
        }
        if (!sLogFile.exists()) {
            resetLogFile();
        }
        WriteCall writeCall = new WriteCall(str);
        sLogService.addExecuteTask(writeCall);
    }

    private static class WriteCall implements Runnable {

        private Object mStr;

        public WriteCall(Object mStr) {
            this.mStr = mStr;
        }

        @Override
        public void run() {
            if (sLogFile != null) {
                long logFileSize = getInstance().getLogFileSize(sLogFile);
                // 若日志文件超出了预设大小，则重置日志文件
                if (logFileSize > LOG_MAX_SIZE) {
                    getInstance().resetLogFile();
                }
                //输出流操作 输出日志信息至本地存储空间内
                PrintWriter pw;
                try {
                    pw = new PrintWriter(new FileWriter(sLogFile, true), true);
                    if (pw != null) {
                        if (mStr instanceof Throwable) {
                            //写入异常信息
                            printEx(pw);
                        } else {
                            //写入普通log
                            pw.println(getInstance().getFunctionInfo(null) + " - " + mStr.toString());
                        }
                        pw.println("------>end of log");
                        pw.println();
                        pw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private PrintWriter printEx(PrintWriter pw) {
            pw.println("crash_time：" + sLogSDF.format(new Date()));
            ((Throwable) mStr).printStackTrace(pw);
            return pw;
        }
    }
}