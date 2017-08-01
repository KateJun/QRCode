package com.jmgzs.qrcode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhaotun on 15/11/3.
 */
public class DensityUtils {

    /**
     * 屏幕宽度(px)
     */
    public static int SCREEN_WIDTH_PIXELS;

    /**
     * 屏幕高度(px)
     */
    public static int SCREEN_HEIGHT_PIXELS;

    /**
     * 屏幕宽度(dp)
     */
    public static int SCREEN_WIDTH_DP;

    /**
     * 屏幕高度(dp)
     */
    public static int SCREEN_HEIGHT_DP;

    /**
     * 屏幕密度
     */
    public static float SCREEN_DENSITY = 0f;

    /**  */
    public static float SCREEN_SCALED_DENSITY = 0f;

    /**
     * 屏幕对角线长（inches）
     */
    public static double SCREEN_PHYSICAL_SIZE = 0f;

    private static boolean mInitialed = false;

    private DensityUtils() {
    }

    public static void init(Context context) {
        if (mInitialed || context == null) {
            if (context == null)
                throw new NullPointerException("density is init or context is null");
            return;
        }
//        mInitialed = true;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            SCREEN_WIDTH_PIXELS = dm.heightPixels;
            SCREEN_HEIGHT_PIXELS = dm.widthPixels;
        } else {
            SCREEN_WIDTH_PIXELS = dm.widthPixels;
            SCREEN_HEIGHT_PIXELS = dm.heightPixels;
        }
        SCREEN_DENSITY = dm.density;
        SCREEN_SCALED_DENSITY = dm.scaledDensity;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / dm.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / dm.density);

        double x = Math.pow(SCREEN_WIDTH_PIXELS / dm.xdpi, 2);
        double y = Math.pow(SCREEN_HEIGHT_PIXELS / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        SCREEN_PHYSICAL_SIZE = screenInches;

    }

    public static int getScreenWidthPixels(Context ct) {
        if (SCREEN_WIDTH_PIXELS == 0)
            init(ct);
        return SCREEN_WIDTH_PIXELS;
    }

    public static int getScreenHeightPixels(Context ct) {
        if (SCREEN_HEIGHT_PIXELS == 0) {
            init(ct);
        }
        return SCREEN_HEIGHT_PIXELS;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        return dip2px(null, dipValue);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     *
     * @param context  上下文
     * @param dipValue dip的值
     * @return 转换成px的值
     */
    public static int dip2px(Context context, float dipValue) {
        if (SCREEN_DENSITY == 0) {

            init(context);
        }

        final float scale = SCREEN_DENSITY;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dip
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        return px2dip(null, pxValue);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dip
     *
     * @param context 上下文
     * @param pxValue 待转换的px的值
     * @return 转换成dip的值
     */
    public static int px2dip(Context context, float pxValue) {
        if (SCREEN_DENSITY == 0) {
            init(context);
        }

        final float scale = SCREEN_DENSITY;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        return px2sp(null, pxValue);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context 上下文
     * @param pxValue 待转换的px的值
     * @return 转换后的sp值
     */
    public static int px2sp(Context context, float pxValue) {
        if (SCREEN_SCALED_DENSITY == 0) {
            init(context);
        }

        final float fontScale = SCREEN_SCALED_DENSITY;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        return sp2px(null, spValue);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context 上下文
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        if (SCREEN_SCALED_DENSITY == 0) {
            init(context);
        }

        final float fontScale = SCREEN_SCALED_DENSITY;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static int statusBarHeight;

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight > 0) {
            return statusBarHeight;
        }
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            if (statusBarHeight == 0) statusBarHeight = DensityUtils.dip2px(context, 25f);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
            statusBarHeight = DensityUtils.dip2px(context, 25f);
        }
        return statusBarHeight;
    }

    /**
     * 获取状态栏，虚拟物理键高度
     *
     * @return [状态栏，虚拟按键高度]
     */
    public static int[] getBarHeight(Context context) {
        int x = 0;
        int y = 0;
        boolean hasMenuKey = false; //是否有实体按键
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            x = context.getResources().getDimensionPixelSize(x);
            hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();


            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            // 适配魅族手机 SmartBar
            if ("Meizu".equals(android.os.Build.MANUFACTURER)) {
                Resources resources = context.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0 && !hasMenuKey) {
                    y = resources.getDimensionPixelSize(resourceId);
                }
            } else {

                if (!hasMenuKey && !hasBackKey) {
                    Field field2 = c.getField("navigation_bar_height");
                    y = Integer.parseInt(field2.get(obj).toString());
                    y = context.getResources().getDimensionPixelSize(y);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
//            x = context.getResources().getDimensionPixelOffset(R.dimen.padding_size_25);
            //			y = !hasMenuKey ? context.getResources().getDimensionPixelOffset(
            //					R.dimen.padding_size_48) : 0;
        }
        return new int[]{x, y};
    }


    /**
     * 获取屏幕真正的高度（包括底部虚拟导航栏）
     * 有些手机获取的 dm.heightPixels 包括导航栏，有些手机不包括
     *
     * @return
     */
    public static int getRealHeight(Activity activity) {
        int realWidth = 0;
        int realHeight = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);
                realWidth = metrics.widthPixels;
                realHeight = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    realWidth = (Integer) mGetRawW.invoke(display);
                    realHeight = (Integer) mGetRawH.invoke(display);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            realHeight = display.getHeight();
        }

        return realHeight;
    }

    /**
     * 是否当前窗口是透明状态栏
     *
     * @param activity
     * @return
     */
    public static boolean isTransStatusBar(Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & flags) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否当前窗口是透明虚拟键盘
     *
     * @param activity
     * @return
     */
    public static boolean isTransNavigationBar(Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION & flags) != 0) {
            return true;
        } else {
            return false;
        }
    }
}
