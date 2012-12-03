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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;

/**
 * 宝宝智力测试
 */
public class TestQuestionActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "TestQuestionActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private TextView mQuestionContent;

    private RadioGroup mRadioGroup;

    private RadioButton mOptionA;

    private RadioButton mOptionB;

    private RadioButton mOptionC;

    private RadioButton mOptionD;

    private RadioButton mOptionE;

    private View mPrevious;

    private View mNext;

    private View mShowResult;

    private TextView mTextLabel;

    private View mFavor;

    private View mQuestionPanelView;

    private View mResultPanelView;

    private View mUnTestPanelView;

    private View mBottomPanelView;

    private TextView mTextScore;

    private ListView mUnTestListView;

    private TestHelper mTestHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_question);
        mContext = getApplicationContext();
        mQuestionContent = (TextView) findViewById(R.id.question_content);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);

        mOptionA = (RadioButton) findViewById(R.id.radio1);
        mOptionB = (RadioButton) findViewById(R.id.radio2);
        mOptionC = (RadioButton) findViewById(R.id.radio3);
        mOptionD = (RadioButton) findViewById(R.id.radio4);
        mOptionE = (RadioButton) findViewById(R.id.radio5);

        mPrevious = findViewById(R.id.btn_previous);
        mPrevious.setOnClickListener(this);
        mNext = findViewById(R.id.btn_next);
        mNext.setOnClickListener(this);
        mShowResult = findViewById(R.id.btn_show_result);
        mShowResult.setOnClickListener(this);
        mTextLabel = (TextView) findViewById(R.id.txt_label);
        mTextLabel.setOnClickListener(this);
        mFavor = findViewById(R.id.btn_favor);
        mFavor.setOnClickListener(this);

        mUnTestListView = (ListView) findViewById(R.id.untest_list);
        mQuestionPanelView = findViewById(R.id.test_question_panel);
        mResultPanelView = findViewById(R.id.test_result_panel);
        mUnTestPanelView = findViewById(R.id.test_untest_panel);
        mBottomPanelView = findViewById(R.id.bottom_panel);
        mTextScore = (TextView) findViewById(R.id.test_score);

        Intent intent = getIntent();
        int stagePosition = intent.getIntExtra("stagePosition", 0);
        int groupPosition = intent.getIntExtra("groupPosition", 0);
        int childPosition = intent.getIntExtra("childPosition", 0);
        if (DEBUG) Log.d(TAG, "stagePosition:" + stagePosition + " groupPosition:" + groupPosition
                + " childPosition:" + childPosition);

        mTestHelper = new TestHelper(mContext, stagePosition, groupPosition, childPosition);

        setTitle(mTestHelper.getTestPhase().title);

        setTestQuestionView(mTestHelper.getTestQuestion());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTestHelper.save();
    }

    private RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int radioButtonId = group.getCheckedRadioButtonId();

            switch (radioButtonId) {
                case R.id.radio1:
                    mTestHelper.setSelectOption("a");
                    break;
                case R.id.radio2:
                    mTestHelper.setSelectOption("b");
                    break;
                case R.id.radio3:
                    mTestHelper.setSelectOption("c");
                    break;
                case R.id.radio4:
                    mTestHelper.setSelectOption("d");
                    break;
                case R.id.radio5:
                    mTestHelper.setSelectOption("e");
                    break;

                default:
                    break;
            }
        }
    };

    private void setTestQuestionView(TestQuestion testQuestion) {
        mBottomPanelView.setVisibility(View.VISIBLE);
        mQuestionContent.setText(testQuestion.desc);
        mOptionA.setText(getString(R.string.test_option, testQuestion.a, testQuestion.aScore));
        mOptionB.setText(getString(R.string.test_option, testQuestion.b, testQuestion.bScore));
        if (TextUtils.isEmpty(testQuestion.c)) {
            mOptionC.setVisibility(View.GONE);
        } else {
            mOptionC.setText(getString(R.string.test_option, testQuestion.c, testQuestion.cScore));
            mOptionC.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(testQuestion.d)) {
            mOptionD.setVisibility(View.GONE);
        } else {
            mOptionD.setText(getString(R.string.test_option, testQuestion.d, testQuestion.dScore));
            mOptionD.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(testQuestion.e)) {
            mOptionE.setVisibility(View.GONE);
        } else {
            mOptionE.setText(getString(R.string.test_option, testQuestion.e, testQuestion.eScore));
            mOptionE.setVisibility(View.VISIBLE);
        }

        if ("a".equals(testQuestion.select)) {
            mRadioGroup.check(R.id.radio1);
        } else if ("b".equals(testQuestion.select)) {
            mRadioGroup.check(R.id.radio2);
        } else if ("c".equals(testQuestion.select)) {
            mRadioGroup.check(R.id.radio3);
        } else if ("d".equals(testQuestion.select)) {
            mRadioGroup.check(R.id.radio4);
        } else if ("e".equals(testQuestion.select)) {
            mRadioGroup.check(R.id.radio5);
        } else {
            mRadioGroup.clearCheck();
        }

        mTextLabel.setText(mTestHelper.getCurrentPosition());

        if (mTestHelper.isFrist()) {
            mPrevious.setVisibility(View.INVISIBLE);
        } else {
            mPrevious.setVisibility(View.VISIBLE);
        }

        mQuestionPanelView.setVisibility(View.VISIBLE);
        mResultPanelView.setVisibility(View.GONE);
        mUnTestPanelView.setVisibility(View.GONE);

        if (mTestHelper.isLast()) {
            mNext.setVisibility(View.INVISIBLE);
        } else {
            mNext.setVisibility(View.VISIBLE);
        }
        mShowResult.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                setTestQuestionView(mTestHelper.getPreviousTestQuestion());
                break;
            case R.id.btn_next:
                setTestQuestionView(mTestHelper.getNextTestQuestion());
                break;
            case R.id.btn_show_result:
                //                @结果分析1、2、4题测认知能力，应得30分；3、5题测精细能力应得20分；6、7题测语言能力应得20分；8题测社交能力应得12分；9题测自理能力应得8分；10、11、12测大肌肉运动应得20分，共计可得110分，总分在90-110分之间正常，120分以上为优秀，70分以下为暂时落后。哪一道题在及格以下可先复习0-30天相应的题目，练好后再学习本年龄组的试题。若哪一道题在A以上，可跨过本月的练习，事先练习下一个月相应题。

                TestPhase testPhase = mTestHelper.getTestResult();

                List<TestQuestion> unTestQuestionList = testPhase.unTestQuestionList;
                if (unTestQuestionList != null) {
                    //试题未做完
                    final EfficientAdapter mAdapter = new EfficientAdapter(mContext,
                            unTestQuestionList);
                    mUnTestListView.setAdapter(mAdapter);

                    mUnTestListView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            TestQuestion testQuestion = mAdapter.getItem(arg2);
                            mTestHelper.setPosition(testQuestion.id);
                            setTestQuestionView(mTestHelper.getTestQuestion());
                        }
                    });
                    mQuestionPanelView.setVisibility(View.GONE);
                    mResultPanelView.setVisibility(View.GONE);
                    mBottomPanelView.setVisibility(View.GONE);
                    mUnTestPanelView.setVisibility(View.VISIBLE);

                } else {
                    //查看测试结果
                    mQuestionPanelView.setVisibility(View.GONE);
                    mResultPanelView.setVisibility(View.VISIBLE);
                    mBottomPanelView.setVisibility(View.GONE);
                    mUnTestPanelView.setVisibility(View.GONE);
                    testPhase.initTestAnalysis();
                    TestAnalysis[] testAnalysiss = testPhase.testAnalysis;
                    if (testAnalysiss != null) {

                        ListView list = (ListView) findViewById(R.id.test_analysis);
                        final AnalysisAdapter mAdapter = new AnalysisAdapter(mContext,
                                testAnalysiss);
                        list.setAdapter(mAdapter);

                        list.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                                TestAnalysis testAnalysis = mAdapter.getItem(arg2);
                                mTestHelper.setPosition(testAnalysis.getList().get(0));
                                setTestQuestionView(mTestHelper.getTestQuestion());
                            }
                        });
                    }
                    mTextScore.setText(getString(R.string.test_score, testPhase.score,
                            testPhase.answer));
                }
                mBottomPanelView.setVisibility(View.GONE);
                break;
            case R.id.btn_favor:
                break;

            default:
                break;
        }
    }

    private static class EfficientAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<TestQuestion> unTestQuestionList;

        public EfficientAdapter(Context context, List<TestQuestion> unTestQuestionList) {
            mInflater = LayoutInflater.from(context);
            this.unTestQuestionList = unTestQuestionList;
        }

        public int getCount() {
            return unTestQuestionList.size();
        }

        public TestQuestion getItem(int position) {
            return unTestQuestionList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.test_list_item_view, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(getItem(position).desc);
            return convertView;
        }

        static class ViewHolder {

            TextView text;
        }
    }

    private static class AnalysisAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private TestAnalysis[] testAnalysiss;

        private Context mContext;

        public AnalysisAdapter(Context context, TestAnalysis[] testAnalysiss) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            this.testAnalysiss = testAnalysiss;
        }

        public int getCount() {
            return testAnalysiss.length;
        }

        public TestAnalysis getItem(int position) {
            return testAnalysiss[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.test_list_item_view, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TestAnalysis testAnalysis = getItem(position);
            holder.text.setText(mContext.getString(R.string.test_analysis, testAnalysis.questions,
                    testAnalysis.desc, testAnalysis.scroe, testAnalysis.realScore));
            return convertView;
        }

        static class ViewHolder {

            TextView text;
        }
    }
}
