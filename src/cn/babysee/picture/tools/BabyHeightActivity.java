package cn.babysee.picture.tools;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;

import com.baidu.mobstat.StatActivity;

public class BabyHeightActivity extends StatActivity implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "BabyHeightActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_height);

        mContext = getApplicationContext();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }
}
