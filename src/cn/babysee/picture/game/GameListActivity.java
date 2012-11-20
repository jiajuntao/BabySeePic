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
package cn.babysee.picture.game;

import java.util.List;

import com.baidu.mobstat.StatService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import cn.babysee.picture.env.StatServiceEnv;

/**
 * 亲子游戏
 */
public class GameListActivity extends BaseListNavigation implements
        ExpandableListView.OnChildClickListener {

    private static final String TAG = "GameHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private GameHelper mGameHelper;

    private int mStagePosition;
    
    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAdapter;

    private Dialog gameDialog;

    private TextView gameDescView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list);

        mExpandableListView = (ExpandableListView) findViewById(R.id.game_list);
        mGameHelper = new GameHelper(mContext);
        mExpandableListView.setOnChildClickListener(this);

        int position = SharePref.getInt(mContext, SharePref.GAME_STAGE, 0);
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        Game game = (Game) mAdapter.getChild(groupPosition, childPosition);
        showGameDescDialog(game.desc);
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        super.onNavigationItemSelected(itemPosition, itemId);
        
        //打点
        switch (itemPosition) {
            case 0:
                StatService.onEvent(mContext, StatServiceEnv.GAME_PHASE1_EVENT_ID,
                        StatServiceEnv.GAME_PHASE1_LABEL, 1);
                break;
            case 1:
                StatService.onEvent(mContext, StatServiceEnv.GAME_PHASE2_EVENT_ID,
                        StatServiceEnv.GAME_PHASE2_LABEL, 1);
                break;
            case 2:
                StatService.onEvent(mContext, StatServiceEnv.GAME_PHASE3_EVENT_ID,
                        StatServiceEnv.GAME_PHASE3_LABEL, 1);
                break;

            default:
                break;
        }

        mAdapter = new MyExpandableListAdapter(mContext, mGameHelper.getGameList(itemPosition));
        mExpandableListView.setAdapter(mAdapter);
        mStagePosition = itemPosition;
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharePref.setInt(mContext, SharePref.GAME_STAGE, mStagePosition);
    }

    @Override
    protected int getActionBarDropDownViewResource() {
        return R.array.game_phases;
    }

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
    

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<GameList> gameLists;

        public MyExpandableListAdapter(Context context, List<GameList> gameLists) {
            mInflater = LayoutInflater.from(context);
            this.gameLists = gameLists;
        }

        public Game getChild(int groupPosition, int childPosition) {

            GameList gameList = gameLists.get(groupPosition);
            Game game = gameList.get().get(childPosition);

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
            Game game = getChild(groupPosition, childPosition);

            title.setText(game.name);
            summary.setText(game.summary);

            return view;
        }

        public GameList getGroup(int groupPosition) {
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
}
