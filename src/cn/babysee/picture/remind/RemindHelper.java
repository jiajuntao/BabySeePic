package cn.babysee.picture.remind;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import cn.babysee.picture.R;
import cn.babysee.picture.draw.DrawBoardActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharePref;
import cn.babysee.picture.game.GameListActivity;
import cn.babysee.picture.nutrition.NutritionFragmentTabNavigation;
import cn.babysee.picture.test.TestListActivity;

public class RemindHelper {

    private static final String TAG = "RemindHelper";

    //3天后才能提醒
    public static final long MARKET_SCORE_TIME = 3 * 24 * 60 * 60 * 1000;

    public static boolean goToSupportUs(Context context) {
        //3天之后提醒一次，应用评价，以后不在提醒，避免对用户造成骚扰；

        long nowTime = System.currentTimeMillis();

        long lastTime = SharePref.getLong(context, SharePref.MARKET_SCORE_REMIND, nowTime);

        //第一次安装，不提醒
        if (lastTime == nowTime) {
            SharePref.setLong(context, SharePref.MARKET_SCORE_REMIND, nowTime);
            return false;
        }

        //已经提醒过
        if (lastTime == 1) {
            return false;
        }

        //已提醒过，不在提醒
        if ((System.currentTimeMillis() - lastTime) > MARKET_SCORE_TIME) {
            //设置已提醒标志为：1
            SharePref.setLong(context, SharePref.MARKET_SCORE_REMIND, 1);
            return true;
        }
        return false;
    }

    public static void goToMarketScore(Context context) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        intent.setData(uri);
        context.startActivity(intent);
    }

    // Use a layout id for a unique identifier
    public static final int NOTIF_TEST_ID = 1452793630;
    public static final int NOTIF_DRAW_ID = 1454793630;
    public static final int NOTIF_GAME_ID = 1452393630;
    public static final int NOTIF_SEEPIC_ID = 1452792630;
    public static final int NOTIF_NUTRION_ID = 1452793130;

    private static void showNotification(Context context, int moodId, int textId, int notifId, Class classPend) {
        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = context.getResources().getText(textId);

        // Set the icon, scrolling text and timestamp.
        // Note that in this example, we pass null for tickerText.  We update the icon enough that
        // it is distracting to show the ticker text every time it changes.  We strongly suggest
        // that you do this as well.  (Think of of the "New hardware found" or "Network connection
        // changed" messages that always pop up)
        Notification notification = new Notification(moodId, null, System.currentTimeMillis());

        Intent intent = new Intent(context, classPend);
        intent.putExtra("NotificationId", notifId);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Set the info for the views that show in the notification panel.
        notification
                .setLatestEventInfo(context, context.getResources().getText(R.string.app_name), text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(notifId, notification);
    }

    public static void showNotification(Context context) {
        
        
        String firstDayOfWeek = getFirstDayOfWeek();
        if (isWeekDay()) {

            String gameLastTime = SharePref.getString(context, SharePref.NOTIF_GAME_TIME, null);
            if (gameLastTime == null || !firstDayOfWeek.equals(gameLastTime)) {
                if (AppEnv.DEBUG) {
                    Log.i(TAG, "notif_game");
                }
                showNotification(context, R.drawable.ic_launcher, R.string.notif_game, NOTIF_GAME_ID,
                        GameListActivity.class);
                SharePref.setString(context, SharePref.NOTIF_GAME_TIME, firstDayOfWeek);
                return;
            }

            String nutrionLastTime = SharePref.getString(context, SharePref.NOTIF_NUTRION_TIME, null);
            if (nutrionLastTime == null || !firstDayOfWeek.equals(nutrionLastTime)) {
                if (AppEnv.DEBUG) {
                    Log.i(TAG, "notif_nutrion");
                }
                showNotification(context, R.drawable.ic_launcher, R.string.notif_nutrion, NOTIF_NUTRION_ID,
                        NutritionFragmentTabNavigation.class);
                SharePref.setString(context, SharePref.NOTIF_NUTRION_TIME, firstDayOfWeek);
                return;
            }
        }

        String drawLastTime = SharePref.getString(context, SharePref.NOTIF_DRAW_TIME, null);
        if (drawLastTime == null || !firstDayOfWeek.equals(drawLastTime)) {
            if (AppEnv.DEBUG) {
                Log.i(TAG, "notif_draw");
            }
            showNotification(context, R.drawable.ic_launcher, R.string.notif_draw, NOTIF_DRAW_ID,
                    DrawBoardActivity.class);
            SharePref.setString(context, SharePref.NOTIF_DRAW_TIME, firstDayOfWeek);
            return;
        }

        String month = getMonth();
        String testLastTime = SharePref.getString(context, SharePref.NOTIF_TEST_TIME, null);
        if (testLastTime == null || !month.equals(testLastTime)) {
            if (AppEnv.DEBUG) {
                Log.i(TAG, "notif_test");
            }
            showNotification(context, R.drawable.ic_launcher, R.string.notif_draw, NOTIF_TEST_ID,
                    TestListActivity.class);
            SharePref.setString(context, SharePref.NOTIF_TEST_TIME, month);
            return;
        }
    }

    public static void removeNotification(Context context, int notifId) {
        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.cancel(notifId);
    }

    public static boolean isWeekDay() {
        String weekStr = "";
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (week) {
        case 0:
            weekStr = "星期日";
            return true;
        case 1:
            weekStr = "星期一";
            break;
        case 2:
            weekStr = "星期二";
            break;
        case 3:
            weekStr = "星期三";
            break;
        case 4:
            weekStr = "星期四";
            break;
        case 5:
            weekStr = "星期五";
            break;
        case 6:
            weekStr = "星期六";
            return true;
        }
        return false;
    }

    public static String getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        String firstDayOfWeek = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DATE) + "日";
        return firstDayOfWeek;
    }

    public static String getMonth() {
        Calendar calendar = Calendar.getInstance();
        String month = (calendar.get(Calendar.MONTH) + 1) + "月";
        return month;
    }
}
