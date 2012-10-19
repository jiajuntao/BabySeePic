package cn.babysee.picture.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class BaseListActivity extends Activity {

    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }
}
