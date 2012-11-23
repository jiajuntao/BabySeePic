package cn.babysee.picture.book;

import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import cn.babysee.utils.FileUtils;

public class ZhiLiFangChengBookHelper {

    private Book book;
    private static final String BASE_PATH = "zhilifangcheng/";
    private Context context;

    public ZhiLiFangChengBookHelper(Context context) {
        this.context = context;
        initBook();
    }

    public Book getBook(Context context) {
        return book;
    }

    public List<Chapter> getChapterList() {
        if (book == null) {
            return null;
        }
        return book.chapterList;
    }

    public void initBook() {
        Gson gson = new Gson();
        book = gson.fromJson(FileUtils.getAssetFile(context, BASE_PATH + "book"), Book.class);
    }

    public String getChapterSubContent(String filePath) {
        return FileUtils.getAssetFile(context, BASE_PATH + filePath);
    }

}
