package cn.babysee.picture.book;

import java.util.List;

public class Chapter {
    
    public String name;
    public List<ChapterSub> chapterSubList;
    
    @Override
    public String toString() {
        return "Chapter [name=" + name + ", chapterSubList=" + chapterSubList + "]";
    }
}
