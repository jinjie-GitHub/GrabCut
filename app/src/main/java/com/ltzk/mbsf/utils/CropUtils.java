package com.ltzk.mbsf.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.List;

/**
 * 描述：工具类
 */
public class CropUtils {

    /**
     * 判断按下的点是否在圆圈内,即两点之间的距离小于半径
     * 点阵示意：
     * 0   1
     * 2   3
     *
     * @param x                                按下的X坐标
     * @param y                                按下的Y坐标
     * @param four_corner_coordinate_positions 坐标顶点
     * @param region_radius                    响应区域半径
     * @return 返回按到的是哪个点, 没有则返回-1
     */
    public static int isInTheCornerCircle(float x, float y, float[][] four_corner_coordinate_positions, int region_radius) {
        for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
            float a = four_corner_coordinate_positions[i][0];
            float b = four_corner_coordinate_positions[i][1];
            float temp1 = (float) Math.pow((x - a), 2);
            float temp2 = (float) Math.pow((y - b), 2);
            if (((float) region_radius) >= Math.sqrt(temp1 + temp2)) {
                return i;
            }
        }
        return -1;
    }

    public static int isInTheCornerCircle2(float x, float y, float[][] four_corner_coordinate_positions, float region_radius) {
        float a = (four_corner_coordinate_positions[3][0] - four_corner_coordinate_positions[0][0]) / 2;
        float b = (four_corner_coordinate_positions[3][1] - four_corner_coordinate_positions[0][1]) / 2;
        a = (four_corner_coordinate_positions[3][0] - a);
        b = (four_corner_coordinate_positions[3][1] - b);
        float temp1 = (float) Math.pow((x - a), 2);
        float temp2 = (float) Math.pow((y - b), 2);
        if ((region_radius) >= Math.sqrt(temp1 + temp2)) {
            return 0;
        }
        return -1;
    }

    public static int isInTheCornerCircle(float x, float y, float[][] four_corner_coordinate_positions, int region_radius, Integer[] except) {
        List<Integer> integerList = null;
        if (except != null && except.length > 0) {
            integerList = Arrays.asList(except);
        }
        for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
            if (integerList != null && integerList.size() > 0 && integerList.contains(i)) {
                continue;
            }
            float a = four_corner_coordinate_positions[i][0];
            float b = four_corner_coordinate_positions[i][1];
            float temp1 = (float) Math.pow((x - a), 2);
            float temp2 = (float) Math.pow((y - b), 2);
            if (((float) region_radius) >= Math.sqrt(temp1 + temp2)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 扩大缩放方法
     * 根据用户传来的点改变其他点的坐标
     * 按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对边的点坐标不变
     * 点阵示意：
     * 0   1
     * 2   3
     *
     * @param point     用户按的点
     * @param offsetX   X轴偏移量
     * @param offsetY   Y轴偏移量
     * @param positions 坐标顶点
     * @param max       最大偏移量
     */
    public static void changePositions(int point, int offsetX, int offsetY, float[][] positions, float max) {
        switch (point) {
            case 0:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化0点的位置   suoxiao
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化1点的Y轴
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化2点的X轴
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                } else {
                    //变化0点的位置   kuoda
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                    //变化1点的Y轴
                    positions[1][0] += max;
                    positions[1][1] -= max;
                    //变化2点的X轴
                    positions[2][0] -= max;
                    positions[2][1] += max;
                    positions[3][0] += max;
                    positions[3][1] += max;
                }
                break;
            case 1:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化1点的位置
                    positions[1][0] += max;
                    positions[1][1] -= max;
                    //变化0点的Y轴
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                    //变化3点的X轴
                    positions[3][0] += max;
                    positions[3][1] += max;
                    positions[2][0] -= max;
                    positions[2][1] += max;
                } else if ((offsetX < 0 && offsetY > 0) || (offsetX < 0 && offsetY < 0)) {
                    //变化1点的位置
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化0点的Y轴
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化3点的X轴
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    positions[2][0] += max;
                    positions[2][1] -= max;
                }
                break;
            case 2:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化2点的位置   suoxiao
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    //变化0点的X轴
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化3点的Y轴
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    positions[1][0] -= max;
                    positions[1][1] += max;
                } else {
                    //变化2点的位置   kuoda
                    positions[2][0] -= max;
                    positions[2][1] += max;
                    //变化0点的X轴
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                    //变化3点的Y轴
                    positions[3][0] += max;
                    positions[3][1] += max;
                    positions[1][0] += max;
                    positions[1][1] -= max;
                }
                break;
            case 3:
                if ((offsetX > 0 && offsetY < 0) || ((offsetX > 0 && offsetY > 0))) {
                    //变化3点的位置   kuoda
                    positions[3][0] += max;
                    positions[3][1] += max;
                    //变化1点的X轴
                    positions[1][0] += max;
                    positions[1][1] -= max;
                    //变化2点的Y轴
                    positions[2][0] -= max;
                    positions[2][1] += max;
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                } else {
                    //变化3点的位置   suoxiao
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    //变化1点的X轴
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化2点的Y轴
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    positions[0][0] += max;
                    positions[0][1] += max;
                }
                break;
        }
    }

    public static void changePositionsNew(int point, int offsetX, int offsetY, float[][] positions, float max, int w, int h) {
        switch (point) {
            case 0:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化0点的位置   suoxiao
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化1点的Y轴
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化2点的X轴
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                } else {
                    //变化0点的位置   kuoda
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                    //变化1点的Y轴
                    positions[1][1] -= max;
                    //变化2点的X轴
                    positions[2][0] -= max;

                    if (positions[3][0] >= w) {
                        positions[3][0] = w;
                        positions[1][0] = w;
                        positions[0][0] -= max;
                        positions[2][0] -= max;
                    } else {
                        positions[3][0] += max;
                        positions[1][0] += max;
                    }

                    if (positions[3][1] >= h) {
                        positions[3][1] = h;
                        positions[2][1] = h;
                        positions[0][1] -= max;
                        positions[1][1] -= max;
                    } else {
                        positions[2][1] += max;
                        positions[3][1] += max;
                    }
                }
                break;
            case 1:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化1点的位置 kuoda
                    positions[1][0] += max;
                    positions[1][1] -= max;
                    //变化0点的Y轴
                    positions[0][1] -= max;
                    //变化3点的X轴
                    positions[3][0] += max;

                    if (positions[2][0] <= 0) {
                        positions[0][0] = 0;
                        positions[2][0] = 0;
                        positions[1][0] += max;
                        positions[3][0] += max;
                    } else {
                        positions[0][0] -= max;
                        positions[2][0] -= max;
                    }

                    if (positions[2][1] >= h) {
                        positions[2][1] = h;
                        positions[3][1] = h;
                        positions[0][1] -= max;
                        positions[1][1] -= max;
                    } else {
                        positions[2][1] += max;
                        positions[3][1] += max;
                    }
                } else if ((offsetX < 0 && offsetY > 0) || (offsetX < 0 && offsetY < 0)) {
                    //变化1点的位置 suoxiao
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化0点的Y轴
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化3点的X轴
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    positions[2][0] += max;
                    positions[2][1] -= max;
                }
                break;
            case 2:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化2点的位置   suoxiao
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    //变化0点的X轴
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化3点的Y轴
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    positions[1][0] -= max;
                    positions[1][1] += max;
                } else {
                    //变化2点的位置   kuoda
                    positions[2][0] -= max;
                    positions[2][1] += max;
                    //变化0点的X轴
                    positions[0][0] -= max;
                    //变化3点的Y轴
                    positions[3][1] += max;

                    if (positions[1][0] >= w) {
                        positions[1][0] = w;
                        positions[3][0] = w;
                        positions[0][0] -= max;
                        positions[2][0] -= max;
                    } else {
                        positions[1][0] += max;
                        positions[3][0] += max;
                    }

                    if (positions[1][1] <= 0) {
                        positions[1][1] = 0;
                        positions[0][1] = 0;
                        positions[2][1] += max;
                        positions[3][1] += max;
                    } else {
                        positions[0][1] -= max;
                        positions[1][1] -= max;
                    }
                }
                break;
            case 3:
                if ((offsetX > 0 && offsetY < 0) || ((offsetX > 0 && offsetY > 0))) {
                    //变化3点的位置   kuoda
                    positions[3][0] += max;
                    positions[3][1] += max;
                    //变化1点的X轴
                    positions[1][0] += max;
                    //变化2点的Y轴
                    positions[2][1] += max;

                    if (positions[0][0] <= 0) {
                        positions[0][0] = 0;
                        positions[2][0] = 0;
                        positions[1][0] += max;
                        positions[3][0] += max;
                    } else {
                        positions[0][0] -= max;
                        positions[2][0] -= max;
                    }

                    if (positions[0][1] <= 0) {
                        positions[0][1] = 0;
                        positions[1][1] = 0;
                        positions[2][1] += max;
                        positions[3][1] += max;
                    } else {
                        positions[0][1] -= max;
                        positions[1][1] -= max;
                    }

                } else {
                    //变化3点的位置   suoxiao
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    //变化1点的X轴
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化2点的Y轴
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    positions[0][0] += max;
                    positions[0][1] += max;
                }
                break;
        }
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0) || "null".equals(str));
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        return dm.heightPixels;
    }
}