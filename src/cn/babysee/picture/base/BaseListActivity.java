package cn.babysee.picture.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.baidu.mobstat.StatService;

public class BaseListActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(mContext);
    }

    @Override
    protected void onPause() {
        StatService.onPause(mContext);
        super.onPause();
    }
}
