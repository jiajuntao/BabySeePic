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

public class GrowAdviceHelper {

    private static final String TAG = "GrowAdviceHelper";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private List<Guide> list;

    public GrowAdviceHelper(Context context) {
        this.mContext = context;
    }

    public List<Guide> getList() {
        if (list != null) {
            return list;
        }

        InputStream inStream = null;
        try {
            inStream = mContext.getResources().getAssets().open("guide/grow_advice");
            if (inStream == null) {
                return null;
            }
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }

        XmlPullParser parser = Xml.newPullParser();
        Guide currentGame = null;
//        <article>
//        <title>   针对孩子注意力不集中的具体训练方法</title>
//        <content>孩子注意力不集中，易分心，
        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                        list = new ArrayList<Guide>();
                        break;
                    case XmlPullParser.START_TAG://开始元素事件
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("title")) {
                            currentGame = new Guide();
                            currentGame.phase = parser.nextText();
                        } else if (name.equalsIgnoreCase("content")) {
                            currentGame.desc0 = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG://结束元素事件
                        if (parser.getName().equals("article")) {
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
