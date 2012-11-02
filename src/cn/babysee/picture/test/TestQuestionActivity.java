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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.base.BaseActivity;
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
    
    private View mTestQuestionView;
    
    private View mTestResultPanelView;
    private TextView mTestResultView;

    private ITest mTestHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_question);
        mContext = getApplicationContext();
        mQuestionContent = (TextView) findViewById(R.id.question_content);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

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
        });

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

        mTestQuestionView = findViewById(R.id.test_question_panel);
        mTestResultPanelView = findViewById(R.id.test_result_panel);
        mTestResultView = (TextView)findViewById(R.id.test_result);
        
        Intent intent = getIntent();
        int stagePosition = intent.getIntExtra("stagePosition", 0);
        int groupPosition = intent.getIntExtra("groupPosition", 0);
        int childPosition = intent.getIntExtra("childPosition", 0);
        if (DEBUG)
            Log.d(TAG, "stagePosition:" + stagePosition + " groupPosition:" + groupPosition + " childPosition:"
                    + childPosition);

        mTestHelper = new TestHelper(mContext, stagePosition, groupPosition, childPosition);

        setView(mTestHelper.getTestQuestion());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTestHelper.save();
    }

    private void setView(TestQuestion testQuestion) {
        mQuestionContent.setText(testQuestion.desc);
        mOptionA.setText(testQuestion.a);
        mOptionB.setText(testQuestion.b);
        if (TextUtils.isEmpty(testQuestion.c)) {
            mOptionC.setVisibility(View.GONE);
        } else {
            mOptionC.setText(testQuestion.c);
            mOptionC.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(testQuestion.d)) {
            mOptionD.setVisibility(View.GONE);
        } else {
            mOptionD.setText(testQuestion.d);
            mOptionD.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(testQuestion.e)) {
            mOptionE.setVisibility(View.GONE);
        } else {
            mOptionE.setText(testQuestion.e);
            mOptionE.setVisibility(View.VISIBLE);
        }

        mTextLabel.setText(mTestHelper.getCurrentPosition());

        if (mTestHelper.isFrist()) {
            mPrevious.setVisibility(View.INVISIBLE);
        } else {
            mPrevious.setVisibility(View.VISIBLE);
        }

        if (mTestHelper.isLast()) {
            mNext.setVisibility(View.GONE);
            mShowResult.setVisibility(View.VISIBLE);
        } else {
            mNext.setVisibility(View.VISIBLE);
            mShowResult.setVisibility(View.GONE);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_previous:
            setView(mTestHelper.getPreviousTestQuestion());
            break;
        case R.id.btn_next:
            setView(mTestHelper.getNextTestQuestion());
            break;
        case R.id.txt_label:
            break;
        case R.id.btn_favor:
            break;

        default:
            break;
        }
    }
}
