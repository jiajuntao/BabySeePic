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
import cn.babysee.picture.base.BaseFragment;
import cn.babysee.picture.env.AppEnv;

public class NutritionFragment extends BaseFragment {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "NutritionListActivity";

    private ListView mListView;

    private INutrionHelper mNutritionHelper;

    int mNum;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static NutritionFragment newInstance(int num) {
        NutritionFragment f = new NutritionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        if (mNum == 0) {
            mNutritionHelper = new NutritionHelper1_36(mContext);
        } else {
            mNutritionHelper = new NutritionHelperGuidance(mContext);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.draw_list, container, false);
        mListView = (ListView) v.findViewById(R.id.list);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setAdapter(new EfficientAdapter(mContext, mNutritionHelper.getList()));
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
                convertView = mInflater.inflate(R.layout.list_item_sub_view, null);

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
