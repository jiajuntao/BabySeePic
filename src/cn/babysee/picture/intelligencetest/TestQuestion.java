package cn.babysee.picture.intelligencetest;


/**
 * 考试题 
 */
public class TestQuestion {

    public String desc;

    public String a;
    public String b;
    public String c;
    public String d;
    public String aScore;
    public String bScore;
    public String cScore;
    public String dScore;
    
    @Override
    public String toString() {
        return "TestQuestion [desc=" + desc + ", a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + ", aScore=" + aScore
                + ", bScore=" + bScore + ", cScore=" + cScore + ", dScore=" + dScore + "]";
    }
}
