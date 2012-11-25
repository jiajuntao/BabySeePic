package cn.babysee.utils;

import android.content.Context;
import android.widget.Toast;

public class UIUtils {

    public static void showToast(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }
    
    public static void showToast(Context context, int textId, int duration) {
        Toast.makeText(context, textId, duration).show();
    }
}
