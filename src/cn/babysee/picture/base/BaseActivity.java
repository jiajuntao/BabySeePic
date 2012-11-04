package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import cn.babysee.picture.env.ThemeHelper;

import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.mobstat.StatService;

public class BaseActivity extends SherlockActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
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
}
