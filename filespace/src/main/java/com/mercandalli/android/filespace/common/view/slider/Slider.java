package com.mercandalli.android.filespace.common.view.slider;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mercandalli.android.filespace.R;

/**
 * Created by Jonathan on 23/09/2015.
 * https://github.com/navasmdc/MaterialDesignLibrary
 */
public class Slider extends SliderCustomView {

    private int backgroundColor = Color.parseColor("#4CAF50");
    private Ball ball;
    private Bitmap bitmap;
    private int max = 100;
    private int min = 0;
    private NumberIndicator numberIndicator;
    private OnValueChangedListener onValueChangedListener;
    private boolean placedBall = false;
    private boolean press = false;
    private boolean showNumberIndicator = false;
    private int value = 0;
    private ValueToDisplay valueToDisplay;
    private int initialValue = 0;

    public boolean isNumberIndicator = true;

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public void setOnValueChangedListener(
            OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }

    // GETTERS & SETTERS

    public int getValue() {
        return value;
    }

    public void setProgress(final int value) {
        setValue(value);
    }

    public void setValue(final int value) {
        if (placedBall == false)
            post(new Runnable() {

                @Override
                public void run() {
                    setValue(value);
                }
            });
        else {
            this.value = value;
            float division = (ball.xFin - ball.xIni) / max;
            ViewHelper.setX(ball,
                    value * division + getHeight() / 2 - ball.getWidth() / 2);
            ball.changeBackground();
        }

    }

    @Override
    public void invalidate() {
        ball.invalidate();
        super.invalidate();
    }

    public boolean isShowNumberIndicator() {
        return showNumberIndicator;
    }

    public void setShowNumberIndicator(boolean showNumberIndicator) {
        this.showNumberIndicator = showNumberIndicator;
        numberIndicator = (showNumberIndicator) ? new NumberIndicator(
                getContext()) : null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouch(event);
    }

    public boolean onTouch(MotionEvent event) {
        isLastTouch = true;
        if (isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {

                if (numberIndicator != null
                        && numberIndicator.isShowing() == false
                        && isNumberIndicator) {
                    numberIndicator.show();
                }

                if ((event.getX() <= getWidth() && event.getX() >= 0)) {
                    press = true;
                    // calculate value
                    int newValue = 0;
                    float division = (ball.xFin - ball.xIni) / (max - min);
                    if (event.getX() > ball.xFin) {
                        newValue = max;
                    } else if (event.getX() < ball.xIni) {
                        newValue = min;
                    } else {
                        newValue = min + (int) ((event.getX() - ball.xIni) / division);
                    }

                    if (value != newValue) {
                        value = newValue;
                        if (onValueChangedListener != null)
                            onValueChangedListener.onValueChanged(newValue);
                    }

                    // move ball indicator
                    float x = event.getX();
                    x = (x < ball.xIni) ? ball.xIni : x;
                    x = (x > ball.xFin) ? ball.xFin : x;
                    ViewHelper.setX(ball, x);
                    ball.changeBackground();

                    // If slider has number indicator
                    if (numberIndicator != null && isNumberIndicator) {
                        // move number indicator
                        numberIndicator.indicator.x = x;
                        numberIndicator.indicator.finalY = SliderUtils
                                .getRelativeTop(this) - getHeight() / 2;
                        numberIndicator.indicator.finalSize = getHeight() / 2;
                        numberIndicator.numberIndicator.setText("");
                    }

                } else {
                    press = false;
                    isLastTouch = false;
                    if (numberIndicator != null)
                        numberIndicator.dismiss();

                }

            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {

                if (event.getAction() == MotionEvent.ACTION_UP)
                    if (onValueChangedListener != null)
                        onValueChangedListener.onValueChangedUp(value);

                if (numberIndicator != null)
                    numberIndicator.dismiss();
                isLastTouch = false;
                press = false;

            }
        }
        return true;
    }


    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        if (isEnabled())
            beforeBackground = backgroundColor;
    }

    /**
     * Make a dark color to press effect
     *
     * @return
     */
    protected int makePressColor() {
        int r = (this.backgroundColor >> 16) & 0xFF;
        int g = (this.backgroundColor >> 8) & 0xFF;
        int b = (this.backgroundColor >> 0) & 0xFF;
        r = (r - 30 < 0) ? 0 : r - 30;
        g = (g - 30 < 0) ? 0 : g - 30;
        b = (b - 30 < 0) ? 0 : b - 30;
        return Color.argb(70, r, g, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!placedBall) {
            placeBall();
        }

        Paint paint = new Paint();

        if (value == min) {
            // Crop line to transparent effect

            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(canvas.getWidth(),
                        canvas.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas temp = new Canvas(bitmap);
            paint.setColor(Color.parseColor("#B0B0B0"));
            paint.setStrokeWidth(SliderUtils.dpToPx(2, getResources()));
            temp.drawLine(getHeight() / 2, getHeight() / 2, getWidth()
                    - getHeight() / 2, getHeight() / 2, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setColor(getResources().getColor(
                    android.R.color.transparent));
            transparentPaint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR));
            temp.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2,
                    ViewHelper.getY(ball) + ball.getHeight() / 2,
                    ball.getWidth() / 2, transparentPaint);

            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        } else {
            paint.setColor(Color.parseColor("#B0B0B0"));
            paint.setStrokeWidth(SliderUtils.dpToPx(2, getResources()));
            canvas.drawLine(getHeight() / 2, getHeight() / 2, getWidth()
                    - getHeight() / 2, getHeight() / 2, paint);
            paint.setColor(backgroundColor);
            float division = (ball.xFin - ball.xIni) / (max - min);
            int value = this.value - min;

            canvas.drawLine(getHeight() / 2, getHeight() / 2, value * division
                    + getHeight() / 2, getHeight() / 2, paint);

        }

        if (press && !showNumberIndicator) {
            paint.setColor(backgroundColor);
            paint.setAntiAlias(true);
            canvas.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2,
                    getHeight() / 2, getHeight() / 3, paint);
        }
        invalidate();
    }

    // Set attributes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        setBackgroundResource(R.drawable.background_transparent);

        // Set size of view
        setMinimumHeight(SliderUtils.dpToPx(48, getResources()));
        setMinimumWidth(SliderUtils.dpToPx(80, getResources()));

        // Set background Color
        // Color by resource
        int backgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,
                "background", -1);
        if (backgroundColor != -1) {
            setBackgroundColor(getResources().getColor(backgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }

        showNumberIndicator = attrs.getAttributeBooleanValue(MATERIALDESIGNXML,
                "showNumberIndicator", false);
        min = attrs.getAttributeIntValue(MATERIALDESIGNXML, "min", 0);
        max = attrs.getAttributeIntValue(MATERIALDESIGNXML, "max", 0);
        value = attrs.getAttributeIntValue(MATERIALDESIGNXML, "value", min);

        ball = new Ball(getContext());
        RelativeLayout.LayoutParams params = new LayoutParams(SliderUtils.dpToPx(20,
                getResources()), SliderUtils.dpToPx(20, getResources()));
        params.addRule(CENTER_VERTICAL, TRUE);
        ball.setLayoutParams(params);
        addView(ball);

        initialValue = min;

        if (value != min) {
            initialValue = value;
            setValue(value);
        }

        // Set if slider content number indicator
        // TODO
        if (showNumberIndicator) {
            numberIndicator = new NumberIndicator(getContext());
        }

    }

    public void updateAfterRotation() {
        ball.xFin = getWidth() - getHeight() / 2 - ball.getWidth() / 2;
    }

    private void placeBall() {
        ViewHelper.setX(ball, getHeight() / 2 - ball.getWidth() / 2);
        ball.xIni = ViewHelper.getX(ball);
        ball.xFin = getWidth() - getHeight() / 2 - ball.getWidth() / 2;
        ball.xCen = getWidth() / 2 - ball.getWidth() / 2;
        placedBall = true;
    }

    // Event when slider change value
    public interface OnValueChangedListener {
        public void onValueChanged(int value);

        public void onValueChangedUp(int value);
    }

    class Ball extends View {

        float xIni, xFin, xCen;

        public Ball(Context context) {
            super(context);
            setBackgroundResource(R.drawable.background_switch_ball_uncheck);
        }

        public void changeBackground() {
            if (value != min) {
                setBackgroundResource(R.drawable.background_checkbox);
                LayerDrawable layer = (LayerDrawable) getBackground();
                GradientDrawable shape = (GradientDrawable) layer
                        .findDrawableByLayerId(R.id.shape_bacground);
                shape.setColor(backgroundColor);
            } else {
                setBackgroundResource(R.drawable.background_switch_ball_uncheck);
            }
        }

    }

    // Slider Number Indicator

    class Indicator extends RelativeLayout {

        boolean animate = true;
        // Final size after animation
        float finalSize = 0;
        // Final y position after animation
        float finalY = 0;
        boolean numberIndicatorResize = false;
        // Size of number indicator
        float size = 0;
        // Position of number indicator
        float x = 0;
        float y = 0;

        private Paint paint, paintBorder;

        public Indicator(Context context) {
            super(context);
            setBackgroundColor(getResources().getColor(android.R.color.transparent));
            //setBackgroundColor(getResources().getColor(R.color.actionbar));

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(backgroundColor);

            // Border and shadow
            paintBorder = new Paint();
            paintBorder.setAntiAlias(true);
            this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (numberIndicatorResize == false) {
                LayoutParams params = (LayoutParams) numberIndicator.numberIndicator
                        .getLayoutParams();
                params.height = (int) finalSize * 2;
                params.width = (int) finalSize * 2;
                numberIndicator.numberIndicator.setLayoutParams(params);
            }

            if (animate) {
                if (y == 0)
                    y = finalY + finalSize * 2;
                y -= SliderUtils.dpToPx(6, getResources());
                size += SliderUtils.dpToPx(2, getResources());
            }

            float cx = ViewHelper.getX(ball) + SliderUtils.getRelativeLeft((View) ball.getParent())
                    + ball.getWidth() / 2;

            paintBorder.setShadowLayer(size / 1.6f, 0.0f, size / 4.0f, Color.BLACK);

            canvas.drawCircle(cx, y, size / 1.6f, paintBorder);
            canvas.drawCircle(cx, y, size, paint);

            if (animate && size >= finalSize)
                animate = false;
            if (animate == false) {
                ViewHelper.setX(numberIndicator.numberIndicator,
                        cx - size);
                ViewHelper.setY(numberIndicator.numberIndicator, y - size);

                if (valueToDisplay != null)
                    numberIndicator.numberIndicator.setText(valueToDisplay.convert(value));
                else
                    numberIndicator.numberIndicator.setText(value + "");
            }

            invalidate();
        }

    }

    class NumberIndicator extends Dialog {

        Indicator indicator;
        TextView numberIndicator;

        public NumberIndicator(Context context) {
            //super(context, android.R.style.Theme_Translucent);
            super(context, android.R.style.Theme_Translucent);
        }

        @Override
        public void dismiss() {
            super.dismiss();
            if (indicator != null) {
                indicator.y = 0;
                indicator.size = 0;
                indicator.animate = true;
            }
        }

        @Override
        public void onBackPressed() {
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.number_indicator_spinner);
            setCanceledOnTouchOutside(false);

            RelativeLayout content = (RelativeLayout) this
                    .findViewById(R.id.number_indicator_spinner_content);
            indicator = new Indicator(this.getContext());
            content.addView(indicator);

            numberIndicator = new TextView(getContext());
            numberIndicator.setTextColor(Color.WHITE);
            numberIndicator.setTypeface(null, Typeface.BOLD);
            numberIndicator.setGravity(Gravity.CENTER);
            content.addView(numberIndicator);

            indicator.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT,
                    RelativeLayout.LayoutParams.FILL_PARENT));
        }

    }

    public boolean isPress() {
        return press;
    }

    public interface ValueToDisplay {
        public String convert(int value);
    }

    public void setValueToDisplay(ValueToDisplay valueToDisplay) {
        this.valueToDisplay = valueToDisplay;
    }
}