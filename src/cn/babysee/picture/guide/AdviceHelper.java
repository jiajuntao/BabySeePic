package cn.babysee.picture.guide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import cn.babysee.picture.env.AppEnv;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public abstract class AdviceHelper {

    static final String TAG = "AdviceHelper";
    boolean DEBUG = AppEnv.DEBUG;
    protected String filePath;
    
    protected Context mContext;
    private List<Guide> list;

    public AdviceHelper(Context context) {
        this.mContext = context;
        init();
    }
    
    abstract void init();

    public List<Guide> getList() {
            if (list != null) {
                return list;
            }
    
            InputStream inStream = null;
            try {
                inStream = mContext.getResources().getAssets().open(filePath);
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
    
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                            list = new ArrayList<Guide>();
                            break;
                        case XmlPullParser.START_TAG://开始元素事件
                            String name = parser.getName();
                            if (name.equalsIgnoreCase("title")) {
                                currentGame = new Guide();
                                currentGame.phase = parser.nextText().trim();
                            } else if (name.equalsIgnoreCase("content")) {
                                currentGame.desc0 = parser.nextText().trim();
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