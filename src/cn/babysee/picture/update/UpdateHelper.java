package cn.babysee.picture.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharedPref;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.http.Utility;
import cn.babysee.utils.FileUtils;
import cn.babysee.utils.NetworkUtil;
import cn.babysee.utils.UIUtils;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;

public class UpdateHelper {

    private static final boolean DEBUG = AppEnv.DEBUG;
    private static final String TAG = "UpdateHelper";

    private String updateConfigUrl = AppEnv.UPDATE_CONFIG_URL;

    private Activity mContext;
    private String currentVersion;

    private UpdateConfig updateConfig;
    
    private static final int MSG_SHOW_UPDATE_DIALOG = 1;
    private static final int MSG_SHOW_NO_UPDATE_TOAST = 2;
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_SHOW_UPDATE_DIALOG:
                StatService.onEvent(mContext, StatServiceEnv.UPDATE_REMIND_EVENT_ID,
                        StatServiceEnv.UPDATE_REMIND_LABEL, 1);
                createUpdateDialog(updateConfig);
                break;
            case MSG_SHOW_NO_UPDATE_TOAST:
                StatService.onEvent(mContext, StatServiceEnv.UPDATE_REMIND_EVENT_ID,
                        StatServiceEnv.UPDATE_REMIND_LABEL, 1);
                UIUtils.showToast(mContext, R.string.update_last_version_message, Toast.LENGTH_LONG);
                break;

            default:
                break;
            }
        };
    };

    public UpdateHelper(Activity context) {
        mContext = context;
        currentVersion = getVersionName();
    }

    //检查间隔1天；
    private static final int CHECK_UPDATE_TIME = 1 * AppEnv.ONE_DAY;
    //提醒间隔3天；
    private static final int ALERT_UPDATE_TIME = 3 * AppEnv.ONE_DAY;

    //自动检查更新
    public void checkUpdate() {

        long lastCheckUpdateTime = SharedPref.getLong(mContext, SharedPref.CHECK_UPDATE_TIME, 0);

        if ((lastCheckUpdateTime == 0) || (System.currentTimeMillis() - lastCheckUpdateTime > CHECK_UPDATE_TIME)) {
            update(true);
        }
    }

    //手动检查更新
    public void update(final boolean backgroundCheckUpdate) {
        if (DEBUG)
            Log.d(TAG, "checkUpdate backgroundCheckUpdate:" + backgroundCheckUpdate);

        new Thread(new Runnable() {

            @Override
            public void run() {

                updateConfig = getUpdateConfig(mContext);
                if (updateConfig == null) {
                    if (DEBUG)
                        Log.w(TAG, "check update fail config is null");
                    return;
                }

                SharedPref.setLong(mContext, SharedPref.CHECK_UPDATE_TIME, System.currentTimeMillis());
                SharedPref.setString(mContext, SharedPref.NEWEST_VERSION, updateConfig.versionName);

                if (compareVersion(currentVersion, updateConfig.versionName) < 0) {
                    //有新版本，升级提醒
                    if (DEBUG)
                        Log.i(TAG, "has new version");
                    //                    if (backgroundCheckUpdate) {
                    //                        long alertUpdateTime = SharedPref.getLong(mContext, SharedPref.ALERT_UPDATE_TIME, 0);
                    //                        if ((alertUpdateTime == 0)
                    //                                || (System.currentTimeMillis() - alertUpdateTime > ALERT_UPDATE_TIME)) {
                    //                            mHandler.sendEmptyMessage(MSG_SHOW_UPDATE_DIALOG);
                    //                        }
                    //                    } else {
                    //                    }
                    
                    //自动检查更新，只在wifi或者3G网络下提供反刷
                    if (backgroundCheckUpdate) {
                        int netType = NetworkUtil.getNetworkType(mContext);
                        if ((NetworkUtil.NETWORK_3G == netType) || (NetworkUtil.NETWORK_WIFI == netType)) {
                            mHandler.sendEmptyMessage(MSG_SHOW_UPDATE_DIALOG);
                        }
                    } else {
                        mHandler.sendEmptyMessage(MSG_SHOW_UPDATE_DIALOG);
                    }
                    
                } else {
                    if (!backgroundCheckUpdate) {
                        mHandler.sendEmptyMessage(MSG_SHOW_NO_UPDATE_TOAST);
                    }
                }
            }
        }).start();
    }

    private void createUpdateDialog(UpdateConfig updateConfig) {
        if (updateConfig == null) {
            return;
        }

        if (mContext.isFinishing()) {
            return;
        }

        Builder builder = new AlertDialog.Builder(mContext);

        Dialog dialog = builder
                .setTitle(R.string.update_find_new_version_title)
                .setMessage(
                        mContext.getString(R.string.update_find_new_version_message, updateConfig.versionName,
                                updateConfig.updateDesc))
                .setNeutralButton(R.string.update_find_new_version_ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StatService.onEvent(mContext, StatServiceEnv.UPDATE_NOW_EVENT_ID,
                                StatServiceEnv.UPDATE_NOW_LABEL, 1);
                        if (FileUtils.isSdcardValid(mContext)) {
                            downloadApk();
                        }
                        // 隐藏对话框
                        dialog.dismiss();
                    }

                }).setNegativeButton(R.string.update_find_new_version_cancel, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StatService.onEvent(mContext, StatServiceEnv.UPDATE_LATER_EVENT_ID,
                                StatServiceEnv.UPDATE_LATER_LABEL, 1);
                        // 隐藏对话框
                        dialog.dismiss();
                    }
                }).create();
        
        try {
            dialog.show();
            SharedPref.setLong(mContext, SharedPref.ALERT_UPDATE_TIME, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadApk() {
        if (DEBUG)
            Log.d(TAG, "downloadApk");

        Intent updateIntent = new Intent(mContext, UpdateService.class);
        updateIntent.putExtra("downloadUrl", updateConfig.downloadUrl);
        mContext.startService(updateIntent);
    }

    private UpdateConfig getUpdateConfig(Context context) {

        try {
            //            String content = BabySeePicApi.getStaticPage(updateConfigUrl);
            String content = Utility.openUrl(context, updateConfigUrl, "GET", null, null);
            if (DEBUG)
                Log.d(TAG, "getUpdateConfig" + content);

            Gson gson = new Gson();

            UpdateConfig updateConfig = gson.fromJson(content, UpdateConfig.class);

            if (DEBUG)
                Log.d(TAG, "UpdateConfig: " + updateConfig);
            return updateConfig;
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    public String getVersionName() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }

        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "0.0.0.0";
    }

    public int getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }

        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * 检查是否有新版本
     */
    public boolean hasNewVersion() {
        String newestVersion = SharedPref.getString(mContext, SharedPref.NEWEST_VERSION, null);

        if (DEBUG)
            Log.d(TAG, "hasNewVersion currentVersion:" + currentVersion + " newestVersion:" + newestVersion);
        if (TextUtils.isEmpty(newestVersion)) {
            return false;
        } else {
            if (compareVersion(currentVersion, newestVersion) < 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 检查是否需要强制升级
     * 
     * @param context
     * @param validateVersion
     */
    private boolean validateVersion(String currentVersion, String validateVersion) {

        if (DEBUG)
            Log.d(TAG, "currentVersion:" + currentVersion + " validateVersion: " + validateVersion);

        boolean forceUpdate = false;
        try {
            if (validateVersion == null) {
                return forceUpdate;
            }

            /** 限定小于强制 */
            if (validateVersion.matches("<\\d+\\.\\d+\\.\\d+.\\d+")) {
                if (compareVersion(currentVersion, validateVersion.substring("<".length())) < 0) {
                    forceUpdate = true;
                } else {
                    forceUpdate = false;
                }
            } else if (validateVersion.matches("\\d+\\.\\d+\\.\\d+.\\d+-\\d+\\.\\d+\\.\\d+.\\d+")) {
                /** 限定范围强制 */
                String[] a2b = validateVersion.split("-");
                if (compareVersion(currentVersion, a2b[0]) >= 0 && compareVersion(currentVersion, a2b[1]) <= 0) {
                    forceUpdate = true;
                } else {
                    forceUpdate = false;
                }
            } else if (validateVersion.matches("\\d+\\.\\d+\\.\\d+.\\d+")) {
                /** 限定指定版本强制 */
                if (compareVersion(currentVersion, validateVersion) == 0) {
                    forceUpdate = true;
                } else {
                    forceUpdate = false;
                }
            } else {
                /** 失败 */
                forceUpdate = false;
            }
            validateVersion = null;
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }
        return forceUpdate;
    }

    /**
     * 比较版本
     * 
     * @param v1
     *            版本1
     * @param v2
     *            版本2
     * @return -1： 小于 0：等于 1：大于
     */
    private final int compareVersion(String v1, String v2) {
        if (DEBUG)
            Log.v(TAG, "compareVersion:" + v1 + " <---> " + v2);
        int r = 0;

        int[] vi1 = new int[] { 0, 0, 0, 0 };
        int[] vi2 = new int[] { 0, 0, 0, 0 };

        String[] vs1 = v1.split("\\.");
        String[] vs2 = v2.split("\\.");
        int len1 = vs1.length;
        int len2 = vs2.length;

        int len = Math.min(len1, len2);

        for (int i = 0; i < len; i++) {
            vi1[i] = str2Int(i < len1 ? vs1[i] : "0");
        }
        for (int i = 0; i < len; i++) {
            vi2[i] = str2Int(i < len2 ? vs2[i] : "0");
        }

        for (int i = 0; i < len; i++) {
            if (vi1[i] < vi2[i]) {
                r = -1;
                return r;
            } else if (vi1[i] > vi2[i]) {
                r = 1;
                return r;
            }
        }
        return r;
    }

    private final int str2Int(String str) {
        int n = 0;
        try {
            if (str != null && !"".equals(str))
                n = Integer.parseInt(str);
        } catch (Exception exception) {
        }
        return n;
    }
}
