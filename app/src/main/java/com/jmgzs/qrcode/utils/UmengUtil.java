package com.jmgzs.qrcode.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * Created by mac on 17/7/24.
 * Description:
 */

public class UmengUtil {

//    private static final String KEY_UMENG = "59755a588630f527ed0008b7";

    public static final String U_FLASHLIGHT = "click_Flashlight";//,二维码扫描-闪光灯,0
    public static final String U_ALBUM = "click_Album";//,二维码扫描-相册,0
    public static final String U_COPY = "click_Copy";//,二维码扫描-复制,0
    public static final String U_SCAN_CREATE = "click_scan_create";//,二维码扫描-生成,0
    public static final String U_CREATE = "click_Create";//,二维码生成-生成,0
    public static final String U_RESET = "click_Reset";//,二维码生成-重新生成,0
    public static final String U_RESET_CONFIG = "click_ResetConfig";//,二维码生成-重置,0
    public static final String U_SAVE = "click_Save";//,二维码生成-保存,0
    public static final String U_ADDICON = "click_AddIcon";//,二维码生成-添加头像,0
    public static final String U_COLOR = "click_color";//,二维码生成-设置二维码颜色,0


    public static void event(Context ct, String key) {
        MobclickAgent.onEvent(ct, key);
    }


    public static void event(Context ct, String key, Map<String, String> map) {
        MobclickAgent.onEvent(ct, key, map);
    }

}
