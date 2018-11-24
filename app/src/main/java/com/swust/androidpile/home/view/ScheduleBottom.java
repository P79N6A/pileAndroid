package com.swust.androidpile.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.swust.androidpile.utils.ToastUtil;

/**
 * Created by Administrator on 2017/3/9/009.
 */

public class ScheduleBottom extends View {

    private Context context;
    private Paint paint;
    private float dpDensity;
    private int screenWidth;
    private int screenHeight;
    private int outHeight;
    private int iWidth;
    private int outWidth;
    private String out_v;
    private String out_i;

    public ScheduleBottom(Context context) {
        this(context, null);
    }

    public ScheduleBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        this.context = context;
        dpDensity = context.getResources().getDisplayMetrics().density; //dp的密度值
    }

    public void setInvilate(String out_v, String out_i) {
        this.out_v = out_v;
        this.out_i = out_i;
        ScheduleBottom.this.invalidate();//重新绘制
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);//去除画笔锯齿
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);//描边 + 内部不填充，不然就是内部全填充颜色Paint.Style.FILL
        paint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (screenWidth == 0 || screenHeight == 0) {
            screenWidth = getMeasuredWidth();
            screenHeight = getMeasuredHeight();
//	        System.out.println("++++++++++++++++++++++++++++"+width+", "+height);
        }

        //绘制一条竖线
        float marginTop = screenHeight / 5;
        for (float i = marginTop; i < screenHeight / 5 * 3; i++) {//距离顶部为高度的五分之一，画到高度的五分之三，线条长度为2
            canvas.drawLine(screenWidth / 2, i, screenWidth / 2 + 2, i, paint);
        }

        //绘制电压电流
        String vTitle = "电压";
        String cTitle = "电流";
        paint.setTextSize(ToastUtil.sp2px( context,15));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(0);

        outHeight = screenHeight / 5 * 2;
        outWidth = screenWidth / 4;
        iWidth = screenWidth / 4 * 3;

        Rect vcBounds = new Rect();
        paint.getTextBounds(vTitle, 0, vTitle.length(), vcBounds);
        canvas.drawText(vTitle, outWidth, outHeight - vcBounds.height() / 2 - ToastUtil.dp2px(context,10), paint);

        paint.getTextBounds(cTitle, 0, cTitle.length(), vcBounds);
        canvas.drawText(cTitle, iWidth, outHeight - vcBounds.height() / 2 - ToastUtil.dp2px(context,10), paint);

        //绘制电压电流值
        paint.setColor(Color.WHITE);
        String vValue = out_v+"伏";
        String cValue = out_i+"安";

        Rect valueBounds = new Rect();
        paint.getTextBounds(vValue, 0, vValue.length(), valueBounds);
        canvas.drawText(vValue, outWidth, outHeight + valueBounds.height() / 2 + ToastUtil.dp2px(context,10), paint);

        paint.getTextBounds(cValue, 0, cValue.length(), valueBounds);
        canvas.drawText(cValue, iWidth, outHeight + valueBounds.height() / 2 + ToastUtil.dp2px(context,10), paint);

    }

}
