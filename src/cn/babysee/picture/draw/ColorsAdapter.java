package cn.babysee.picture.draw;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;

public class ColorsAdapter extends BaseAdapter {
    private Context context;

    private int[] colors = AppEnv.COLORS;

    private int width = 36;

    public ColorsAdapter(Context context) {
        this.context = context;
        width = (int) (context.getResources().getDimension(R.dimen.dimen_36_dip));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView i;

        if (convertView == null) {
            i = new TextView(context);
            i.setLayoutParams(new GridView.LayoutParams(width, width));
        } else {
            i = (TextView) convertView;
        }
        i.setBackgroundColor(getItem(position));
        return i;
    }

    public final int getCount() {
        return colors.length;
    }

    public final Integer getItem(int position) {
        return colors[position];
    }

    public final long getItemId(int position) {
        return position;
    }
}