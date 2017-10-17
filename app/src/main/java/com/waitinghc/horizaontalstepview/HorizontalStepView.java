package com.waitinghc.horizaontalstepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by:     何超
 * Create Time:   2017/9/27
 * Brief Desc:    OTC理财详情步骤view
 */

public class HorizontalStepView extends View {

    private Paint mTextPaint;//文字画笔
    private Paint mLinePaint;//默认线画笔
    private Paint mCirclePaint;//默认圆环画笔
    private Paint mInnerCirclePaint;//默认内部圆画笔
    private Paint mIndicatePaint;//圆点上方指示线
    private float mRadius = 15f;//默认圆点半径
    private float mStrokeWidth = 5f;//默认画笔宽度
    private float mInnerRadius = mRadius - mStrokeWidth;//内部圆半径
    private float mTextSize = 35f;//默认文字大小
    private float mVerticalSpacing = 30f;//垂直间距
    private int mTextColor;//默认文字颜色
    private int mDefaultLineColor;//默认进度颜色
    private int mCurrentLineColor;//当前进度颜色
    private int mDefaultCircleColor;//圆环默认颜色
    private int mCircleColor;//圆环当前颜色
    private int mDefaultInnerCircleColor;//默认内部圆颜色
    private int mInnerCircleColor;//内部圆颜色
    private int mIndicateColor;//指示线颜色
    private int mIndicateStart = -1, mIndicateEnd = -1;//圆点上方指示线开始位置，结束位置
    private String mCenterText;//中线上文字（计息满xx天）
    private List<Item> mItems;
    private float mCurrentItem = -1;//当前位置
    private float mLineWidth = 0f;

    public HorizontalStepView(Context context) {
        this(context, null);
    }

    public HorizontalStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalStepView, 0, 0);

        mTextSize = typedArray.getDimension(R.styleable.HorizontalStepView_textSize, mTextSize);
        mTextColor = typedArray.getColor(R.styleable.HorizontalStepView_textColor, ContextCompat.getColor(context, R.color.colorAccent));
        mDefaultLineColor = typedArray.getColor(R.styleable.HorizontalStepView_defaultLineColor, Color.parseColor("#FFDDB9"));
        mCurrentLineColor = typedArray.getColor(R.styleable.HorizontalStepView_currentLineColor, Color.parseColor("#ff6800"));
        mDefaultCircleColor = typedArray.getColor(R.styleable.HorizontalStepView_defaultCircleColor, mDefaultLineColor);
        mCircleColor = typedArray.getColor(R.styleable.HorizontalStepView_currentCircleColor, mCurrentLineColor);
        mDefaultInnerCircleColor = typedArray.getColor(R.styleable.HorizontalStepView_defaultInnerCircleColor, Color.parseColor("#FFFFFF"));
        mInnerCircleColor = typedArray.getColor(R.styleable.HorizontalStepView_currentInnerCircleColor, mDefaultInnerCircleColor);
        mIndicateColor = typedArray.getColor(R.styleable.HorizontalStepView_indicateColor, Color.parseColor("#E2E2E2"));

        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {

        mItems = new ArrayList<>();

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);

        mLinePaint = new Paint();
        mLinePaint.setColor(mDefaultLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mStrokeWidth);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mDefaultCircleColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(mDefaultInnerCircleColor);
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStrokeWidth(mStrokeWidth);
        mInnerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mIndicatePaint = new Paint();
        mIndicatePaint.setColor(mIndicateColor);
        mIndicatePaint.setAntiAlias(true);
        mIndicatePaint.setStrokeWidth(3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (mLineWidth * 4 + (mRadius + mStrokeWidth) * 2 * 4);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mTextPaint.setTextSize(mTextSize);
            String text = "测试";
            Rect rect = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), rect);
            float textHeight = rect.height();
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = (int) (desired + textHeight + mRadius * 2 + mStrokeWidth * 2 + mVerticalSpacing * 2);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();

        Rect defaultTextRect = new Rect();
        mTextPaint.getTextBounds("测试", 0, 2, defaultTextRect);


        float marginTop = defaultTextRect.height() + mStrokeWidth + 2 + mVerticalSpacing + mRadius;

        //绘制默认进度线
        canvas.drawLine(mRadius, marginTop, width - mRadius, marginTop, mLinePaint);

        int length = mItems.size();
        if (length == 0 || width == 0)
            return;
        float divider = width / length;//默认分隔距离

        float currentLineStopX = 0;
        int offsetPosition = 3, lastPosition = length - 1;

        //绘制当前进度线
        if (length >= 3) {
            for (int i = 0; i < length; i++) {
                Item item = mItems.get(i);
                if (item.offset == 2) {
                    offsetPosition = i;
                    break;
                }
            }
            if (mItems.get(offsetPosition).offset == 1) {
                if (mCurrentItem > offsetPosition && mCurrentItem < lastPosition) {
                    currentLineStopX = lastPosition * divider;
                } else if (mCurrentItem >= lastPosition) {
                    currentLineStopX = length * divider - mRadius;
                } else {
                    currentLineStopX = mCurrentItem * divider;
                    if (mCurrentItem < 1)
                        currentLineStopX += mRadius;
                }
            } else {
                if (mCurrentItem > offsetPosition - 1 && mCurrentItem < offsetPosition) {
                    currentLineStopX = offsetPosition * divider;
                }
                else if (mCurrentItem == offsetPosition) {
                    currentLineStopX = lastPosition * divider;
                }
                else if (mCurrentItem > offsetPosition && mCurrentItem < lastPosition) {
                    currentLineStopX = (offsetPosition+1)* divider;
                }
                else if (mCurrentItem >= lastPosition) {
                    currentLineStopX = length * divider - mRadius;
                }
                else {
                    currentLineStopX = mCurrentItem * divider;
                    if (mCurrentItem < 1) {
                        currentLineStopX += mRadius;
                    }
                }
            }
        }

        if (mCurrentItem >= 0) {
            mLinePaint.setColor(mCurrentLineColor);
            canvas.drawLine(mRadius, marginTop, currentLineStopX, marginTop, mLinePaint);
        }

        //重置进度线默认色
        mLinePaint.setColor(mDefaultLineColor);

        for (int i = 0; i < length; i++) {
            Item item = mItems.get(i);

            //获取文字高度宽度
            mTextPaint.getTextBounds(item.title, 0, item.title.length(), defaultTextRect);

            //计算文字左边偏移量
            int titleTextLeftOffset = i == 0 ? 0 : (i < lastPosition ? defaultTextRect.width() / 2 : defaultTextRect.width());

            //计算当前需要绘制的距离倍数据
            int offset = i == lastPosition ? length : ((i == offsetPosition && item.offset > 1) ? offsetPosition : i);

            if (i >= (offsetPosition + 1) && i != lastPosition) {
                offset += 1;
            }

            //绘制标题文字
            canvas.drawText(item.title, divider * offset - titleTextLeftOffset - 1, defaultTextRect.height(), mTextPaint);

            //计算圆点绘制位置
            int circleLeftOffset = i == 0 ? (int) -mStrokeWidth : (i < lastPosition ? (int) mRadius : (int) (mRadius * 2 + mStrokeWidth));
            int innerCircleLeftOffset = i == 0 ? (int) -mStrokeWidth * 2 : (i < lastPosition ? (int) mInnerRadius : (int) (mInnerRadius * 2 + mStrokeWidth * 2));

            if (i <= mCurrentItem && mCurrentItem >= 0) {
                mCirclePaint.setColor(mCircleColor);
                mInnerCirclePaint.setColor(mInnerCircleColor);
            } else {
                mCirclePaint.setColor(mDefaultCircleColor);
                mInnerCirclePaint.setColor(mDefaultInnerCircleColor);
            }


            canvas.drawCircle(divider * offset + mRadius - circleLeftOffset, marginTop, mRadius, mCirclePaint);
            canvas.drawCircle(divider * offset + mInnerRadius - innerCircleLeftOffset, marginTop, mInnerRadius, mInnerCirclePaint);

            //获取底部日期高度、宽度
            mTextPaint.getTextBounds(item.date, 0, item.date.length(), defaultTextRect);
            int dateTextLeftOffset = i == 0 ? -2 : (i < lastPosition ? defaultTextRect.width() / 2 : defaultTextRect.width());

            //绘制底部日期
            canvas.drawText(item.date, divider * offset - dateTextLeftOffset - 1, defaultTextRect.height() + marginTop + mRadius + mVerticalSpacing, mTextPaint);

            //绘制计息日提示文字
            if (!TextUtils.isEmpty(mCenterText) && i == offsetPosition) {
                mTextPaint.getTextBounds(mCenterText, 0, mCenterText.length(), defaultTextRect);

                canvas.drawText(mCenterText, divider * (item.offset > 1 ? 2 : 3) - defaultTextRect.width() / 2, titleTextLeftOffset + mRadius * 2, mTextPaint);
            }

            //绘制指示线
            if (mIndicateStart == i || mIndicateEnd == i) {
                canvas.drawLine(divider * offset + mRadius - circleLeftOffset, defaultTextRect.height() + 15, divider * offset + mRadius - circleLeftOffset, marginTop - mRadius - mStrokeWidth - 1, mIndicatePaint);
            }
        }
    }

    /**
     * 设置进度显示数据源
     *
     * @param items
     */
    public void setItems(List<Item> items) {
        if (items != null && items.size() > 0)
            mItems.addAll(items);

        invalidate();
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }


    /**
     * 设置默认线、点颜色
     *
     * @param color
     */
    public void setDefaultColor(int color) {
        invalidate();
    }

    /**
     * 设置当前线、点颜色
     *
     * @param color
     */
    public void setCurrentColor(int color) {
        mCurrentLineColor = color;
        invalidate();
    }

    /**
     * 设置当前选中项
     *
     * @param position
     */
    public void setCurrentItem(float position) {
        if (position < 0) position = -1;
        if (position >= mItems.size()) position = mItems.size() - 1;
        mCurrentItem = position;

        invalidate();
    }

    /**
     * 绘制圆点上方指示线
     *
     * @param start 指示线开始位置
     * @param end   指示线结束位置
     */
    public void setIndicate(int start, int end) {
        mIndicateStart = start;
        mIndicateEnd = end;
        invalidate();
    }

    public void setCenterText(String text) {
        mCenterText = text;
        invalidate();
    }

    public static class Item {

        public String date;
        public String title;
        public int offset = 1;

        public Item() {
        }

        public Item(String title, String date) {
            this.title = title;
            this.date = date;
        }

        public Item(String title, String date, int offset) {
            this.title = title;
            this.date = date;
            this.offset = offset;
        }
    }
}
