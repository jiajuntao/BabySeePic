package cn.babysee.picture.remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharedPref;

public class RemindReceiver extends BroadcastReceiver {

    private static final int TIME = 24 * 60 * 60 * 1000;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (AppEnv.DEBUG)
                Log.d("RemindReceiver", action);
//            ConnectivityManager connectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//            String name = info.getTypeName();

            //屏幕亮相才提醒用户
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (!isScreenOn) {
                return;
            }

            long lastRemindTime = SharedPref.getLong(context, SharedPref.NOTIF_LAST_TIME, 0);
            long nowTime = System.currentTimeMillis();
            if ((nowTime - lastRemindTime) > TIME) {
                if (AppEnv.DEBUG)
                    Log.i("RemindReceiver", "showNotification");
                SharedPref.setLong(context, SharedPref.NOTIF_LAST_TIME, nowTime);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        RemindHelper.showNotification(context);
                    }
                }).start();
            }
            //            if (info != null && info.isAvailable()) {
            //            } else {
            //                if (AppEnv.DEBUG) Log.i("RemindReceiver", "没有可用网络");
            //            }
        }

    }

}
