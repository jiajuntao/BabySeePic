package cn.babysee.picture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import cn.babysee.R;
import cn.babysee.base.BaseFragmentActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.remind.RemindHelper;
import cn.babysee.picture.update.UpdateHelper;
import cn.babysee.utils.Utils;

import com.baidu.mobstat.StatService;

public class MainActivity extends BaseFragmentActivity {

    private static final boolean DEBUG = AppEnv.DEBUG;
    private static final String TAG = "MainActivity";

    private static final int DIALOG_ABOUT = 1;
    private static final int DIALOG_MARKET = 2;

    private UpdateHelper updateHelper = null;
    private static final int NUM_ITEMS = 2;

    private MyAdapter mAdapter;
    private ViewPager mPager;
    private ImageView mDot1;
    private ImageView mDot2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AppEnv.initScreen(this);

        mDot1 = (ImageView) findViewById(R.id.dot1);
        mDot2 = (ImageView) findViewById(R.id.dot2);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mDot1.setImageResource(R.drawable.guide_dot_green);
                    mDot2.setImageResource(R.drawable.guide_dot_normal);
                } else if (position == 1) {
                    mDot1.setImageResource(R.drawable.guide_dot_normal);
                    mDot2.setImageResource(R.drawable.guide_dot_green);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        if (RemindHelper.goToSupportUs(mContext)) {
            StatService.onEvent(mContext, StatServiceEnv.MAIN_REMIND_SUPPORT_EVENT_ID,
                    StatServiceEnv.MAIN_REMIND_SUPPORT_LABEL, 1);
            showDialog(DIALOG_MARKET);
        }

        updateHelper = new UpdateHelper(this);
        updateHelper.checkUpdate();
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
                if (DEBUG)
                    e.printStackTrace();
            }
            break;
        case R.id.menu_support_us:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_SUPPORT_US_EVENT_ID,
                    StatServiceEnv.MAIN_SUPPORT_US_LABEL, 1);
            showDialog(DIALOG_MARKET);
            break;
        case R.id.menu_update:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_CHECK_UPDATE_EVENT_ID,
                    StatServiceEnv.MAIN_CHECK_UPDATE_LABEL, 1);
            updateHelper.update(false);
            break;
        case R.id.menu_share2friend:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_SHARE_FRIEND_EVENT_ID,
                    StatServiceEnv.MAIN_SHARE_FRIEND_LABEL, 1);
            Utils.share2friend(this, R.string.share2friends_title, R.string.share2friends_content);
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

            return new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(versionName).setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dismissDialog(DIALOG_ABOUT);
                        }
                    }).create();

        case DIALOG_MARKET:

            return new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
                    .setTitle(R.string.goto_market_dialog_title).setMessage(R.string.goto_market_dialog_content)
                    .setPositiveButton(R.string.goto_market_dialog_btn_ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            StatService.onEvent(mContext, StatServiceEnv.MAIN_SUPPORT_US_NOW_EVENT_ID,
                                    StatServiceEnv.MAIN_SUPPORT_US_NOW_LABEL, 1);
                            RemindHelper.goToMarketScore(MainActivity.this);
                        }
                    }).setNegativeButton(R.string.goto_market_dialog_btn_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            StatService.onEvent(mContext, StatServiceEnv.MAIN_SUPPORT_US_LATER_EVENT_ID,
                                    StatServiceEnv.MAIN_SUPPORT_US_LATER_LABEL, 1);
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

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MainFragment();
            } else {
                return new MainFragment2();
            }
        }
    }
}
