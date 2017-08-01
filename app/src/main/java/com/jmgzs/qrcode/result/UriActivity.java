package com.jmgzs.qrcode.result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgzs.qrcode.R;
import com.jmgzs.qrcode.base.BaseActivity;
import com.jmgzs.zxing.scanner.common.Scanner;
import com.jmgzs.zxing.scanner.result.URIResult;

/**
 * URI显示
 */
public class UriActivity extends BaseActivity {
    private WebView swipeRefreshWebView;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_uri;
    }

    @Override
    protected void initView() {

        String uri = ((URIResult) getIntent().getSerializableExtra(Scanner.Scan.RESULT)).getUri();

        swipeRefreshWebView = (WebView) findViewById(R.id.webView);
        swipeRefreshWebView.loadUrl(uri);
        swipeRefreshWebView.setWebViewClient(new SampleWebViewClient());
    }

    private class SampleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
//            swipeRefreshWebView.autoRefresh();
            return true;
        }
    }

    public static void gotoActivity(Activity activity, Bundle bundle) {
        activity.startActivity(new Intent(activity, UriActivity.class).putExtras(bundle));
    }
}
