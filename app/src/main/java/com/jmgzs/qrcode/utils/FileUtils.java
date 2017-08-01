package com.jmgzs.qrcode.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Wxl on 2017/6/9.
 */

public class FileUtils {

    public static String getAppBaseFile(Context context) {
        File sdCardPath;
        if (PermissionUtil.getInstance().isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            sdCardPath = Environment.getExternalStorageDirectory();
            return sdCardPath.toString() + File.separator + "QRCode";
        }
        return context.getCacheDir().getPath();
    }

    private static final int BUFFER = 8192;


    // 读取文件
    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
            ;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    // 从流中读取文件
    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }

    // 将文本内容写入文件
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

//    public static boolean isReleaseAssetsOk(Context context, String assetsDir,
//                                            String releaseDir){
//        AssetManager assets = context.getAssets();
//        int assetsLength = 0;
//        try {
//            String[] fileNames = assets.list(assetsDir);//只能获取到文件(夹)名,所以还得判断是文件夹还是文件
//            if (fileNames.length > 0) {// is dir
//                for (String name : fileNames) {
//                    if (!TextUtils.isEmpty(assetsDir)) {
//                        name = assetsDir + File.separator + name;//补全assets资源路径
//                    }
//    //                    Log.i(, brian name= + name);
//                    String[] childNames = assets.list(name);//判断是文件还是文件夹
//                    if (!TextUtils.isEmpty(name) && childNames.length > 0) {
//                        checkFolderExists(releaseDir + File.separator + name);
//                        releaseAssets(context, name, releaseDir);//递归, 因为资源都是带着全路径,
//                        //所以不需要在递归是设置目标文件夹的路径
//                    } else {
//                        InputStream is = assets.open(name);
//    //                        FileUtil.writeFile(releaseDir + File.separator + name, is);
//                        writeFile(releaseDir + File.separator + name, is);
//                    }
//                }
//            } else {// is file
//                InputStream is = assets.open(assetsDir);
//                // 写入文件前, 需要提前级联创建好路径, 下面有代码贴出
//    //                FileUtil.writeFile(releaseDir + File.separator + assetsDir, is);
//                writeFile(releaseDir + File.separator + assetsDir, is);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private static int getFileLength(){
//
//    }

    public static void releaseAssets(Context context, String assetsDir,
                                     String releaseDir) {
        if (TextUtils.isEmpty(releaseDir)) {
            return;
        } else if (releaseDir.endsWith("/")) {
            releaseDir = releaseDir.substring(0, releaseDir.length() - 1);
        }

        if (TextUtils.isEmpty(assetsDir) || assetsDir.equals("/")) {
            assetsDir = "";
        } else if (assetsDir.endsWith("/")) {
            assetsDir = assetsDir.substring(0, assetsDir.length() - 1);
        }

        AssetManager assets = context.getAssets();
        try {
            String[] fileNames = assets.list(assetsDir);//只能获取到文件(夹)名,所以还得判断是文件夹还是文件
            if (fileNames.length > 0) {// is dir
                for (String name : fileNames) {
                    if (!TextUtils.isEmpty(assetsDir)) {
                        name = assetsDir + File.separator + name;//补全assets资源路径
                    }
//                    Log.i(, brian name= + name);
                    String[] childNames = assets.list(name);//判断是文件还是文件夹
                    if (!TextUtils.isEmpty(name) && childNames.length > 0) {
                        checkFolderExists(releaseDir + File.separator + name);
                        releaseAssets(context, name, releaseDir);//递归, 因为资源都是带着全路径,
                        //所以不需要在递归是设置目标文件夹的路径
                    } else {
                        InputStream is = assets.open(name);
//                        FileUtil.writeFile(releaseDir + File.separator + name, is);
                        writeFile(releaseDir + File.separator + name, is);
                    }
                }
            } else {// is file
                InputStream is = assets.open(assetsDir);
                // 写入文件前, 需要提前级联创建好路径, 下面有代码贴出
//                FileUtil.writeFile(releaseDir + File.separator + assetsDir, is);
                writeFile(releaseDir + File.separator + assetsDir, is);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean writeFile(String fileName, InputStream in) throws IOException {
        boolean bRet = true;
        try {
            OutputStream os = new FileOutputStream(fileName);
            byte[] buffer = new byte[4112];
            int read;
            while ((read = in.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            in.close();
            in = null;
            os.flush();
            os.close();
            os = null;
//          Log.v(TAG, "copyed file: " + fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bRet = false;
        }
        return bRet;
    }

    private static void checkFolderExists(String path) {
        File file = new File(path);
        if ((file.exists() && !file.isDirectory()) || !file.exists()) {
            file.mkdirs();
        }
    }

    //复制文件夹
    public static void copyFileDir(File sourceFile, File targetFile)
            throws IOException {
        if (sourceFile.isDirectory() && targetFile.isDirectory()) {
            File newDir = new File(targetFile, sourceFile.getName());
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File[] subFiles = sourceFile.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File subFile : subFiles) {
                    if (subFile != null && subFile.exists()) {
                        if (subFile.isDirectory()) {
                            copyFileDir(subFile, newDir);
                        } else {
                            File newFile = new File(newDir, subFile.getName());
                            if (!newFile.exists()) {
                                newFile.createNewFile();
                            }
                            copyFile(subFile, newFile);
                        }
                    }
                }
            }
        }
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[BUFFER];
            int length;
            while ((length = inBuff.read(buffer)) != -1) {
                outBuff.write(buffer, 0, length);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null) {
                inBuff.close();
            }
            if (outBuff != null) {
                outBuff.close();
            }
        }
    }

    // 创建文件夹
    public static File createDir(String dir) {
        File folderDir = new File(dir);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }
        return folderDir;
    }

    // 创建文件
    public static File createFile(Context context, String dir, String fileName) {
        File folderDir = new File(dir);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }
        File fileNew = new File(folderDir, fileName);
        if (!fileNew.exists()) {
            try {
                fileNew.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return fileNew;
    }

    // 创建文件用于缓存
    public static File createFile(Context context, String cacheDir,
                                  String subDir, String fileName) {
        if (!isDirExist(context, cacheDir)) {
            File file = new File(getFilePath(context, cacheDir));
            file.mkdirs();
        }

        String cacheTempDir = getFilePath(context, cacheDir, subDir);
        File fileDir = new File(cacheTempDir);
        if (!fileDir.exists()) {
            File subFile = new File(cacheTempDir);
            subFile.mkdirs();
        }

        String fileTargetDir = cacheTempDir + fileName;
        File fileNew = new File(fileTargetDir);
        if (!fileNew.exists()) {
            try {
                fileNew.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }

        return fileNew;
    }

    public static String getCachePath(Context context) {
        return getFilePath(context, "cache");
    }

    // 获取文件的路径
    public static String getFilePath(Context context, String fileDir) {
        String filePath = getAppBaseFile(context) + File.separator + fileDir;
        createDir(filePath);
        return filePath;
    }

    //
    public static String getFilePath(Context context, String cacheDir,
                                     String subDir) {
        String baseDir = getAppBaseFile(context);
        if (!"".equals(baseDir)) {
            baseDir = baseDir + File.separator + cacheDir + File.separator
                    + subDir + File.separator;
        }
        createDir(baseDir);
        return baseDir;
    }

    public static String getFileDirBasePath(Context context) {
        return context.getFilesDir().getPath() + File.separator;
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        deleteDir(file);
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身

    }

    public static boolean isDirExist(Context context, String cacheDir) {
        File file = new File(getFilePath(context, cacheDir));
        return file.isDirectory() && file.exists();
    }

    public static File createSDDir(Context context, String dirName) {
        File dir = new File(getAppBaseFile(context) + File.separator + dirName);
        if (!dir.exists() && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
//            L.d("createSDDir:" + dir.getAbsolutePath());
            dir.mkdir();
        }
        return dir;
    }

    public static boolean isFileExist(Context ct, String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        File file = new File(getAppBaseFile(ct) + File.separator + fileName);
        return isFileExist(file);
    }

    public static boolean isFileExistOnFileDir(Context ct, String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        File file = new File(getFileDirBasePath(ct) + fileName);
        return isFileExist(file);
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        return isFileExist(file);
    }

    public static boolean isFileExist(File file) {
        return file != null && file.isFile() && file.exists();
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1)
                        break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }


    public static File getOutPutMediaFile(Context ct) {
        if (!PermissionUtil.getInstance().isGranted(ct, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            T.toastS(ct, "未授权存储卡访问权限!");
            return null;
        }
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (dir.exists() || (!dir.exists() && dir.mkdirs())) {
            String timeMill = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            return new File(dir.getAbsolutePath() + File.separator + "IMG_" + timeMill + ".jpeg");
        } else {
            T.toastS(ct, "未找到存储卡,无法存储二维码!");
            return null;
        }
    }

    public static Uri savePicture(Context ct, Bitmap bmp) {
        if (bmp == null || ct == null || bmp.isRecycled()) return null;
        File f = getOutPutMediaFile(ct);
        if (f == null) return null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bos.toByteArray());
            fos.flush();
            bmp.recycle();
            Uri uri = Uri.fromFile(f);
            updateMediaDB(ct, uri);
            return uri;

        } catch (Exception ee) {
            ee.printStackTrace();
            boolean b = f.delete();
        }
        return null;
    }

    /**
     * 更新系统相册
     * @param ct
     * @param data
     */
    public static void updateMediaDB(Context ct, Uri data) {
        if (ct != null && data != null) {
            Intent scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scan.setData(data);
            ct.sendBroadcast(scan);
        }
    }
}
