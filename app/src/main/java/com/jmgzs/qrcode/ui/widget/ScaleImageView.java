package com.jmgzs.qrcode.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * @author zjy
 */
public class ScaleImageView extends ImageView {

    private Animation anim1;
    private Animation anim2;
    private int mHeight;
    private int mWidth;
    private float mX, mY;
    private float ratio = 0.08f;

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        int[] location = new int[2];
        this.getLocationOnScreen(location);
        mX = location[0];
        mY = location[1];
    }

    private void init() {

        anim1 = new ScaleAnimation(1, 0.75f, 1, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim1.setFillAfter(true);
        anim1.setDuration(150);
        anim1.setInterpolator(new LinearInterpolator());


        anim2 = new ScaleAnimation(0.75f, 1, 0.75f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim1.setFillAfter(true);
        anim1.setDuration(200);
        anim1.setInterpolator(new OvershootInterpolator(1.0f));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//           if(mAnimationBuffer!=null&&mAnimationBuffer.size()>0){
//               return  false;
//           }

//            generateAnimation(1, 0.65f);
//            triggerNextAnimation();
//
            anim2.cancel();
            startAnimation(anim1);


        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//            generateAnimation(0.65f, 1);
//            triggerNextAnimation();

            anim1.cancel();
            startAnimation(anim2);


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            generateAnimation(0.65f, 1);
//            triggerNextAnimation();

            anim1.cancel();
            startAnimation(anim2);
//            if (listener != null) {
//                //listener.onScaleButtonClick(this);
//            }
        }


        return true;
    }

    // 按下的点是否在View内
    protected boolean innerImageView(float x, float y) {
        if (x >= mX && x <= mX + mWidth) {
            if (y >= mY && y <= mY + mHeight) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    public void scale(float f) {
        if (f != 1.0f) {
            this.setScaleX(1.0f + f * ratio);
            this.setScaleY(1.0f + f * ratio);
        }
    }


    public void reset() {
        if (this.getScaleX() != 1.0) {
            PropertyValuesHolder valueHolderX = PropertyValuesHolder.ofFloat(
                    "scaleX", 1 + ratio, 1f);
            PropertyValuesHolder valuesHolderY = PropertyValuesHolder.ofFloat(
                    "scaleY", 1 + ratio, 1f);
            Animator anim = ObjectAnimator.ofPropertyValuesHolder(this, valueHolderX,
                    valuesHolderY);
            anim.setDuration(200);
            anim.setInterpolator(new OvershootInterpolator(4.0f));
            anim.start();
        }

    }

}
