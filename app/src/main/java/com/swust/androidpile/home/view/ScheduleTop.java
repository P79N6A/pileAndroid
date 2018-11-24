package com.swust.androidpile.home.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.swust.androidpile.utils.ToastUtil;

/**
 * Created by Administrator on 2017/3/9/009.
 */

public class ScheduleTop extends View {

    private Context context;
    private Paint paint;
    private String stationname = "西科大充电站"; //电站名
    private int soc = 25;//充电进度
    private float snHeight;
    private int screenWidth;
    private int screenHeight;
    private float outOvalWidth;
    private float dpDensity;
    private int outOvalHeight;
    private float inOvalWidth;
    private float inOvalHeight;
    private int outOvalItem;
    private int itemWidth;
    private int inItemHeight1;
    private int inItemHeight2;
    private int outItemYHeight1;
    private int outItemYHeight2;
    private int indexItemHeight1;
    private int socHeight;
    private int tipTextHeight;
    private int outHeight;
    private float outWidth;
    private float iWidth;
    private int outOvalRadius;
    private int inOvalRadius;
    private int inItemXWith;
    private int inItemYHeight1;
    private int inItemYHeight2;
    private int inItemYHeight;
    private boolean flag = false;
    private Canvas cv;

    public ScheduleTop(Context context) {
        this(context, null);
    }

    public ScheduleTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        dpDensity = context.getResources().getDisplayMetrics().density; //dp的密度值
    }

    /**
     * 重新绘制图
     * @param soc
     */
    public void setInvilate(String soc) {
        this.soc = Integer.parseInt(soc);
//        Log.i("test", "soc= "+soc+", out_v= "+out_v+", out_i= "+out_i);
        ScheduleTop.this.invalidate();//重新绘制
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);//去除画笔锯齿
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);//描边 + 内部不填充，不然就是内部全填充颜色Paint.Style.FILL
        paint.setColor(Color.GRAY);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (screenWidth == 0 || screenHeight == 0) {
            screenWidth = getMeasuredWidth();
            screenHeight = getMeasuredHeight();
        }
        //初始化画笔
        initPaint();
        //绘制图标的具体代码
        initSchedule(canvas);
    }

    /**
     * 画图
     * @param canvas
     */
    private void initSchedule(Canvas canvas) {
        /********************************************圆的设置**********************************************************/
        //设置外圆
        if (screenWidth < screenHeight) {
            outOvalRadius = (int) (screenWidth / 2 * 0.5);//设置外圆半径（屏宽的一半*0.6）
        } else {
            outOvalRadius = (int) (screenHeight / 2 * 0.5);//设置外圆半径
        }
        outOvalWidth = screenWidth / 2; //设置外圆X坐标（屏宽的一半）
        outOvalHeight = screenHeight / 2; //设置外圆Y坐标（屏高的一半）
        outOvalItem = ToastUtil.dp2px(context, 20); //外圆线宽（20dp）
        paint.setStrokeWidth(outOvalItem);//设置外圆线宽
        canvas.drawCircle(outOvalWidth, outOvalHeight, outOvalRadius, paint);

        int left = (int) (screenWidth / 2 - outOvalRadius);
        int top = screenHeight / 2 - outOvalRadius;
        int right = screenWidth - left;
        int bottom = screenHeight - top;
        //覆盖圆
        paint.setColor(Color.GREEN);//重置画笔颜色
        RectF r3 = new RectF(left, top, right, bottom);
        canvas.drawArc(r3, 270, Float.parseFloat("" + 3.6 * soc), false, paint);//绘制扇形(soc=1则代表3.6度)
        //内圆(半径：外圆半径的75%,线宽2dp)
        paint.setStrokeWidth(ToastUtil.dp2px(context, 2));//设置内圆线宽
        inOvalWidth = outOvalWidth;//内圆X坐标
        inOvalHeight = outOvalHeight;//内圆Y坐标
        inOvalRadius = (int) (outOvalRadius * 0.75);
        canvas.drawCircle(inOvalWidth, inOvalHeight, inOvalRadius, paint);

        paint.setColor(Color.GRAY);//重置画笔颜色
        //圆外环小格子设置（线宽=1dp)
        paint.setStrokeWidth(ToastUtil.dp2px(context, 1));//设置线宽
        paint.setColor(Color.rgb(96, 96, 96));
        inItemXWith = (int) inOvalWidth; //表盘中心X坐标
        inItemYHeight = (int) inOvalHeight;//表盘中心Y坐标
        //格子长度相差7dp
        inItemYHeight1 = (int) (top - outOvalItem / 2 - 6 * dpDensity);
        inItemYHeight2 = (int) (inItemYHeight1 - 7 * dpDensity);
        for (int i = 1; i <= 40; i++) {
            //保存画布当前状态
            canvas.save();
            //指定旋转角度与旋转点
            canvas.rotate(360 / 40 * i, inItemXWith, inItemYHeight);
            //绘制表盘
            canvas.drawLine(inItemXWith, inItemYHeight1, inItemXWith, inItemYHeight2, paint);
            //恢复开始位置
            canvas.restore();
        }

        //圆外环大格子设置
        outItemYHeight1 = inItemYHeight1;
        outItemYHeight2 = (int) (inItemYHeight1 - 14 * dpDensity);
        indexItemHeight1 = ToastUtil.dp2px(context, 68);
        paint.setColor(Color.GRAY);
        for (int i = 1; i <= 10; i++) {
            paint.setStrokeWidth(ToastUtil.dp2px(context, 2));//设置线宽
            //保存画布当前状态
            canvas.save();
            //指定旋转角度与旋转点
            canvas.rotate(360 / 10 * i, inItemXWith, inItemYHeight);
            //绘制表盘
            canvas.drawLine(inItemXWith, outItemYHeight1, inItemXWith, outItemYHeight2, paint);
            //绘制文字
            paint.setStrokeWidth(1);//设置字体线宽
            paint.setStyle(Paint.Style.FILL);//设置
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(ToastUtil.dp2px(context, 12));
            if (i == 10) {
                canvas.drawText("0", inOvalWidth, outItemYHeight2 - ToastUtil.dp2px(context, 3), paint);//数字与格子距离3dp
                break;
            }
            canvas.drawText("" + i, inOvalWidth, outItemYHeight2 - ToastUtil.dp2px(context, 3), paint);//数字与格子距离3dp
            //恢复开始位置
            canvas.restore();
        }

        //设置常量文本
        String socInfo = "已充电量";
        paint.setColor(Color.GRAY);
        paint.setTextSize(ToastUtil.sp2px(context, 12));//设置“已充电量”字体大小
        Rect textBounds = new Rect();
        paint.getTextBounds(socInfo, 0, socInfo.length(), textBounds);
        canvas.drawText(socInfo, inOvalWidth, inOvalHeight - textBounds.height() / 2 - ToastUtil.dp2px(context, 10), paint);

        //不断更新充电进度
        String socText = "" + soc + "%";
        paint.setColor(Color.GREEN);
        paint.setTextSize(ToastUtil.sp2px(context, 30));//设置“充电进度”字体大小
        Rect socBounds = new Rect();
        paint.getTextBounds(socText, 0, socText.length(), socBounds);
        canvas.drawText(socText, inOvalWidth, inOvalHeight + socBounds.height() / 2 + ToastUtil.dp2px(context, 10), paint);

        //底部绘制一条线
        paint.setStrokeWidth(ToastUtil.dp2px(context, 1));
        canvas.drawLine(0, screenHeight, screenWidth, screenHeight, paint);

    }

}
