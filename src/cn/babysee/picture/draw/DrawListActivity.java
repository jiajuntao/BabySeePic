package cn.babysee.picture.draw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.utils.FileUtils;
import cn.babysee.utils.ImageUtils;

import com.baidu.mobstat.StatService;

public class DrawListActivity extends BaseActivity implements OnItemClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "DrawListActivity";

    private ListView mListView;

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
        setContentView(R.layout.draw_list);

        mListView = (ListView) findViewById(R.id.list);
        getPicList();
        if (picList.size() <= 0) {
            Toast.makeText(mContext, R.string.pic_empty, Toast.LENGTH_LONG).show();
            finish();
        } else {
            mListView.setAdapter(new EfficientAdapter(this, picList));
        }
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StatService.onEvent(mContext, StatServiceEnv.BABYWORKS_VIEW_EVENT_ID,
                StatServiceEnv.BABYWORKS_VIEW_LABEL, 1);
        String fileName = picList.get(position);
        if (DEBUG) Log.d(TAG, FileUtils.getImageFolderPath() + fileName);

        Uri uri = Uri.fromFile(new File(FileUtils.getImageFolderPath() + fileName));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(Intent.createChooser(intent, getString(R.string.pic_view)));
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
                convertView = mInflater.inflate(R.layout.list_item_view, null);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
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

}
