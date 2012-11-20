package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cn.babysee.picture.env.ThemeHelper;

public class BaseFragmentActivity extends FragmentActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }
}
