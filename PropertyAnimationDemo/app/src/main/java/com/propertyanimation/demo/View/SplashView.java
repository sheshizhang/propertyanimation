package com.propertyanimation.demo.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.propertyanimation.demo.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashView extends View{
    // 小圆圈的颜色列表，在initialize方法里面初始化
    private int[] mCircleColors;
    // 每一个小圆的半径
    private float mCircleRadius = 18;
    // 大圆(里面包含很多小圆的)的半径
    private float mRotationRadius = 90;
    //绘制圆圈的画笔
    private Paint mPaint=new Paint();
    //绘制背景画笔
    private Paint mPaintBackground = new Paint();
    // 屏幕正中心点坐标
    private float mCenterX;
    private float mCenterY;
    //屏幕对角线一半
    private float mDiagonalDist;

    // 大圆和小圆旋转的时间
    private long mRotationDuration = 1200; //ms
    // 第二部分动画的执行总时间(包括二个动画时间，各占1/2)
    private long mSplashDuration = 1200; //ms
    // 整体的背景颜色
    private int mSplashBgColor = Color.WHITE;

    //空心圆初始半径
    private float mHoleRadius = 0F;
    //当前大圆旋转角度(弧度)
    private float mCurrentRotationAngle = 0F;
    //当前大圆的半径
    private float mCurrentRotationRadius = mRotationRadius;

    private ValueAnimator valueAnimator=null;
    private Handler mHandler;

//    private ExecutorService executorService;

    public SplashView(Context context) {
        this(context,null);

    }

    public SplashView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
//        executorService= Executors.newCachedThreadPool();
        mHandler=new Handler();
        mCircleColors=getResources().getIntArray(R.array.splash_circle_colors);
        //画笔初始化
        //消除锯齿
        mPaint.setAntiAlias(true);
        mPaintBackground.setAntiAlias(true);
        //设置样式---边框样式--描边
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
    }

    /**
     * 消失动画
     */
    public void splashDisappear(){
        if (mState!=null&&mState instanceof RotateState){
            //结束旋转动画
            RotateState rotateState= (RotateState) mState;
            rotateState.cancel();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mState = new MergingState();
                }
            });
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //开启第二个动画
        if (mState==null){
            //开启第一个动画
            mState=new RotateState();
        }

        mState.drawState(canvas);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterX=w/2f;
        mCenterY=h/2f;
        mDiagonalDist = (float) Math.sqrt((w*w+h*h))/2f;//勾股定律
    }

    private SplashState mState=null;
    //策略模式:State---三种动画状态(旋转，聚合，水波纹动画)
    private abstract class SplashState{
        public abstract void drawState(Canvas canvas);
    }


    //旋转动画
    public class RotateState extends SplashState{
        public RotateState(){
            //开启工作，开启动画
            valueAnimator = ValueAnimator.ofFloat(0f,(float)Math.PI*2);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationAngle= (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.w("zhangfeiran100","旋转结束");
                    splashDisappear();
                }
            });
            valueAnimator.setDuration(mRotationDuration);
            valueAnimator.start();
        }

        public void cancel(){
            valueAnimator.cancel();
        }

        @Override
        public void drawState(Canvas canvas) {
            //1.背景--擦黑板，涂成白色
            drawBackground(canvas);
            //2.绘制小圆
            drawCircles(canvas);
        }
    }

    //聚合动画
    public class MergingState extends SplashState{
        public MergingState(){
            //花1200ms，计算某个时刻当前的大圆半径是多少？ r~0中的某个值
            valueAnimator=ValueAnimator.ofFloat(0,mRotationRadius);
            valueAnimator.setDuration(mRotationDuration);
            valueAnimator.setInterpolator(new OvershootInterpolator(10f));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationRadius= (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.w("zhangfeiran100","聚合结束");
                    cancel();
                    mState=new ExpandState();
                }
            });

//            valueAnimator.reverse();
            valueAnimator.start();
        }
        public void cancel(){
            valueAnimator.cancel();
        }

        @Override
        public void drawState(Canvas canvas) {
            //1.背景--擦黑板，涂成白色
            drawBackground(canvas);
            //2.绘制小圆
            drawCircles(canvas);
        }
    }

    //水波纹扩散动画
    public class ExpandState extends SplashState{
        public ExpandState(){
            //花1200ms，计算某个时刻当前的空心圆的半径是多少？ r~0中的某个值
            valueAnimator = ValueAnimator.ofFloat(mCircleRadius, mDiagonalDist);
            valueAnimator.setDuration(mRotationDuration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //当前的空心圆的半径是多少？
                    mHoleRadius = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.w("zhangfeiran100","水波纹结束");
                    Log.w("zhangfeiran100","mtodoStartActivity=="+mtodoStartActivity);
                    if (mtodoStartActivity!=null){
                        Log.w("zhangfeiran100","开始跳转");
                        mtodoStartActivity.startActivity();
                    }
                }
            });
            valueAnimator.start();
        }

        public void cancel(){
            valueAnimator.cancel();
        }

        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (mHoleRadius>0f){
            float strokeWidth=mDiagonalDist-mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);
            //画圆的半径 = 空心圆的半径 + 画笔的宽度/2
            float radius=mHoleRadius+mCenterX/2;
            canvas.drawCircle(mCenterX,mCenterY,radius,mPaintBackground);
        }else{
            canvas.drawColor(mSplashBgColor);
        }

    }

    private void drawCircles(Canvas canvas) {
        //每个小圆之间的间隔角度 = 2π/小圆的个数
        float rotationAngle = (float) (2*Math.PI/mCircleColors.length);
        for (int i=0; i < mCircleColors.length; i++){
            /**
             * x = r*cos(a) +centerX
             y=  r*sin(a) + centerY
             每个小圆i*间隔角度 + 旋转的角度 = 当前小圆的真是角度
             */
            double angle = i*rotationAngle + mCurrentRotationAngle;
            float cx = (float) (mCurrentRotationRadius*Math.cos(angle) + mCenterX);
            float cy = (float) (mCurrentRotationRadius*Math.sin(angle) + mCenterY);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx,cy,mCircleRadius,mPaint);
        }
    }

    private todoStartActivity mtodoStartActivity;

    public void setTodoStartActivity(todoStartActivity mtodoStartActivity){
        this.mtodoStartActivity=mtodoStartActivity;
    }
    public interface todoStartActivity{
        void startActivity();
    }

}
