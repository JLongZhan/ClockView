package com.beacon.clockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;


/**
 * @author: F1331886
 * @date: 2018/4/12 0012.
 * @describe:
 */

public class ClockView extends View {

    private int mWidth;
    private final static int DEFAULT_SIZE = 300;
    private static final String TAG = "ClockView";
    private Paint mClockPaint;
    private int mClockWidth = 12;
    private final static int DEFAULT_COLOR = Color.BLACK;
    /**
     * 每一秒秒针要移动的角度
     */
    private final static float SECOND_ANGLE = 6;
    /**
     * 每一秒分钟要移动的距离
     */
    private final static float MINUTE_ANGLE = 0.1f;
    /**
     * 每一秒时钟要移动的角度
     */
    private final static float HOUR_ANGLE = 0.0083f;

    /**
     * 默认旋转了90度
     */
    private final static float DEFAULT_ANGLE = 90;


    private float hour;
    private float minute;
    private float second;
    private Paint mSecondPaint;
    private Paint mMinutePaint;
    private Paint mHourPaint;
    private Paint mPointPaint;
    private RectF mHourRect;
    private RectF mMinuteRect;
    private RectF mSecondRect;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            second += SECOND_ANGLE;
            minute += MINUTE_ANGLE;
            hour += HOUR_ANGLE;
            invalidate();
        }
    };

    /**
     * 初始化操作
     */
    private void init() {

        mClockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClockPaint.setStyle(Paint.Style.STROKE);
        mClockPaint.setColor(DEFAULT_COLOR);
        mClockPaint.setStrokeWidth(mClockWidth);

        mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourPaint.setColor(Color.BLACK);
        mHourPaint.setStyle(Paint.Style.STROKE);
        mHourPaint.setStrokeWidth(6);

        mMinutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinutePaint.setColor(Color.BLACK);
        mMinutePaint.setStyle(Paint.Style.STROKE);
        mMinutePaint.setStrokeWidth(4);

        mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondPaint.setColor(Color.RED);
        mSecondPaint.setStyle(Paint.Style.STROKE);
        mSecondPaint.setStrokeWidth(2);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointPaint.setStrokeWidth(16);
        mPointPaint.setColor(Color.parseColor("#F00000"));

        mHourRect = new RectF();

        mMinuteRect = new RectF();

        mSecondRect = new RectF();

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(System.currentTimeMillis());
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
//        将时分秒转换为旋转角度
        hour = (hour * 3600 + minute * 60 + second) * HOUR_ANGLE - DEFAULT_ANGLE;
        minute = (minute * 60 + second) * MINUTE_ANGLE - DEFAULT_ANGLE;
        second = second * SECOND_ANGLE - DEFAULT_ANGLE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            width = DEFAULT_SIZE;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = DEFAULT_SIZE;
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        mSecondRect.left = mSecondRect.top = getMeasuredHeight() / 8;
        mSecondRect.bottom = mSecondRect.right = getMeasuredWidth() - mSecondRect.left;
        canvas.drawArc(mSecondRect, second, 0, true, mSecondPaint);

        mMinuteRect.left = mMinuteRect.top = getMeasuredHeight() / 5;
        mMinuteRect.bottom = mMinuteRect.right = getMeasuredWidth() - mMinuteRect.left;
        canvas.drawArc(mMinuteRect, minute, 0, true, mMinutePaint);

        mHourRect.left = mHourRect.top = getMeasuredHeight() / 4;
        mHourRect.bottom = mHourRect.right = getMeasuredWidth() - mHourRect.left;
        canvas.drawArc(mHourRect, hour, 0, true, mHourPaint);

        canvas.drawPoint(getMeasuredHeight() / 2, getMeasuredHeight() / 2, mPointPaint);


        mClockPaint.setShader(new LinearGradient(getMeasuredHeight() / 2, 0, getMeasuredWidth() / 2,
                getMeasuredHeight(), Color.parseColor("#777777"), Color.BLACK, Shader.TileMode.CLAMP));
        canvas.drawCircle(getMeasuredHeight() / 2, getMeasuredHeight() / 2, getMeasuredHeight() / 2 - mClockWidth, mClockPaint);

        mHandler.sendEmptyMessageDelayed(0, 1000);
    }
}
