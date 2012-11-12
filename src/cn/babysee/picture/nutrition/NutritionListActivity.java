package cn.babysee.picture.nutrition;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.base.BaseActivity;
import cn.babysee.picture.env.AppEnv;

public class NutritionListActivity extends BaseActivity {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "NutritionListActivity";

    private Context mContext;

    private ListView mListView;

    private NutritionHelper mNutritionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_list);

        mListView = (ListView) findViewById(R.id.list);
        mContext = getApplicationContext();
        mNutritionHelper = new NutritionHelper(mContext);
        mListView.setAdapter(new EfficientAdapter(this, mNutritionHelper.getList()));
    }

    private static class EfficientAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<Nutrition> picList;

        public EfficientAdapter(Context context, List<Nutrition> picList) {
            mInflater = LayoutInflater.from(context);
            this.picList = picList;
        }

        public int getCount() {
            return picList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.game_list_item_sub_view, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Nutrition nutrition = picList.get(position);
            holder.title.setText(nutrition.phase);
            holder.summary.setText(nutrition.desc);

            return convertView;
        }

        static class ViewHolder {

            TextView title;

            TextView summary;
        }
    }

}
