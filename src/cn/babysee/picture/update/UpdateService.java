package cn.babysee.picture.update;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.babysee.picture.MainActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.http.Utility;
import cn.babysee.picture.http.Utility.DownloadCallback;
import cn.babysee.picture.http.WeiboException;
import cn.babysee.picture.remind.RemindHelper;
import cn.babysee.utils.UIUtils;
import cn.babysee.utils.Utils;

public class UpdateService extends Service {
    private static final boolean DEBUG = AppEnv.DEBUG;
    private static final String TAG = "UpdateService";

    private String downloadUrl;
    private String appName;

    //文件存储
    private File updateDir = null;
    private File updateFile = null;

    //通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    //下载状态
    private final static int MSG_DOWNLOAD_FINISH = 0;
    private final static int MSG_DOWNLOAD_FAIL = 1;

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_DOWNLOAD_FINISH:
                //点击安装PendingIntent
//                Uri uri = Uri.fromFile(updateFile);
//                Intent installIntent = new Intent(Intent.ACTION_VIEW);
//                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
//                updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);

                updateNotification.defaults = Notification.DEFAULT_SOUND;//铃声提醒 
                updateNotification.setLatestEventInfo(UpdateService.this, appName,
                        getString(R.string.download_apk_success_install_msg), updatePendingIntent);
                updateNotificationManager.notify(RemindHelper.NOTIF_UPDATE_ID, updateNotification);
                
                UIUtils.showToast(getApplicationContext(), R.string.update_download_finish, Toast.LENGTH_LONG);
                
                Utils.installApk(getApplicationContext(), updateFile);
                updateNotificationManager.cancel(RemindHelper.NOTIF_UPDATE_ID);
                //停止服务
                stopService(updateIntent);
                break;
            case MSG_DOWNLOAD_FAIL:
                //下载失败
                updateNotification.setLatestEventInfo(UpdateService.this, appName,
                        getString(R.string.download_failed_generic_msg), updatePendingIntent);
                updateNotificationManager.notify(RemindHelper.NOTIF_UPDATE_ID, updateNotification);
                break;
            default:
                stopService(updateIntent);
                break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取传值
        downloadUrl = intent.getStringExtra("downloadUrl");
        appName = getString(R.string.app_name);
        if (DEBUG)
            Log.d(TAG, "onStartCommand: " + downloadUrl);
        if (TextUtils.isEmpty(downloadUrl)) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        //创建文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), AppEnv.DOWNLOAD_PATH);
            updateFile = new File(updateDir.getPath(), AppEnv.DOWNLOAD_NAME);
        }

        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.updateNotification = new Notification();

        //设置下载过程中，点击通知栏，回到主界面
        updateIntent = new Intent(this, MainActivity.class);
        updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        //设置通知栏显示内容
        updateNotification.icon = R.drawable.download_notification_icon;
        updateNotification.tickerText = getString(R.string.download_start);
        updateNotification.setLatestEventInfo(this, appName, null, updatePendingIntent);
        //发出通知
        updateNotificationManager.notify(RemindHelper.NOTIF_UPDATE_ID, updateNotification);

        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
        new Thread(new UpdateRunnable()).start();//这个是下载的重点，是下载的过程

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    class UpdateRunnable implements Runnable {

        public void run() {
            try {
                cheanUpdateFile();
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }
                downloadUpdateFile(downloadUrl, updateFile);
            } catch (Exception ex) {
                if (DEBUG)
                    ex.printStackTrace();
                //下载失败
                updateHandler.sendEmptyMessage(MSG_DOWNLOAD_FAIL);
            }
        }
    }

    public void downloadUpdateFile(String downloadUrl, File saveFile) throws WeiboException {
        Utility.downloadFile(this, downloadUrl, saveFile, new DownloadCallback() {

            @Override
            public void onDownloadStart() {
                if (DEBUG)
                    Log.d(TAG, "onDownloadStart");
            }

            @Override
            public void onDownloadProcess(int downloadPercent) {
                updateNotification.setLatestEventInfo(UpdateService.this,
                        getString(R.string.downloading_apk_msg, appName, downloadPercent) + "%", null,
                        updatePendingIntent);
                updateNotificationManager.notify(RemindHelper.NOTIF_UPDATE_ID, updateNotification);
            }

            @Override
            public void onDownloadFinish() {
                if (DEBUG)
                    Log.d(TAG, "onDownloadFinish");
                updateHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
            }
        });
    }

    public void cheanUpdateFile() {
        File updateFile = new File(AppEnv.DOWNLOAD_PATH, getResources().getString(R.string.app_name) + ".apk");
        if (updateFile.exists()) {
            //当不需要的时候，清除之前的下载文件，避免浪费用户空间
            updateFile.delete();
        }
    }
}
