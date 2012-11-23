package cn.babysee.picture.update;

public class UpdateConfig {

    public String downloadUrl;
    public String updateDesc;
    public int versionCode;
    public String versionName;
    public String forceUpdateVersion;

    @Override
    public String toString() {
        return "UpdateConfig [downloadUrl=" + downloadUrl + ", updateDesc=" + updateDesc + ", versionCode="
                + versionCode + ", versionName=" + versionName + ", forceUpdateVersion=" + forceUpdateVersion + "]";
    }

}
