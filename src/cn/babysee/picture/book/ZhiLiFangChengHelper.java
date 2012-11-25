package cn.babysee.picture.book;

import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import cn.babysee.utils.FileUtils;

public class ZhiLiFangChengHelper implements IBookHelper {

    private Book book;
    private static final String BASE_PATH = "zhilifangcheng/";
    private Context context;

    public ZhiLiFangChengHelper(Context context) {
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
        Gson gson = new Gson();
        book = gson.fromJson(FileUtils.getAssetFile(context, BASE_PATH + "book"), Book.class);
    }

    /* (non-Javadoc)
     * @see cn.babysee.picture.book.IBookHelper#getChapterSubContent(java.lang.String)
     */
    @Override
    public String getChapterSubContent(String filePath) {
        return FileUtils.getAssetFile(context, BASE_PATH + filePath);
    }

}
