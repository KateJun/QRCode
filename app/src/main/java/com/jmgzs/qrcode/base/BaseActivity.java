package com.jmgzs.qrcode.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.utils.DensityUtils;


/**
 * Created by mac on 17/6/5.
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected LinearLayout root;
    protected View paddingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentStatusBar();
        this.registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        parent.removeAllViews();
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        parent.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (getContent(savedInstanceState) != 0)
            setContentView(getContent(savedInstanceState));
        initView();

    }

    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected int getStatusBarColor() {
        return this.getResources().getColor(R.color.colorPrimary);
    }

    public void setStatusBarColor(int color) {
        if (paddingView != null) {
            paddingView = new View(this);
            paddingView.setBackgroundColor(color);
        }
    }

    protected void addPaddingAboveContentView() {
        int statusBarHeight = DensityUtils.getStatusBarHeight(this);
        if (paddingView == null) {
            paddingView = new View(this);
        } else {
            ((ViewGroup) paddingView.getParent()).removeView(paddingView);
        }
        paddingView.setBackgroundColor(getStatusBarColor());
//        L.e("状态栏高度："+statusBarHeight);
        root.addView(paddingView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();//res.getConfiguration();
//        config.fontScale = 2f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ViewGroup parent = root;
        root.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, parent, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    @Override
    public void setContentView(View view) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        if (mHomeKeyEventReceiver != null)
            unregisterReceiver(mHomeKeyEventReceiver);
        super.onDestroy();
    }

    protected abstract int getContent(Bundle save);

    protected abstract void initView();


    protected <E extends View> E getView(int resID) {
        try {
            return (E) findViewById(resID);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    protected <E extends View> E getView(View rootView, int resID) {
        try {
            if (null != rootView) {
                return (E) findViewById(resID);
            } else throw new NullPointerException();
        } catch (Exception e) {
            throw e;
        }
    }


    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };

}
