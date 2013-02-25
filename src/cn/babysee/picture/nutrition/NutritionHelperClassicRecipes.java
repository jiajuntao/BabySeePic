package cn.babysee.picture.nutrition;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class NutritionHelperClassicRecipes implements INutrionHelper {

    private static final String TAG = "NutritionHelperClassicRecipes";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private List<NutritionCategory> list;

    public NutritionHelperClassicRecipes(Context context) {
        this.mContext = context;
    }

    /**
     * #主食篇 
     * *001 汤面 
     * 原料：熟面条--1/2 小碗，黄瓜--1/8 个，酱油--若干 制法
     */
    public List<NutritionCategory> getNutritionCategoryList() {
        if (list != null) {
            return list;
        }

        List<String> lines = FileUtils.getAssetFileByLine(mContext, "nutrition/nutrition_classic_recipes", true, null);
        List<NutritionCategory> cateList = new ArrayList<NutritionCategory>();
        NutritionCategory nutritionCategory = null;
        Nutrition nutrition = null;
        StringBuffer sb = null;
        for (String line : lines) {
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            if (line.startsWith("#")) {
                if (nutritionCategory != null) {
                    if (nutrition != null) {
                        nutrition.desc = sb.toString();
                        nutritionCategory.add(nutrition);
                    }
                    cateList.add(nutritionCategory);
                }
                nutritionCategory = new NutritionCategory();
                nutritionCategory.title = line.substring(1);
                nutrition = null;
            }else if (line.startsWith("*")) {
                if (nutrition != null) {
                    nutrition.desc = sb.toString();
                    nutritionCategory.add(nutrition);
                }
                nutrition = new Nutrition();
                sb = new StringBuffer();
                nutrition.phase = line.substring(1);

            } else {
                sb.append(line + "\n");
            }
        }
        //加上最后一个
        if (nutritionCategory != null) {
            if (nutrition != null) {
                nutrition.desc = sb.toString();
                nutritionCategory.add(nutrition);
            }
            cateList.add(nutritionCategory);
        }

        if (DEBUG) Log.d(TAG, "cateList: " + cateList);

        return cateList;
    }

    @Override
    public List<Nutrition> getList() {
        return null;
    }
}
