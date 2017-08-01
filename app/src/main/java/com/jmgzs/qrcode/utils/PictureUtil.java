package com.jmgzs.qrcode.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

/**
 * 图片处理工具类
 *
 * @author XJ
 */
public class PictureUtil {


    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 根据宽度从本地图片路径获取该图片的缩略图
     *
     * @param localImagePath 本地图片的路径
     * @param width          缩略图的宽
     * @param addedScaling   额外可以加的缩放比例
     * @return bitmap 指定宽高的缩略图
     */
    public static Bitmap getBitmapByWidth(String localImagePath, int width,
                                          int addedScaling) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }
        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();
            outOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(localImagePath, outOptions);
            if (outOptions.outWidth > width) {
                outOptions.inSampleSize = outOptions.outWidth / width + 1
                        + addedScaling;
                outOptions.outWidth = width;
                outOptions.outHeight = outOptions.outHeight
                        / outOptions.inSampleSize;
            } else if (outOptions.outHeight > width) {
                outOptions.inSampleSize = outOptions.outHeight / width + 1
                        + addedScaling;
                outOptions.outHeight = width;
                outOptions.outWidth = outOptions.outWidth
                        / outOptions.inSampleSize;

            }
            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
            int degree = readPictureDegree(localImagePath);
            if (degree != 0) {// 旋转照片角度
                temBitmap = rotateBitmap(temBitmap, degree);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示 默认根据尺寸800*480缩放显示
     *
     * @param filePath 图片路径
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        return getSmallBitmap(filePath, 480, 800);
    }

    /**
     * 根据路径获得图片返回缩略图
     *
     * @param filePath  图片路径
     * @param reqWidth  宽度
     * @param reqHeight 高度
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth,
                                        int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap temBitmap = BitmapFactory.decodeFile(filePath, options);
        int degree = readPictureDegree(filePath);
        if (degree != 0) {// 旋转照片角度
            temBitmap = rotateBitmap(temBitmap, degree);
        }
        return temBitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /*********************** 图片旋转 **********************/
    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     *
     * @param context
     * @param filePath 路径
     * @param fileName 文件名称
     * @param q        压缩质量 0-100
     * @return
     * @throws FileNotFoundException
     */
    public static String processImageDregee(Context context, String filePath,
                                            String fileName, int q) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath);
        int degree = readPictureDegree(filePath);
        if (degree != 0) {// 旋转照片角度
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(filePath, fileName);
        FileOutputStream out = new FileOutputStream(outputFile);
        bm.compress(CompressFormat.JPEG, q, out);
        return outputFile.getPath();
    }


    /**
     * 判断照片角度
     *
     * @param path 图片路径
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap  图片
     * @param degress 旋转角度
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), m, true);
            //	bitmap.recycle(); // modify by xiaosong.xu to recycle useless bitmap
            return bitmapRotated;
        }
        return bitmap;
    }

    /**
     * 圆形头像
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toOvalBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // 消除锯齿
        paint.setAntiAlias(true);
        // 和画圆形图片就这块不同
        canvas.drawOval(rectF, paint);
        // 这句一定要加 设置两图交映的效果
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
        // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
        // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }

    /**
     * 圆角图片
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // 消除锯齿
        paint.setAntiAlias(true);
        // 和画圆形图片就这块不同
        canvas.drawRoundRect(rectF, pixels, pixels, paint);
        // 这句一定要加 设置两图交映的效果
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }

    /**
     * 1)Drawable → Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 2)Drawable → Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap convertDrawable2BitmapSimple(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * Bitmap → Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        return bd;
    }

/*	public static void setAlphaSelector(Context context, View img, int res) {
        setAlphaSelector(context, img, res, 0);

	}*/

    /**
     * @param context
     * @param img       ImageView , Button , TextView
     * @param res
     * @param selection 0,1,2,3 :left ,top ,right,bottom
     */
    public static void setAlphaSelector(Context context, View img, int res,
                                        int selection) {
        if (context == null || res < 0 || img == null) {
            return;
        }

        // NinePatchDrawable d ;
        StateListDrawable selector = new StateListDrawable();
        Drawable sourcePic = context.getResources().getDrawable(res);
        if (sourcePic instanceof BitmapDrawable) {
            Drawable pic = new AlphaBitmapDrawable(context.getResources(),
                    ((BitmapDrawable) sourcePic).getBitmap(), 130);
            selector.addState(new int[]{android.R.attr.state_pressed}, pic);
            Drawable pic2 = new AlphaBitmapDrawable(context.getResources(),
                    ((BitmapDrawable) sourcePic).getBitmap(), 255);
            selector.addState(new int[]{}, pic2);
            if (img instanceof ImageView) {
                if (selection == 0) {
                    ((ImageView) img).setImageDrawable(selector);
                } else {
                    img.setBackgroundDrawable(selector);
                }
            } else if (img instanceof TextView) {
                switch (selection) {
                    case 0:
                        ((TextView) img).setCompoundDrawablesWithIntrinsicBounds(
                                selector, null, null, null);
                        break;
                    case 1:
                        ((TextView) img).setCompoundDrawablesWithIntrinsicBounds(
                                null, selector, null, null);
                        break;
                    case 2:
                        ((TextView) img).setCompoundDrawablesWithIntrinsicBounds(
                                null, null, selector, null);
                        break;
                    case 3:
                        ((TextView) img).setCompoundDrawablesWithIntrinsicBounds(
                                null, null, null, selector);
                        break;
                    default:
                        ((TextView) img).setBackgroundDrawable(selector);
                        break;
                }
            } else {
                img.setBackgroundDrawable(selector);
            }
        }
    }

    /**
     * 给图片设置透明度
     *
     * @param context
     * @param sourcePic
     * @param selection -1拒绝物流单 显示灰色
     */
    public static Drawable setAlphaBitmap(Context context, Bitmap sourcePic,
                                          int selection) {
        if (context == null || null == (sourcePic)) {
            return null;
        }
        StateListDrawable selector = new StateListDrawable();
        Drawable pic = new AlphaBitmapDrawable(context.getResources(),
                sourcePic, 125);
        selector.addState(new int[]{}, pic);
        if (selection == -1) {
            return selector;
        } else {
            return convertBitmap2Drawable(sourcePic);
        }
    }

    /**
     * @param uri
     * @param c
     * @return URI转path
     * @author xuxs
     */
    public static String getPath(Uri uri, Context c) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String imagePath = "";
        Cursor cursor = c.getContentResolver().query(uri, proj,// 查哪一列
                null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }
        return imagePath;
    }

    /**
     * @param uri
     * @return
     * @author xuxs
     */
    private Bitmap decodeUriAsBitmap(Uri uri, Context c) {
        if (null == (uri)) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(c.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return 文件后缀名
     */
    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase(Locale.CHINA);
                return fileType;
            }
        }
        return "";
    }

    /**
     * 根据后缀名判断是否是图片文件
     *
     * @param type
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String type) {
        if (type != null
                && (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("gif")
                || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("jpeg")
                || type.equalsIgnoreCase("bmp") || type.equalsIgnoreCase("wbmp")
                || type.equalsIgnoreCase("ico") || type.equalsIgnoreCase("jpe"))) {
            return true;
        }
        return false;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
//            if(null != bitmap){
//                bitmap.recycle();
//                bitmap = null;
//            }
        return output;
    }

    static HashMap<Integer, WeakReference<Bitmap>> backgroundMap = new HashMap<Integer, WeakReference<Bitmap>>();

    public static BitmapDrawable getBitmapForRaw(Context context, int resId) {
        WeakReference<Bitmap> background = backgroundMap.get(resId);
        Bitmap bitmap = null;
        if (null != background && null != (bitmap = background.get())
                && !bitmap.isRecycled()) {
            return new BitmapDrawable(context.getResources(), bitmap);
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        InputStream is = context.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(is, null, opt);
        int size = PictureUtil.calculateInSampleSize(opt, context
                .getResources().getDisplayMetrics().widthPixels, context
                .getResources().getDisplayMetrics().heightPixels);
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = size;
        bitmap = BitmapFactory.decodeStream(is, null, opt);
        backgroundMap.put(resId, background);

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }


    public static Bitmap getBitmapFromResource(Context ct, Integer id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Resources res = ct.getResources();
        for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
            try {
                bitmap = BitmapFactory.decodeResource(res, id, options);
//				L.d("Decoded successfully for sampleSize " + options.inSampleSize);
                break;
            } catch (OutOfMemoryError oom) {
                // If an OutOfMemoryError occurred, we continue with for loop and next inSampleSize value
                Log.e("OOM", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize + " retrying with " +
                        "higher value");
                System.gc();
            }
        }
        return bitmap;
    }


    //提取图像Alpha位图
    public static Bitmap getAlphaBitmap(Bitmap mBitmap, int mColor) {
//          BitmapDrawable mBitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.enemy_infantry_ninja);
//          Bitmap mBitmap = mBitmapDrawable.getBitmap();

        //BitmapDrawable的getIntrinsicWidth（）方法，Bitmap的getWidth（）方法
        //注意这两个方法的区别
        //Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmapDrawable.getIntrinsicWidth(), mBitmapDrawable.getIntrinsicHeight(), Config.ARGB_8888);
        Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);

        Canvas mCanvas = new Canvas(mAlphaBitmap);
        Paint mPaint = new Paint();

        mPaint.setColor(mColor);
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = mBitmap.extractAlpha();
        //在画布上（mAlphaBitmap）绘制alpha位图
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

        return mAlphaBitmap;
    }

    private static class AlphaBitmapDrawable extends BitmapDrawable {
        private int alpha;

        AlphaBitmapDrawable(Resources resources, Bitmap bitmap, int i) {
            super(resources, bitmap);
            this.alpha = i;
            super.setAlpha(alpha);
        }

        public void setBitmapAlpha(int a) {
            this.alpha = a;
            super.setAlpha(alpha);
        }
    }
}
