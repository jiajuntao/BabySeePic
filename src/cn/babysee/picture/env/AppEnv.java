package cn.babysee.picture.env;

import android.app.Activity;
import android.util.DisplayMetrics;

public class AppEnv {

    public static final boolean DEBUG = true;

    public static final String TAG = "BabyStudy";

    public static int screenWeight;

    public static int screenHeight;

    public static final int[] COLORS = { 0XFFC0C0C0, 0XFF292421, 0XFF191970, 0XFF7FFFD4,
            0XFFFFD700, 0XFFFF8000, 0XFF7CFC00, 0XFF6B8E23, 0XFF32CD32, 0XFFDA70D6, 0XFF33A1C9,
            0XFFFF7F50, 0XFFFF6347, 0XFF00C78C, 0XFF8A2BE2, 0XFFE3170D, 0XFF082E54, 0XFF40E0D0,
            0XFF6A5ACD, 0XFFB0E0E6, 0XFFFFFF00, 0XFFB03060, 0XFFFFC0CB, 0XFFF0E68C, 0XFF8B4513,
            0XFF3D59AB, 0XFF03A89E, 0XFFB0171F, 0XFFA066D3, 0XFFC76114, 0XFF708069, 0XFF385E0F };

    //    public static final int[] COLORS = { Color.BLUE, Color.CYAN, Color.BLACK, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY,
    //        Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW, 0xFFff458B00, 0xFFff8B0000, 0xFFff7CFC00, 0xFFffFF00FF, 0xFFffEE1289, 
    //        0xFFffB23AEE, 0xFFff00FFFF, 0xFFff27408B, 0xFFffFF8247, 0xFFffFFFF00, 0xFFff458B74, 0xFFff8B7500, 0xFFff8C8C8C, };

    /** 百度统计appkey */
    public static final String APPKEY_BAIDU = "f0234c2f5c";

    public static void initScreen(Activity activity) {
        DisplayMetrics DM = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(DM);
        AppEnv.screenWeight = DM.widthPixels;
        AppEnv.screenHeight = DM.heightPixels;
    }
}
