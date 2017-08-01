package com.jmgzs.qrcode.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.zxing.client.result.ParsedResultType;
import com.jmgzs.lib.colorpicker.ColorPickerDialog;
import com.jmgzs.lib.colorpicker.OnColorPickerListener;
import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;
import com.jmgzs.zxing.scanner.common.Scanner;
import com.jmgzs.zxing.scanner.encode.QREncode;

import java.io.ByteArrayOutputStream;

import static com.jmgzs.qrcode.R.id.checkBox;

public class MainNewActivity extends BaseActivity {


    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_main_new;
    }

    @Override
    protected void initView() {
        getView(R.id.toolbar_title).setVisibility(View.VISIBLE);
        findViewById(R.id.imgbtn_scan).setOnClickListener(this);
        findViewById(R.id.imgbtn_create).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_create:
                startActivity(new Intent(MainNewActivity.this, CreateCodeActivity.class));

                break;
            case R.id.imgbtn_scan:
                if (ContextCompat.checkSelfPermission(MainNewActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    ActivityCompat.requestPermissions(MainNewActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 60);
                } else {
                    //权限已经被授予，在这里直接写要执行的相应方法即可
                    ScannerActivity.gotoActivity(MainNewActivity.this, false, 1);
                }
                break;
            default:
                break;
        }
    }
}
