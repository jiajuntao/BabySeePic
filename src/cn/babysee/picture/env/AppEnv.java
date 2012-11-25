package cn.babysee.picture.env;

import android.app.Activity;
import android.util.DisplayMetrics;

public class AppEnv {

    public static final boolean DEBUG = false;

    public static final String TAG = "BabyStudy";
    public static final String DOWNLOAD_PATH = "BabySeePic";
    public static final String DOWNLOAD_NAME = "BabySeePic.apk";
    
    public static final int ONE_DAY = 24 * 60 * 60 * 1000;

    public static int screenWeight;

    public static int screenHeight;

    public static final int[] COLORS = { 0XFFC0C0C0, 0XFF292421, 0XFF191970, 0XFF7FFFD4,
            0XFFFFD700, 0XFFFF8000, 0XFF7CFC00, 0XFF6B8E23, 0XFF32CD32, 0XFFDA70D6, 0XFF33A1C9,
            0XFFFF7F50, 0XFFFF6347, 0XFF00C78C, 0XFF8A2BE2, 0XFFE3170D, 0XFF082E54, 0XFF40E0D0,
            0XFF6A5ACD, 0XFFB0E0E6, 0XFFFFFF00, 0XFFB03060, 0XFFFFC0CB, 0XFFF0E68C, 0XFF8B4513,
            0XFF3D59AB, 0XFF03A89E, 0XFFB0171F, 0XFFA066D3, 0XFFC76114, 0XFF708069, 0XFF385E0F };
    
    
    //升级文件地址
    public static final String UPDATE_CONFIG_URL = "http://bcs.duapp.com/babyseepic/%2Fbabyseepic_update_config?sign=MBO:102ff356464477239159a6e735ea2791:xLlH0ezC3Rz5ueTRL%2B%2FdOC1xXgQ%3D";

    /** 百度统计appkey */
    public static final String APPKEY_BAIDU = "f0234c2f5c";

    public static void initScreen(Activity activity) {
        DisplayMetrics DM = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(DM);
        AppEnv.screenWeight = DM.widthPixels;
        AppEnv.screenHeight = DM.heightPixels;
    }
}
