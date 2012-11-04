package cn.babysee.picture.test;

import java.util.ArrayList;
import java.util.List;



/**
 * 测试结果分析
 * @1、2、3:测认知能力:25;4、5:测精细能力:15;6、7:测语言能力:20;8:测社交能力:12;9:测自理能力:10;10、11、12:测大肌肉运动:28;
 * &共计可得110分，总分在90-110分之间正常
 */
public class TestAnalysis {

    //测试描述
    public String desc;
    //实际得分
    public int realScore;
    //应得分数
    public int scroe;
    //测试题列表
    public String questions;
    
    public List<Integer> list;
    
    public List<Integer> getList() {
        
        if (list != null) {
            return list;
        }
        
        List<Integer> list = new ArrayList<Integer>();
        String[] l = questions.split("、");
        if (l == null) {
            list.add(Integer.valueOf(questions)-1);
        }
        for(int i=0, len = l.length; i < len; i++) {
            list.add(Integer.valueOf(l[i])-1);
        }
        return list;
    }
    
    @Override
    public String toString() {
        return "TestAnalysis [desc=" + desc + ", realScore=" + realScore + ", scroe=" + scroe
                + ", questions=" + questions + "]";
    }
}
