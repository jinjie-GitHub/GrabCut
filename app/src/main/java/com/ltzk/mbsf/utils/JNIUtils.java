package com.ltzk.mbsf.utils;

public class JNIUtils {
    public native int[] gray(int[] buf, int w, int h);
    public native int[] binarization(int[] buf, int w, int h);

    static {
        try {
            //System.loadLibrary("native-lib");
        } catch (Throwable t) {}
    }
}