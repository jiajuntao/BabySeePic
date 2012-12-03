package cn.babysee.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.babysee.picture.env.ThemeHelper;
import cn.babysee.picture.remind.RemindHelper;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;

public class BaseActivity extends SherlockActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        if(intent != null) {
            int notifId = intent.getIntExtra("NotificationId", -1);
            if (notifId != -1) {
                RemindHelper.removeNotification(mContext, notifId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
