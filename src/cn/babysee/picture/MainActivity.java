package cn.babysee.picture;

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
import cn.babysee.picture.draw.DrawBoradActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.game.GameListActivity;
import cn.babysee.picture.test.TestListActivity;

import com.baidu.mobstat.StatActivity;
import com.baidu.mobstat.StatService;

public class MainActivity extends StatActivity implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "MainActivity";

    private View title;

    private View brush;
    private View test;
    private View game;
    private View seepic;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = getApplicationContext();

        title = findViewById(R.id.title);
        title.setOnClickListener(this);
        brush = findViewById(R.id.brush);
        brush.setOnClickListener(this);
        test = findViewById(R.id.test);
        test.setOnClickListener(this);
        game = findViewById(R.id.game);
        game.setOnClickListener(this);
        seepic = findViewById(R.id.seepic);
        seepic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title:
            //                startActivity(new Intent(context, WebViewActivity.class));
            break;
        case R.id.brush:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYDRAW_EVENT_ID, StatServiceEnv.MAIN_BABYDRAW_LABEL, 1);
            startActivity(new Intent(mContext, DrawBoradActivity.class));
            break;
        case R.id.game:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYGAME_EVENT_ID, StatServiceEnv.MAIN_BABYGAME_LABEL, 1);
            startActivity(new Intent(mContext, GameListActivity.class));
            break;
        case R.id.test:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_TEST_EVENT_ID, StatServiceEnv.MAIN_TEST_LABEL, 1);
            startActivity(new Intent(mContext, TestListActivity.class));
            break;
        case R.id.seepic:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_SEE_PIC_EVENT_ID, StatServiceEnv.MAIN_SEE_PIC_LABEL, 1);
            startActivity(new Intent(mContext, SeePicActivity.class));
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
        if (DEBUG)
            Log.d(TAG, "onMenuItemSelected");

        switch (item.getItemId()) {
        case R.id.menu_about:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_MENU_ABOUT_EVENT_ID,
                    StatServiceEnv.MAIN_MENU_ABOUT_LABEL, 1);
            showDialog(0);
            break;
        case R.id.menu_advice:

            StatService.onEvent(mContext, StatServiceEnv.MAIN_MENU_ADVICE_EVENT_ID,
                    StatServiceEnv.MAIN_MENU_ADVICE_LABEL, 1);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri maiUri = Uri.parse("mailto:babyseepic@gmail.com");
                intent.setData(maiUri);
                startActivity(Intent.createChooser(intent, getString(R.string.menu_advice)));
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }
            break;
        //        case R.id.menu_settings:
        //
        //            break;

        default:
            break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        View view = View.inflate(this, R.layout.about, null);

        return new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(R.string.app_version)
                .setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dismissDialog(0);
                    }
                }).create();
    }
}
