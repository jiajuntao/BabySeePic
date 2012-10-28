package cn.babysee.picture.test;

import java.io.Serializable;


/**
 * 考试题 
 */
public class TestQuestion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4905797962559898948L;

    public String desc;
    public String select;

    public String a;
    public String b;
    public String c;
    public String d;
    public String e;
    public int aScore;
    public int bScore;
    public int cScore;
    public int dScore;
    public int eScore;
    
    public int getScore(String option) {
        if ("a".equals(option)) {
            return aScore;
        }
        if ("b".equals(option)) {
            return bScore;
        }
        if ("c".equals(option)) {
            return cScore;
        }
        if ("d".equals(option)) {
            return dScore;
        }
        if ("e".equals(option)) {
            return eScore;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "TestQuestion [desc=" + desc + ", a=" + a + ", b=" + b + ", c=" + c + ", d=" + d
                + ", e=" + e + ", aScore=" + aScore + ", bScore=" + bScore + ", cScore=" + cScore
                + ", dScore=" + dScore + ", eScore=" + eScore + "]";
    }
}
