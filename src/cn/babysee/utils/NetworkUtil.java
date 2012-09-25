/**
 * 
 */
package cn.babysee.utils;



import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import cn.babysee.picture.env.AppEnv;

/**
 * @author Administrator
 *
 */
public class NetworkUtil {

	private static final String TAG = "Network";

    //***********************网络接入方式*************************//
    /**
     * 接入方式：0 NONE
     */
    public static final int APN_NONE = 0;
    /**
     * 接入方式：1 CMWAP
     */
    public static final int APN_PROXY = 1;
    /**
     * 接入方式：2 CMNET
     */
    public static final int APN_DIRECT = 2;

    //***********************网络连接方式*************************//
    /**
     * 网络连接：-1 UNKNOWN
     */
    public static final int NETWORK_UNKNOWN = -1;
    /**
     * 网络连接：-1 UNKNOWN
     */
    public static final int NETWORK_NONE = 0;
    /**
     * 网络连接：0 WIFI
     */
    public static final int NETWORK_WIFI = 1;
    /**
     * 网络连接：1 MOBILE
     */
    public static final int NETWORK_MOBILE = 2;
    /**
     * 网络连接：2 2G
     */
    public static final int NETWORK_2G = 3;
    /**
     * 网络连接：3 3G
     */
    public static final int NETWORK_3G = 4;
    
    private static String hostIp = null;
    private static int hostPort = 0;
    
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    /**
     * 获取接入点的相关信息：1、CMWAP，2、CMNET
     * */
    public static int getAccessPointType(Context context) {
        
        ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 区分是WIFI网络还是移动手机网络
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        
        if ((info!=null) && info.isAvailable()) {
            if (info.getTypeName().toLowerCase().equals("mobile")) {
                hostIp = Proxy.getHost(context);
                hostPort = Proxy.getPort(context);
                // 使用代理了
                if (!TextUtils.isEmpty(hostIp) && (hostPort!=0)) {
                    // 客户端是否使用代理
                    return APN_PROXY;
                } else {
                    return APN_DIRECT;
                }
//            } else if (info.getTypeName().toLowerCase().equals("wifi")) {
//                return APN_DIRECT;
            } else {
                return APN_DIRECT;
            }
        }
        return APN_NONE;
    }

	public static String getHostIp() {
		return hostIp;
	}

	public static int getHostPort() {
		return hostPort;
	}
    
    /**
     * 获取网络类型：-1、未知，0、无网络，1、WiFi，2、移动网络，3、2G（移动网络），4、3G（移动网络）
     * */
    public static int getNetworkType(Context context) {
        
        int networkType = NETWORK_UNKNOWN;
        
        ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 区分是WIFI网络还是移动手机网络
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        
        if ((info!=null) && info.isAvailable()) {
            if(info.getTypeName().toLowerCase().equals("wifi")) {
                networkType = NETWORK_WIFI;
            } else if(info.getTypeName().toLowerCase().equals("mobile")) {
                networkType = NETWORK_MOBILE;
                // 然后根据TelephonyManager来获取网络类型，判断当前是2G还是3G网络
                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                int netType = mTelephonyManager.getNetworkType();
                if (netType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                    networkType = NETWORK_UNKNOWN;
                } if (netType==TelephonyManager.NETWORK_TYPE_GPRS || netType==TelephonyManager.NETWORK_TYPE_EDGE) {
                    networkType = NETWORK_2G;
                } else {
                    networkType = NETWORK_3G;
                }
            }
        } else {
            networkType = NETWORK_NONE;
        }
        return networkType;
    }
    
    public static String getNetworkStringByType(int networkType) {
        String networkString = "Unknown";
        switch (networkType) {
            case NETWORK_NONE:
                networkString = "None";
                break;
            case NETWORK_WIFI:
                networkString = "WiFi";
                break;
            case NETWORK_MOBILE:
                networkString = "Mobile";
                break;
            case NETWORK_2G:
                networkString = "2G";
                break;
            case NETWORK_3G:
                networkString = "3G";
                break;
            default:
                networkString = "Unknown";
                break;
        }
        return networkString;
    }

	public void getApn(Context context){
		Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, new String[]{"_id", "apn", "type"},null, null, null );
        cursor.moveToFirst();
        
        long id = cursor.getLong(0);
        String apn = cursor.getString(1);
        String type = cursor.getString(2);
        if (AppEnv.DEBUG) {
            Log.v(TAG," "+id + " "+apn + " "+type);
        }
	}
	public ApnNode getCurApnNode(Context context){
		String id = "";
		String apn = "";
		String proxy = "";
		String name = "";
		String port = "";
		String mcc = "";
		String mnc = "";
		String numeric = "";
		String type="";
		ApnNode apnNode = new ApnNode();
		Cursor mCursor = context.getContentResolver().query(PREFERRED_APN_URI, null,null, null, null);
		if(mCursor==null){
			return null;
		}
		while ((mCursor != null) && mCursor.moveToFirst()) {
			id = mCursor.getString(mCursor.getColumnIndex("_id"));
			name = mCursor.getString(mCursor.getColumnIndex("name"));
			apn = mCursor.getString(mCursor.getColumnIndex("apn")).toLowerCase();
			proxy = mCursor.getString(mCursor.getColumnIndex("proxy"));
			port = mCursor.getString(mCursor.getColumnIndex("port"));
			mcc = mCursor.getString(mCursor.getColumnIndex("mcc"));
			mnc = mCursor.getString(mCursor.getColumnIndex("mnc"));
			numeric = mCursor.getString(mCursor.getColumnIndex("numeric"));
			type = mCursor.getString(mCursor.getColumnIndex("type"));
		}
		apnNode.setId(id);
		apnNode.setApn(apn);
		apnNode.setName(name);
		apnNode.setProxy(proxy);
		apnNode.setPort(port);
		apnNode.setMcc(mcc);
		apnNode.setMnc(mnc);
		apnNode.setNumeric(numeric);
		apnNode.setNumeric(type);
		return apnNode;
	}
	public static class ApnNode{
		
		String id = "";
		
		String apn = "";
		
		String proxy = "";
		
		String name = "";
		
		String port = "";
		
		String mcc = "";
		
		String mnc = "";
		
		String numeric = "";
		
		String type="";
		
		

		ApnNode(){
			
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			if((type==null)||"".equals(type)){
				this.type="null";
			}else{
				this.type = type;
			}
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			if((id==null)||"".equals(id)){
				this.id="null";
			}else{
				this.id = id;
			}
		}

		public String getApn() {
			return apn;
		}

		public void setApn(String apn) {
			if((apn==null)||"".equals(apn)){
				this.apn="null";
			}else{
				this.apn = apn;
			}
		}

		public String getProxy() {
			return proxy;
		}

		public void setProxy(String proxy) {
			if((proxy==null)||"".equals(proxy)){
				this.proxy="null";
			}else{
				this.proxy = proxy;
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			if((name==null)||"".equals(name)){
				this.name="null";
			}else{
				this.name = name;
			}
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			if((port==null)||"".equals(port)){
				this.port="null";
			}else{
				this.port = port;
			}
		}

		public String getMcc() {
			return mcc;
		}

		public void setMcc(String mcc) {
			if((mcc==null)||"".equals(mcc)){
				this.mcc="null";
			}else{
				this.mcc = mcc;
			}
		}

		public String getMnc() {
			return mnc;
		}

		public void setMnc(String mnc) {
			if((mnc==null)||"".equals(mnc)){
				this.mnc="null";
			}else{
				this.mnc = mnc;
			}
		}

		public String getNumeric() {
			return numeric;
		}

		public void setNumeric(String numeric) {
			if((numeric==null)||"".equals(numeric)){
				this.numeric="null";
			}else{
				this.numeric = numeric;
			}
		}
		
	}
}
