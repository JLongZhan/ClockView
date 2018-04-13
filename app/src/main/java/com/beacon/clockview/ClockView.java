package com.beacon.clockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;


/**
 * @author: F1331886
 * @date: 2018/4/12 0012.
 * @describe:
 */

public class ClockView extends View {

    private static final String TAG = "ClockView";
    private final static int DEFAULT_SIZE = 300;
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
    private int secondHandColor;
    private int minuteHandColor;
    private int hourHandColor;
    /**
     * 钟表内的颜色
     */
    private int clockColor;
    /**
     * 钟表外圈圆的颜色
     */
    private int clockCircleColor;
    /**
     * 钟表中心点的颜色
     */
    private int clockCenterPointColor;
    private float clockCircleWidth;
    private Paint mSecondPaint;
    private Paint mMinutePaint;
    private Paint mHourPaint;
    private Paint mClockCirclePaint;
    private Paint mPointPaint;
    private RectF mHourRect;
    private RectF mMinuteRect;
    private RectF mSecondRect;
    private Paint mClockPaint;
    private int mClockSize;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, 0);
        secondHandColor = ta.getColor(R.styleable.ClockView_secondHandColor, Color.RED);
        minuteHandColor = ta.getColor(R.styleable.ClockView_minuteHandColor, Color.BLACK);
        hourHandColor = ta.getColor(R.styleable.ClockView_hourHandColor, Color.BLACK);
        clockColor = ta.getColor(R.styleable.ClockView_clockColor, Color.LTGRAY);
        clockCircleColor = ta.getColor(R.styleable.ClockView_clockCircleColor, Color.BLACK);
        clockCenterPointColor = ta.getColor(R.styleable.ClockView_clockCenterPointColor, Color.parseColor("#F00000"));
        clockCircleWidth = ta.getDimension(R.styleable.ClockView_clockCircleWidth, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics()
        ));

        ta.recycle();

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
        mClockPaint.setStyle(Paint.Style.FILL);
        mClockPaint.setColor(clockColor);

        mClockCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClockCirclePaint.setStyle(Paint.Style.STROKE);
        mClockCirclePaint.setColor(clockCircleColor);
        mClockCirclePaint.setStrokeWidth(clockCircleWidth);

        mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourPaint.setColor(hourHandColor);
        mHourPaint.setStyle(Paint.Style.STROKE);
        mHourPaint.setStrokeWidth(6);

        mMinutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinutePaint.setColor(minuteHandColor);
        mMinutePaint.setStyle(Paint.Style.STROKE);
        mMinutePaint.setStrokeWidth(4);

        mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondPaint.setColor(secondHandColor);
        mSecondPaint.setStyle(Paint.Style.STROKE);
        mSecondPaint.setStrokeWidth(2);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointPaint.setStrokeWidth(16);
        mPointPaint.setColor(clockCenterPointColor);

        mHourRect = new RectF();
        mMinuteRect = new RectF();
        mSecondRect = new RectF();

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
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
        mClockSize = Math.min(width, height);
        setMeasuredDimension(mClockSize, mClockSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mClockSize / 2, mClockSize / 2, mClockSize / 2 - clockCircleWidth, mClockPaint);

        mSecondRect.left = mSecondRect.top = mClockSize / 8;
        mSecondRect.bottom = mSecondRect.right = mClockSize - mSecondRect.left;
        canvas.drawArc(mSecondRect, second, 0, true, mSecondPaint);

        mMinuteRect.left = mMinuteRect.top = mClockSize / 5;
        mMinuteRect.bottom = mMinuteRect.right = mClockSize - mMinuteRect.left;
        canvas.drawArc(mMinuteRect, minute, 0, true, mMinutePaint);

        mHourRect.left = mHourRect.top = mClockSize / 4;
        mHourRect.bottom = mHourRect.right = mClockSize - mHourRect.left;
        canvas.drawArc(mHourRect, hour, 0, true, mHourPaint);

        canvas.drawPoint(mClockSize / 2, mClockSize / 2, mPointPaint);


        mClockCirclePaint.setShader(new LinearGradient(mClockSize / 2, 0, mClockSize / 2,
                mClockSize, Color.GRAY, Color.BLACK, Shader.TileMode.CLAMP));
        canvas.drawCircle(mClockSize / 2, mClockSize / 2, mClockSize / 2 - clockCircleWidth, mClockCirclePaint);

        mHandler.sendEmptyMessageDelayed(0, 1000);
    }
}
