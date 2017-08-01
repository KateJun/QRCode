package com.jmgzs.qrcode.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.Result;
import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.picture.PickPictureTotalActivity;
import com.jmgzs.qrcode.utils.DensityUtils;
import com.jmgzs.qrcode.utils.T;
import com.jmgzs.zxing.scanner.ScannerView;
import com.jmgzs.zxing.scanner.common.Scanner;
import com.jmgzs.zxing.scanner.decode.QRDecode;

import static com.jmgzs.qrcode.R.id.toggleButton;

/**
 * 扫描
 */
public class ScannerActivity extends DeCodeActivity {

    public static final String EXTRA_LASER_LINE_MODE = "laser_line_mode";
    public static final int EXTRA_LASER_LINE_MODE_0 = 0;
    public static final int EXTRA_LASER_LINE_MODE_1 = 1;
    public static final int EXTRA_LASER_LINE_MODE_2 = 2;
    public static final int APPLY_READ_EXTERNAL_STORAGE = 0x111;

    private ScannerView mScannerView;
    private Result mLastResult;
    private int laserMode;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView flashLightIcon;
    private CheckBox toggleButton;

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(android.R.color.black);
    }

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_scanner;
    }

    @Override
    protected void initView() {
        initToolbar();

        mScannerView = (ScannerView) findViewById(R.id.scanner_view);
        mScannerView.setOnScannerCompletionListener(this);

        flashLightIcon = getView(R.id.flashlight_icon);
        toggleButton = (CheckBox) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeFlashLight(isChecked);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    ActivityCompat.requestPermissions(ScannerActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            APPLY_READ_EXTERNAL_STORAGE);
                } else {
                    PickPictureTotalActivity.gotoActivity(ScannerActivity.this);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            laserMode = extras.getInt(EXTRA_LASER_LINE_MODE);
        }
        mScannerView.setMediaResId(R.raw.beep);//设置扫描成功的声音
//        mScannerView.setDrawText("将取景框对准二维码,可自动扫描", true);
        mScannerView.setDrawTextColor(Color.WHITE);
//        mScannerView.setScanMode(Scanner.ScanMode.PRODUCT_MODE);

        mScannerView.setLaserFrameTopMargin(80);//扫描框与屏幕上方距离
//        mScannerView.setLaserFrameSize(200, 200);//扫描框大小
//        mScannerView.setLaserFrameCornerLength(25);//设置4角长度
//        mScannerView.setLaserLineHeight(5);//设置扫描线高度

        switch (laserMode) {
            case EXTRA_LASER_LINE_MODE_0:
                mScannerView.setLaserLineResId(R.mipmap.wx_scan_line);//线图
                break;
            case EXTRA_LASER_LINE_MODE_1:
                mScannerView.setLaserGridLineResId(R.mipmap.zfb_grid_scan_line);//网格图
                mScannerView.setLaserFrameBoundColor(0xFF66FF00);//支付宝颜色
                break;
            case EXTRA_LASER_LINE_MODE_2:
                mScannerView.setLaserColor(Color.RED);
                break;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        LinearLayout bottomLayout = getView(R.id.scan_bottom_layout);
        int bottomMargin = DensityUtils.getRealHeight(this) -
                (int) (DensityUtils.getScreenWidthPixels(this) * 6.0f / 8 + DensityUtils.dip2px(this, 120 + 50 + 55) + DensityUtils.getStatusBarHeight(this));
        RelativeLayout.LayoutParams bottomParam = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
        bottomParam.bottomMargin = bottomMargin;
        bottomLayout.setLayoutParams(bottomParam);

        initSensors();
    }

    private void changeFlashLight(boolean isChecked) {
        mScannerView.toggleLight(isChecked);
        if (isChecked) flashLightIcon.setImageResource(R.mipmap.open_flashlight);
        else flashLightIcon.setImageResource(R.mipmap.close_flashlight);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ScannerActivity.this, MainActivity.class));
                finish();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_create_qrcode:
                        startActivity(new Intent(ScannerActivity.this, CreateCodeActivity.class));
                        break;
//                    case R.id.item_share:
//                        T.toastS(ScannerActivity.this, item.getTitle());
//                        break;
//                    case R.id.item_more:
//                        T.toastS(ScannerActivity.this, item.getTitle());
//                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        toolbar.setPopupTheme(R.style.PopupMenu);
    }

    private SensorManager sm;
    private SensorEventListener listener;

    private void initSensors() {
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lux = event.values[0];
                if (lux < 10f && mScannerView.isCameraOpen()) {//白天室内大约300,最小为0,数字越小表示越暗
                    toggleButton.setChecked(true);
                    sm.unregisterListener(listener);
                }
                Log.e("LUX", "lux:" + lux);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        if (sm != null && listener != null) sm.unregisterListener(listener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APPLY_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PickPictureTotalActivity.gotoActivity(ScannerActivity.this);
            } else {
                Toast.makeText(ScannerActivity.this, "请给予权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        mScannerView.onResume();
        resetStatusView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mLastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                } else {
//                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void restartPreviewAfterDelay(long delayMS) {
        mScannerView.restartPreviewAfterDelay(delayMS);
        resetStatusView();
    }

    private void resetStatusView() {
        mLastResult = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED && resultCode == Activity.RESULT_OK) {
            if (requestCode == PickPictureTotalActivity.REQUEST_CODE_SELECT_PICTURE) {
                String picturePath = data.getStringExtra(PickPictureTotalActivity
                        .EXTRA_PICTURE_PATH);
                QRDecode.decodeQR(picturePath, this);
            }
        }
    }

    public static void gotoActivity(Activity activity, boolean isBackResult, int laserMode) {
        activity.startActivityForResult(new Intent(Scanner.Scan.ACTION)
                        .putExtra(ScannerActivity.EXTRA_RETURN_SCANNER_RESULT, isBackResult)
                        .putExtra(EXTRA_LASER_LINE_MODE, laserMode)
                , ScannerActivity.REQUEST_CODE_SCANNER);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Scanner Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
