package cn.babysee.picture;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePref {

    private static final String SHARE_PREF_NAME = "babaysee";

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
