package com.ltzk.mbsf.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import com.ltzk.mbsf.R;
import org.json.JSONArray;

/**
 * Created by JinJie on 2020/7/9
 */
public class MySPUtils extends SPUtils {
    private static final String KEY_FIRST_LAUNCHER = "first_launcher";
    private static final String key_paint_alpha = "paint_alpha";
    private static final String key_paint_color = "paint_color";
    private static final String key_paint_type = "paint_type";
    private static final String key_compare_alpha = "compare_alpha";
    private static final String key_compare_color = "compare_color";
    private static final String key_printer_alpha = "printer_alpha";
    private static final String key_gravity_state = "gravity_state";
    private static final String key_writer_alpha = "writer_alpha";

    private static final String KEY_PRIVATE_POLICY = "private_policy";
    private static final String KEY_PRINTER_N1 = "printer_n1";
    private static final String KEY_PRINTER_N2 = "printer_n2";
    private static final String KEY_PRINTER_N3 = "printer_n3";
    private static final String KEY_PRINTER_N4 = "printer_n4";
    private static final String KEY_BORDER_LENGTH = "border_length";
    private static final String KEY_AD_SETTING = "ad_setting";
    private static final String KEY_BRUSH_COLOR = "brush_color";
    private static final String KEY_BRUSH_WIDTH = "brush_width";
    private static final String KEY_MATRIX_SX = "matrix_sx";
    private static final String KEY_MATRIX_SY = "matrix_sy";
    private static final String KEY_WRITE_MARK_LENGTH = "write_mark_length";
    private static final String KEY_JIZI_ZITIE_NAME = "jizi_zitie_name";
    private static final String KEY_JIZI_ZITIE_ID = "jizi_zitie_id";

    private static final String KEY_JIZI_ZITIE_AUTHOR = "jizi_zitie_author";
    private static final String KEY_JIZI_AUTO_AUTHOR = "jizi_auto_author";
    private static final String KEY_JIZI_AUTO_KIND = "jizi_auto_kind";
    private static final String KEY_JIZI_AUTO_FONT = "jizi_auto_font";
    private static final String KEY_VIVO_AD_SETTING = "vivo_ad_setting";

    private static final String KEY_GX_PAINT_ALPHA = "gx_paint_alpha";
    private static final String KEY_GX_PAINT_COLOR = "gx_paint_color";
    private static final String KEY_GX_PAINT_TYPE = "gx_paint_type";

    public static void setJiZiZiTieAuthor(Context ctx, String author) {
        put(ctx, KEY_JIZI_ZITIE_AUTHOR, author);
    }

    public static String getJiZiZiTieAuthor(Context ctx) {
        return (String) get(ctx, KEY_JIZI_ZITIE_AUTHOR, "");
    }

    public static void setJiZiAutoAuthor(Context ctx, String author) {
        put(ctx, KEY_JIZI_AUTO_AUTHOR, author);
    }

    public static String getJiZiAutoAuthor(Context ctx) {
        return (String) get(ctx, KEY_JIZI_AUTO_AUTHOR, "");
    }

    public static void setJiZiAutoKind(Context ctx, int checkId) {
        put(ctx, KEY_JIZI_AUTO_KIND, checkId);
    }

    public static int getJiZiAutoKind(Context ctx) {
        return (int) get(ctx, KEY_JIZI_AUTO_KIND, 0);
    }

    public static void setJiZiAutoFont(Context ctx, int checkId) {
        put(ctx, KEY_JIZI_AUTO_FONT, checkId);
    }

    public static int getJiZiAutoFont(Context ctx) {
        return (int) get(ctx, KEY_JIZI_AUTO_FONT, 0);
    }

    public static void setZiTieName(Context ctx, String name) {
        put(ctx, KEY_JIZI_ZITIE_NAME, name);
    }

    public static String getZiTieName(Context ctx) {
        return (String) get(ctx, KEY_JIZI_ZITIE_NAME, "");
    }

    public static void setZiTieId(Context ctx, String name) {
        put(ctx, KEY_JIZI_ZITIE_ID, name);
    }

    public static String getZiTieId(Context ctx) {
        return (String) get(ctx, KEY_JIZI_ZITIE_ID, "");
    }

    /**
     * 保存透明度
     *
     * @param ctx
     * @param progress
     */
    public static void putPaintAlpha(Context ctx, int progress) {
        put(ctx, key_paint_alpha, progress);
    }

    /**
     * 获取透明度
     *
     * @param ctx
     * @return
     */
    public static int getPaintAlpha(Context ctx) {
        return (int) get(ctx, key_paint_alpha, 100);
    }

    /**
     * 保存颜色
     *
     * @param ctx
     * @param color
     */
    public static void putPaintColor(Context ctx, int color) {
        put(ctx, key_paint_color, color);
    }

    /**
     * 获取颜色
     *
     * @param ctx
     * @return
     */
    public static int getPaintColor(Context ctx) {
        return (int) get(ctx, key_paint_color, ContextCompat.getColor(ctx, R.color.colorPrimary));
    }


    /**
     * 保存样式
     *
     * @param ctx
     * @param type
     */
    public static void putPaintType(Context ctx, int type) {
        put(ctx, key_paint_type, type);
    }

    /**
     * 获取样式
     *
     * @param ctx
     * @return
     */
    public static int getPaintType(Context ctx) {
        return (int) get(ctx, key_paint_type, 6);
    }


    public static void putCompAlpha(Context ctx, int progress) {
        put(ctx, key_compare_alpha, progress);
    }

    public static int getCompAlpha(Context ctx) {
        return (int) get(ctx, key_compare_alpha, 100);
    }

    public static void putCompColor(Context ctx, int color) {
        put(ctx, key_compare_color, color);
    }

    public static int getCompColor(Context ctx) {
        return (int) get(ctx, key_compare_color, ContextCompat.getColor(ctx, R.color.colorPrimary));
    }

    public static void putPrinterAlpha(Context ctx, int progress) {
        put(ctx, key_printer_alpha, progress);
    }

    public static int getPrinterAlpha(Context ctx) {
        return (int) get(ctx, key_printer_alpha, 100);
    }

    public static void putWriterAlpha(Context ctx, int progress) {
        put(ctx, key_writer_alpha, progress);
    }

    public static int getWriterAlpha(Context ctx) {
        return (int) get(ctx, key_writer_alpha, 25);
    }

    public static void putGravityState(Context ctx, boolean state) {
        put(ctx, key_gravity_state, state);
    }

    public static boolean getGravityState(Context ctx) {
        return (boolean) get(ctx, key_gravity_state, false);
    }

    public static void setPrinterN1(Context ctx, int n1) {
        put(ctx, KEY_PRINTER_N1, n1);
    }

    public static int getPrinterN1(Context ctx) {
        return (int) get(ctx, KEY_PRINTER_N1, 5);
    }

    public static void setPrinterN2(Context ctx, int n2) {
        put(ctx, KEY_PRINTER_N2, n2);
    }

    public static int getPrinterN2(Context ctx) {
        return (int) get(ctx, KEY_PRINTER_N2, 0);
    }

    public static void setPrinterN3(Context ctx, int n3) {
        put(ctx, KEY_PRINTER_N3, n3);
    }

    public static int getPrinterN3(Context ctx) {
        return (int) get(ctx, KEY_PRINTER_N3, 1);
    }

    public static void setPrinterN4(Context ctx, int n4) {
        put(ctx, KEY_PRINTER_N4, n4);
    }

    public static int getPrinterN4(Context ctx) {
        return (int) get(ctx, KEY_PRINTER_N4, 0);
    }

    public static void setBorderLength(Context ctx, int length) {
        put(ctx, KEY_BORDER_LENGTH, length);
    }

    public static int getBorderLength(Context ctx) {
        return (int) get(ctx, KEY_BORDER_LENGTH, 0);
    }

    public static void setMarkLength(Context ctx, int length) {
        put(ctx, KEY_WRITE_MARK_LENGTH, length);
    }

    public static int getMarkLength(Context ctx) {
        return (int) get(ctx, KEY_WRITE_MARK_LENGTH, 0);
    }

    public static void setPrivatePolicy(Context ctx, boolean state) {
        put(ctx, KEY_PRIVATE_POLICY, state);
    }

    public static boolean getPrivatePolicy(Context ctx) {
        return (boolean) get(ctx, KEY_PRIVATE_POLICY, false);
    }

    public static void setFirstLauncher(Context ctx, boolean isFirst) {
        put(ctx, KEY_FIRST_LAUNCHER, isFirst);
    }

    public static boolean getFirstLauncher(Context ctx) {
        return (boolean) get(ctx, KEY_FIRST_LAUNCHER, true);
    }

    public static void setAdSettings(Context ctx, boolean state) {
        put(ctx, KEY_AD_SETTING, state);
    }

    public static boolean getAdSettings(Context ctx) {
        return (boolean) get(ctx, KEY_AD_SETTING, true);
    }

    public static void setSplashADState(Context ctx, boolean state) {
        put(ctx, KEY_VIVO_AD_SETTING, state);
    }

    public static boolean getSplashADState(Context ctx) {
        return (boolean) get(ctx, KEY_VIVO_AD_SETTING, false);
    }

    public static void putBrushColor(Context ctx, int color) {
        put(ctx, KEY_BRUSH_COLOR, color);
    }

    public static int getBrushColor(Context ctx) {
        return (int) get(ctx, KEY_BRUSH_COLOR, ContextCompat.getColor(ctx, R.color.darkRed));
    }

    public static void putBrushWidth(Context ctx, int width) {
        put(ctx, KEY_BRUSH_WIDTH, width);
    }

    public static int getBrushWidth(Context ctx) {
        return (int) get(ctx, KEY_BRUSH_WIDTH, 64);
    }

    /**
     * 保存透明度
     */
    public static void putGXPaintAlpha(Context ctx, int progress) {
        put(ctx, KEY_GX_PAINT_ALPHA, progress);
    }

    /**
     * 获取透明度
     */
    public static int getGXPaintAlpha(Context ctx) {
        return (int) get(ctx, KEY_GX_PAINT_ALPHA, 100);
    }

    /**
     * 保存颜色
     */
    public static void putGXPaintColor(Context ctx, int color) {
        put(ctx, KEY_GX_PAINT_COLOR, color);
    }

    /**
     * 获取颜色
     */
    public static int getGXPaintColor(Context ctx) {
        return (int) get(ctx, KEY_GX_PAINT_COLOR, ContextCompat.getColor(ctx, R.color.colorPrimary));
    }

    /**
     * 保存样式
     */
    public static void putGXPaintType(Context ctx, int type) {
        put(ctx, KEY_GX_PAINT_TYPE, type);
    }

    /**
     * 获取样式
     */
    public static int getGXPaintType(Context ctx) {
        return (int) get(ctx, KEY_GX_PAINT_TYPE, 6);
    }

    public static void setMatrixSX(Context ctx, float sx) {
        put(ctx, KEY_MATRIX_SX, sx);
    }

    public static float getMatrixSX(Context ctx) {
        return (float) get(ctx, KEY_MATRIX_SX, 0f);
    }

    public static void setMatrixSY(Context ctx, float sy) {
        put(ctx, KEY_MATRIX_SY, sy);
    }

    public static float getMatrixSY(Context ctx) {
        return (float) get(ctx, KEY_MATRIX_SY, 0f);
    }
}