package cn.babysee.picture.guide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import cn.babysee.picture.env.AppEnv;

public class GuideHelper {

    private static final String TAG = "NutritionHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private List<Guide> list;

    public GuideHelper(Context context) {
        this.mContext = context;
    }

    public List<Guide> getList() {
        if (list != null) {
            return list;
        }

        InputStream inStream = null;
        try {
            inStream = mContext.getResources().getAssets().open("guide/baby_guide");
            if (inStream == null) {
                return null;
            }
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }

        XmlPullParser parser = Xml.newPullParser();
        Guide currentGame = null;

        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();
            int count = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                        list = new ArrayList<Guide>();
                        break;
                    case XmlPullParser.START_TAG://开始元素事件
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("key")) {
                            currentGame = new Guide();
                            currentGame.phase = parser.nextText();
                            count = 0;
                        } else if (name.equalsIgnoreCase("string")) {
                            switch (count) {
                                case 0:
                                    currentGame.desc0 = parser.nextText();
                                    count++;
                                    break;
                                case 1:
                                    currentGame.desc1 = parser.nextText();
                                    count++;
                                    break;
                                case 2:
                                    currentGame.desc2 = parser.nextText();
                                    break;

                                default:
                                    break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG://结束元素事件
                        if (parser.getName().equals("array")) {
                            list.add(currentGame);
                            currentGame = null;
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

        if (DEBUG) Log.d(TAG, "list: " + list);

        return list;
    }
}
