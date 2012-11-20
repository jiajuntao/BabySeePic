package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import cn.babysee.picture.env.ThemeHelper;

import com.baidu.mobstat.StatActivity;

public class BaseStatActivity extends StatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }
}
