package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;

import cn.babysee.picture.env.ThemeHelper;

import com.actionbarsherlock.app.SherlockActivity;

public class BaseActivity extends SherlockActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME); 
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }
}
