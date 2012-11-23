package cn.babysee.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import cn.babysee.picture.env.AppEnv;

public class Utils {

    private static final String TAG = "Utils";

    private static final char CHAR_LINE = '_';

    private static final char CHAR_DOT = '.';

    private static final String TAG_MAIL_REX = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

    public static Typeface getFontTypeFace(Context context, String fontPath) {
        //默认获取改字体的Typeface，当字体加载失败时获取当前正在使用的Typeface
        Typeface fontFace = null;
        try {
            fontFace = Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return fontFace;
    }

    /**
     * 手机信息类型：1屏幕宽度
     */
    public static final int TYPE_SCREENW = 1;

    /**
     * 手机信息类型：2屏幕高度
     */
    public static final int TYPE_SCREENH = 2;

    public static Context context = null;

    public static void setContext(Context appContext) {
        context = appContext;
    }

    public static Context getContext() {
        return context;
    }

    public static final String PACKAGE_NAME = "cn.babysee.pic";

    /**
     * Constructs the version string of the application.
     * 
     * @param context
     *            the context to use for getting package info
     * @return the versions string of the application
     */
    public static String getVersionName(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return pi.versionName;
        } catch (Exception e) {
            if (AppEnv.DEBUG)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * Constructs the version string of the application.
     * 
     * @param context
     *            the context to use for getting package info
     * @return the versions code of the application
     */
    public static int getVersionCode(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            if (AppEnv.DEBUG)
                e.printStackTrace();
            return -1;
        }
    }

    /***************************************************************************
     * 获得指定的手机屏幕的相关信息
     * 
     * @param 当前显示的activity和要获取的信息类型
     * @return 返回要获取的信息
     */
    public static int getScreenInfo(Activity activity, int infoType) {

        // 获得手机屏幕宽度和高度
        Display display = activity.getWindowManager().getDefaultDisplay();

        switch (infoType) {
        case TYPE_SCREENW:
            return display.getWidth();
        case TYPE_SCREENH:
            return display.getHeight();
        default:
            if (AppEnv.DEBUG)
                Log.v(TAG, "Unknow Screen type wao founded.");
            return 0;
        }
    }

    /**
     * dip转换成px
     * */
    public static int dip2px(float scale, float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * dip转换成px
     * */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转换成dip
     * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getPhotoPathFromContentResolver(Context context, Uri uri) {
        String path = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, new String[] { MediaColumns.DATA }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        }
        return path;
    }

    /**
     * 根据图片的URL获取图片的宽度和高度
     * */
    public static int[] getImageWidthHeight(String url) {

        int[] widthAndHeight = { -1, -1 };
        int firstLineIndex = -1, lastLineIndex = -1, dotIndex = -1;

        if (!TextUtils.isEmpty(url)) {
            // 取最后一个点的位置和最后两个下滑线的位置
            dotIndex = TextUtils.lastIndexOf(url, CHAR_DOT);
            lastLineIndex = TextUtils.lastIndexOf(url, CHAR_LINE);
            firstLineIndex = TextUtils.lastIndexOf(url, CHAR_LINE, lastLineIndex - 1);
            // 判断位置是否合理
            if (dotIndex < 0 || lastLineIndex < 0 || firstLineIndex < 0) {
                if (AppEnv.DEBUG)
                    Log.e(TAG, "图片的URL中未包括宽和高：" + url);
                return widthAndHeight;
            }
            // 分别设置宽和高
            String temp = TextUtils.substring(url, firstLineIndex + 1, lastLineIndex);
            if (TextUtils.isDigitsOnly(temp)) {
                widthAndHeight[0] = Integer.parseInt(temp);
            }
            temp = TextUtils.substring(url, lastLineIndex + 1, dotIndex);
            if (TextUtils.isDigitsOnly(temp)) {
                widthAndHeight[1] = Integer.parseInt(temp);
            }
        }
        return widthAndHeight;
    }

    /**
     * 获取当前的时间（格式：YYYYMMDDTHHMMSS）
     **/
    public static String getNowTimeString() {
        Time time = new Time();
        time.setToNow();
        return time.format2445();
    }

    private static final DecimalFormat dcmFmt = new DecimalFormat("0.000");

    /**
     * 获取当前的时间（格式：YYYYMMDDTHHMMSS）
     **/
    public static String getFormatNumString(double f) {
        return dcmFmt.format(f);
    }

    /**
     * 获取当前的时间（格式：yyyy-MM-dd H:m:s）.
     **/
    public static String getCurrentTime(long time) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date(time));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");

        return format.format(c1.getTime());
    }

    /**
     * 获取当前的时间（格式：毫秒）
     **/
    public static long getNowTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getSimpleTimeString(long time) {
        Date date = new Date(time);
        java.text.DateFormat format = new java.text.SimpleDateFormat("MM-dd");
        return format.format(date);
    }

    public static String getComplexTimeString(long time) {
        Date date = new Date(time);
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static boolean isConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);//获取系统的连接服务
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况

        // Skip if no connection, or background data disabled NetworkInfo info = mConnectivity.getActiveNetworkInfo(); 
        if ((activeNetInfo == null) || !connectivityManager.getBackgroundDataSetting()) {
            return false;
        }
        int netType = activeNetInfo.getType();
        int netSubtype = activeNetInfo.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return activeNetInfo.isConnected();
        } else if ((netType == ConnectivityManager.TYPE_MOBILE) && (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS)
                && !mTelephony.isNetworkRoaming()) {
            return activeNetInfo.isConnected();
        } else {
            return false;
        }
    }

    public static boolean is3GOrWifi(Context context) {
        int networkType = NetworkUtil.getNetworkType(context);
        return (networkType == NetworkUtil.NETWORK_3G || networkType == NetworkUtil.NETWORK_WIFI);
    }

    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    public static void Copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        copyStream(in, out);
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /***************************************************************************
     * 在SharedPreferences中读取指定的内容
     */
    public static Map<String, ?> ReadSharedPreferences(String name) {
        SharedPreferences userInfo;
        try {
            userInfo = context.getSharedPreferences(name, 0);
            return userInfo.getAll();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /***************************************************************************
     * 在SharedPreferences中读取指定的内容
     */
    public static int ReadSharedPreferencesInt(String name, String key) {
        try {
            SharedPreferences userInfo = context.getSharedPreferences(name, 0);
            return userInfo.getInt(key, -1);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    /***************************************************************************
     * 在SharedPreferences中读取指定的内容
     */
    public static long ReadSharedPreferencesLong(String name, String key) {
        try {
            SharedPreferences userInfo = context.getSharedPreferences(name, 0);
            return userInfo.getLong(key, -1);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    /***************************************************************************
     * 在SharedPreferences中读取指定的内容
     */
    public static boolean ReadSharedPreferencesBoolean(String name, String key) {
        try {
            SharedPreferences userInfo = context.getSharedPreferences(name, 0);
            return userInfo.getBoolean(key, true);
        } catch (NullPointerException e) {
            return true;
        }
    }

    /***************************************************************************
     * 在SharedPreferences中读取指定的内容
     */
    public static String ReadSharedPreferencesString(String name, String key) {
        try {
            SharedPreferences userInfo = context.getSharedPreferences(name, 0);
            return userInfo.getString(key, null);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /***************************************************************************
     * 删除SharedPreferences的内容
     */
    public static void DeleteSharedPreferences(String name) {
        SharedPreferences.Editor userInfoEditor = context.getSharedPreferences(name, 0).edit();
        userInfoEditor.clear();
        userInfoEditor.commit();
    }

    /***************************************************************************
     * 向SharedPreferences中写入指定的内容
     */
    public static void WriteSharedPreferences(String name, String key, String value) {
        SharedPreferences.Editor userInfoEditor = context.getSharedPreferences(name, 0).edit();
        userInfoEditor.putString(key, value);
        userInfoEditor.commit();
    }

    /***************************************************************************
     * 向SharedPreferences中写入指定的内容
     */
    public static void WriteSharedPreferences(String name, String key, int value) {
        SharedPreferences.Editor userInfoEditor = context.getSharedPreferences(name, 0).edit();
        userInfoEditor.putInt(key, value);
        userInfoEditor.commit();
    }

    /***************************************************************************
     * 向SharedPreferences中写入指定的内容
     */
    public static void WriteSharedPreferences(String name, String key, long value) {
        SharedPreferences.Editor userInfoEditor = context.getSharedPreferences(name, 0).edit();
        userInfoEditor.putLong(key, value);
        userInfoEditor.commit();
    }

    /***************************************************************************
     * 向SharedPreferences中写入指定的内容
     */
    public static void WriteSharedPreferences(String name, String key, boolean value) {
        SharedPreferences.Editor userInfoEditor = context.getSharedPreferences(name, 0).edit();
        userInfoEditor.putBoolean(key, value);
        userInfoEditor.commit();
    }

    /**
     * 判断邮箱是否合法
     */
    public static boolean isMailAddressValid(String data) {
        Pattern pattern = Pattern.compile(TAG_MAIL_REX);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    public static long parseLong(CharSequence s) {
        long out = 0;
        byte shifts = 0;
        char c;
        for (int i = 0; i < s.length() && shifts < 16; i++) {
            c = s.charAt(i);
            if ((c > 47) && (c < 58)) {
                ++shifts;
                out <<= 4;
                out |= c - 48;
            } else if ((c > 64) && (c < 71)) {
                ++shifts;
                out <<= 4;
                out |= c - 55;
            } else if ((c > 96) && (c < 103)) {
                ++shifts;
                out <<= 4;
                out |= c - 87;
            }
        }
        return out;
    }

    /**
     * 根据feedid生成时间
     * 
     * @param uuidStr
     * @return
     * @throws Exception
     */
    public static long parseTime(String uuidStr) throws Exception {
        long time = parseLong(uuidStr.subSequence(0, 18));
        if ((time & 0x1000) == 0) {
            throw new Exception(uuidStr + " is not a time relative uuid");
        }
        long currLow = time >> 32;
        long currMid = (time & 0x0FFFF0000L) << 16;
        long currHi = (time & 0x0FFF) << 48;
        return (currLow + currMid + currHi - 0x01B21DD213814000L) / 10000;
    }

    private static final String IMAGE_URL = "http://m{catchid}.img.libdd.com/dynamic/{imageid}/{width}/{height}/";

    /**
     * 根据宽度获得静态的图片（如果是gif图片取第一帧）
     * */
    public static String getStaticImageUrlByWidth(String imageId, String imageType, int imageWidth, int requestWidth) {
        if (imageId == null) {
            return null;
        }
        if (imageWidth > 0 && requestWidth >= imageWidth) {
            requestWidth = imageWidth - 1;
        }
        char c[] = imageId.toCharArray();
        int catchId = c[c.length - 1];
        catchId = catchId % 3 + 1;
        String imageUrl = IMAGE_URL.replace("{catchid}", String.valueOf(catchId)).replace("{imageid}", imageId)
                .replace("{width}", String.valueOf(requestWidth)).replace("{height}", "0");
        return imageUrl;
    }

    /**
     * 根据高度获得静态的图片
     * */
    public static String getStaticImageUrlByHeight(String imageId, String imageType, int imageHeight, int requestHeight) {
        if (imageId == null) {
            return null;
        }
        if (imageHeight > 0 && requestHeight >= imageHeight) {
            requestHeight = imageHeight - 1;
        }
        char c[] = imageId.toCharArray();
        int catchId = c[c.length - 1];
        catchId = catchId % 3 + 1;
        String imageUrl = IMAGE_URL.replace("{catchid}", String.valueOf(catchId)).replace("{imageid}", imageId)
                .replace("{width}", "0").replace("{height}", String.valueOf(requestHeight));
        return imageUrl;
    }

    /**
     * 取整张图片
     * */
    public static String getFullImageUrl(String imageId, int width, int height) {
        if (imageId == null) {
            return null;
        }
        char c[] = imageId.toCharArray();
        int catchId = c[c.length - 1];
        catchId = catchId % 3 + 1;
        String imageUrl = IMAGE_URL.replace("{catchid}", String.valueOf(catchId)).replace("{imageid}", imageId)
                .replace("{width}", String.valueOf(width)).replace("{height}", String.valueOf(height));
        //        PrintLog.d(TAG, "imageUrl=" + imageUrl);
        return imageUrl;
    }

    /**
     * 获取经纬度
     * 
     * @param context
     * @return
     */
    public static double[] getLatitudeAndLongitude(Context context) {
        double[] latLong = null;

        LocationManager loctionManager;
        loctionManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //使用标准集合，让系统自动选择可用的最佳位置提供器，提供位置
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//低功耗

        //从可用的位置提供器中，匹配以上标准的最佳提供器
        String provider = loctionManager.getBestProvider(criteria, true);
        if (TextUtils.isEmpty(provider)) {
            provider = LocationManager.GPS_PROVIDER;
        }

        //获得最后一次变化的位置
        Location location = loctionManager.getLastKnownLocation(provider);
        if (location != null) {
            latLong = new double[2];
            latLong[0] = location.getLatitude();
            latLong[1] = location.getLongitude();
            if (AppEnv.DEBUG)
                Log.d(TAG, "latitude: " + latLong[0] + " longitude: " + latLong[1]);
        } else {
            if (AppEnv.DEBUG)
                Log.d(TAG, "can't find Location info");
        }
        return latLong;
    }

    /**
     * 获取图片矩阵变换后的X坐标
     * 
     * @param m
     * @return
     */
    public static float getImageMatrixPointX(ImageView view) {
        float[] values = new float[9];
        view.getImageMatrix().getValues(values);
        return values[2];
    }

    /**
     * 获取图片矩阵变换后的Y坐标
     * 
     * @param m
     * @return
     */
    public static float getImageMatrixPointY(ImageView view) {
        float[] values = new float[9];
        view.getImageMatrix().getValues(values);
        return values[5];
    }

    /**
     * 获取图片矩阵变换后的缩放比例(在不旋转和长宽比一样的情况下)
     * 
     * @param m
     * @return
     */
    public static float getImageMatrixScale(ImageView view) {
        float[] values = new float[9];
        view.getImageMatrix().getValues(values);
        return values[8] * values[0];
    }

    /**
     * 检测packageName是否已经安装
     * 
     * @param packageName
     * @return
     */
    public static boolean checkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件名称
     * 
     * @return 返回 yyyy-MM-dd(HH_mm_ss)
     */
    public static String getFileName() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd(HH_mm_ss)");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
