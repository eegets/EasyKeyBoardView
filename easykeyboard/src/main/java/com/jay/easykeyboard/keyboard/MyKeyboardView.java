package com.jay.easykeyboard.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import java.util.List;

//核心类，承担绘制工作
public class MyKeyboardView extends KeyboardView {

    private Drawable mKeyDrawable;
    private Drawable mKeyPositionDrawable;
    private Rect rect;
    private Paint paint;

    /**
     * 某一个Text的色值
     */
    private int someOneTextColor;
    /**
     * 通用的textColor色值
     */
    private int textColor = Color.BLACK;

    /**
     * 一行展示多少列，默认为3列
     */
    private int column = 3;

    /**
     * 纵向间距，靠近最左侧屏幕间距以及靠近最右侧屏幕间距均为`columnPadding`值，中间的间距为`columnPadding/2`
     */
    private int columnPadding = 16;

    /**
     * 横向间距，靠近最顶部屏幕间距以及靠近最底部屏幕间距均为`rowPadding`值，中间的间距为`rowPadding/2`
     */
    private int rowPadding = 16;

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources(context);
    }

    public MyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context);
    }

    private void initResources(Context context) {
        Resources res = context.getResources();
        rect = new Rect();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(res.getColor(android.R.color.black));

        setViewBackgroundColor(Color.TRANSPARENT);

    }

    public void setKeyDrawable(Drawable mKeyDrawable){
        this.mKeyDrawable = mKeyDrawable;
        invalidate();
    }

    private int position;

    /**
     * 给单独某一个key设置Drawable
     * @param mKeyPositionDrawable
     */
    public void setSomeOneKeyDrawable(Drawable mKeyPositionDrawable){
        this.mKeyPositionDrawable = mKeyPositionDrawable;
        invalidate();
    }

    public Paint getPaint(){
        return paint;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
        if (paint==null){
            throw new NullPointerException("Paint is not null");
        }
        invalidate();
    }

    void addColumn(int row) {
        this.column = row;
    }

    /**
     * 纵向间距
     * @param columnPadding
     */
    public void columnPadding(int columnPadding) {
        this.columnPadding = columnPadding;
    }

    /**
     * 横向间距
     * @param rowPadding
     */
    public void rowPadding(int rowPadding) {
        this.rowPadding = rowPadding;
    }

    /**
     * 设置字体大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        paint.setTextSize(textSize);
    }

    /**
     * 给单独某一个key设置textColor
     * @param color
     */
    public void setSomeOneTextColor(@ColorInt int color) {
        this.someOneTextColor = color;
    }

    /**
     * 设置字体大小
     * @param color
     */
    public void setTextColor(@ColorInt int color) {
        textColor = color;
    }

    /**
     * 设置背景色，默认为白色
     * @param backgroundColor
     */
    public void setViewBackgroundColor(@ColorInt int backgroundColor) {
        setBackgroundColor(backgroundColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        List<Key> keys = getKeyboard().getKeys();
        for (int i = 0; i < keys.size(); i++) {
            Key key = keys.get(i);
            System.out.println("MyKeyboardView onDraw key: " + key + "; i: " + (i) + "; i%column: " + (i) % column);

            canvas.save();
            int offsetY = 0;
            if (key.y == 0) {
                offsetY = 1;
            }

            int drawY = key.y + offsetY;

            if (i % column == 0) {  //第一列

                rect.left = key.x + columnPadding;
                rect.right = key.x + key.width - columnPadding / 2;

            } else if (column - i % column == 1) {  //最后一列

                rect.left = key.x + columnPadding / 2;
                rect.right = key.x + key.width - columnPadding;

            } else {   //中间的某一列

                rect.left = key.x + columnPadding / 2;
                rect.right = key.x + key.width - columnPadding / 2;

            }

            if (i < column) {  //第一行

                rect.top = drawY + rowPadding / 2;
                rect.bottom = key.y + key.height - rowPadding / 2;

            } else if (keys.size() - i <= column) {  //最后一行

                rect.top = drawY + rowPadding / 2;
                rect.bottom = key.y + key.height - rowPadding / 2;

            } else {  //中间某一行

                rect.top = drawY + rowPadding / 2;
                rect.bottom = key.y + key.height - rowPadding / 2;

            }

            if (mKeyDrawable!=null) {
                canvas.clipRect(rect);
                int[] state = key.getCurrentDrawableState();
                //设置按压效果
                mKeyDrawable.setState(state);
                //设置边距
                mKeyDrawable.setBounds(rect);
                mKeyDrawable.draw(canvas);
            }

            if (mKeyPositionDrawable != null && i == 11) {
                canvas.clipRect(rect);
                int[] state = key.getCurrentDrawableState();
                //设置按压效果
                mKeyPositionDrawable.setState(state);
                //设置边距
                mKeyPositionDrawable.setBounds(rect);
                mKeyPositionDrawable.draw(canvas);

                paint.setColor(someOneTextColor);
            } else {
                paint.setColor(textColor);
            }

            if (key.label != null) {
                canvas.drawText(
                        key.label.toString(),
                        key.x + (key.width / 2),
                        drawY + (key.height + paint.getTextSize() - paint.descent()) / 2,
                        paint);

            }

            if (key.icon != null) {
                drawIcon(key, canvas, drawY);
            }
            canvas.restore();
        }
    }


    private void drawIcon(Key key, Canvas canvas, int drawY) {
        int intrinsicWidth = key.icon.getIntrinsicWidth();
        int intrinsicHeight = key.icon.getIntrinsicHeight();

        final int drawableX = key.x + (key.width - intrinsicWidth) / 2;
        final int drawableY = drawY + (key.height - intrinsicHeight) / 2;

        key.icon.setBounds(
                drawableX, drawableY, drawableX + intrinsicWidth,
                drawableY + intrinsicHeight);

        key.icon.draw(canvas);
    }
}
