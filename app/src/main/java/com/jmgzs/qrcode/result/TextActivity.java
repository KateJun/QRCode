package com.jmgzs.qrcode.result;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.result.ParsedResultType;
import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;
import com.jmgzs.qrcode.ui.DeCodeActivity;
import com.jmgzs.qrcode.utils.T;
import com.jmgzs.qrcode.utils.UmengUtil;
import com.jmgzs.zxing.scanner.common.Scanner;
import com.jmgzs.zxing.scanner.result.AddressBookResult;
import com.jmgzs.zxing.scanner.result.ISBNResult;
import com.jmgzs.zxing.scanner.result.ProductResult;
import com.jmgzs.zxing.scanner.result.URIResult;

import java.io.Serializable;

/**
 * 纯文本显示
 */
public class TextActivity extends BaseActivity {

    private Button copyBtn;
    private TextView resultTxt;

    @Override
    protected void initView() {
        Toolbar toolbar = getView(R.id.toolbar);
        TextView title = getView(R.id.toolbar_title);
        title.setText(R.string.result_scan);
        title.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        copyBtn = getView(R.id.btn_copy);
        copyBtn.setOnClickListener(this);
        resultTxt = getView(R.id.textView3);

        showResult();
    }

    private void showResult() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ParsedResultType type = (ParsedResultType) extras.getSerializable(Scanner.Scan.RESULT_TYPE);
            if (type == null) finish();
            StringBuilder sb = new StringBuilder();
            Serializable serializable = extras.getSerializable(Scanner.Scan.RESULT);
            switch (type) {
                case ADDRESSBOOK:
                    AddressBookResult addressBookResult = (AddressBookResult) serializable;
                    if (addressBookResult == null) finish();

                    String[] names = addressBookResult.getNames();
                    String[] phoneNumbers = addressBookResult.getPhoneNumbers();
                    String[] emails = addressBookResult.getEmails();


                    if (names != null && names.length > 0) {
                        sb.append("姓名：").append(names[0]).append("\n");
                    }

                    if (phoneNumbers != null && phoneNumbers.length > 0) {
                        sb.append("电话：").append(phoneNumbers[0]).append("\n");
                    }

                    if (emails != null && emails.length > 0) {
                        sb.append("邮箱：").append(emails[0]);
                    }
                    break;
                case PRODUCT:
                case ISBN:
                    if (serializable instanceof ProductResult) {
                        ProductResult productResult = (ProductResult) serializable;
                        sb.append(productResult.getProductID());
                    } else if (serializable instanceof ISBNResult) {
                        ISBNResult isbnResult = (ISBNResult) serializable;
                        sb.append(isbnResult.getISBN());
                    }
                    break;
                case URI:
                    URIResult uriR = (URIResult) serializable;
                    if (uriR != null) {
                        sb.append(uriR.getUri());
                    }
                    break;
                case TEXT:
                    sb.append(extras.getString(Scanner.Scan.RESULT));
                    break;
                case GEO:
                    break;
                case TEL:
                    break;
                case SMS:
                    break;
            }
            if (sb.length() > 0)
                resultTxt.setText(sb.toString());

        } else finish();
    }

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_text;
    }


    public static void gotoActivity(Activity activity, Bundle bundle) {
        activity.startActivity(new Intent(activity, TextActivity.class).putExtras(bundle));
    }

    @Override
    public void onClick(View view) {
        UmengUtil.event(this,UmengUtil.U_COPY);
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(null, resultTxt.getText()));
        T.toastS(this, "复制成功,可以发给朋友们了。");
    }
}
