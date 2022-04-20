package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import com.ltzk.mbsf.R;

/**
 * 描述：
 * 作者： on 2020-5-7 19:03
 * 邮箱：499629556@qq.com
 */
public class LineNoEditText extends AppCompatEditText {

    Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
    int startX = 100;
    Paint paintline = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintNum = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintNum2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    final int MAX_LINE = 50;
    int mWidth,mHeight;
    int mWidth_lineNo;
    float[] cutWidth = new float[1];

    public LineNoEditText(@NonNull Context context) {
        super(context);
    }

    public LineNoEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        paintline.setColor(getResources().getColor(R.color.whiteSmoke));
        paintline.setStrokeWidth(5);

        paintNum.setTextSize(getTextSize()/2);
        paintNum.getFontMetrics(fontMetrics);
        paintNum.setColor(getResources().getColor(R.color.colorPrimary));

        paintNum2.setTextSize(getTextSize()/2);
        paintNum2.getFontMetrics(fontMetrics);
        paintNum2.setColor(getResources().getColor(R.color.qmui_config_color_red));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mWidth_lineNo = getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            String text = getText().toString();
            String[] textArray = text.split("\n",-1);
            int length = textArray.length;
            String texText;
            //行号
            String lineNoText;
            int lineCount = 0;
            for (int i = 0; i < length; i++) {
                lineNoText = ""+(i+1);
                float widthNeet = paintNum.measureText(lineNoText);
                float startX = (mWidth_lineNo - widthNeet)/2;

                int baseline = getLayout().getLineBaseline(lineCount)+getPaddingTop() - (int)getTextSize()/4;
                if(i<MAX_LINE){
                    canvas.drawText(lineNoText,  startX, baseline, paintNum);
                }else {
                    canvas.drawText(lineNoText,  startX, baseline, paintNum2);
                }
                //该行文字
                texText = textArray[i];
                //计算文字数量
                int countTotal = texText.length();
                //绘制文字
                //canvas.drawText(texText, 0, countTotal, startX, verticalOffset, paint);

                if(countTotal == 0){ //空行
                    lineCount ++ ;
                    continue;
                }

                for (int start = 0; start < countTotal; ) {//自动换行
                    lineCount ++;
                    int count =  getPaint().breakText(texText, start, countTotal, true, mWidth, cutWidth);
                    start += count;
                }
            }
        }catch (Exception e){}
    }
}


