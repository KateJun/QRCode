package com.jmgzs.qrcode.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.client.result.ParsedResultType;
import com.jmgzs.lib.adv.AdvUtil;
import com.jmgzs.lib.adv.enums.AdSlotType;
import com.jmgzs.lib.colorpicker.ColorPickerDialog;
import com.jmgzs.lib.colorpicker.OnColorPickerListener;
import com.jmgzs.lib.view.roundedimage.RoundedDrawable;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;
import com.jmgzs.qrcode.base.GlideApp;
import com.jmgzs.qrcode.picture.PickPictureTotalActivity;
import com.jmgzs.qrcode.utils.DensityUtils;
import com.jmgzs.qrcode.utils.FileProvider7;
import com.jmgzs.qrcode.utils.FileUtils;
import com.jmgzs.qrcode.utils.GetPathFromUri4kitkat;
import com.jmgzs.qrcode.utils.PictureUtil;
import com.jmgzs.qrcode.utils.T;
import com.jmgzs.qrcode.utils.UmengUtil;
import com.jmgzs.zxing.scanner.encode.QREncode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.R.attr.width;
import static android.R.id.edit;
import static com.jmgzs.qrcode.R.id.btn_create;
import static com.jmgzs.qrcode.R.id.editText;
import static com.jmgzs.qrcode.R.id.imageView;
import static com.jmgzs.qrcode.R.id.imgbtn_create;

/**
 * Created by mac on 17/7/24.
 * Description:
 */

public class CreateCodeActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_GALLERY = 101;
    private static final int PHOTO_REQUEST_CUT = 102;
    private ImageView imageQR;
    private RoundedImageView imageLogo;
    private RoundedImageView imageColorForground;
    private Button btnCreate;
    private ImageButton imgBtnClear;

    private EditText edtContent;

    private int color = Color.BLACK;
    private Bitmap logo;
    private String cropPath = null;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_createcode;
    }

    @Override
    protected void initView() {
        initToolbar();

        Button btnCreate = getView(R.id.btn_create);
        btnCreate.setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = getView(R.id.toolbar);
        TextView title = getView(R.id.toolbar_title);
        title.setText(R.string.create_qrcode);
        title.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        UmengUtil.event(CreateCodeActivity.this, UmengUtil.U_CREATE);
                        createCode();
                        break;
                    case R.id.item_reset:
                        UmengUtil.event(CreateCodeActivity.this, UmengUtil.U_RESET);
                        changeBtn(R.string.create_qrcode_btntx, true);
                        break;
                    default:
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
        });
        toolbar.setPopupTheme(R.style.PopupMenu);


        imageQR = getView(R.id.img_qrcode);
        edtContent = getView(R.id.edt_content);
        imageColorForground = getView(R.id.img_color);
        imageLogo = getView(R.id.img_logo);
        getView(R.id.layout_colorpicker).setOnClickListener(this);
        getView(R.id.layout_add_logo).setOnClickListener(this);
        getView(R.id.btn_reset).setOnClickListener(this);
        btnCreate = getView(R.id.btn_create);
        btnCreate.setOnClickListener(this);
        getView(R.id.btn_save).setOnClickListener(this);
        imgBtnClear = getView(R.id.imgBtn_clear);
        imgBtnClear.setOnClickListener(this);

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!canCreateBtnBack() && s.length() > 0) {
                    imgBtnClear.setVisibility(View.VISIBLE);
                } else {
                    imgBtnClear.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_add_logo:
                UmengUtil.event(this, UmengUtil.U_ADDICON);
                gallery();
                break;
            case R.id.layout_colorpicker:
                UmengUtil.event(this, UmengUtil.U_COLOR);
                showColorPicker();
                break;
//            case R.id.btn_create:
//                if (canCreateBtnBack())
//                    changeBtn(R.string.create_qrcode_btntx, true);
//                else
//                    createCode();
//                break;
            case R.id.btn_reset:
                UmengUtil.event(this, UmengUtil.U_RESET_CONFIG);
                reset();
                break;
            case R.id.btn_save:
                UmengUtil.event(this, UmengUtil.U_SAVE);
                save();
                break;
            case R.id.imgBtn_clear:
                edtContent.setText(null);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBackHome){
            AdSlotType type = AdSlotType.getRandomInsertType();
            AdvUtil.getInstance().showInsertAdv(this, type.getTemplateId(), null);
            isBackHome = false;
        }
    }

    @Override
    protected void onStop() {
        hideSoftInputMethod();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_qr, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (canCreateBtnBack()) {
            menu.findItem(R.id.item_create_qrcode).setVisible(false);
            menu.findItem(R.id.item_reset).setVisible(true);
        } else {
            menu.findItem(R.id.item_create_qrcode).setVisible(true);
            menu.findItem(R.id.item_reset).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void save() {
        if (canCreateBtnBack()) {
            imageQR.setDrawingCacheEnabled(true);//step 1
            Bitmap bitmap = imageQR.getDrawingCache();//step 2
            //step 3 转bytes
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            DeCodeActivity.gotoActivity(CreateCodeActivity.this, baos.toByteArray());//step 4
            Uri u = FileUtils.savePicture(this, bitmap);
            if (u != null) {
                T.toastS(this, "二维码保存成功");
            } else T.toastS(this, "二维码保存失败");
            imageQR.setDrawingCacheEnabled(false);
        } else {
            T.toastS(this, "请生成二维码");
        }
    }

    private void reset() {
        logo = null;
        color = Color.BLACK;
        imageLogo.setImageBitmap(null);
        imageColorForground.setImageBitmap(null);
        if (canCreateBtnBack())
            createCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_GALLERY:
                    String picturePath = data.getStringExtra(PickPictureTotalActivity.EXTRA_PICTURE_PATH);
                    if (picturePath == null || !FileUtils.isFileExist(picturePath)) {
                        T.toastS(this, "图片不存在");
                    } else {
//                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                            cropPath = GetPathFromUri4kitkat.getPath(this, data.getData());
//                        } else
//                            cropPath = (data.getData().getPath());
//                    }
//                    if (null != cropPath && FileUtils.isFileExist(cropPath)) {
                        BitmapFactory.Options outOptions = new BitmapFactory.Options();
                        outOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(picturePath, outOptions);
                        outOptions.inJustDecodeBounds = false;
                        if (Math.abs(outOptions.outWidth - outOptions.outHeight) <= 50) {
                            loadImageLogo(picturePath);
                        } else {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                                crop(Uri.parse("file://" + picturePath));
                            } else
                                crop(Uri.parse(picturePath));

                        }
//                    } else {
//                        T.toastS(this, "图片不存在");
                    }
                    Log.e(getClass().getSimpleName(), picturePath + "");

                    break;
                case PHOTO_REQUEST_CUT:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null && uri.getPath() != null) {
                            cropPath = uri.getPath();
                        }
                    }
                    if (null != cropPath && FileUtils.isFileExist(cropPath)) {
                        loadImageLogo(cropPath);
                    } else {
                        T.toastS(this, "图片不存在");
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void loadImageLogo(String cropPath) {
        GlideApp.with(this).asBitmap().load(cropPath).override(50).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                logo = RoundedDrawable.drawableToBitmap(RoundedDrawable.fromBitmap(resource));
                logo = resource;
                imageLogo.setImageBitmap(resource);
                if (canCreateBtnBack())
                    createCode();
                return true;
            }
        }).into(imageLogo);
    }

    private void createCode() {
        String qrContent = edtContent.getText().toString().trim();
        if (TextUtils.isEmpty(qrContent)) {
            T.toastS(this, "请输入二维码内容");
            edtContent.requestFocus();
            showSoftInputMethod();
            return;
        }
        hideSoftInputMethod();
        Bitmap bitmap = null;

        QREncode.Builder builder = new QREncode.Builder(this)
                .setColor(color)
                .setParsedResultType(ParsedResultType.TEXT)
                .setContents(qrContent)
//                .setSize(DensityUtils.getScreenWidthPixels(this)-20);
                .setSize(edtContent.getMeasuredHeight() - 4);
        if (logo != null)
            builder.setLogoBitmap(logo);
        bitmap = builder.build().encodeAsBitmap();

        if (bitmap != null) {
            imageQR.setImageBitmap(bitmap);
//            scanImage();
            changeBtn(R.string.back, false);
        }

    }

    private void changeBtn(int titleID, boolean hideQRimage) {
        btnCreate.setText(titleID);
        edtContent.setEnabled(hideQRimage);
        if (hideQRimage) {
            edtContent.setTextColor(Color.BLACK);
            imgBtnClear.setVisibility(View.VISIBLE);
            imageQR.setVisibility(View.INVISIBLE);
        } else {
            edtContent.setTextColor(Color.WHITE);
            imgBtnClear.setVisibility(View.INVISIBLE);
            imageQR.setVisibility(View.VISIBLE);
        }
    }


    private void scanImage() {
        imageQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputMethod();
                imageQR.setDrawingCacheEnabled(true);//step 1
                Bitmap bitmap = imageQR.getDrawingCache();//step 2
                //step 3 转bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                DeCodeActivity.gotoActivity(CreateCodeActivity.this, baos.toByteArray());//step 4
                imageQR.setDrawingCacheEnabled(false);//step 5
            }
        });
    }

    private void showColorPicker() {
        ColorPickerDialog dialog = new ColorPickerDialog(this, Color.BLACK, new OnColorPickerListener() {
            @Override
            public void onColorCancel(ColorPickerDialog dialog) {

            }

            @Override
            public void onColorChange(ColorPickerDialog dialog, int color) {

            }

            @Override
            public void onColorConfirm(ColorPickerDialog dialog, int cc) {
                color = cc;
                Bitmap b = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                c.drawColor(cc);
                imageColorForground.setImageBitmap(b);
                if (canCreateBtnBack()) {
                    createCode();
                }

            }
        });
        dialog.show();

    }

    /**
     * 从相册获取
     */
    public void gallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setType("image/*");
//        this.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

        Intent intent = new Intent(this, PickPictureTotalActivity.class);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:zjy
     * @date:
     */
    private void crop(Uri uri) {
        // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.CHINA);
//        String address = sDateFormat.format(new java.util.Date());
        File cropFile = FileUtils.getOutPutMediaFile(this);
//                new File(FileUtils.getFilePath(this, CACHE_TAKE_PHOTO), "IMG_"+address + ".JPEG");
        Uri imageUri = FileProvider7.getUriForFile(this, cropFile);
        cropPath = imageUri.getPath();
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        // 输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // 图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);// 取消人脸识别
        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private boolean canCreateBtnBack() {
        return (imageQR.getVisibility() == View.VISIBLE);
    }


    public void showSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtContent, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public void hideSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(edtContent.getWindowToken(), 0);
    }
}
