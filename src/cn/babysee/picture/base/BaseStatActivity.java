package cn.babysee.picture.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.babysee.picture.env.ThemeHelper;
import cn.babysee.picture.remind.RemindHelper;

import com.baidu.mobstat.StatActivity;

public class BaseStatActivity extends StatActivity {

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
