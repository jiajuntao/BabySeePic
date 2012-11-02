package cn.babysee.picture.test;

import java.util.List;

public interface ITest {

    public abstract void setSelectOption(String option);

    public abstract boolean isFrist();

    public abstract boolean isLast();

    public abstract TestQuestion getPreviousTestQuestion();

    public abstract TestQuestion getNextTestQuestion();

    public abstract TestQuestion getTestQuestion();
    
    public abstract String getTestResult();

    public abstract String getCurrentPosition();

    public abstract void save();

    public abstract List<String> getSelectOption();

    public abstract List<TestPhase> getPhaseList(int type);

    public abstract List<TestPhase> getPhaseList(String filePath);

}