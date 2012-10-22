package cn.babysee.picture.exam;

import java.util.ArrayList;
import java.util.List;

public class TopicList {

    public String title;

    public String answer;

    public List<Topic> list = new ArrayList<Topic>();

    public void addTopic(Topic topic) {
        list.add(topic);
    }

    public List<Topic> get() {
        return list;
    }

    @Override
    public String toString() {
        return "TopicList [title=" + title + ", answer=" + answer + ", list=" + list + "]";
    }
}
