package com.yhy.version.helper.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.yhy.version.helper.R;

import java.util.Locale;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2018-01-30 10:41
 * version: 1.0.0
 * desc   : 带数字文本的进度条
 */
public class NumberProgressBar extends View {
    // 总宽高
    private float mWidth;
    private float mHeight;

    // 进度条高度，默认8dp
    private float mBarHeight;
    // 进度条颜色，默认#cccccc
    private int mBarColor;
    // 当前进度，默认0.0f
    @FloatRange(from = 0.0f, to = 1.0f)
    private float mProgress;
    // 进度颜色，默认#444444
    private int mProgressColor;
    // 保留小数位数，默认0
    private int mProgressFormatPoint;
    // 边框左右内边距，默认12dp
    private float mPaddingLeftRight;
    // 边框上下内边距，默认4dp
    private float mPaddingTopBottom;
    // 边框内部填充颜色，默认#ffffff
    private int mTextBoxInnerColor;
    // 边框宽度，默认2dp
    private float mTextBoxBorderWidth;
    // 边框颜色，默认#444444
    private int mTextBoxBorderColor;
    // 字体大小，默认12sp
    private float mTextSize;
    // 字体颜色，默认#444444
    private int mTextColor;

    private String mText;
    private float mTextWidth;
    private float mTextHeight;

    private Paint mBarPaint;
    private Paint mBoxPaint;
    private Paint mBoxBorderPaint;
    private Paint mTextPaint;

    private float mLastProgress;
    private OnProgressChangedListener mListener;

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberProgressBar);
        mBarHeight = ta.getDimensionPixelSize(R.styleable.NumberProgressBar_npb_bar_height, dp2px(8));
        mBarColor = ta.getColor(R.styleable.NumberProgressBar_npb_bar_color, Color.LTGRAY);
        mProgress = ta.getFloat(R.styleable.NumberProgressBar_npb_progress, 0.0f);
        mProgressColor = ta.getColor(R.styleable.NumberProgressBar_npb_progress_color, Color.DKGRAY);
        mProgressFormatPoint = ta.getInteger(R.styleable.NumberProgressBar_npb_progress_format_point, 0);
        mPaddingLeftRight = ta.getDimensionPixelSize(R.styleable.NumberProgressBar_npb_text_box_padding_left_right, dp2px(12));
        mPaddingTopBottom = ta.getDimensionPixelSize(R.styleable.NumberProgressBar_npb_text_box_padding_top_bottom, dp2px(4));
        mTextBoxInnerColor = ta.getColor(R.styleable.NumberProgressBar_npb_text_box_inner_color, Color.WHITE);
        mTextBoxBorderWidth = ta.getDimensionPixelSize(R.styleable.NumberProgressBar_npb_text_box_border_width, dp2px(2));
        mTextBoxBorderColor = ta.getColor(R.styleable.NumberProgressBar_npb_text_box_border_color, mProgressColor);
        mTextSize = ta.getDimensionPixelSize(R.styleable.NumberProgressBar_npb_text_size, sp2px(12));
        mTextColor = ta.getColor(R.styleable.NumberProgressBar_npb_text_color, mProgressColor);
        ta.recycle();

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setAntiAlias(true);

        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setStyle(Paint.Style.FILL);
        mBoxPaint.setStrokeWidth(mTextBoxBorderWidth);
        mBoxPaint.setColor(mTextBoxInnerColor);

        mBoxBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxBorderPaint.setAntiAlias(true);
        mBoxBorderPaint.setStyle(Paint.Style.STROKE);
        mBoxBorderPaint.setStrokeWidth(mTextBoxBorderWidth);
        mBoxBorderPaint.setColor(mTextBoxBorderColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 计算文本
        calculateText();

        mWidth = getMeasuredWidth();
        mHeight = Math.max(mBarHeight, mTextHeight);
        // 设置宽高为包含内容宽高
        setMeasuredDimension((int) (mWidth + 0.5f), (int) (mHeight + 0.5f));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mProgress < 0.0f || mProgress > 1.0f) {
            return;
        }

        // 计算文本
        calculateText();
        // 绘制进度条
        drawBar(canvas);
        // 绘制进度
        drawProgress(canvas);
        // 绘制文本盒子
        drawTextBox(canvas);
    }

    /**
     * 设置进度
     *
     * @param progress 进度值
     */
    public void setProgress(@FloatRange(from = 0.0f, to = 1.0f) float progress) {
        if (progress < 0.0f || progress > 1.0f) {
            throw new IllegalArgumentException("The 'progress' must between 0.0 and 1.0.");
        }
        mProgress = progress;
        // 强制刷新重绘界面
        postInvalidate();
    }

    /**
     * 获取当前进度
     *
     * @return 当前进度
     */
    public float getProgress() {
        return mProgress;
    }

    private void drawTextBox(Canvas canvas) {
        // 根据当前进度计算进度的x坐标
        float progressWidth = mWidth * mProgress;
        float progressX = progressWidth < mTextWidth / 2 ? 0 : progressWidth > mWidth - mTextWidth / 2 ? mWidth - mTextWidth : progressWidth - mTextWidth / 2;

        // 盒子区域
        RectF rect = new RectF(mTextBoxBorderWidth / 2 + progressX, mTextBoxBorderWidth / 2, mTextWidth - mTextBoxBorderWidth / 2 + progressX, mTextHeight - mTextBoxBorderWidth / 2);
        // 绘制盒子内部，即第一层底色
        canvas.drawRoundRect(rect, mTextHeight / 2, mTextHeight / 2, mBoxPaint);
        // 再绘制盒子边框
        canvas.drawRoundRect(rect, mTextHeight / 2, mTextHeight / 2, mBoxBorderPaint);

        // 计算文本参考线坐标
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (int) ((mTextHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);
        // mTextPaint设置为居中，所以这里的x为mTextWidth / 2
        canvas.drawText(mText, mTextWidth / 2 + progressX, baseline, mTextPaint);
    }

    private void drawProgress(Canvas canvas) {
        float y = (mHeight - mBarHeight) / 2;
        float progressWidth = mWidth * mProgress;

        RectF rect = new RectF(0, y, progressWidth, y + mBarHeight);
        mBarPaint.setColor(mProgressColor);
        canvas.drawRoundRect(rect, mBarHeight / 2, mBarHeight / 2, mBarPaint);

        // 回调进度变化监听器
        if (mLastProgress != mProgress) {
            if (null != mListener) {
                mListener.onChanged(this, mProgress);
            }
            mLastProgress = mProgress;
        }
    }

    private void drawBar(Canvas canvas) {
        float y = (mHeight - mBarHeight) / 2;
        RectF rect = new RectF(0, y, mWidth, y + mBarHeight);
        mBarPaint.setColor(mBarColor);
        canvas.drawRoundRect(rect, mBarHeight / 2, mBarHeight / 2, mBarPaint);
    }

    private void calculateText() {
        // 格式化进度字符串，并测量得到字符串宽高大小
        mText = String.format(Locale.getDefault(), "%." + mProgressFormatPoint + "f", mProgress * 100) + "%";
        Rect textRect = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), textRect);
        mTextWidth = textRect.width() + mTextBoxBorderWidth * 2 + mPaddingLeftRight * 2;
        mTextHeight = textRect.height() + mTextBoxBorderWidth * 2 + mPaddingTopBottom * 2;
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpVal, getContext().getResources().getDisplayMetrics());
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置进度变化监听器
     *
     * @param listener 监听器
     */
    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        mListener = listener;
    }

    /**
     * 进度变化监听器
     */
    public interface OnProgressChangedListener {

        /**
         * 进度变化回调
         *
         * @param progressBar 当前进度条
         * @param progress    当前进度
         */
        void onChanged(NumberProgressBar progressBar, float progress);
    }
}
