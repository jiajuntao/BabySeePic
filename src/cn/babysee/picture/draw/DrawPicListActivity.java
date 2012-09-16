package cn.babysee.picture.draw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.babysee.picture.R;
import cn.babysee.utils.FileUtils;
import cn.babysee.utils.ImageUtils;

public class DrawPicListActivity extends ListActivity implements OnItemClickListener {

    private Context mContext;
    private List<String> picList = new ArrayList<String>();

    private List<String> getPicList() {

        File fileDir = new File(FileUtils.getImageFolderPath());
        File[] pics = fileDir.listFiles();
        if (pics == null) {
            return null;
        }

        int len = pics.length;

        for (int i = 0; i < len; i++) {
            picList.add(pics[i].getName());
        }

        return picList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        getPicList();
        if (picList.size() <= 0) {
            Toast.makeText(mContext, R.string.pic_empty, Toast.LENGTH_LONG).show();
            finish();
        } else {
            setListAdapter(new EfficientAdapter(this, picList));
        }
        getListView().setOnItemClickListener(this);
    }

    private static class EfficientAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private String filePath;

        private List<String> picList;

        public EfficientAdapter(Context context, List<String> picList) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            this.picList = picList;
            this.filePath = FileUtils.getImageFolderPath();
        }

        /**
         * The number of items in the list is determined by the number of
         * speeches in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return picList.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex
         * data structure, we would return whatever object represents one
         * row in the list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         * 
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         * 
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            String fileName = picList.get(position);
            // Bind the data efficiently with the holder.
            holder.text.setText(fileName);
            try {
                holder.icon.setImageBitmap(ImageUtils.makeMiNiBitmap(filePath + fileName));
            } catch (Exception e) {
                holder.icon.setImageResource(R.drawable.ic_launcher);
            }

            return convertView;
        }

        static class ViewHolder {

            TextView text;

            ImageView icon;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String fileName = picList.get(position);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setData(Uri.fromFile(new File(FileUtils.getImageFolderPath() + fileName)));
        startActivity(Intent.createChooser(intent, getString(R.string.pic_view)));
    }
}
