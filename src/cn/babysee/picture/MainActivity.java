package cn.babysee.picture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.babysee.picture.base.BaseStatActivity;
import cn.babysee.picture.draw.DrawBoardActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.game.GameListActivity;
import cn.babysee.picture.guide.GuideListActivity;
import cn.babysee.picture.nutrition.NutritionFragmentTabNavigation;
import cn.babysee.picture.remind.RemindHelper;
import cn.babysee.picture.test.TestListActivity;
import cn.babysee.picture.tools.BabyHeightActivity;

import com.baidu.mobstat.StatService;

public class MainActivity extends BaseStatActivity implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "MainActivity";

    private static final int DIALOG_ABOUT = 1;

    private static final int DIALOG_MARKET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AppEnv.initScreen(this);

        findViewById(R.id.title).setOnClickListener(this);
        findViewById(R.id.brush).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
        findViewById(R.id.game).setOnClickListener(this);
        findViewById(R.id.seepic).setOnClickListener(this);
        findViewById(R.id.guide).setOnClickListener(this);
        findViewById(R.id.nutrition).setOnClickListener(this);
        findViewById(R.id.tools).setOnClickListener(this);

        if (RemindHelper.goToSupportUs(mContext)) {
            showDialog(DIALOG_MARKET);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.brush:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYDRAW_EVENT_ID,
                        StatServiceEnv.MAIN_BABYDRAW_LABEL, 1);
                startActivity(new Intent(mContext, DrawBoardActivity.class));
                break;
            case R.id.game:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYGAME_EVENT_ID,
                        StatServiceEnv.MAIN_BABYGAME_LABEL, 1);
                startActivity(new Intent(mContext, GameListActivity.class));
                break;
            case R.id.test:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_TEST_EVENT_ID,
                        StatServiceEnv.MAIN_TEST_LABEL, 1);
                startActivity(new Intent(mContext, TestListActivity.class));
                break;
            case R.id.seepic:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_SEE_PIC_EVENT_ID,
                        StatServiceEnv.MAIN_SEE_PIC_LABEL, 1);
                startActivity(new Intent(mContext, SeePicActivity.class));
                break;
            case R.id.guide:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_GUIDE_EVENT_ID,
                        StatServiceEnv.MAIN_GUIDE_LABEL, 1);
                startActivity(new Intent(mContext, GuideListActivity.class));
                break;
            case R.id.nutrition:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_NUTRITION_EVENT_ID,
                        StatServiceEnv.MAIN_NUTRITION_LABEL, 1);
                startActivity(new Intent(mContext, NutritionFragmentTabNavigation.class));
                break;
            case R.id.tools:
                StatService.onEvent(mContext, StatServiceEnv.MAIN_TOOLS_EVENT_ID,
                        StatServiceEnv.MAIN_TOOLS_LABEL, 1);
                startActivity(new Intent(mContext, BabyHeightActivity.class));
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
                StatService.onEvent(mContext, StatServiceEnv.MAIN_MENU_ABOUT_EVENT_ID,
                        StatServiceEnv.MAIN_MENU_ABOUT_LABEL, 1);
                showDialog(DIALOG_ABOUT);
                break;
            case R.id.menu_advice:

                StatService.onEvent(mContext, StatServiceEnv.MAIN_MENU_ADVICE_EVENT_ID,
                        StatServiceEnv.MAIN_MENU_ADVICE_LABEL, 1);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri maiUri = Uri.parse("mailto:yxstudio@gmail.com");
                    intent.setData(maiUri);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_advice)));
                } catch (Exception e) {
                    if (DEBUG) e.printStackTrace();
                }
                break;
            case R.id.menu_support_us:
                showDialog(DIALOG_MARKET);
                break;

            default:
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DIALOG_ABOUT:

                String versionName = getString(R.string.app_name);
                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    versionName += packageInfo.versionName;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                View view = View.inflate(this, R.layout.about, null);

                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(versionName)
                        .setView(view)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dismissDialog(DIALOG_ABOUT);
                                    }
                                }).create();

            case DIALOG_MARKET:

                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.goto_market_dialog_title)
                        .setMessage(R.string.goto_market_dialog_content)
                        .setPositiveButton(R.string.goto_market_dialog_btn_ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        RemindHelper.goToMarketScore(MainActivity.this);
                                    }
                                })
                        .setNegativeButton(R.string.goto_market_dialog_btn_cancel,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        RemindHelper.showNotification(mContext);
                                        dismissDialog(DIALOG_MARKET);
                                    }
                                })

                        .create();

            default:
                break;
        }

        return super.onCreateDialog(id);
    }

}
