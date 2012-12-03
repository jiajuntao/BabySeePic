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
package cn.babysee.picture.guide;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;

/**
 * 成长向导
 */
public class GuideListActivity extends BaseActivity {

    private static final String TAG = "TestListActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private GuideHelper mTestHelper;

    private ExpandableListView mExpandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        mContext = getApplicationContext();

        mExpandableListView = (ExpandableListView) findViewById(R.id.game_list);

        mTestHelper = new GuideHelper(mContext);
        mExpandableListView.setAdapter(new MyExpandableListAdapter(mContext, mTestHelper.getList()));
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<Guide> gameLists;

        public MyExpandableListAdapter(Context context, List<Guide> gameLists) {
            mInflater = LayoutInflater.from(context);
            this.gameLists = gameLists;
        }

        public Guide getChild(int groupPosition, int childPosition) {

            return gameLists.get(groupPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        public TextView getGenericView() {
            return (TextView) mInflater.inflate(R.layout.list_item_title_view, null);
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {

            View view = mInflater.inflate(R.layout.list_sub_item_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            Guide nutrition = getChild(groupPosition, childPosition);
            title.setText(nutrition.desc0 + "\n\n" + nutrition.desc1 + "\n\n" + nutrition.desc2);

            return view;
        }

        public Guide getGroup(int groupPosition) {
            return gameLists.get(groupPosition);
        }

        public int getGroupCount() {
            return gameLists.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).phase);
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
