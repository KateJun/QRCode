package com.jmgzs.qrcode.ui;

import android.os.CountDownTimer;

import com.jmgzs.lib.adv.AdvUtil;
import com.jmgzs.lib.adv.ui.AdvSplashActivity;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.qrcode.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;


/**
 * Created by mac on 17/7/18.
 * Description:
 */

public class SplashActivity extends AdvSplashActivity {


    private CountDownTimer countDownTimer;

    @Override
    protected int getLogoResId() {
        initUmeng();
        return R.mipmap.logo;
    }

    @Override
    protected String getAppInfo() {
        return this.getResources().getString(R.string.app_name);
    }

    @Override
    protected String getActivityName() {
        return MainNewActivity.class.getName();
    }

    @Override
    protected String getTempDir() {
        return FileUtils.getCachePath(this) + File.separator + "info";
    }

//    @Override
//    protected int getContent(Bundle save) {
//        return R.layout.activity_splash;
//    }
//
//    @Override
//    protected void initView() {
//        initUmeng();
//        countDownTimer = new CountDownTimer(2000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                goMain();
//            }
//        };
//        countDownTimer.start();
//    }
//
//    private void goMain() {
//        startActivity(new Intent(SplashActivity.this, MainNewActivity.class));
//        finish();
//
//    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(android.R.color.transparent);
    }

    private void initUmeng() {
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setDebugMode(false);

        AdvUtil.setAdvOpen(true);
        AdvUtil.getInstance().init(this, FileUtils.getCachePath(this)+ File.separator + "adv");

    }
}
