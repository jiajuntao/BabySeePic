package cn.babysee.picture.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import cn.babysee.picture.env.AppEnv;

public class GameHelper {

    private static final String TAG = "GameHelper";
    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    public GameHelper(Context context) {
        this.mContext = context;
    }

    public List<GameList> getGameList() {
        InputStream inStream = null;
        try {
            inStream = mContext.getResources().getAssets().open("game.plist");
            if (inStream == null) {
                return null;
            }
        } catch (IOException e) {
            if (DEBUG)
                e.printStackTrace();
        }

        XmlPullParser parser = Xml.newPullParser();
        List<GameList> gameLists = null;
        GameList currentGameList = null;
        Game currentGame = null;

        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();

            boolean start = true;
            int stringCount = 0;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                    gameLists = new ArrayList<GameList>();
                    break;
                case XmlPullParser.START_TAG://开始元素事件
                    String name = parser.getName();
                    if (name.equalsIgnoreCase("key")) {
                        if (start) {
                            currentGameList = new GameList();
                            start = false;
                            currentGameList.title = parser.nextText();
                        }
                    } else if (currentGameList != null) {
                        if (name.equalsIgnoreCase("string")) {

                            switch (stringCount) {
                            case 0:
                                stringCount++;
                                currentGame = new Game();
                                currentGame.name = parser.nextText();
                                break;
                            case 1:
                                stringCount++;
                                currentGame.desc = parser.nextText();
                                break;
                            case 2:
                                currentGame.summary = parser.nextText();// 如果后面是Text元素,即返回它的值

                                currentGameList.addGame(currentGame);
                                stringCount = 0;
                                break;

                            default:
                                break;
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG://结束元素事件
                    if (parser.getName().equalsIgnoreCase("array") && currentGameList != null) {
                        gameLists.add(currentGameList);
                        currentGameList = null;
                        start = true;
                    }
                    break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            }
        }
        
        if (DEBUG)
            Log.d(TAG, gameLists.toString());
        
        return gameLists;
    }
}