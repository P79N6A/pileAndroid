package com.swust.androidpile.mine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.swust.androidpile.R.color;

/**
 * Created by 怪蜀黍 on 2016/12/3.
 */

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 * 设置颜色在xml布局文件中由自定义属性配置参数指定
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {
    private int mBorderThickness = 0;
    private Context mContext;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderColor = 0;
    // 控件默认长、宽
    private int width = 0;
    private int height = 0;
    private int radius;
    private Bitmap bitmap;
    private Bitmap roundBitmap;

    //构造方法，参数上下文
    public RoundImageView(Context context) {
        super(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        width = getWidth(); //图片宽度
        height = getHeight(); //图片高度
        mBorderColor = this.getResources().getColor(color.white); //边框颜色
        mBorderThickness = 10; //边框厚度
        radius = (width < height ? width : height) / 2 - mBorderThickness; //圆半径=图片宽度 的一半-边框的厚度

        //初始化圆半径
        radius = (width < height ? width : height) / 2 - mBorderThickness;
        // 画圆
        drawCircleBorder(canvas, radius, mBorderColor);
        //画边框
        drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderColor);

        bitmap = ((BitmapDrawable) drawable).getBitmap(); //图片的Bitmap
        roundBitmap = getCroppedRoundBitmap(bitmap, radius); //修正过的图片Bitmap
        canvas.drawBitmap(roundBitmap, width / 2 - radius, height / 2 - radius, null); //canvas画修正过的图片
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);

        //画圆形的重点
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//取2圆绘制交集，显示上层
        canvas.drawBitmap(scaledSrcBmp, 0, 0, paint);

        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
            /* 去锯齿 */
        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
        paint.setColor(color);
            /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
            /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }
}
