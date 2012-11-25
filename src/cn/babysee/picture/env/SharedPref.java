package cn.babysee.picture.env;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {

    private static final String SHARE_PREF_NAME = "babaysee";
    
    private static final String PRE_NAME = "normal_preference";

    public static final String GAME_STAGE = "game_stage";

    public static final String GAME_STAGE_POSITION = "game_stage_position";
    
    public static final String TEST_PHASE = "test_phase";
    
    public static final String SEEPIC_PHASE = "seepic_phase";
    
    public static final String TEST_PHASE_POSITION = "test_phase_position";
    
    //市场打分提醒
    public static final String MARKET_SCORE_REMIND = "market_score_remind";
    public static final String NOTIF_TEST_TIME = "notif_test_time";
    public static final String NOTIF_GAME_TIME = "notif_game_time";
    public static final String NOTIF_DRAW_TIME = "notif_draw_time";
    public static final String NOTIF_NUTRION_TIME = "notif_nutrion_time";
    public static final String NOTIF_LAST_TIME = "notif_last_time";
    public static final String NOTIF_YUERBAIKE_TIME = "notif_yuerbaike_time";
    public static final String NOTIF_YUERZHINAN_TIME = "notif_yuerzhinan_time";
    
    //最新版本号
    public static final String NEWEST_VERSION = "newest_version";
    //上次检查更新时间
    public static final String CHECK_UPDATE_TIME = "check_update_time";
    //上次提醒更新时间
    public static final String ALERT_UPDATE_TIME = "alert_update_time";

    public static void setString(Context context, String key, String value) {
        Editor sharedata = context.getSharedPreferences(PRE_NAME, 0).edit();
        sharedata.putString(key, value);
        sharedata.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        return context.getSharedPreferences(PRE_NAME, 0).getString(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        return preference.getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        return preference.getInt(key, defValue);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        return preference.getLong(key, defValue);
    }
    
    public static void setLong(Context context, String key, long value) {
        SharedPreferences preference = context.getSharedPreferences(SHARE_PREF_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
