package cn.babysee.picture.intelligencetest;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试阶段（如：11-12月）
 */
public class TestPhase {

    public String title;

    public String answer;

    public List<TestQuestion> list = new ArrayList<TestQuestion>();

    public void addTopic(TestQuestion topic) {
        list.add(topic);
    }

    public List<TestQuestion> get() {
        return list;
    }

    @Override
    public String toString() {
        return "TestPhase [title=" + title + ", answer=" + answer + ", list=" + list + "]";
    }

}
