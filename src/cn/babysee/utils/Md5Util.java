package cn.babysee.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

public class Md5Util {

    // MD5 hasher.
    private static MessageDigest mDigest;

    {
        try {
            mDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // This shouldn't happen.
            throw new RuntimeException("No MD5 algorithm.");
        }
    }

    // MD5 hases are used to generate filenames based off a URL.
    public static String getMd5(String url) {
        if (!TextUtils.isEmpty(url) && mDigest != null) {
            mDigest.update(url.getBytes());
            return getHashString(mDigest);
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        if (digest == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        byte[] byteArray = digest.digest();
        if (byteArray == null) {
            return null;
        }

        for (byte b : byteArray) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }

        return builder.toString();
    }
}
