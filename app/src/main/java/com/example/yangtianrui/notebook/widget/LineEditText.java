package com.example.yangtianrui.notebook.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

/**
 * Created by yangtianrui on 16-6-12.
 * 实现信纸效果
 */
public class LineEditText extends EditText {
    private int color;

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        //颜色默认时浅灰色
        color = Color.parseColor("#cccccc");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int lineHeight = this.getLineHeight();
        Paint mPaint = getPaint();
        mPaint.setColor(color);
        int topPadding = this.getPaddingTop();
        int leftPadding = this.getPaddingLeft();
        float textSize = getTextSize();
        setGravity(Gravity.LEFT | Gravity.TOP);
        int y = (int) (topPadding + textSize);
        for (int i = 0; i < getLineCount(); i++) {
            canvas.drawLine(leftPadding, y + 10, getRight() - leftPadding, y + 10, mPaint);
            y += lineHeight;
        }
        canvas.translate(0, 0);
        super.onDraw(canvas);
    }

    /**
     * 设置记事本的编辑框背景线条颜色
     *
     * @param color int type【代表颜色的整数】
     */
    public void setBGColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * 设置记事本的编辑框背景线条颜色
     *
     * @param colorId int type【代表颜色的资源id】
     */
    public void setBGColorId(int colorId) {
        this.color = getResources().getColor(colorId);
        invalidate();
    }

}
