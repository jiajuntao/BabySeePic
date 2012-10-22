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
package cn.babysee.picture.exam;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import cn.babysee.picture.exam.IntelligenceExamHelper;

/**
 * 宝宝智力测试
 */
public class IntelligenceExamActivity extends BaseListNavigation implements
        ExpandableListView.OnChildClickListener {

    private static final String TAG = "GameHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private IntelligenceExamHelper mGameHelper;

    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAdapter;

    private View progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);
        mContext = getApplicationContext();
        mGameHelper = new IntelligenceExamHelper(mContext);

        mExpandableListView = (ExpandableListView) findViewById(R.id.game_list);
        progressView = findViewById(R.id.pb_loading);

        mExpandableListView.setVisibility(View.VISIBLE);
        mExpandableListView.setOnChildClickListener(this);
        progressView.setVisibility(View.GONE);
        
        if (DEBUG)
            Log.d(TAG, new IntelligenceExamHelper(mContext).getTopicList().toString());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        Topic game = (Topic) mAdapter.getChild(groupPosition, childPosition);
        showGameDescDialog(game.desc);
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        super.onNavigationItemSelected(itemPosition, itemId);
        mAdapter = new MyExpandableListAdapter(mContext, mGameHelper.getTopicList(itemPosition));
        mExpandableListView.setAdapter(mAdapter);
        return true;
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<TopicList> gameLists;

        public MyExpandableListAdapter(Context context, List<TopicList> gameLists) {
            mInflater = LayoutInflater.from(context);
            this.gameLists = gameLists;
        }

        public Topic getChild(int groupPosition, int childPosition) {

            TopicList gameList = gameLists.get(groupPosition);
            Topic game = gameList.get().get(childPosition);

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

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {

            View view = mInflater.inflate(R.layout.game_list_item_sub_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView summary = (TextView) view.findViewById(R.id.summary);
            Topic game = getChild(groupPosition, childPosition);

            title.setText(game.desc);
            summary.setText(game.a);

            return view;
        }

        public TopicList getGroup(int groupPosition) {
            return gameLists.get(groupPosition);
        }

        public int getGroupCount() {
            return gameLists.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
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
        return R.array.locations;
    }

    private Dialog gameDialog;

    private TextView gameDescView;

    protected void showGameDescDialog(String msg) {

        if (gameDialog == null) {
            View view = View.inflate(this, R.layout.game_desc_dialog, null);
            gameDescView = (TextView) view.findViewById(R.id.game_desc);

            gameDialog = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
                    .setTitle(R.string.game_desc).setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            gameDialog.dismiss();
                        }
                    }).create();

        }
        gameDescView.setText(msg);
        gameDialog.show();
    }
}
