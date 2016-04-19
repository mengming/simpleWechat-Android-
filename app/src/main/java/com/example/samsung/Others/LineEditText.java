package com.example.samsung.Others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText{

    private Paint paint;

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int lineCount = getLineCount();
        int lineHeight = getLineHeight();
        for (int i=0;i<lineCount;i++) {
            int lineY = (i+1)*lineHeight;
            canvas.drawLine(0,lineY,this.getWidth(),lineY,paint);
        }
    }
}
