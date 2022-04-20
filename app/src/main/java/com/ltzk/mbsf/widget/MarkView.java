package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.CropUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.ViewUtil;

/**
 * Created by JinJie
 */
public class MarkView extends View {

    //四个点坐标
    private float[][] four_corner_coordinate_positions;
    private int sPoint = -1; // 用户按下的点
    private int sMax;
    private int VIEW_HEIGHT; //视图高
    private int VIEW_WIDTH; //视图宽

    private int BORDER_LENGTH = 200; //边框长度
    private int RECT_CORNER_WITH = 6; //四个角的粗
    private int SCAN_LINE_WIDTH = 3;
    private int RECT_CORNER_HEIGHT = 10; //四个角的长度
    private int MIN_BORDER_LENGTH = RECT_CORNER_HEIGHT * 2; //最小边框长度
    private int POINT_STATE = -1; //判断用户是缩小还是放大 0放大 1缩小
    private static boolean MOVE_OR_ZOOM_STATE = true; //移动或缩放状态， true 为移动

    //显示控制
    private boolean CORNER_SHOW = true; //拐角线
    private boolean BG_SHOW = false; //背景色
    private int CORNER_LINE_COLOR = Color.BLUE;//拐角颜色
    private int SCAN_LINE_COLOR = Color.BLUE;//网格颜色

    private int mType = 6;
    private int lastX = 0; //上次按下的X位置
    private int lastY = 0; //上次按下的Y位置
    private int offsetX = 0; //X轴偏移量
    private int offsetY = 0; //Y轴偏移量

    private Paint mPaintRect = new Paint();
    private Paint mPaintLine = new Paint();

    public MarkView(Context context) {
        this(context, null);
    }

    public MarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;//屏幕像素密度
        handleStyleable(context, attrs, defStyleAttr, density);

        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(CORNER_LINE_COLOR);
        mPaintRect.setStrokeCap(Paint.Cap.SQUARE);
        mPaintRect.setStrokeWidth(RECT_CORNER_WITH);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(SCAN_LINE_COLOR);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(SCAN_LINE_WIDTH);
        mPaintLine.setPathEffect(new DashPathEffect(new float[]{10, 5, 10, 5}, 0));
    }

    public void setPaintColor(int color) {
        mPaintLine.setColor(color);
        mPaintRect.setColor(color);
        final int alpha = (255 * MySPUtils.getGXPaintAlpha(getContext()) / 100);
        mPaintLine.setAlpha(alpha);
        mPaintRect.setAlpha(alpha);
        this.setEnabled(alpha > 0);
        invalidate();
    }

    public void setPaintAlpha(int a) {
        mPaintLine.setAlpha(a);
        mPaintRect.setAlpha(a);
        invalidate();
    }

    public void setPaintType(int type) {
        mType = type;
        invalidate();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle, float mDensity) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CropView, defStyle, 0);
        CORNER_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_corner_line_color, CORNER_LINE_COLOR);
        SCAN_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_scan_line_color, SCAN_LINE_COLOR);
        RECT_CORNER_WITH = ta.getDimensionPixelSize(R.styleable.CropView_cv_corner_line_with, (int) (mDensity * RECT_CORNER_WITH));
        RECT_CORNER_HEIGHT = ta.getDimensionPixelSize(R.styleable.CropView_cv_corner_line_height, (int) (mDensity * RECT_CORNER_HEIGHT));
        BORDER_LENGTH = ta.getDimensionPixelSize(R.styleable.CropView_cv_border_length, (int) (mDensity * BORDER_LENGTH));
        MIN_BORDER_LENGTH = ta.getDimensionPixelSize(R.styleable.CropView_cv_min_border_length, (RECT_CORNER_HEIGHT * 5));
        //线条显示
        CORNER_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_corner_line, true);
        BG_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_alpha_bg, false);
        ta.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || event.getPointerCount() > 1) {
            return false;
        }
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                if (isInTheCornerCircle(event.getX(), event.getY()) != -1) {
                    //开始缩放操作
                    MOVE_OR_ZOOM_STATE = false; //设置false为缩放状态
                    sPoint = isInTheCornerCircle(event.getX(), event.getY());
                    return true;
                } else if (isInTheCornerCircle2(event.getX(), event.getY()) != -1) {
                    MOVE_OR_ZOOM_STATE = true; //设置false为缩放状态
                    sPoint = isInTheCornerCircle(event.getX(), event.getY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = x - lastX;
                offsetY = y - lastY;
                //判断当前是扩大还是缩小操作
                judgementXAndY();
                //限定移动范围,移动状态：只有在移动状态下才能移动
                if (MOVE_OR_ZOOM_STATE) {
                    getOffSetXAndOffSetY();
                    //四个点的坐标信息也要随之改变
                    for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
                        four_corner_coordinate_positions[i][0] += offsetX;
                        four_corner_coordinate_positions[i][1] += offsetY;
                        invalidate();
                    }
                } else { //在缩放状态下
                    //按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对点坐标不变
                    sMax = Math.abs(offsetX) >= Math.abs(offsetY) ? Math.abs(offsetX) : Math.abs(offsetY);
                    //只有在扩大操作才进行边界范围判断
                    if (POINT_STATE == 0) {
                        getOffSetXAndOffSetY(); //边界范围判断
                    } else if (POINT_STATE == 1) {//缩小操作时进行边界不能太小判断
                        //如果边长+max太小，直接返回
                        if (BORDER_LENGTH - sMax <= MIN_BORDER_LENGTH * 1.5) {//缩小操作时进行边界不能太小判断
                            sMax = 0;
                        }
                    }

                    //改变坐标
                    changeEightCoordinatePositions(sPoint, offsetX, offsetY);
                    //更新边长
                    notifyNowBorderLength();
                    //更新回调接口
                    invalidate();
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                MOVE_OR_ZOOM_STATE = true; //回归为默认的移动状态
                MySPUtils.setMarkLength(getContext(), BORDER_LENGTH);
                //invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveViewWithFinger(int offsetX, int offsetY) {
        layout(getLeft() + offsetX,
                getTop() + offsetY,
                getRight() + offsetX,
                getBottom() + offsetY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        drawRect(canvas);
        canvas.restore();
    }

    /**
     * 初始化布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //如果没有改变不用重新绘制，否则会导致移动裁剪框时重绘，不能保持当前状态
        if (!changed) {
            return;
        }
        //获取视图尺寸
        VIEW_HEIGHT = this.getHeight();
        VIEW_WIDTH = this.getWidth();
        //检测裁剪框边长是否超出视图宽高范围
        validateSize();

        //初始化四个点的坐标
        four_corner_coordinate_positions = new float[][]{
                {(VIEW_WIDTH - BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2}, //左上
                {(VIEW_WIDTH + BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2}, //右上
                {(VIEW_WIDTH - BORDER_LENGTH) / 2, (VIEW_HEIGHT + BORDER_LENGTH) / 2}, //左下
                {(VIEW_WIDTH + BORDER_LENGTH) / 2, (VIEW_HEIGHT + BORDER_LENGTH) / 2} //右上
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 验证裁剪框的尺寸是否超出视图范围
     **/
    private void validateSize() {
        //边框长度<最小边框长度
        final int len = MySPUtils.getMarkLength(getContext());
        BORDER_LENGTH = len == 0 ? ViewUtil.dpToPx(80) : len;

        if (BORDER_LENGTH < MIN_BORDER_LENGTH) {
            BORDER_LENGTH = MIN_BORDER_LENGTH;
        }

        if (BORDER_LENGTH + RECT_CORNER_WITH * 2 >= Math.min(VIEW_WIDTH, VIEW_HEIGHT)) {
            BORDER_LENGTH = Math.min(VIEW_WIDTH, VIEW_HEIGHT) - RECT_CORNER_HEIGHT * 3;
        }

        if (RECT_CORNER_HEIGHT * 3 >= BORDER_LENGTH) {
            RECT_CORNER_HEIGHT = (BORDER_LENGTH / 5);
        }
    }

    //-----------------------------------------绘制方法START----------------------------------------//

    /**
     * 矩形裁剪模式
     **/
    private void drawRect(Canvas canvas) {
        drawScanLine(canvas);
        if (BG_SHOW) {
            drawAlphaBg(canvas);
        }
        if (CORNER_SHOW) {
            drawCornerLine(canvas);
        }
    }

    private void drawAlphaBg(Canvas canvas) {
        Paint paintRect = new Paint();  //初始化画笔
        //画边框的画笔
        paintRect.setColor(Color.parseColor("#99000000"));
        paintRect.setStyle(Paint.Style.FILL); //设置实心
        //上半部分背景
        canvas.drawRect(0, 0,
                getWidth(),
                four_corner_coordinate_positions[0][1],
                paintRect);
        //下半部分背景
        canvas.drawRect(0, four_corner_coordinate_positions[2][1],
                getWidth(),
                getHeight(),
                paintRect);
        //左边背景
        canvas.drawRect(0,
                four_corner_coordinate_positions[0][1],
                four_corner_coordinate_positions[2][0],
                four_corner_coordinate_positions[2][1],
                paintRect);
        //右边背景
        canvas.drawRect(four_corner_coordinate_positions[1][0],
                four_corner_coordinate_positions[1][1],
                getWidth(),
                four_corner_coordinate_positions[3][1],
                paintRect);

        paintRect.reset();
    }

    /**
     * 绘制四个拐角线
     **/
    private void drawCornerLine(Canvas canvas) {
        //左上角的两根
        //左
        canvas.drawLine(four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH * 2 + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[0][1] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                mPaintRect);
        //上
        canvas.drawLine(four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[0][0] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH * 2,
                mPaintRect);

        //左下角的两根
        //左
        canvas.drawLine(four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH * 2 - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[2][1] - RECT_CORNER_HEIGHT + RECT_CORNER_WITH,
                mPaintRect);
        //下
        canvas.drawLine(four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[2][0] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH * 2,
                mPaintRect);

        //右上角的两根
        //上
        canvas.drawLine(four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH * 2,
                mPaintRect);
        //右
        canvas.drawLine(four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH * 2 + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[1][1] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH
                , mPaintRect);

        //右下角的两根
        //下
        canvas.drawLine(four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH * 2,
                mPaintRect);
        //右
        canvas.drawLine(four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH * 2 - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH * 2,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                mPaintRect);
    }

    /**
     * 绘制米子格
     **/
    private void drawScanLine(final Canvas canvas) {
        switch (mType) {
            case 0:
                drawType0(canvas);
                break;
            case 1:
                drawType1(canvas);
                break;
            case 2:
                drawType2(canvas);
                break;
            case 3:
                drawType3(canvas);
                break;
            case 4:
                drawType4(canvas);
                break;
            case 5:
                drawType5(canvas);
                break;
            case 6:
                drawType6(canvas);
                break;
            case 7:
                drawType7(canvas);
                break;
            case 8:
                drawType8(canvas);
                break;
            case 9:
                drawType9(canvas);
                break;
        }
    }

    private void drawTypeBase(final Canvas canvas, final int scaleX, final int scaleY) {
        final Path path = new Path();
        path.moveTo(four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / scaleX, four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / scaleY);
        path.lineTo(four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / scaleX, four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / scaleY);
        path.lineTo(four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / scaleX, four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / scaleY);
        path.lineTo(four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / scaleX, four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / scaleY);
        path.close();
        canvas.drawPath(path, mPaintLine);
    }

    private void drawType9(final Canvas canvas) {
        drawType0(canvas);
        float x3 = four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 10;
        float y3 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 10;
        float x4 = four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 10;
        float y4 = four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 10;

        float x0 = four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y0 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        float x1 = four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y1 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        float y2 = four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        RectF rectF = new RectF(x3, y3, x4, y4);
        canvas.drawArc(rectF, 0, 360, true, mPaintLine);
        canvas.drawLine(x0 + (x1 - x0) / 3, y1, x0 + (x1 - x0) / 3, y2, mPaintLine);
        canvas.drawLine(x0 + 2 * (x1 - x0) / 3, y1, x0 + 2 * (x1 - x0) / 3, y2, mPaintLine);
        canvas.drawLine(x0, y0 + (y2 - y0) / 3, x1, y0 + (y2 - y0) / 3, mPaintLine);
        canvas.drawLine(x0, y0 + 2 * (y2 - y0) / 3, x1, y0 + 2 * (y2 - y0) / 3, mPaintLine);
    }

    private void drawType8(final Canvas canvas) {
        drawType0(canvas);
        float x0 = four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y0 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        float x1 = four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y2 = four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        canvas.drawLine(x0 + (x1 - x0) / 3, four_corner_coordinate_positions[0][1], x0 + (x1 - x0) / 3, four_corner_coordinate_positions[3][1], mPaintLine);
        canvas.drawLine(x0 + 2 * (x1 - x0) / 3, four_corner_coordinate_positions[0][1], x0 + 2 * (x1 - x0) / 3, four_corner_coordinate_positions[3][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[0][0], y0 + (y2 - y0) / 3, four_corner_coordinate_positions[1][0], y0 + (y2 - y0) / 3, mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[0][0], y0 + 2 * (y2 - y0) / 3, four_corner_coordinate_positions[1][0], y0 + 2 * (y2 - y0) / 3, mPaintLine);
    }

    private void drawType7(final Canvas canvas) {
        drawType0(canvas);
        final Path path = new Path();
        float x0 = four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y0 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        path.moveTo(x0, y0);

        float x1 = four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y1 = four_corner_coordinate_positions[0][1] + (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        path.lineTo(x1, y1);

        float x2 = four_corner_coordinate_positions[1][0] - (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y2 = four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        path.lineTo(x2, y2);

        float x3 = four_corner_coordinate_positions[0][0] + (four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]) / 8;
        float y3 = four_corner_coordinate_positions[3][1] - (four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]) / 8;
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, mPaintLine);

        canvas.drawLine(x0 + (x1 - x0) / 3, y1, x0 + (x1 - x0) / 3, y2, mPaintLine);
        canvas.drawLine(x0 + 2 * (x1 - x0) / 3, y1, x0 + 2 * (x1 - x0) / 3, y2, mPaintLine);
        canvas.drawLine(x0, y0 + (y2 - y0) / 3, x1, y0 + (y2 - y0) / 3, mPaintLine);
        canvas.drawLine(x0, y0 + 2 * (y2 - y0) / 3, x1, y0 + 2 * (y2 - y0) / 3, mPaintLine);
    }

    private void drawType6(final Canvas canvas) {
        drawType2(canvas);
        drawTypeBase(canvas, 6, 6);
    }

    private void drawType5(final Canvas canvas) {
        drawType1(canvas);
        drawTypeBase(canvas, 6, 6);
    }

    private void drawType4(final Canvas canvas) {
        drawType1(canvas);
        drawTypeBase(canvas, 6, 4);
    }

    private void drawType3(final Canvas canvas) {
        drawType1(canvas);
        drawTypeBase(canvas, 4, 6);
    }

    private void drawType2(final Canvas canvas) {
        drawType1(canvas);
        canvas.drawLine(four_corner_coordinate_positions[0][0], four_corner_coordinate_positions[0][1], four_corner_coordinate_positions[3][0], four_corner_coordinate_positions[3][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[1][0], four_corner_coordinate_positions[1][1], four_corner_coordinate_positions[2][0], four_corner_coordinate_positions[2][1], mPaintLine);
    }

    private void drawType1(final Canvas canvas) {
        drawType0(canvas);
        canvas.drawLine((four_corner_coordinate_positions[0][0] + four_corner_coordinate_positions[1][0]) / 2, four_corner_coordinate_positions[0][1], (four_corner_coordinate_positions[2][0] + four_corner_coordinate_positions[3][0]) / 2, four_corner_coordinate_positions[2][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[0][0], (four_corner_coordinate_positions[0][1] + four_corner_coordinate_positions[2][1]) / 2, four_corner_coordinate_positions[1][0], (four_corner_coordinate_positions[1][1] + four_corner_coordinate_positions[3][1]) / 2, mPaintLine);
    }

    private void drawType0(final Canvas canvas) {
        canvas.drawLine(four_corner_coordinate_positions[0][0], four_corner_coordinate_positions[0][1], four_corner_coordinate_positions[2][0], four_corner_coordinate_positions[2][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[0][0], four_corner_coordinate_positions[0][1], four_corner_coordinate_positions[1][0], four_corner_coordinate_positions[1][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[1][0], four_corner_coordinate_positions[1][1], four_corner_coordinate_positions[3][0], four_corner_coordinate_positions[3][1], mPaintLine);
        canvas.drawLine(four_corner_coordinate_positions[2][0], four_corner_coordinate_positions[2][1], four_corner_coordinate_positions[3][0], four_corner_coordinate_positions[3][1], mPaintLine);
    }

    //-----------------------------------------绘制方法END----------------------------------------//

    /**
     * 判断按下的点在圆圈内
     * 点阵示意：
     * 0   1
     * 2   3
     */
    private int isInTheCornerCircle(float x, float y) {
        return CropUtils.isInTheCornerCircle(x, y,
                four_corner_coordinate_positions,
                RECT_CORNER_HEIGHT, new Integer[]{4, 5, 6, 7});
    }

    private int isInTheCornerCircle2(float x, float y) {
        return CropUtils.isInTheCornerCircle2(x, y, four_corner_coordinate_positions, BORDER_LENGTH / 2);
    }

    /**
     * POINT_STATE: 0放大， 1缩小
     */
    private void judgementXAndY() {
        switch (sPoint) {
            case 0:
                if ((offsetX <= 0 && offsetY <= 0) || (offsetX <= 0 && offsetY >= 0)) {
                    POINT_STATE = 0;//扩大
                } else {
                    POINT_STATE = 1;//缩小
                }
                break;
            case 1:
                if ((offsetX >= 0 && offsetY <= 0) || (offsetX >= 0 && offsetY >= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            case 2:
                if ((offsetX <= 0 && offsetY >= 0) || (offsetX <= 0 && offsetY <= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            case 3:
                if ((offsetX >= 0 && offsetY >= 0) || (offsetX >= 0 && offsetY <= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
        }
    }

    /**
     * 防止X和Y溢出边界的算法
     */
    private void getOffSetXAndOffSetY() {
        if (MOVE_OR_ZOOM_STATE) {//如果是移动状态
            if ((four_corner_coordinate_positions[0][0] + offsetX <= 0) ||
                    (four_corner_coordinate_positions[1][0] + offsetX >= VIEW_WIDTH)
            ) {
                offsetX = 0;
            }
            if ((four_corner_coordinate_positions[0][1] + offsetY <= 0) ||
                    (four_corner_coordinate_positions[2][1] + offsetY >= VIEW_HEIGHT)
            ) {
                offsetY = 0;
            }
        } else {//如果是缩放状态
            switch (sPoint) {
                case 0:
                    if ((four_corner_coordinate_positions[0][0] - sMax <= RECT_CORNER_HEIGHT) ||
                            (four_corner_coordinate_positions[0][1] - sMax <= RECT_CORNER_HEIGHT)) {
                        sMax = 0;
                    }
                    break;
                case 1:
                    if ((four_corner_coordinate_positions[1][0] + sMax >= VIEW_WIDTH - RECT_CORNER_HEIGHT) ||
                            (four_corner_coordinate_positions[1][1] - sMax <= 0)) {
                        sMax = 0;
                    }
                    break;
                case 2:
                    if ((four_corner_coordinate_positions[2][0] - sMax <= 0) ||
                            (four_corner_coordinate_positions[2][1] + sMax >= VIEW_HEIGHT - RECT_CORNER_HEIGHT)) {
                        sMax = 0;
                    }
                    break;
                case 3:
                    if ((four_corner_coordinate_positions[3][0] + sMax >= VIEW_WIDTH - RECT_CORNER_HEIGHT) ||
                            (four_corner_coordinate_positions[3][1] + sMax >= VIEW_HEIGHT - RECT_CORNER_HEIGHT)) {
                        sMax = 0;
                    }
                    break;
            }
        }
    }

    /**
     * 扩大缩放方法
     * 根据用户传来的点改变其他点的坐标
     * 按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对边的点坐标不变
     * 点阵示意：
     * 0    1
     * 2    3
     */
    private void changeEightCoordinatePositions(int point, int offsetX, int offsetY) {
        CropUtils.changePositionsNew(point, offsetX, offsetY, four_corner_coordinate_positions, sMax,VIEW_WIDTH,VIEW_HEIGHT);
    }

    /**
     * 更新矩形框对角线的方法
     */
    private void notifyNowBorderLength() {
        float a = four_corner_coordinate_positions[0][0];
        float b = four_corner_coordinate_positions[0][1];
        float c = four_corner_coordinate_positions[1][0];
        float d = four_corner_coordinate_positions[1][1];
        float temp1 = (float) Math.pow(a - c, 2);
        float temp2 = (float) Math.pow(b - d, 2);
        BORDER_LENGTH = (int) Math.sqrt(temp1 + temp2);
    }
}