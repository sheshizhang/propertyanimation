package com.propertyanimation.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import com.propertyanimation.demo.View.SplashView;

public class SplashActivity extends AppCompatActivity implements SplashView.todoStartActivity {

    private SplashView mSplashView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        mSplashView=(SplashView)findViewById(R.id.mSplashViewid);
        mSplashView.setTodoStartActivity(this);
    }

    public void startActivityTodo(){
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void startActivity() {
        startActivityTodo();
    }
}
