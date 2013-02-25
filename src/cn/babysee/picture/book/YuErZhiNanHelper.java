package cn.babysee.picture.book;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class YuErZhiNanHelper implements IBookHelper {

    private static final String TAG = "YuErZhiNanHelper";
    private static final boolean DEBUG = AppEnv.DEBUG;

    private Book book;
    private static final String BASE_PATH = "yuerzhinan/";
    private static final String BASE__CONTENT_PATH = "yuerzhinan/content/";
    private Context context;

    public YuErZhiNanHelper(Context context) {
        this.context = context;
        initBook();
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#getBook(android.content.Context)
     */
    @Override
    public Book getBook(Context context) {
        return book;
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#getChapterList()
     */
    @Override
    public List<Chapter> getChapterList() {
        if (book == null) {
            return null;
        }
        return book.chapterList;
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#initBook()
     */
    @Override
    public void initBook() {
        InputStream inStream = FileUtils.getAssetFile(context, BASE_PATH + "g_title.xml", true);

        List<Chapter> list = new ArrayList<Chapter>();

        Chapter chapter = null;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
                    book = new Book();
                    break;
                case XmlPullParser.START_TAG://开始元素事件
                    String name = parser.getName();
                    if (name.equals("book")) {
                        chapter = new Chapter();
                    } else if (name.equals("title")) {
                        chapter.name = parser.nextText();
                    } else if (name.equals("path")) {
                        chapter.chapterSubList = getChapterSubList(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG://结束元素事件
                    if (parser.getName().equals("book")) {
                        list.add(chapter);
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
        book.chapterList = list;
        if (DEBUG)
            Log.d(TAG, "" + book);
    }

    private List<ChapterSub> getChapterSubList(String path) {
        InputStream inStream = FileUtils.getAssetFile(context, BASE_PATH + path, true);

        List<ChapterSub> subList = new ArrayList<ChapterSub>();

        ChapterSub chapterSub = null;

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_TAG://开始元素事件
                    String name = parser.getName();
                    if (name.equals("meng")) {
                        chapterSub = new ChapterSub();
                    } else if (name.equals("title")) {
                        chapterSub.name = parser.nextText();
                    } else if (name.equals("path")) {
                        chapterSub.contentPath = BASE__CONTENT_PATH + parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG://结束元素事件
                    if (parser.getName().equals("meng")) {
                        subList.add(chapterSub);
                    }
                    break;
                default:
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
        return subList;
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#getChapterSubContent(java.lang.String)
     */
    @Override
    public String getChapterSubContent(String filePath) {
        return FileUtils.getAssetFile(context, BASE_PATH + filePath);
    }
}
