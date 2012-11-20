package cn.babysee.picture.nutrition;

import java.util.ArrayList;
import java.util.List;

public class NutritionCategory {

    public String title;
    public List<Nutrition> list = new ArrayList<Nutrition>();
    
    public void add(Nutrition game) {
        list.add(game);
    }
    
    public List<Nutrition> get() {
        return list;
    }

    @Override
    public String toString() {
        return "NutritionCategory [title=" + title + ", list=" + list + "]";
    }
}
