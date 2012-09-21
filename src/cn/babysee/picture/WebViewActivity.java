package cn.babysee.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;


public class WebViewActivity extends Activity {
    
    private WebView webview;
    
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
        context = getApplicationContext();
        
        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        webview.loadUrl("file:///android_asset/help.html");
    }
}
