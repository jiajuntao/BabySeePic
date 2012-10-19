package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.babysee.picture.R;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class BaseListNavigation extends SherlockActivity implements ActionBar.OnNavigationListener {
    private TextView mSelected;
    private String[] mLocations;
    public static int THEME = R.style.Theme_Sherlock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_navigation);
        mSelected = (TextView)findViewById(R.id.text);

        mLocations = getResources().getStringArray(R.array.locations);

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mSelected.setText("Selected: " + mLocations[itemPosition]);
        return true;
    }
}
