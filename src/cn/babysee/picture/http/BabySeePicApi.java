package cn.babysee.picture.http;

import com.google.gson.Gson;

import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.update.UpdateConfig;
import android.content.Context;
import android.util.Log;

public class BabySeePicApi {

    private static final boolean DEBUG = AppEnv.DEBUG;
    private static final String TAG = "BabySeePicApi";
    private static final String updateConfigUrl = "http://bcs.duapp.com/babyseepic/%2Fbabyseepic_update_config?sign=MBO:102ff356464477239159a6e735ea2791:xLlH0ezC3Rz5ueTRL%2B%2FdOC1xXgQ%3D";
    
    public static void getUpdateConfig(Context context) {
        
        
        try {
            String content = Utility.openUrl(context, updateConfigUrl, "GET", null, null);
//            String content = getStaticPage(updateConfigUrl);
            if (DEBUG)
                Log.d(TAG, "getUpdateConfig" + content);
            
            Gson gson = new Gson();
            if (DEBUG)
                Log.d(TAG, "UpdateConfig: " + gson.fromJson(content, UpdateConfig.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getStaticPage(String surl) {
        String htmlContent = "";
        try {
            java.io.InputStream inputStream;
            java.net.URL url = new java.net.URL(surl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024 * 2000];
            int index = 0;
            int count = inputStream.read(bytes, index, 1024 * 2000);
            while (count != -1) {
                index += count;
                count = inputStream.read(bytes, index, 1);
            }
            htmlContent = new String(bytes, "utf-8");
            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return htmlContent.trim();
    }
}
