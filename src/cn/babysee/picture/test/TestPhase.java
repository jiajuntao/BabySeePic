package cn.babysee.picture.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 测试阶段（如：11-12月）
 */
public class TestPhase implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2193050466059920100L;

    public String title;

    public String answer;
    
    public int score;
    
    public List<TestQuestion> unTestQuestionList;

    public List<TestQuestion> questionList = new ArrayList<TestQuestion>();

    public TestAnalysis[] testAnalysis;

    public void addTopic(TestQuestion topic) {
        questionList.add(topic);
    }

    public List<TestQuestion> get() {
        return questionList;
    }
    
    public void initTestAnalysis() {
        if (testAnalysis == null) {
            return;
        }
        
        for (TestAnalysis t : testAnalysis) {
            List<Integer> i = t.getList();
            int totalScore = 0;
            for (Integer integer : i) {
                totalScore += questionList.get(integer).getScore();
            }
            t.realScore = totalScore;
        }
        
    }

    @Override
    public String toString() {
        return "TestPhase [title=" + title + ", answer=" + answer + ", score=" + score
                + ", unTestQuestionList=" + unTestQuestionList + ", questionList=" + questionList
                + ", testAnalysisList=" + Arrays.toString(testAnalysis) + "]";
    }

    
}
