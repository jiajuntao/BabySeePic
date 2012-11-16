package cn.babysee.picture.env;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;



public class AppEnv {

    public static final boolean DEBUG = true;
    public static final String TAG = "BabyStudy";
    
    public static int screenWeight;
    public static int screenHeight;
    
    public static final int[] COLORS = { Color.BLUE, Color.CYAN, Color.BLACK, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY,
        Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW };

    /** 百度统计appkey */
    public static final String APPKEY_BAIDU = "f0234c2f5c";
    
    public static void initScreen(Activity activity) {
        DisplayMetrics DM = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(DM);
        AppEnv.screenWeight = DM.widthPixels;
        AppEnv.screenHeight = DM.heightPixels;
    }
}
