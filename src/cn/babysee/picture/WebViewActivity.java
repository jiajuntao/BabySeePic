package cn.babysee.picture;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import cn.babysee.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            webview.loadUrl("file:///android_asset/help.html");
        } else {
            webview.getSettings().setDefaultTextEncodingName("gb2312");
            webview.loadUrl("file:///android_asset/" + url);
        }
    }
}
