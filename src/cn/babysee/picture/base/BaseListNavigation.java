package cn.babysee.picture.base;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import cn.babysee.picture.R;
import cn.babysee.picture.env.ThemeHelper;
import cn.babysee.picture.remind.RemindHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public abstract class BaseListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {
    protected String[] mLocations;
    private int arrayRId;
    protected Context mContext;
    protected int mNavigationItemPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME); 
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = getApplicationContext();
        setContentView(R.layout.list_navigation);

        arrayRId = getActionBarDropDownViewResource();
        mLocations = getResources().getStringArray(arrayRId);

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, arrayRId, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        

        Intent intent = getIntent();
        if(intent != null) {
            int notifId = intent.getIntExtra("NotificationId", -1);
            if (notifId != -1) {
                RemindHelper.removeNotification(mContext, notifId);
            }
        }
    }
    
    protected abstract int getActionBarDropDownViewResource();
    
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (mLocations == null || itemPosition >= mLocations.length) {
            Log.e("BaseListNavigation", "error ! index out itemPosition:" + itemPosition + Arrays.toString(mLocations));
            return false;
        }
        
        mNavigationItemPosition = itemPosition;
        return true;
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
