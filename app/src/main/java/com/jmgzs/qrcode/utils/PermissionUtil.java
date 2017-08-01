package com.jmgzs.qrcode.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by XJ on 2017/4/19.
 */

public final class PermissionUtil {

    private static volatile PermissionUtil instance;

    private PermissionUtil() {

    }

    public static PermissionUtil getInstance() {
        if (null == instance) {
            synchronized (PermissionUtil.class) {
                if (null == instance) {
                    instance = new PermissionUtil();
                }
            }
        }
        return instance;
    }

    /**
     * @param ct
     * @param permission
     * @return true is ok, false is not grant
     */
    public boolean isGranted(Context ct, String... permission) {
        return !isMarshmallow() || isGranted_(ct, permission);
    }


    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;//Build.VERSION_CODES.M;
    }

    private boolean isGranted_(Context ct, String... permission) {
        if (ct == null)
            return false;
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(ct, p) != PackageManager.PERMISSION_GRANTED) {
                if (ct instanceof Activity)
                    ActivityCompat.requestPermissions((Activity) ct, permission, 0);
                return false;
            }
        }
        return true;
    }

    private void requestPermission(Activity ct, String permission, int requestCode) {
        if (!isGranted(ct, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ct, permission)) {

            } else {
                ActivityCompat.requestPermissions(ct, new String[]{permission}, requestCode);
            }
        } else {
            //直接执行相应操作了
        }
    }

    private boolean isPermissionOk(Activity ct) {
        if (hasPermission(ct)) {
            Log.e("app", "权限正常");
            return true;
        } else {
            Log.e("app", "缺少权限");
            return false;
        }
    }

    private boolean hasPermission(Activity ct) {
        return isGranted(ct, Manifest.permission.ACCESS_COARSE_LOCATION) && isGranted(ct, Manifest.permission.ACCESS_FINE_LOCATION) && isGranted(ct, Manifest.permission.ACCESS_WIFI_STATE) && isGranted(ct, Manifest.permission.READ_PHONE_STATE) && isGranted(ct, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
