package cn.babysee.picture.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharePref;

public class TestHelper {

    private static final String TAG = "TestHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private static final int TYPE_0_1 = 0;

    private static final int TYPE_1_2 = 1;

    private static final int TYPE_2_6 = 2;

    private Context mContext;
    private List<TestPhase> mTipicLists;
    private TestPhase mTestPhase;
    private List<TestQuestion> mTestQuestions;
    private TestQuestion mTestQuestion;
    private int mStagePosition;
    private int mGroupPosition;
    private int mChildPosition;
    private int mTotalSize;
    private String mOperateSaveKey;

    public TestHelper(Context context) {
        this.mContext = context;
    }
    
    public TestHelper(Context context, int stagePosition, int groupPosition, int childPosition) {
        if (DEBUG)
            Log.d(TAG, "IntelligenceTestHelper stagePosition:" + stagePosition + " groupPosition:" + groupPosition + " childPosition:" + childPosition);
        this.mContext = context;
        this.mStagePosition = stagePosition;
        this.mGroupPosition = groupPosition;
        this.mChildPosition = childPosition;
        mOperateSaveKey = "test_select"+":" +mStagePosition + ":" + mGroupPosition;
        
        if (mTipicLists == null) {
            mTipicLists = getTopicList(mStagePosition);
        }
        
        if (mTestPhase == null) {
            mTestPhase = mTipicLists.get(mGroupPosition);
        }
        
        if (mTestQuestions == null) {
            mTestQuestions = getTopicList(mStagePosition).get(mGroupPosition).list;
        }
        
        mTotalSize = mTestQuestions.size();
        
        List<String> saveSelect = getSelectOption();
        if (saveSelect != null) {
            int size = mTestQuestions.size();
            for(int i=0; i<size; i++) {
                TestQuestion testQuestion  = mTestQuestions.get(i);
                testQuestion.select = saveSelect.get(i);
            }
        }
    }
    
    public void setSelectOption(String option) {
        mTestQuestion.select = option;
    }
    
    public boolean isFrist() {
        if (mChildPosition == 0) {
            return true;
        }
        return false;
    }
    
    public boolean isLast() {
        if (mChildPosition == (mTotalSize - 1)) {
            return true;
        }
        return false;
    }
    
    public TestQuestion getPreviousTestQuestion() {
        if (DEBUG)
            Log.d(TAG, "getPreviousTestQuestion  childPosition:" + mChildPosition);
        
        mChildPosition = mChildPosition - 1;
        if (mChildPosition < 0) {
            mChildPosition = 0;
        } 
        return getTestQuestion(); 
    }
    
    public TestQuestion getNextTestQuestion() {
        if (DEBUG)
            Log.d(TAG, "getNextTestQuestion  childPosition:" + mChildPosition);
        int maxIndex = mTestQuestions.size() - 1;
        mChildPosition = mChildPosition + 1;
        
        if (mChildPosition > maxIndex) {
            mChildPosition = maxIndex;
        } 
        
        return getTestQuestion(); 
    }
    
    public TestQuestion getTestQuestion() {
        if (DEBUG)
            Log.d(TAG, "getTestQuestion  childPosition:" + mChildPosition);
        mTestQuestion = mTestQuestions.get(mChildPosition);
        
        return mTestQuestion; 
    }
    
    public String getCurrentPosition() {
        if (DEBUG)
            Log.d(TAG, "getCurrentPosition ");
        
        return (mChildPosition + 1) + "/" + mTotalSize; 
    }
    
    public void save() {
        int size = mTestQuestions.size();
        StringBuffer sb = new StringBuffer();
        for (int i=0; i< size; i++) {
            TestQuestion testQuestion = mTestQuestions.get(i);
            if (TextUtils.isEmpty(testQuestion.select)) {
                sb.append(i);
            } else {
                sb.append(i).append(":").append(testQuestion.select);
            }
            sb.append(";");
        }
        if (DEBUG)
            Log.d(TAG, "save: " + sb.toString());
        SharePref.setString(mContext, mOperateSaveKey, sb.toString());
    }
    
    public List<String> getSelectOption() {
        String saveSelect = SharePref.getString(mContext, mOperateSaveKey, null);
        if (DEBUG)
            Log.d(TAG, "getSelectOption: " + saveSelect);
        if (TextUtils.isEmpty(saveSelect)) {
            return null;
        }
        
        String[] s = saveSelect.split(";");
        List<String> list = new ArrayList<String>();
        String[] temp = null;
        for (String string : s) {
            if(string.contains(":")) {
                temp = string.split(":");
                list.add(temp[1]);
            } else {
                list.add(null);
            }
        }
        if (DEBUG)
            Log.d(TAG, "getSelectOption: " + list.toString());
        return list;
    }
    
    public List<TestPhase> getTopicList(int type) {

        if (mTipicLists == null) {
            mTipicLists = getTopicList();
        }

        int size = mTipicLists.size();

        int startIndex = 0;
        int endIndex = size - 1;

        List<TestPhase> sublist = new ArrayList<TestPhase>(13);
        switch (type) {
            case TYPE_0_1:
                startIndex = 0;
                endIndex = 11;
                break;
            case TYPE_1_2:
                startIndex = 12;
                endIndex = 23;

                break;
            case TYPE_2_6:
                startIndex = 24;
                endIndex = size;

                break;

            default:
                break;
        }
        int i = 0;
        for (TestPhase gameList : mTipicLists) {
            if (i >= startIndex && i <= endIndex) {
                sublist.add(gameList);
            }
            i++;
        }

        return sublist;
    }

    public List<TestPhase> getTopicList() {
        
        InputStream inStream = null;
        try {
            inStream = mContext.getResources().getAssets().open("test.plist");
            if (inStream == null) {
                return null;
            }
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }
        
        XmlPullParser parser = Xml.newPullParser();
        List<TestPhase> gameLists = null;
        TestPhase currentGameList = null;
        TestQuestion currentGame = null;

        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                        gameLists = new ArrayList<TestPhase>();
                        break;
                    case XmlPullParser.START_TAG://开始元素事件
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("stage")) {
                            currentGameList = new TestPhase();
                        }  else if (name.equals("key")) {
                            currentGameList.title = parser.nextText();
                        } else if (name.equals("answer")) {
                            currentGameList.answer = parser.nextText();
                        }  else if (name.equals("topic")) {
                            currentGame = new TestQuestion();
                        } else if (name.equals("desc")) {
                            currentGame.desc = parser.nextText();
                        } else if (name.equals("a")) {
                            currentGame.a = parser.nextText();
                        } else if (name.equals("a_score")) {
                            currentGame.aScore = Integer.valueOf(parser.nextText());
                        } else if (name.equals("b")) {
                            currentGame.b = parser.nextText();
                        } else if (name.equals("b_score")) {
                            currentGame.bScore = Integer.valueOf(parser.nextText());
                        } else if (name.equals("c")) {
                            currentGame.c = parser.nextText();
                        } else if (name.equals("c_score")) {
                            currentGame.cScore = Integer.valueOf(parser.nextText());
                        } else if (name.equals("d")) {
                            currentGame.d = parser.nextText();
                        } else if (name.equals("d_score")) {
                            currentGame.dScore = Integer.valueOf(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG://结束元素事件
                        if (parser.getName().equals("stage") && currentGameList != null) {
                            gameLists.add(currentGameList);
                        } else if (parser.getName().equals("topic")) {
                            currentGameList.addTopic(currentGame);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    if (DEBUG) e.printStackTrace();
                }
            }
        }

        if (DEBUG) Log.d(TAG, gameLists.toString());

        return gameLists;
    }
}
