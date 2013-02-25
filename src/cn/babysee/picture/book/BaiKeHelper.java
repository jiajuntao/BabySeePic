package cn.babysee.picture.book;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.babysee.utils.FileUtils;

public class BaiKeHelper implements IBookHelper {

    private Book book;
    private static final String BASE_PATH = "baike/";
    private Context context;

    public BaiKeHelper(Context context) {
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
        List<String> lines = FileUtils.getAssetFileByLine(context, BASE_PATH + "chart.txt", true, null);
        book = new Book();
        List<Chapter> list = new ArrayList<Chapter>();
        List<ChapterSub> subList = null;

        Chapter chapter = null;
        ChapterSub chapterSub = null;
        int i = 0;
        for (String line : lines) {
            i++;
            if (line.startsWith("*")) {
                if (chapter != null) {
                    chapter.chapterSubList = subList;
                    list.add(chapter);
                }
                chapter = new Chapter();
                chapter.name = line.substring(1);
                subList = new ArrayList<ChapterSub>();
                continue;
            }
            chapterSub = new ChapterSub();
            chapterSub.name = line;
            chapterSub.contentPath = BASE_PATH + i+".html";
            subList.add(chapterSub);
        }
        book.chapterList = list;
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#getChapterSubContent(java.lang.String)
     */
    @Override
    public String getChapterSubContent(String filePath) {
        return FileUtils.getAssetFile(context, BASE_PATH + filePath);
    }

}
