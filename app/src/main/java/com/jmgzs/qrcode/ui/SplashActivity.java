package com.jmgzs.qrcode.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;

/**
 * Created by mac on 17/7/18.
 * Description:
 */

public class SplashActivity extends BaseActivity {


    private CountDownTimer countDownTimer;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                timeTv.setText("广告 " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                goMain();
            }
        };
        countDownTimer.start();
    }

    private void goMain() {
        startActivity(new Intent(SplashActivity.this, MainNewActivity.class));
        finish();

    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(android.R.color.transparent);
    }
}
