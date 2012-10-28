package cn.babysee.picture.env;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePref {

    private static final String SHARE_PREF_NAME = "babaysee";
    
    private static final String PRE_NAME = "normal_preference";

    public static final String GAME_STAGE = "game_stage";

    public static final String GAME_STAGE_POSITION = "game_stage_position";
    
    public static final String TEST_PHASE = "test_phase";
    
    public static final String TEST_PHASE_POSITION = "test_phase_position";

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
}
