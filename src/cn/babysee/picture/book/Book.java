package cn.babysee.picture.book;

import java.util.List;

public class Book {

    public String name;
    public List<Chapter> chapterList;
    
    @Override
    public String toString() {
        return "Book [name=" + name + ", chapterList=" + chapterList + "]";
    }
}
