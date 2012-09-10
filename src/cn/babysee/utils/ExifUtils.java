package cn.babysee.utils;

import java.io.IOException;

import android.media.ExifInterface;
import android.text.TextUtils;

public class ExifUtils {

    private ExifUtils() {
    }

    public static int getExifRotation(String imgPath) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR) {
            return 0;
        }

        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public static boolean saveExif(String sourcePicPath, String targetPicPath) throws Exception {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR) {
            return false;
        }
        Exif originalExif = ExifUtils.getExif(sourcePicPath);

        ExifInterface exif = new ExifInterface(targetPicPath);

        if (originalExif.mDateTime != null) {
            exif.setAttribute(ExifInterface.TAG_DATETIME, originalExif.mDateTime);
        }
        if (originalExif.mFlash != null) {
            exif.setAttribute(ExifInterface.TAG_FLASH, originalExif.mFlash);
        }
        if (originalExif.mMake != null) {
            exif.setAttribute(ExifInterface.TAG_MAKE, originalExif.mMake);
        }
        if (originalExif.mModel != null) {
            exif.setAttribute(ExifInterface.TAG_MODEL, originalExif.mModel);
        }
        if (originalExif.mOrientation != null) {
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, originalExif.mOrientation);
        }
        if (originalExif.mWhiteBalance != null) {
            exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, originalExif.mWhiteBalance);
        }
        if (originalExif.mLatitudeRef != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, originalExif.mLatitudeRef);
        }
        if (originalExif.mLatitude != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, originalExif.mLatitude);
        }

        if (originalExif.mLongitudeRef != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, originalExif.mLongitudeRef);
        }
        if (originalExif.mLongitude != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, originalExif.mLongitude);
        }

        exif.saveAttributes();
        return true;
    }

    public static boolean saveLatitudeAndLongitude(String targetPicPath, double latitude,
            double longitude) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR) {
            return false;
        }
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(targetPicPath);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(latitude));
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(longitude));

        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Exif getExif(String imgPath) {
        Exif mExif = new Exif();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR) {
            return mExif;
        }
        try {
            ExifInterface exif = new ExifInterface(imgPath);

            mExif.mDateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            mExif.mFlash = String.valueOf(exif.getAttributeInt(ExifInterface.TAG_FLASH, -1));
            mExif.mMake = exif.getAttribute(ExifInterface.TAG_MAKE);
            mExif.mModel = exif.getAttribute(ExifInterface.TAG_MODEL);
            mExif.mOrientation = String.valueOf(ExifInterface.ORIENTATION_NORMAL);
            mExif.mWhiteBalance = String.valueOf(exif.getAttributeInt(
                    ExifInterface.TAG_WHITE_BALANCE, -1));
            mExif.mLatitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            mExif.mLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            mExif.mLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            mExif.mLongitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mExif;
    }
}
