package com.example.zhangzhaoxiang.bottle;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.zhangzhaoxiang.bottle.R;

public class SplashScreenActiviy extends Activity {

    private TextView tv_countDown;
    private LinearLayout ll_splashActivity;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 通过下面两行代码也可实现全屏无标题栏显示activity
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splashscreen);

        tv_countDown = (TextView) findViewById(R.id.tv_countDown);
        ll_splashActivity = (LinearLayout) findViewById(R.id.ll_splashActivity);

        /********************************************************************************
         *
         * 普通闪屏实现方式
         *
         * ******************************************************************************/
		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000*4);*/


        /********************************************************************************
         *
         * 倒计时闪屏实现方式
         *
         * ******************************************************************************/
		/*MyCountDownTimer mc = new MyCountDownTimer(4000, 1000);
		mc.start();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000*4);*/

        /********************************************************************************
         *
         * 倒计时+动画闪屏实现方式
         *
         * ******************************************************************************/
        MyCountDownTimer mc = new MyCountDownTimer(4000, 1000);
        mc.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //左移动画
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
                ta.setDuration(100); //设置动画执行的时间
                ta.setFillAfter(true);//当动画结束后 动画停留在结束位置,然后等启动主界面后将其销毁
                ll_splashActivity.startAnimation(ta);

                ta.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {

                    }
                    @Override
                    public void onAnimationRepeat(Animation arg0) {

                    }
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }, 1000*4);

    }

    class MyCountDownTimer extends CountDownTimer {
        //millisInFuture:倒计时的总数,单位毫秒
        //例如 millisInFuture=1000;表示1秒
        //countDownInterval:表示间隔多少毫秒,调用一次onTick方法()
        //例如: countDownInterval =1000;表示每1000毫秒调用一次onTick()
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        public void onFinish() {
            tv_countDown.setText("开始跳转……");
        }
        public void onTick(long millisUntilFinished) {
            tv_countDown.setText("(" + millisUntilFinished / 1000 + ")");
        }
    }

}

