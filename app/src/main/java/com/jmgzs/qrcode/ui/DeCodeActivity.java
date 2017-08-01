package com.jmgzs.qrcode.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResultType;
import com.jmgzs.qrcode.result.AddressBookActivity;
import com.jmgzs.qrcode.result.BarcodeActivity;
import com.jmgzs.qrcode.result.TextActivity;
import com.jmgzs.qrcode.result.UriActivity;
import com.jmgzs.zxing.scanner.decode.QRDecode;

import static com.google.zxing.client.result.ParsedResultType.GEO;
import static com.google.zxing.client.result.ParsedResultType.TEL;

/**
 * 单击解析图片
 */
public class DeCodeActivity extends BasicScannerActivity {


    @Override
    void onResultActivity(Result result, ParsedResultType type, Bundle bundle) {
//        switch (type) {
//            case ADDRESSBOOK:
//                AddressBookActivity.gotoActivity(DeCodeActivity.this, bundle);
//                break;
//            case PRODUCT:
//                BarcodeActivity.gotoActivity(DeCodeActivity.this, bundle);
//                break;
//            case ISBN:
//                BarcodeActivity.gotoActivity(DeCodeActivity.this, bundle);
//                break;
//            case URI:
//                UriActivity.gotoActivity(DeCodeActivity.this, bundle);
//                break;
//            case TEXT:
                TextActivity.gotoActivity(DeCodeActivity.this, bundle);
//                break;
//            case GEO:
//                break;
//            case TEL:
//                break;
//            case SMS:
//                break;
//        }
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        finish();
    }

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] bytes = extras.getByteArray("bytes");
            if (bytes != null && bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("请稍候...");
                    progressDialog.show();
                    QRDecode.decodeQR(bitmap, this);
                }
            }
        }
    }

    @Override
    protected int getContent(Bundle save) {
        return 0;
    }

    @Override
    protected void initView() {

    }

    public static void gotoActivity(Activity activity, byte[] bytes) {
        activity.startActivity(new Intent(activity, DeCodeActivity.class).putExtra("bytes", bytes));
    }
}
