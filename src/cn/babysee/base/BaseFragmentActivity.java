package cn.babysee.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cn.babysee.picture.env.ThemeHelper;
import cn.babysee.picture.remind.RemindHelper;

public class BaseFragmentActivity extends FragmentActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        Intent intent = getIntent();
        if(intent != null) {
            int notifId = intent.getIntExtra("NotificationId", -1);
            if (notifId != -1) {
                RemindHelper.removeNotification(mContext, notifId);
            }
        }
    }
}
