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
package cn.babysee.picture.test;

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
import cn.babysee.picture.base.BaseListNavigation;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharePref;

/**
 * 宝宝智力测试
 */
public class TestListActivity extends BaseListNavigation implements ExpandableListView.OnChildClickListener {

    private static final String TAG = "TestListActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private TestHelper mTestHelper;

    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAdapter;

    private View progressView;

    private int mStagePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        mContext = getApplicationContext();

        mExpandableListView = (ExpandableListView) findViewById(R.id.game_list);
        progressView = findViewById(R.id.pb_loading);

        mExpandableListView.setVisibility(View.VISIBLE);
        mExpandableListView.setOnChildClickListener(this);
        progressView.setVisibility(View.GONE);

        int position = SharePref.getInt(mContext, SharePref.TEST_PHASE, 0);
        getSupportActionBar().setSelectedNavigationItem(position);

        mTestHelper = new TestHelper(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharePref.setInt(mContext, SharePref.TEST_PHASE, mStagePosition);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        //        TestQuestion testQuestion = (TestQuestion) mAdapter.getChild(groupPosition, childPosition);

        Intent intent = new Intent(mContext, TestQuestionActivity.class);
        intent.putExtra("stagePosition", mStagePosition);
        intent.putExtra("groupPosition", groupPosition);
        intent.putExtra("childPosition", childPosition);

        startActivity(intent);
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        super.onNavigationItemSelected(itemPosition, itemId);
        mAdapter = new MyExpandableListAdapter(mContext, mTestHelper.getPhaseList(itemPosition));
        mExpandableListView.setAdapter(mAdapter);
        mStagePosition = itemPosition;
        return true;
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<TestPhase> gameLists;

        public MyExpandableListAdapter(Context context, List<TestPhase> gameLists) {
            mInflater = LayoutInflater.from(context);
            this.gameLists = gameLists;
        }

        public TestQuestion getChild(int groupPosition, int childPosition) {

            TestPhase gameList = gameLists.get(groupPosition);
            TestQuestion game = gameList.get().get(childPosition);

            return game;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return gameLists.get(groupPosition).get().size();
        }

        public TextView getGenericView() {
            return (TextView) mInflater.inflate(R.layout.game_list_item_title_view, null);
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {

            View view = mInflater.inflate(R.layout.test_list_item_sub_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView summary = (TextView) view.findViewById(R.id.summary);
            TestQuestion testQuestion = getChild(groupPosition, childPosition);

            title.setText(testQuestion.desc);
            //            summary.setText(testQuestion.a);

            return view;
        }

        public TestPhase getGroup(int groupPosition) {
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

            String title = getGroup(groupPosition).title;
            int index = title.indexOf(",");
            textView.setText(title.substring(index + 1));
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

    @Override
    protected int getActionBarDropDownViewResource() {
        return R.array.test_phases;
    }
}
