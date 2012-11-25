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
package cn.babysee.picture.nutrition;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.base.BaseFragment;
import cn.babysee.picture.env.AppEnv;

/**
 * 经典食谱
 */
public class NutritionFragment2 extends BaseFragment implements ExpandableListView.OnChildClickListener {

    private static final String TAG = "GameHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private INutrionHelper mHelper;

    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAdapter;

    private int mNum;
    
    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static NutritionFragment2 newInstance(int num) {
        NutritionFragment2 f = new NutritionFragment2();

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
            mHelper = new NutritionHelper1_36(mContext);
        } else {
            mHelper = new NutritionHelperClassicRecipes(mContext);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_list, container, false);
        mExpandableListView = (ExpandableListView) v.findViewById(R.id.game_list);
        mExpandableListView.setOnChildClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MyExpandableListAdapter(mContext, mHelper.getNutritionCategoryList());
        mExpandableListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        Nutrition nutrition = (Nutrition) mAdapter.getChild(groupPosition, childPosition);
        showDialog(nutrition);
        return false;
    }

    void showDialog(Nutrition nutrition) {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = MyDialogFragment.newInstance(nutrition);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    public static class MyDialogFragment extends DialogFragment {

        private TextView gameDescView;

        private Context mContext;

        private Nutrition nutrition;
        
        public MyDialogFragment(Nutrition nutrition) {
            super();
            this.nutrition = nutrition;
        }

        static DialogFragment newInstance(Nutrition nutrition) {
            DialogFragment df = new MyDialogFragment(nutrition);
            return df;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getActivity().getApplicationContext();
        }

        //
        //        @Override
        //        public View onCreateView(LayoutInflater inflater, ViewGroup container,
        //                Bundle savedInstanceState) {
        //            View v = inflater.inflate(R.layout.game_desc_dialog, container, false);
        //            gameDescView = (TextView) v.findViewById(R.id.game_desc);
        //            gameDescView.setText(msg);
        //            return v;
        //        }

        private AlertDialog gameDialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View view = View.inflate(getActivity(), R.layout.game_desc_dialog, null);
            gameDescView = (TextView) view.findViewById(R.id.game_desc);

            gameDialog = new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher)
                    .setTitle(nutrition.phase).setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            gameDialog.dismiss();
                        }
                    }).create();

            gameDescView.setText(nutrition.desc);
            return gameDialog;
        }
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;

        private List<NutritionCategory> mList;

        public MyExpandableListAdapter(Context context, List<NutritionCategory> list) {
            mInflater = LayoutInflater.from(context);
            this.mList = list;
        }

        public Nutrition getChild(int groupPosition, int childPosition) {

            NutritionCategory nutritionCategory = mList.get(groupPosition);
            Nutrition nutrition = nutritionCategory.get().get(childPosition);

            return nutrition;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return mList.get(groupPosition).get().size();
        }

        public TextView getGenericView() {
            return (TextView) mInflater.inflate(R.layout.list_item_title_view, null);
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {

            View view = mInflater.inflate(R.layout.list_sub_item_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            Nutrition nutrition = getChild(groupPosition, childPosition);

            if (mNum == 0) {
                title.setText(nutrition.desc);
            } else {
                title.setText(nutrition.phase);
            }
            return view;
        }

        public NutritionCategory getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        public int getGroupCount() {
            return mList.size();
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
