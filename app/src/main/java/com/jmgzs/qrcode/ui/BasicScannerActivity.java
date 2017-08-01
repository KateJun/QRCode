package com.jmgzs.qrcode.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.jmgzs.qrcode.base.BaseActivity;
import com.jmgzs.zxing.scanner.OnScannerCompletionListener;
import com.jmgzs.zxing.scanner.common.Scanner;
import com.jmgzs.zxing.scanner.result.AddressBookResult;
import com.jmgzs.zxing.scanner.result.ISBNResult;
import com.jmgzs.zxing.scanner.result.ProductResult;
import com.jmgzs.zxing.scanner.result.URIResult;

/**
 * Created by hupei on 2016/7/7.
 */
public abstract class BasicScannerActivity extends BaseActivity implements OnScannerCompletionListener {

    public static final int REQUEST_CODE_SCANNER = 188;
    public static final String EXTRA_RETURN_SCANNER_RESULT = "return_scanner_result";
    private static final String TAG = "BasicScannerActivity";

    private boolean mReturnScanResult;

    /**
     * 子类实现，根据 ParsedResultType 处理业务
     *
     * @param result
     * @param type
     * @param bundle
     */
    abstract void onResultActivity(Result result, ParsedResultType type, Bundle bundle);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mReturnScanResult = extras.getBoolean(EXTRA_RETURN_SCANNER_RESULT);
        }
    }

    @Override
    public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        if (rawResult == null) {
            Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mReturnScanResult) {
            onReturnScanResult(rawResult);
            return;
        }
        Bundle bundle = new Bundle();
        ParsedResultType type = parsedResult.getType();
        bundle.putSerializable(Scanner.Scan.RESULT_TYPE,type);
        Log.i(TAG, "ParsedResultType: " + type);
        switch (type) {
            case ADDRESSBOOK:
                AddressBookParsedResult addressBook = (AddressBookParsedResult) parsedResult;
                bundle.putSerializable(Scanner.Scan.RESULT, new AddressBookResult(addressBook));
                break;
            case PRODUCT:
                ProductParsedResult product = (ProductParsedResult) parsedResult;
                Log.i(TAG, "productID: " + product.getProductID());
                bundle.putSerializable(Scanner.Scan.RESULT, new ProductResult(product));
                break;
            case ISBN:
                ISBNParsedResult isbn = (ISBNParsedResult) parsedResult;
                Log.i(TAG, "isbn: " + isbn.getISBN());
                bundle.putSerializable(Scanner.Scan.RESULT, new ISBNResult(isbn));
                break;
            case URI:
                URIParsedResult uri = (URIParsedResult) parsedResult;
                Log.i(TAG, "uri: " + uri.getURI());
                bundle.putSerializable(Scanner.Scan.RESULT, new URIResult(uri));
                break;
            case TEXT:
                TextParsedResult textParsedResult = (TextParsedResult) parsedResult;
                bundle.putString(Scanner.Scan.RESULT, textParsedResult.getText());
                break;
            case GEO:
                break;
            case TEL:
                break;
            case SMS:
                break;
        }
        onResultActivity(rawResult, type, bundle);
    }

    private void onReturnScanResult(Result rawResult) {
        Intent intent = getIntent();
        intent.putExtra(Scanner.Scan.RESULT, rawResult.getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
