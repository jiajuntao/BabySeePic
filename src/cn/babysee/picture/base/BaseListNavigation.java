package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.env.ThemeHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public abstract class BaseListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {
    private TextView mSelected;
    protected String[] mLocations;
    private int arrayRId;
    protected Context mContext;
    protected int mNavigationItemPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.THEME); 
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.list_navigation);
        mSelected = (TextView)findViewById(R.id.text);

        arrayRId = getActionBarDropDownViewResource();
        mLocations = getResources().getStringArray(arrayRId);

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, arrayRId, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
    }
    
    protected abstract int getActionBarDropDownViewResource();
    
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mSelected.setText("Selected: " + mLocations[itemPosition]);
        mNavigationItemPosition = itemPosition;
        return true;
    }
}
