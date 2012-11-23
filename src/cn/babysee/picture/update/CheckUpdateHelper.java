//package cn.babysee.picture.update;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.StringReader;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnKeyListener;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.Toast;
//import cn.babysee.picture.R;
//import cn.babysee.picture.env.AppEnv;
//import cn.babysee.utils.Utils;
//
//public class CheckUpdateHelper {
//
//    private static final String TAG = "CheckUpdateHelper";
//    private static final boolean DEBUG = AppEnv.DEBUG;
//    private static final int MSG_WHAT_LAST_VERSION = 4;
//    private static final int MSG_WHAT_FIND_NEW_VERSION = 5;
//    private static final int MSG_WHAT_NET_ERR = 6;
//    private static final int MSG_WHAT_GOTO_MARKET_IF_NEED = 7;
//
//    /** 下载包名 */
//    private static final String mLocalApkFile = "360SysOpt.apk";
//
//    /** 升级配置文件服务器 */
//    private static String UPDATE_SERVER_ADDRESS = "http://down.m.360.cn/sysop/";
//
//    /**
//     * 升级文件格式
//     * version=1.7.0.1021
//     * filename=360SysOpt.apk
//     * description=desc.txt
//     */
//    private static final String UPDATE_INI_FILE = "360sysoptimize.ini";
//
//    /** 检测升级间隔时间8个小时 */
//    private int intervalTimeCheckUpdate = 8 * 1000 * 60 * 60;
//    /** 弹框提醒间隔时间24小时 */
//    private int intervalTimeAlert = 24 * 1000 * 60 * 60;
//
//    /** 当前版本信息 */
//    private String currentVersion = SysOptAppEnv.APP_VERSION + "." + SysOptAppEnv.APP_BUILD;
//
//    private Activity mContext;
//    private HttpHandler mHttpHandler;
//
//    /** 线上版本 */
//    private String mNetVersion;
//    /** 线上版本文件名 */
//    private String mNetFilename;
//    /** 线上版本下载地址 */
//    private String mNetDownloadUrl;
//    /** 升级说明文件 */
//    private String mNetDescFile;
//    /** 升级说明内容 */
//    private String mNetDescription;
//    /** 强制升级描述 */
//    private String mNetForceUpdate;
//    /** 升级文件MD5值 */
//    private String mNetMD5;
//
//    /** 是否需要强制升级 */
//    private boolean isForceUpdate = false;
//
//    /**
//     * 自动检查为true 手动检查为false;
//     */
//    private boolean isAutoCheckUpdate = false;
//
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//            case MSG_WHAT_LAST_VERSION:
//                dismissProgressDialog();
//                createLastVersionDialog();
//                showDialog();
//                break;
//            case MSG_WHAT_FIND_NEW_VERSION:
//                if (mContext instanceof Activity) {
//                    if (((Activity) mContext).isFinishing())
//                        break;
//                }
//                // 提示升级
//                dismissProgressDialog();
//                SharedPref.setLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_TIME, System.currentTimeMillis());
//
//                if (isAutoCheckUpdate) {
//                    long lastAlertTime = SharedPref.getLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_ALERT_TIME, 0);
//                    long now = System.currentTimeMillis();
//
//                    if (DEBUG) {
//                        DateFormat format = SimpleDateFormat.getInstance();
//                        Log.d(TAG, "check alert update dialog lastAlertTime: " + format.format(lastAlertTime)
//                                + ", now: " + format.format(now));
//                    }
//
//                    //24小时弹窗升级提醒
//                    if (now - lastAlertTime >= intervalTimeAlert || now < lastAlertTime || isForceUpdate) {
//                        if (DEBUG)
//                            Log.i(TAG, "alert update dialog");
//                        createUpdateDialog();
//                        showDialog();
//                        SharedPref.setLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_ALERT_TIME, now);
//                    }
//
//                } else {
//                    createUpdateDialog();
//                    showDialog();
//                    SharedPref.setLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_ALERT_TIME, System.currentTimeMillis());
//                }
//
//                break;
//            case MSG_WHAT_NET_ERR:
//                UIUtils.showToast(mContext, R.string.connect_server_failed, Toast.LENGTH_SHORT);
//                break;
//            case MSG_WHAT_GOTO_MARKET_IF_NEED:
//                gotoMarketIfNeed();
//                break;
//            default:
//                break;
//            }
//
//        }
//    };
//
//    public CheckUpdateHelper(Activity context) {
//        this.mContext = context;
//        //
//        //        if (DEBUG) {
//        //            intervalTimeCheckUpdate = 0;
//        //            intervalTimeAlert = 0;
//        //        }
//    }
//
//    public void close() {
//        if (DEBUG)
//            Log.d(TAG, "close");
//        if (mHttpHandler != null) {
//            mHttpHandler.cancel(true);
//            mHttpHandler = null;
//        }
//        RandomText.userCanceled();
//    }
//
//    /**
//     * 升级检查
//     * 
//     * 1、 检查升级周期
//     * 每8小时检查一次更新（原来是每24小时）；
//     * 2、 强制升级
//     * 升级配置文件中加一个字段，“forceupdate=0”；
//     * 如果“forceupdate=0”，强制升级关闭；
//     * 如果“forceupdate=1”，强制升级打开；
//     * 软件检查更新的时候如果检查到新版本，并且强制升级打开，则提示用户升级，如果用户取消，软件自动退出，无法进入软件。
//     * 3、 升级提示方式
//     * 每24小时之内有新版本最多只提示一次用户。
//     * 
//     */
//    public boolean checkUpdate() {
//
//        isAutoCheckUpdate = true;
//
//        //若升级开关“关闭”则不进行升级检查
//        if (!SharedPref.getBoolean(mContext, SharedPref.SP_KEY_AUTO_UPDATE, true)) {
//            if (DEBUG)
//                Log.i(TAG, "checkUpdate return [auto update check service close]");
//            return false;
//        }
//
//        //当处于强制升级状态时每次进入主界面都要进行联网升级检查
//        String focesUpdatVersion = SharedPref.getString(mContext, SharedPref.SP_KEY_FORCE_UPDATE_VERSION, null);
//        if (!TextUtils.isEmpty(focesUpdatVersion) && compareVersion(currentVersion, focesUpdatVersion) < 0) {
//            if (DEBUG)
//                Log.w(TAG, "has checkUpdate [you must force update]");
//            update(true);
//            return true;
//        }
//        SharedPref.setString(mContext, SharedPref.SP_KEY_FORCE_UPDATE_VERSION, "");
//
//        long lastCheckTime = SharedPref.getLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_TIME, 0);
//        long now = System.currentTimeMillis();
//
//        if (DEBUG) {
//            DateFormat format = SimpleDateFormat.getInstance();
//            Log.d(TAG, "checkUpdate lastCheckTime: " + format.format(lastCheckTime) + ", now: " + format.format(now));
//        }
//
//        if (now - lastCheckTime >= intervalTimeCheckUpdate || now < lastCheckTime) {
//            RandomText.checkUpdate(mContext, getServerFileUrl());
//            update(true);
//            return true;
//        }
//
//        return false;
//    }
//
//    public void update(final boolean doInBackground) {
//        isAutoCheckUpdate = doInBackground;
//
//        Statistician.report(mContext);
//        //初始化
//        isForceUpdate = false;
//        if (SysOptAppEnv.DEBUG) {
//            String url = getDebugUpdateFileURL();
//            if (!TextUtils.isEmpty(url)) {
//                UPDATE_SERVER_ADDRESS = url;
//            }
//        }
//
//        if (SysOptAppEnv.DEBUG) {
//            Log.i(TAG, "update isAutoCheckUpdate:" + isAutoCheckUpdate + " update server url: " + UPDATE_SERVER_ADDRESS);
//        }
//
//        //若升级检查服务已启动，根据状态提示；
//        if (mHttpHandler != null && mHttpHandler.getStatus() == AsyncTask.Status.RUNNING) {
//            Log.w(TAG, "update is running return");
//            UIUtils.showToast(mContext, R.string.update_is_running, Toast.LENGTH_SHORT);
//            return;
//        }
//
//        mHttpHandler = new HttpHandler(mContext, HttpHandler.OPTI_HTTP_GETSTRING, HttpHandler.OPTI_HTTP_GET, false);
//        mHttpHandler.setRequestUrl(UPDATE_SERVER_ADDRESS + UPDATE_INI_FILE);
//        if (!doInBackground) {
//            showProgressDialog(mContext.getString(R.string.update_progress_title),
//                    mContext.getString(R.string.update_progress_download_ini));
//        }
//        mHttpHandler.setHttpHandlerListener(new HttpStateListener() {
//            @Override
//            public void setHttpResponseState(HttpHandler http, int state) {
//                if (DEBUG)
//                    Log.d(TAG, "setHttpResponseState: " + state);
//            }
//
//        });
//        mHttpHandler.setHttpListener(new HttpConnectionListener() {
//            public void downloadEnd(HttpHandler http, Object data) {
//                if (DEBUG) {
//                    Log.v(TAG, "downloadEnd: ********************* \n" + data + "\n*********************");
//                }
//
//                boolean parseResult = parseUpdateConfigResult((String) data);
//                if (parseResult == false) {
//                    // 联网失败
//                    dismissProgressDialog();
//                    if (!isAutoCheckUpdate) {
//                        mHandler.sendEmptyMessage(MSG_WHAT_NET_ERR);
//                    }
//                }
//            }
//        });
//        mHttpHandler.execute();
//
//    }
//
//    /**
//     * 解析升级配置文件
//     * 
//     * @param result
//     * @param doInbackground
//     * @return
//     */
//    protected boolean parseUpdateConfigResult(String result) {
//        if (DEBUG)
//            Log.d(TAG, "parseUpdateConfigResult: " + result);
//        if (result == null) {
//            return false;
//        }
//
//        StringReader sr = null;
//        try {
//            sr = new StringReader((String) result);
//            IniProperties ini = new IniProperties();
//            ini.load(sr);
//
//            mNetVersion = ini.getProperty("version");
//            mNetFilename = ini.getProperty("filename");
//            mNetDownloadUrl = ini.getProperty("downloadurl");
//            mNetDescFile = ini.getProperty("description");
//            mNetForceUpdate = ini.getProperty("forceupdate");
//            mNetMD5 = ini.getProperty("md5");
//            if (DEBUG) {
//                Log.d(TAG, "mNetVersoin=" + mNetVersion);
//                Log.d(TAG, "mNetFilename=" + mNetFilename);
//                Log.d(TAG, "mNetDownloadUrl=" + mNetDownloadUrl);
//                Log.d(TAG, "mNetDesFile=" + mNetDescFile);
//                Log.d(TAG, "mNetForceUpdate=" + mNetForceUpdate);
//            }
//
//            if (!TextUtils.isEmpty(mNetVersion)) {
//                // 有新版本
//                if (compareVersion(currentVersion, mNetVersion) < 0) {
//
//                    //记录服务器上最新版本，用户Menu菜单new标志是否显示的判断
//                    SharedPref.setString(mContext, SharedPref.SP_KEY_NEWEST_VERSION, mNetVersion);
//
//                    isForceUpdate = checkIfForceUpdate(mNetForceUpdate);
//
//                    //记录需要强制升级的版本；
//                    if (isForceUpdate) {
//                        SharedPref.setString(mContext, SharedPref.SP_KEY_FORCE_UPDATE_VERSION, mNetVersion);
//                    }
//
//                    if (DEBUG)
//                        Log.i(TAG, "has new version forceUpdate: " + isForceUpdate);
//
//                    // 下载升级描述信息
//                    downloadUpdateDesc();
//
//                    //已是最新版本
//                } else {
//                    if (DEBUG)
//                        Log.i(TAG, "you app vesion is new");
//
//                    //避免过分骚扰用户，将GoogleMarket评价放在这里
//                    if (isAutoCheckUpdate) {
//                        mHandler.sendEmptyMessage(MSG_WHAT_GOTO_MARKET_IF_NEED);
//                    } else {
//                        //手动检查时显示已是最新版本提示框
//                        mHandler.sendEmptyMessage(MSG_WHAT_LAST_VERSION);
//                    }
//                }
//                SharedPref.setLong(mContext, SharedPref.SP_KEY_LAST_UPDATE_TIME, System.currentTimeMillis());
//                return true;
//            }
//        } catch (Exception e) {
//            if (DEBUG)
//                e.printStackTrace();
//        } finally {
//            try {
//                if (sr != null)
//                    sr.close();
//            } catch (Exception e) {
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * 获取测试升级配置文件地址
//     * 
//     * @return
//     */
//    private String getDebugUpdateFileURL() {
//        // 测试包，先取一下SD中的配置文件，读取测试配置文件
//        String state = Environment.getExternalStorageState();
//        if (state.equals(Environment.MEDIA_MOUNTED)) {
//            // 测试升级文件地址：/sdcard/360/360sysopt_debug.ini
//            File dir = new File(Environment.getExternalStorageDirectory(), SysOptAppEnv.DIR);
//            File file = new File(dir, SysOptAppEnv.DEBUG_UPDATE_FILE);
//            if (file.isFile() && file.canRead()) {
//
//                Log.d(TAG, "debugConfigFile: " + file.getPath());
//
//                try {
//                    List<String> list = Utils.parseConfigFile(new FileReader(file));
//                    if (list != null && list.size() > 0) {
//                        Log.d(TAG, "getDebugUpdateFileURL: " + list.get(0).trim());
//                        return list.get(0).trim();
//                    }
//                } catch (FileNotFoundException e) {
//                    Log.e(TAG, "", e);
//                }
//
//            } else {
//                Log.i(TAG, "debug update file not ready!");
//            }
//        } else {
//            Log.i(TAG, "SD not ready: " + state);
//        }
//        return "";
//    }
//
//    /**
//     * 下载升级描述文件
//     * 
//     * @param doInBackground
//     */
//    private void downloadUpdateDesc() {
//
//        mHttpHandler = new HttpHandler(mContext, HttpHandler.OPTI_HTTP_GETSTRING, HttpHandler.OPTI_HTTP_GET, false);
//        mHttpHandler.setRequestUrl(UPDATE_SERVER_ADDRESS + mNetDescFile);
//
//        mHttpHandler.setHttpHandlerListener(new HttpStateListener() {
//            @Override
//            public void setHttpResponseState(HttpHandler http, int state) {
//                if (DEBUG)
//                    Log.d(TAG, "downloadDesc setHttpResponseState: " + state + ".");
//            }
//
//        });
//        mHttpHandler.setHttpListener(new HttpConnectionListener() {
//            public void downloadEnd(HttpHandler http, Object data) {
//                dismissProgressDialog();
//                if (DEBUG)
//                    Log.v(TAG, "downloadDesc downloadEnd [" + data + "]");
//                if (data != null) {
//                    mNetDescription = (String) data;
//                    mHandler.sendEmptyMessage(MSG_WHAT_FIND_NEW_VERSION);
//                } else {
//                    // 联网失败
//                    if (!isAutoCheckUpdate) {
//                        mHandler.sendEmptyMessage(MSG_WHAT_NET_ERR);
//                    }
//                }
//            }
//        });
//        mHttpHandler.execute();
//    }
//
//    private Dialog mResultDialog;
//
//    public boolean showDialog() {
//        if (mResultDialog != null) {
//            if (mContext.isFinishing()) {
//                return false;
//            }
//            if (mResultDialog.isShowing()) {
//                mResultDialog.dismiss();
//            }
//            mResultDialog.show();
//            return true;
//        }
//        return false;
//    }
//
//    public void dismissDialog() {
//        dismissProgressDialog();
//        if (mResultDialog != null) {
//            try {
//                mResultDialog.dismiss();
//            } catch (Exception ex) {
//            }
//        }
//    }
//
//    private Dialog createUpdateDialog() {
//        final DialogFactory dialog = new DialogFactory(mContext, R.string.update_find_new_version_title, 0);
//        dialog.mMsg.setText(mContext.getString(R.string.update_find_new_version_message, mNetVersion, mNetDescription));
//        dialog.mBtnOK.setText(R.string.update_find_new_version_ok);
//        dialog.mBtnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadApk();
//                // 隐藏对话框
//                dialog.dismiss();
//                mResultDialog = null;
//            }
//
//        });
//        dialog.mBtnCancel.setText(R.string.update_find_new_version_cancel);
//        dialog.mBtnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 隐藏对话框
//                dialog.dismiss();
//                mResultDialog = null;
//                checkIfFocseUpdate(mContext);
//            }
//
//        });
//        dialog.setCancelable(true);
//        dialog.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent evnet) {
//                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//                    return true;
//                }
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    mResultDialog = null;
//                    checkIfFocseUpdate(mContext);
//                }
//                return false;
//            }
//        });
//        mResultDialog = dialog;
//        return dialog;
//    }
//
//    /**
//     * 若用户不进行强制升级，则退出应用
//     * 
//     * @param context
//     */
//    private void checkIfFocseUpdate(Context context) {
//        if (isForceUpdate) {
//            if (context instanceof Activity) {
//                ((Activity) context).finish();
//            }
//        }
//    }
//
//    private Dialog createLastVersionDialog() {
//        final DialogFactory dialog = new DialogFactory(mContext, R.string.update_last_version_title,
//                R.string.update_last_version_message);
//        dialog.mBtnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 隐藏对话框
//                dialog.dismiss();
//                mResultDialog = null;
//            }
//
//        });
//        dialog.setButtonText(DialogFactory.ID_BTN_OK, R.string.update_back);
//        dialog.mBtnCancel.setVisibility(View.GONE);
//        dialog.setCancelable(true);
//        dialog.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent evnet) {
//                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//                    return true;
//                }
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    mResultDialog = null;
//                }
//                return false;
//            }
//        });
//        mResultDialog = dialog;
//        return dialog;
//    }
//
//    DialogFactory mProgressDialog = null;
//
//    public void showProgressDialog(String title, String msg) {
//        if (msg == null)
//            return;
//
//        if (mProgressDialog != null) {
//            mProgressDialog.setTitle(title);
//            mProgressDialog.setMsg(msg);
//            mProgressDialog.show();
//            return;
//        }
//
//        DialogFactory progressDialog = new DialogFactory(mContext);
//        if (!TextUtils.isEmpty(title)) {
//            progressDialog.setTitle(title);
//        }
//        progressDialog.setMsg(msg);
//        progressDialog.enableMsgProgressBar();
//        progressDialog.setCancelable(true);
//        progressDialog.setButtonVisibility(DialogFactory.ID_BTN_OK, false);
//        progressDialog.setButtonVisibility(DialogFactory.ID_BTN_CANCEL, false);
//        progressDialog.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent evnet) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (mHttpHandler != null) {
//                        mHttpHandler.cancel(true);
//                    }
//                    return false;
//                } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        mProgressDialog = progressDialog;
//        mProgressDialog.show();
//    }
//
//    public void dismissProgressDialog() {
//        if (mProgressDialog != null) {
//            try {
//                mProgressDialog.dismiss();
//            } catch (Exception ex) {
//            }
//        }
//    }
//
//    private void downloadApk() {
//
//        final File downloadApkFile = new File(UpdateUtils.getDownloadSavePath(), mLocalApkFile);
//
//        //若已经下载，则直接安装
//        if (isDownloaded(downloadApkFile)) {
//            if (DEBUG)
//                Log.i(TAG, "has download apk, go to install it");
//            UpdateUtils.installApk(mContext, downloadApkFile);
//            return;
//        }
//
//        // 准备下载
//        if (!UpdateUtils.checkExternalStorage()) {
//            UIUtils.showToast(mContext, R.string.update_no_sdcard, Toast.LENGTH_SHORT);
//        } else {
//
//            File downlaodPath = new File(UpdateUtils.getDownloadSavePath());
//            if (!downlaodPath.exists()) {
//                downlaodPath.mkdirs();
//            }
//
//            if (downlaodPath.exists()) {
//
//                mHttpHandler = new HttpHandler(mContext, HttpHandler.OPTI_HTTP_GETBYTES, HttpHandler.OPTI_HTTP_GET,
//                        false);
//                if (!TextUtils.isEmpty(mNetDownloadUrl)) {
//                    // 优先试用指定的下载链接
//                    mHttpHandler.setRequestUrl(mNetDownloadUrl);
//                } else {
//                    // 如果没有指定下载链接，则试用老的方法，在同一级目录的文件
//                    mHttpHandler.setRequestUrl(UPDATE_SERVER_ADDRESS + mNetFilename);
//                }
//
//                showProgressDialog(mContext.getString(R.string.update_progress_title),
//                        mContext.getString(R.string.update_progress_download_apk));
//
//                mHttpHandler.setHttpHandlerListener(new HttpStateListener() {
//                    @Override
//                    public void setHttpResponseState(HttpHandler http, int state) {
//                        if (DEBUG)
//                            Log.d(TAG, "downloadApk setHttpResponseState： " + state + ".");
//                    }
//
//                });
//                mHttpHandler.setHttpListener(new HttpConnectionListener() {
//                    public void downloadEnd(HttpHandler http, Object data) {
//                        dismissProgressDialog();
//                        if (DEBUG)
//                            Log.d(TAG, "downloadApk downloadEnd[" + data + "]");
//                        if (data != null) {
//                            // 保存到文件中
//                            boolean bSaved = false;
//                            FileOutputStream out = null;
//                            try {
//                                out = new FileOutputStream(downloadApkFile);
//                                out.write((byte[]) data);
//                                bSaved = true;
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            } finally {
//                                try {
//                                    if (out != null)
//                                        out.close();
//                                } catch (Exception ex) {
//
//                                }
//                            }
//                            if (bSaved) {
//                                UpdateUtils.installApk(mContext, downloadApkFile);
//                            }
//
//                        } else {
//                            // 联网失败
//                            mHandler.sendEmptyMessage(MSG_WHAT_NET_ERR);
//                        }
//                    }
//                });
//                mHttpHandler.execute();
//            } else {
//                UIUtils.showToast(mContext, R.string.update_mkdirs_failed, Toast.LENGTH_SHORT);
//            }
//        }
//    }
//
//    public boolean isDownloaded(File file) {
//
//        if (file != null && file.exists()) {
//            if (DEBUG)
//                Log.d(TAG, "downloadApkPath: " + file.getPath());
//
//            String downloadFileMD5 = Utils.getFileMD5(file.getPath());
//            if (!TextUtils.isEmpty(downloadFileMD5) && downloadFileMD5.equals(mNetMD5)) {
//                return true;
//            }
//
//            file.delete();
//            return false;
//        }
//        return false;
//    }
//
//    private void gotoMarketIfNeed() {
//        if (ReminderPref.getBoolean(mContext, ReminderPref.SP_KEY_CREATE_SYSCLEAR_SHORTCUT, true)) {
//            return;
//        }
//
//        if (!SysOptUtils.isPkgInstalled(mContext, SysOptAppEnv.PKGNAME_GOOGLE_PLAY)) {
//            return;
//        }
//        if (!ReminderPref.getBoolean(mContext, ReminderPref.SP_KEY_SHOW_GOTO_MARKET, true)) {
//            return;
//        }
//
//        if (SysOptAppEnv.CID_GOOGLE_MARKET != Statistician.initCID(mContext)) {
//            return;
//        }
//
//        final DialogFactory dialog = new DialogFactory(mContext, R.string.goto_market_dialog_title,
//                R.string.goto_market_dialog_content);
//        dialog.mBtnOK.setText(R.string.goto_market_dialog_btn_ok);
//        dialog.mBtnCancel.setText(R.string.goto_market_dialog_btn_cancel);
//        dialog.mBtnOK.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//                try {
//                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
//                            + SysOptAppEnv.PKGNAME));
//                    gotoMarket.setPackage(SysOptAppEnv.PKGNAME_GOOGLE_PLAY);
//                    mContext.startActivity(gotoMarket);
//                } catch (Exception e) {
//                }
//            }
//        });
//        dialog.mBtnCancel.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCancelable(false);
//        dialog.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
//        if (!mContext.isFinishing()) {
//            dialog.show();
//        }
//
//        ReminderPref.setBoolean(mContext, ReminderPref.SP_KEY_SHOW_GOTO_MARKET, false);
//    }
//
//    private boolean checkIfForceUpdate(String validateStr) {
//
//        if (DEBUG)
//            Log.d(TAG, "checkIfForceUpdate: " + validateStr);
//
//        if (validateStr == null) {
//            return false;
//        }
//
//        String[] validateVersions = validateStr.split(";");
//        if (validateVersions == null) {
//            return false;
//        }
//
//        int len = validateVersions.length;
//
//        if (DEBUG)
//            Log.d(TAG, Arrays.toString(validateVersions));
//
//        for (int i = 0; i < len; i++) {
//            if (validateVersion(currentVersion, validateVersions[i])) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * 检查是否需要强制升级
//     * 
//     * @param context
//     * @param validateVersion
//     */
//    private boolean validateVersion(String currentVersion, String validateVersion) {
//
//        if (DEBUG)
//            Log.d(TAG, "currentVersion:" + currentVersion + " validateVersion: " + validateVersion);
//
//        boolean forceUpdate = false;
//        try {
//            if (validateVersion == null) {
//                return forceUpdate;
//            }
//
//            /** 限定小于强制 */
//            if (validateVersion.matches("<\\d+\\.\\d+\\.\\d+.\\d+")) {
//                if (compareVersion(currentVersion, validateVersion.substring("<".length())) < 0) {
//                    forceUpdate = true;
//                } else {
//                    forceUpdate = false;
//                }
//            } else if (validateVersion.matches("\\d+\\.\\d+\\.\\d+.\\d+-\\d+\\.\\d+\\.\\d+.\\d+")) {
//                /** 限定范围强制 */
//                String[] a2b = validateVersion.split("-");
//                if (compareVersion(currentVersion, a2b[0]) >= 0 && compareVersion(currentVersion, a2b[1]) <= 0) {
//                    forceUpdate = true;
//                } else {
//                    forceUpdate = false;
//                }
//            } else if (validateVersion.matches("\\d+\\.\\d+\\.\\d+.\\d+")) {
//                /** 限定指定版本强制 */
//                if (compareVersion(currentVersion, validateVersion) == 0) {
//                    forceUpdate = true;
//                } else {
//                    forceUpdate = false;
//                }
//            } else {
//                /** 失败 */
//                forceUpdate = false;
//            }
//            validateVersion = null;
//        } catch (Exception e) {
//            if (DEBUG)
//                e.printStackTrace();
//        }
//        return forceUpdate;
//    }
//
//    /**
//     * 检查是否有新版本
//     */
//    public boolean hasNewVersion() {
//        String newestVersion = SharedPref.getString(mContext, SharedPref.SP_KEY_NEWEST_VERSION, null);
//
//        if (DEBUG)
//            Log.d(TAG, "hasNewVersion currentVersion:" + currentVersion + " newestVersion:" + newestVersion);
//        if (TextUtils.isEmpty(newestVersion)) {
//            return false;
//        } else {
//            if (compareVersion(currentVersion, newestVersion) < 0) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }
//
//    /**
//     * 比较版本
//     * 
//     * @param v1
//     *            版本1
//     * @param v2
//     *            版本2
//     * @return -1： 小于 0：等于 1：大于
//     */
//    private final int compareVersion(String v1, String v2) {
//        if (DEBUG)
//            Log.v(TAG, "compareVersion:" + v1 + " <---> " + v2);
//        int r = 0;
//
//        int[] vi1 = new int[] { 0, 0, 0, 0 };
//        int[] vi2 = new int[] { 0, 0, 0, 0 };
//
//        String[] vs1 = v1.split("\\.");
//        String[] vs2 = v2.split("\\.");
//        int len1 = vs1.length;
//        int len2 = vs2.length;
//
//        for (int i = 0; i < 4; i++) {
//            vi1[i] = str2Int(i < len1 ? vs1[i] : "0");
//        }
//        for (int i = 0; i < 4; i++) {
//            vi2[i] = str2Int(i < len2 ? vs2[i] : "0");
//        }
//
//        for (int i = 0; i < 4; i++) {
//            if (vi1[i] < vi2[i]) {
//                r = -1;
//                return r;
//            } else if (vi1[i] > vi2[i]) {
//                r = 1;
//                return r;
//            }
//        }
//        return r;
//    }
//
//    private final int str2Int(String str) {
//        int n = 0;
//        try {
//            if (str != null && !"".equals(str))
//                n = Integer.parseInt(str);
//        } catch (Exception exception) {
//        }
//        return n;
//    }
//
//    private static final String RANDOM_TEXT_URL = "http://shouji.360.cn/down/sysop/random_text.xml";
//
//    private String getServerFileUrl() {
//        String serverFileUrl = RANDOM_TEXT_URL;
//        if (SysOptAppEnv.DEBUG) {
//            // 测试包，先取一下SD中的配置文件，读取测试配置文件
//            String state = Environment.getExternalStorageState();
//            if (state.equals(Environment.MEDIA_MOUNTED)) {
//                File dir = new File(Environment.getExternalStorageDirectory(), SysOptAppEnv.DIR);
//                File file = new File(dir, SysOptAppEnv.DEBUG_UPDATE_FILE);
//                if (file.isFile() && file.canRead()) {
//                    Log.d(TAG, "getDebugServerFileUrl: " + file.getPath());
//                    FileReader fileReader = null;
//                    try {
//                        fileReader = new FileReader(file);
//                        List<String> list = Utils.parseConfigFile(fileReader);
//                        if (list != null && list.size() > 3) {
//                            serverFileUrl = list.get(3).trim();
//                            Log.w(TAG, "GET debug server addr: " + serverFileUrl);
//                        }
//                    } catch (Exception e1) {
//                        Log.e(TAG, "", e1);
//                    } finally {
//                        if (null != fileReader) {
//                            try {
//                                fileReader.close();
//                            } catch (Exception e2) {
//                            }
//                        }
//                    }
//
//                } else {
//                    Log.w(TAG, "debug stat file not ready!");
//                }
//            } else {
//                Log.w(TAG, "SD not ready: " + state);
//            }
//        }
//        return serverFileUrl;
//    }
//}