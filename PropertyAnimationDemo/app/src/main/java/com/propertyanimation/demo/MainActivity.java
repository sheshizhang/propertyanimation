package com.propertyanimation.demo;


/**
 * 属性动画框架
 */

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void OnStartAnimationBtn(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "hehe", 0f,100f);
        animator.setDuration(300);

		animator.setEvaluator(new TypeEvaluator() {
			@Override
			public Object evaluate(float fraction, Object startValue, Object endValue) {
				return null;
			}
		});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 监听动画回调
				animation.getAnimatedFraction();//动画执行的百分比 0~1 //API 12+
                float value= (float) animation.getAnimatedValue();
                imageView.setTranslationX(value*2);
                imageView.setScaleX(0.5f+value/200);
                imageView.setScaleY(0.5f+value/200);
            }
        });

        animator.start();


        //方法 2）---------------ValueAnimator---如果只需要监听值变化就用ValueAnimator---------------
//		ValueAnimator animator = ValueAnimator.ofFloat(0f, 200f);
//		animator.setDuration(200);
//		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				float value = (float) animation.getAnimatedValue();//得到0f~100f当中的这个时间点对应的值
//				imageView.setScaleX(0.5f+value/200);
//				imageView.setScaleY(0.5f+value/200);
//			}
//		});
//		animator.start();


		//方法 3)多个动画结合
//		PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("alpha", 1f,0.5f);
//		PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleX", 1f,0.5f);
//		PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("scaleY", 1f,0.5f);
//		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageView,holder1,holder2,holder3);
//		animator.setDuration(200);
//		animator.start();





    }

}
