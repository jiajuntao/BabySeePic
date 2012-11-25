package cn.babysee.picture.book;

import java.util.List;

import android.content.Context;

public interface IBookHelper {

    public abstract Book getBook(Context context);

    public abstract List<Chapter> getChapterList();

    public abstract void initBook();

    public abstract String getChapterSubContent(String filePath);

}