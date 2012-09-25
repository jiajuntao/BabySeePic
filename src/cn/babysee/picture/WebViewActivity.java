package cn.babysee.picture;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import com.baidu.mobstat.StatActivity;

public class WebViewActivity extends StatActivity {

    private WebView webview;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        context = getApplicationContext();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        webview.loadUrl("file:///android_asset/help.html");
    }
}
