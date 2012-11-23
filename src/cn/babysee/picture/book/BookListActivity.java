/*
 * Copyright 2012 YiXuanStudio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.babysee.picture.book;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.base.BaseActivity;
import cn.babysee.picture.env.AppEnv;

/**
 * 中国儿童智力方程
 */
public class BookListActivity extends BaseActivity implements ExpandableListView.OnChildClickListener {

    private static final String TAG = "BookListActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private ZhiLiFangChengBookHelper mHelper;

    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        mContext = getApplicationContext();

        mExpandableListView = (ExpandableListView) findViewById(R.id.game_list);
        mExpandableListView.setOnChildClickListener(this);
        mHelper = new ZhiLiFangChengBookHelper(mContext);
        mAdapter = new MyExpandableListAdapter(mContext, mHelper.getChapterList());
        mExpandableListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ChapterSub child = (ChapterSub) mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent(this, BookContentActivity.class);
        intent.putExtra("filePath", child.contentPath);
        startActivity(intent);
        return true;
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<Chapter> mList;

        public MyExpandableListAdapter(Context context, List<Chapter> list) {
            mInflater = LayoutInflater.from(context);
            this.mList = list;
        }

        public ChapterSub getChild(int groupPosition, int childPosition) {

            return mList.get(groupPosition).chapterSubList.get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return mList.get(groupPosition).chapterSubList.size();
        }

        public TextView getGenericView() {
            return (TextView) mInflater.inflate(R.layout.list_item_title_view, null);
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {

            View view = mInflater.inflate(R.layout.nutrition_list_item_sub_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            ChapterSub child = getChild(groupPosition, childPosition);
            title.setText(child.name);

            return view;
        }

        public Chapter getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        public int getGroupCount() {
            return mList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).name);
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
