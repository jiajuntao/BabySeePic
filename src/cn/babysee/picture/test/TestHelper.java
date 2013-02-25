package cn.babysee.picture.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharedPref;
import cn.babysee.utils.FileUtils;

public class TestHelper{

    private static final String TAG = "TestHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private TestPhase mTestPhase;

    private List<TestQuestion> mTestQuestions;

    private TestQuestion mTestQuestion;

    private int mStagePosition;

    private int mGroupPosition;

    private int mPosition;
    
    public void setPosition(int mChildPosition) {
        this.mPosition = mChildPosition;
    }

    private int mTotalSize;

    private String mOperateSaveKey;

    private Map<Integer, List<TestPhase>> mTestList = new HashMap<Integer, List<TestPhase>>();

    public TestHelper(Context context) {
        this.mContext = context;
    }

    public TestHelper(Context context, int stagePosition, int groupPosition, int childPosition) {
        if (DEBUG) Log.d(TAG, "IntelligenceTestHelper stagePosition:" + stagePosition
                + " groupPosition:" + groupPosition + " childPosition:" + childPosition);
        this.mContext = context;
        this.mStagePosition = stagePosition;
        this.mGroupPosition = groupPosition;
        this.mPosition = childPosition;
        mOperateSaveKey = "test_select" + ":" + mStagePosition + ":" + mGroupPosition;

        if (mTestPhase == null) {
            mTestPhase = getPhaseList(mStagePosition).get(mGroupPosition);
        }

        if (mTestQuestions == null) {
            mTestQuestions = getPhaseList(mStagePosition).get(mGroupPosition).questionList;
        }

        mTotalSize = mTestQuestions.size();

        List<String> saveSelect = getSelectOption();
        if (saveSelect != null) {
            int size = mTestQuestions.size();
            for (int i = 0; i < size; i++) {
                TestQuestion testQuestion = mTestQuestions.get(i);
                testQuestion.select = saveSelect.get(i);
            }
        }
    }

    public TestPhase getTestPhase() {
        return mTestPhase;
    }
    
    public void setSelectOption(String option) {
        mTestQuestion.select = option;
    }

    public boolean isFrist() {
        if (mPosition == 0) {
            return true;
        }
        return false;
    }

    public boolean isLast() {
        if (mPosition == (mTotalSize - 1)) {
            return true;
        }
        return false;
    }

    public TestQuestion getPreviousTestQuestion() {
        if (DEBUG) Log.d(TAG, "getPreviousTestQuestion  childPosition:" + mPosition);

        mPosition = mPosition - 1;
        if (mPosition < 0) {
            mPosition = 0;
        }
        return getTestQuestion();
    }

    public TestQuestion getNextTestQuestion() {
        if (DEBUG) Log.d(TAG, "getNextTestQuestion  childPosition:" + mPosition);
        int maxIndex = mTestQuestions.size() - 1;
        mPosition = mPosition + 1;

        if (mPosition > maxIndex) {
            mPosition = maxIndex;
        }

        return getTestQuestion();
    }

    public TestQuestion getTestQuestion() {
        if (DEBUG) Log.d(TAG, "getTestQuestion  childPosition:" + mPosition);
        mTestQuestion = mTestQuestions.get(mPosition);

        return mTestQuestion;
    }

    public String getCurrentPosition() {
        if (DEBUG) Log.d(TAG, "getCurrentPosition ");

        return (mPosition + 1) + "/" + mTotalSize;
    }

    public void save() {
        int size = mTestQuestions.size();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            TestQuestion testQuestion = mTestQuestions.get(i);
            if (TextUtils.isEmpty(testQuestion.select)) {
                sb.append(i);
            } else {
                sb.append(i).append(":").append(testQuestion.select);
            }
            sb.append(";");
        }
        if (DEBUG) Log.d(TAG, "save: " + sb.toString());
        SharedPref.setString(mContext, mOperateSaveKey, sb.toString());
    }

    public List<String> getSelectOption() {
        String saveSelect = SharedPref.getString(mContext, mOperateSaveKey, null);
        if (DEBUG) Log.d(TAG, "getSelectOption: " + saveSelect);
        if (TextUtils.isEmpty(saveSelect)) {
            return null;
        }

        String[] s = saveSelect.split(";");
        List<String> list = new ArrayList<String>();
        String[] temp = null;
        for (String string : s) {
            if (string.contains(":")) {
                temp = string.split(":");
                list.add(temp[1]);
            } else {
                list.add(null);
            }
        }
        if (DEBUG) Log.d(TAG, "getSelectOption: " + list.toString());
        return list;
    }

    public List<TestPhase> getPhaseList(int phaseType) {
        if (DEBUG)
            Log.d(TAG, "getPhaseList phaseType:" + phaseType);

        List<TestPhase> list = mTestList.get(Integer.valueOf(phaseType));
        if (list != null) {
            return list;
        }

        String filePath = "test/test_phase1";
        switch (phaseType) {
            case 0:
                filePath = "test/test_phase1";
                break;
            case 1:
                filePath = "test/test_phase2";
                break;
            case 2:
                filePath = "test/test_phase3";
                break;
            case 3:
                filePath = "test/test_phase4";
                break;
            default:
                break;
        }
        list = getPhaseList(filePath);
        mTestList.put(phaseType, list);
        return list;
    }

    /* (non-Javadoc)
     * 
     * #0-30天的测试：
        *1、 第一次注视离眼20厘米模拟母亲脸容的黑白图画(记分：不眨眼连续注视的秒数，每秒可记1分,以10分为及格。)：
        A 10秒以上:10
        B 7秒以上:7
        C 5秒以上:5
        D 3秒以上:3
        
        @结果分析1、2、3题测认知能力，应得25分；4、5题测精细能力应得15分；6、7题测语言能力应得20分；8题测社交能力应得12分；9题测自理能力应得10分；10、11、12测大肌肉运动应得28分，共计可得110分，总分在90-110分之间正常，

     */
    public List<TestPhase> getPhaseList(String filePath) {

        List<String> lines = FileUtils.getAssetFileByLine(mContext, filePath, true, null);
        List<TestPhase> testPhases = new ArrayList<TestPhase>();

        TestPhase testPhase = null;
        TestQuestion testQuestion = null;
        String[] temp = null;
        int id = 0;
        for (String string : lines) {
            if (DEBUG) Log.d(TAG, string);
            if (string.startsWith("#")) {
                testPhase = new TestPhase();
                testPhase.title = string.substring(1);
                id = 0;
            } else if (string.startsWith("*")) {
                if (testQuestion != null) {
                    testPhase.addTopic(testQuestion);
                }

                testQuestion = new TestQuestion();
                testQuestion.id = id++;
                testQuestion.desc = string.substring(1);
            } else if (string.startsWith("$")) {
                //$1、2、3:测认知能力:25;4、5:测精细能力:15;6、7:测语言能力:20;8:测社交能力:12;9:测自理能力:10;10、11、12:测大肌肉运动:28;
                String analysisStr = string.substring(1);
                String[] allAnalysis = analysisStr.split(";");
                int len = allAnalysis.length;
                TestAnalysis[] testAnalysises = new TestAnalysis[len];
                TestAnalysis testAnalysis = null;
                for (int i = 0; i < len; i++) {
                    String[] ll = allAnalysis[i].split(":");
                    testAnalysis = new TestAnalysis();
                    testAnalysis.questions = ll[0];
                    testAnalysis.desc = ll[1];
                    testAnalysis.scroe = Integer.valueOf(ll[2]);
                    testAnalysises[i] = testAnalysis;
                }
                testPhase.testAnalysis = testAnalysises;
            } else if (string.startsWith("@")) {
                testPhase.answer = string.substring(1);
                testPhase.addTopic(testQuestion);
                testPhases.add(testPhase);
                testQuestion = null;
                testPhase = null;
            } else if (string.startsWith("A")) {
                temp = string.split(":");
                testQuestion.a = temp[0];
                testQuestion.aScore = Integer.valueOf((temp[1].trim()));
            } else if (string.startsWith("B")) {
                temp = string.split(":");
                testQuestion.b = temp[0];
                testQuestion.bScore = Integer.valueOf((temp[1].trim()));
            } else if (string.startsWith("C")) {
                temp = string.split(":");
                testQuestion.c = temp[0];
                testQuestion.cScore = Integer.valueOf((temp[1].trim()));
            } else if (string.startsWith("D")) {
                temp = string.split(":");
                testQuestion.d = temp[0];
                testQuestion.dScore = Integer.valueOf((temp[1].trim()));
            } else if (string.startsWith("E")) {
                temp = string.split(":");
                testQuestion.e = temp[0];
                testQuestion.eScore = Integer.valueOf((temp[1].trim()));
            }

        }

        if (DEBUG) Log.d(TAG, testPhases.toString());

        return testPhases;
    }

    /**
     * 获取测试结果
     */
    public TestPhase getTestResult() {
        if (mTestPhase != null) {
            List<TestQuestion> list = mTestPhase.questionList;

            int totalScore = 0;
            List<TestQuestion> unTestList = null;

            for (int i = 0, len = list.size(); i < len; i++) {
                TestQuestion testQuestion = list.get(i);
                if (TextUtils.isEmpty(testQuestion.select)) {
                    if (unTestList == null) {
                        unTestList = new ArrayList<TestQuestion>();
                    }
                    unTestList.add(testQuestion);
                } else {
                    totalScore += testQuestion.getScore();
                }
            }
            mTestPhase.score = totalScore;
            mTestPhase.unTestQuestionList = unTestList;
        }
        return mTestPhase;
    }
}
