package cn.babysee.picture.nutrition;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class NutritionHelper1_36 implements INutrionHelper {

    private static final String TAG = "NutritionHelper1_36";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private List<Nutrition> list;
    
    private List<NutritionCategory> listCate;

    public NutritionHelper1_36(Context context) {
        this.mContext = context;
    }

    /**
     * #25-36个月 星期一 早餐：大米粥，鸡蛋面饼
     * 
     * @return
     */
    public List<Nutrition> getList() {
        if (list != null) {
            return list;
        }

        List<String> lines = FileUtils.getAssetFileByLine(mContext, "nutrition/nutrition_1_36");
        List<Nutrition> list = new ArrayList<Nutrition>();
        Nutrition nutrition = null;
        StringBuffer sb = null;
        for (String line : lines) {
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            if (line.startsWith("#")) {
                if (nutrition != null) {
                    nutrition.desc = sb.toString();
                    list.add(nutrition);
                }
                nutrition = new Nutrition();
                sb = new StringBuffer();
                nutrition.phase = line.substring(1);

            } else {
                sb.append(line + "\n");
            }
        }
        //加上最后一个
        if (nutrition != null) {
            nutrition.desc = sb.toString();
            list.add(nutrition);
        }
        
        this.list = list;
        if (DEBUG) Log.d(TAG, "list: " + list);

        return list;
    }

    @Override
    public List<NutritionCategory> getNutritionCategoryList() {
        if (listCate != null) {
            return listCate;
        }
        
        List<Nutrition> list = getList();
        listCate = new ArrayList<NutritionCategory>(list.size());
        NutritionCategory nutritionCategory;
        for (Nutrition nutrition : list) {
            nutritionCategory = new NutritionCategory();
            nutritionCategory.title = nutrition.phase;
            nutritionCategory.add(nutrition);
            listCate.add(nutritionCategory);
        }
        if (DEBUG)
            Log.d(TAG, "listCate: " + listCate);
        
        return listCate;
    }
}
