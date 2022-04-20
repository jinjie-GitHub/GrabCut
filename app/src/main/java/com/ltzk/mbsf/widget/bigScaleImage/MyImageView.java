package com.ltzk.mbsf.widget.bigScaleImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.popupview.ZiTieSettingPopView;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.SimpleDraw;

import java.util.List;

public class MyImageView extends SubsamplingScaleImageView {

    private boolean showZhujie;
    private boolean showZhegai;
    private boolean showDama;

    Paint paint_zhujie_bg = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint_zhujie_text = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint_zhegai_bg = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint_dama = new Paint(Paint.ANTI_ALIAS_FLAG);

    //尺寸
    int mWidth,mHeight;

    //注解数据
    List<ZiBean> list_zi;

    //遮盖数据
    private Rect rect_bg;
    private float left;
    private float top;
    private float right;
    private float bottom;

    public MyImageView(Context context, AttributeSet attr) {
        super(context, attr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint_zhujie_text.setColor(Color.WHITE);
        paint_zhujie_text.setTextAlign(Paint.Align.CENTER);
        paint_zhegai_bg.setColor(getResources().getColor(R.color.transparentBlack));

        paint_zhujie_bg.setAntiAlias(true);
        paint_zhujie_bg.setStyle(Paint.Style.FILL);
        paint_zhujie_bg.setStrokeCap(Paint.Cap.ROUND);
        init();
    }

    /**
     * 设置默认值
     */
    private void init() {
        paint_zhujie_text.setColor(ZiTieSettingPopView.getAlpha(getContext()) == 0 ?
                ZiTieSettingPopView.getColor(getContext()) : Color.WHITE);
        mType = ZiTieSettingPopView.getStyle(getContext());
        mSize = ZiTieSettingPopView.getSize(getContext(), false) / 100f;
        paint_zhujie_bg.setColor(ZiTieSettingPopView.getColor(getContext()));
        paint_zhujie_bg.setAlpha(ZiTieSettingPopView.getAlpha(getContext()));
    }

    private volatile float p;
    public void setProportion(float s) {
        this.p = s;
    }

    public boolean isShowZhujie() {
        return showZhujie;
    }

    public void setShowZhujie(boolean showZhujie) {
        init();
        this.showZhujie = showZhujie;
        invalidate();
    }

    public boolean isShowZhegai() {
        return showZhegai;
    }

    public void setShowZhegai(boolean showZhegai) {
        this.showZhegai = showZhegai;
    }

    public boolean isShowDama() {
        return showDama;
    }

    public void setShowDama(boolean showDama) {
        this.showDama = showDama;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawZhujie(canvas);
        drawZhegai(canvas);
        drawDama(canvas);
    }

    public void setList_zi(List<ZiBean> list_zi) {
        this.list_zi = list_zi;
    }

    /**
     * 绘制注解
     */
    private void drawZhujie(Canvas canvas) {
        if (!showZhujie || vTranslate == null) {
            return;
        }

        float width = 0f;

        //自定义的画笔
        for (ZiBean zi : list_zi) {

            if (zi.get_hanzi().length() > 1) {
                continue;
            }

            //字形在页面中的具体位置（x,y,w,h）681,59.82,293,324.65
            String[] frames = zi.get_frame().split(",");

            if (width == 0) {
                width = Float.valueOf(frames[2]) / 2f;
            }

            //红色背景的直径
            final float size = width * scale() * mSize;
            Logger.d("--->size: " + size);

            float top = vTranslate.y + Float.valueOf(frames[1]) * scale();
            float right = vTranslate.x + (Float.valueOf(frames[0]) + Float.valueOf(frames[2])) * scale();
            float left = right - size;
            float bottom = top + size;

            float x = (left + right) / 2;
            float y = (top + bottom) / 2;

            if (mType == SimpleDraw.STYLE_INTERSECT) {
                x = right;
                y = top;
            } else if (mType == SimpleDraw.STYLE_OUT) {
                x = right + ViewUtil.dpToPx(24) * scale();
                y = top - ViewUtil.dpToPx(24) * scale();
            }

            canvas.drawCircle(x, y, size / 2, paint_zhujie_bg);

            //字
            paint_zhujie_text.setTextSize(size * 0.8f);
            canvas.drawText(zi.get_hanzi(), x, y + (size / 4), paint_zhujie_text);
        }
    }

    private final float scale() {
        return getScale() * p;
    }

    /**
     * 修改标注颜色
     */
    public void setPaintColor(int color) {
        init();
        invalidate();
    }

    /**
     * 修改标注透明度
     */
    public void setPaintAlpha(int alpha) {
        init();
        invalidate();
    }

    /**
     * 修改字体大小
     */
    private float mSize = 1.0f;//缩放比例
    public void setPaintSize(float size) {
        mSize = size;
        invalidate();
    }

    /**
     * 修改样式
     */
    private int mType = 0;
    public void setPaintType(int type) {
        mType = type;
        invalidate();
    }

    /**
     * 外部调用的接口
     * */
    public void setRect(float left,float top,float right,float bottom){
        this.left=left;
        this.top=top;
        this.right=right;
        this.bottom=bottom;
    }

    /**
     * 绘制遮盖
     */
    private void drawZhegai(Canvas canvas){
        if (mChange != null) {
            mChange.onChange(showZhegai);
        }
        if(!showZhegai || vTranslate==null){
            return;
        }
        //左
        rect_bg=new Rect((int)vTranslate.x,(int)vTranslate.y,(int)(vTranslate.x+left*scale()),(int)(vTranslate.y+sHeight*getScale()));
        canvas.drawRect(rect_bg,paint_zhegai_bg);
        //上
        rect_bg=new Rect((int)(vTranslate.x+left*scale()),(int)vTranslate.y,(int)(vTranslate.x+right*scale()),(int)(vTranslate.y+top*scale()));
        canvas.drawRect(rect_bg,paint_zhegai_bg);
        //右
        rect_bg=new Rect((int)(vTranslate.x+right*scale()),(int)vTranslate.y,(int)(vTranslate.x+sWidth*getScale()),(int)(vTranslate.y+sHeight*getScale()));
        canvas.drawRect(rect_bg,paint_zhegai_bg);
        //下
        rect_bg=new Rect((int)(vTranslate.x+left*scale()),(int)(vTranslate.y+bottom*scale()),(int)(vTranslate.x+right*scale()),(int)(vTranslate.y+sHeight*getScale()));
        canvas.drawRect(rect_bg,paint_zhegai_bg);
    }

    Bitmap bitmap_dama;
    public void setDama(int id){
        bitmap_dama = ((BitmapDrawable)getResources().getDrawable(R.mipmap.vip_bg)).getBitmap();
    }

    /**
     * 绘制非vip打码
     * @param canvas
     */
    private void drawDama(Canvas canvas){
        if (!showDama || vTranslate==null){
            return;
        }

        BitmapShader bitmapShader=new BitmapShader(bitmap_dama, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint_dama.setShader(bitmapShader);
        //画矩形图
        canvas.drawRect(new RectF((int)vTranslate.x,(int)vTranslate.y,(int)(vTranslate.x+sWidth*getScale()),(int)(vTranslate.y+sHeight*getScale())),paint_dama);
    }

    //点击事件
    @Override
    protected void onClick(MotionEvent e) {
        if (callBack != null && vTranslate != null) {
            //点击事件回调
            callBack.onClick(e.getX(), e.getY(), scale(), vTranslate.x, vTranslate.y);
        }
    }
    CallBack callBack;
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public interface CallBack{
        public void onClick(float x,float y,float zoom,float sx,float sy);
    }

    private Change mChange;
    public void setChange(Change change) {
        this.mChange = change;
    }
    public interface Change {
        void onChange(boolean state);
    }
}
