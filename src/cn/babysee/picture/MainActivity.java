package cn.babysee.picture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.babysee.picture.draw.FingerPaint;

public class MainActivity extends Activity implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "MainActivity";

    private View title;

    private View view1;

    private View view2;

    private View view3;

    private View view4;

    private View brush;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = getApplicationContext();

        title = findViewById(R.id.title);
        title.setOnClickListener(this);
        view1 = findViewById(R.id.item_1);
        view1.setOnClickListener(this);
        view2 = findViewById(R.id.item_2);
        view2.setOnClickListener(this);
        view3 = findViewById(R.id.item_3);
        view3.setOnClickListener(this);
        view4 = findViewById(R.id.item_4);
        view4.setOnClickListener(this);
        brush = findViewById(R.id.brush);
        brush.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title:
                //                startActivity(new Intent(context, WebViewActivity.class));
                break;
            case R.id.item_1:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 0));
                break;
            case R.id.item_2:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 1));
                break;
            case R.id.item_3:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 2));
                break;
            case R.id.item_4:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 3));
                break;
            case R.id.brush:
                startActivity(new Intent(context, FingerPaint.class));
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (DEBUG) Log.d(TAG, "onMenuItemSelected");

        switch (item.getItemId()) {
            case R.id.menu_about:
                showDialog(0);
                break;
            case R.id.menu_advice:

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri maiUri = Uri.parse("mailto:babyseepic@gmail.com");
                    intent.setData(maiUri);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_advice)));
                } catch (Exception e) {
                    if (DEBUG) e.printStackTrace();
                }
                break;
            //        case R.id.menu_settings:
            //
            //            break;

            case R.id.title:
                //                startActivity(new Intent(context, WebViewActivity.class));
                break;
            case R.id.item_1:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 0));
                break;
            case R.id.item_2:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 1));
                break;
            case R.id.item_3:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 2));
                break;
            case R.id.item_4:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 3));
                break;
            case R.id.brush:
                startActivity(new Intent(context, FingerPaint.class));
                break;

            default:
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        View view = View.inflate(this, R.layout.about, null);

        return new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.app_version).setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dismissDialog(0);
                    }
                }).create();
    }
}
