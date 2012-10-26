package cn.babysee.picture.intelligencetest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.Xml;
import cn.babysee.picture.env.AppEnv;

public class IntelligenceTestHelper {

    private static final String TAG = "IntelligenceTestHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private static final int TYPE_0_1 = 0;

    private static final int TYPE_1_2 = 1;

    private static final int TYPE_2_6 = 2;

    private Context mContext;

    private List<TestPhase> tipicLists;

    public IntelligenceTestHelper(Context context) {
        this.mContext = context;
    }

    public List<TestPhase> getTopicList(int type) {

        if (tipicLists == null) {
            tipicLists = getTopicList();
        }

        int size = tipicLists.size();

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
        for (TestPhase gameList : tipicLists) {
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
            inStream = mContext.getResources().getAssets().open("intelligence_test");
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
                            currentGame.aScore = parser.nextText();
                        } else if (name.equals("b")) {
                            currentGame.b = parser.nextText();
                        } else if (name.equals("b_score")) {
                            currentGame.bScore = parser.nextText();
                        } else if (name.equals("c")) {
                            currentGame.c = parser.nextText();
                        } else if (name.equals("c_score")) {
                            currentGame.cScore = parser.nextText();
                        } else if (name.equals("d")) {
                            currentGame.d = parser.nextText();
                        } else if (name.equals("d_score")) {
                            currentGame.dScore = parser.nextText();
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
