package cn.babysee.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CacheManager;
import android.webkit.CacheManager.CacheResult;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import cn.babysee.picture.R;

public class FileUtils {

    /**
     * 本程序文件夹名
     */
    public static final String APP_FOLDER_NAME = "babyseepic";

    /**
     * 存放保存的图片的文件夹名
     */
    public static final String IMAGE_FOLDER_NAME = "pic";

    /**
     * 存放临时文件的文件夹名
     */
    public static final String SAVE_DRAW_PIC_DIR = "pic";

    /**
     * 存放临时文件的文件夹名
     */
    public static final String TEMP_FOLDER_NAME = "temp";

    /**
     * 存放临时的图片压缩文件的文件名
     */
    public static final String TEMP_COMPRESS_IMAGE_NAME = "temp_compress";

    /**
     * temporary path to save the cropped image in BlogSettingActivity
     */
    public static final String TEMP_CROP_IMAGE_NAME = "temp_crop_image";

    /**
     * 存放临时的图片查看文件的文件名
     */
    public static final String TEMP_VIEW_IMAGE_NAME = "temp_view";

    private static String appFolderPath = null; // 此程序存放文件的文件夹路径

    private static String imageFolderPath = null; // 存放图片的文件夹名

    private static String tempFolderPath = null;

    static {
        // 获得存储卡的路径
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        if (!sdcardPath.endsWith(File.separator)) {
            sdcardPath = sdcardPath + File.separator;
        }
        // 拼接本程序用到的目录
        appFolderPath = sdcardPath + APP_FOLDER_NAME + File.separator;
        imageFolderPath = appFolderPath + IMAGE_FOLDER_NAME + File.separator;
        tempFolderPath = appFolderPath + TEMP_FOLDER_NAME + File.separator;
    }

    // 判断SD卡是否存在
    public static boolean isSdcardValid(Context context) {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            int msg;

            // 判断SD卡是否正在使用文件共享（U盘模式挂载）
            if (status.equals(Environment.MEDIA_SHARED)) {
                msg = R.string.download_sdcard_busy_dlg_msg;
            } else {
                msg = R.string.download_no_sdcard_dlg_msg;
            }

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // 获得存储卡的路径
            String sdcardPath = Environment.getExternalStorageDirectory().getPath();
            if (!sdcardPath.endsWith(File.separator)) {
                sdcardPath = sdcardPath + File.separator;
            }
            // 拼接本程序用到的目录
            appFolderPath = sdcardPath + APP_FOLDER_NAME + File.separator;
            imageFolderPath = appFolderPath + IMAGE_FOLDER_NAME + File.separator;
            tempFolderPath = appFolderPath + TEMP_FOLDER_NAME + File.separator;
            return true;
        }
    }

    public static boolean isSdcardValidNoToast() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取保存图片的路径
     */
    public static String getAppFolderPath() {
        if (createFolder(appFolderPath)) {
            return appFolderPath;
        } else {
            return null;
        }
    }

    /**
     * 获取保存图片的路径
     */
    public static String getImageFolderPath() {
        if (createFolder(imageFolderPath)) {
            return imageFolderPath;
        } else {
            return null;
        }
    }

    /**
     * 获取临时目录的路径
     */
    public static String getTempFolderPath() {
        if (createFolder(tempFolderPath)) {
            return tempFolderPath;
        } else {
            return null;
        }
    }

    /**
     * 获取临时压缩图片文件的路径
     */
    public static String getTempCompressImagePath() {
        if (createFolder(tempFolderPath)) {
            return tempFolderPath + TEMP_COMPRESS_IMAGE_NAME;
        } else {
            return null;
        }
    }

    /**
     * 获取临时查看图片文件的路径
     */
    public static String getTempViewImagePath() {
        if (createFolder(tempFolderPath)) {
            return tempFolderPath + TEMP_VIEW_IMAGE_NAME;
        } else {
            return null;
        }
    }

    /**
     * get the temp save path by the image url
     * 
     * @param imageUrl
     * @return
     */
    public static String getTempImagePath(String imageUrl) {
        String path = Md5Util.getMd5(imageUrl);
        if (createFolder(tempFolderPath)) {
            return tempFolderPath + path;
        } else {
            return null;
        }
    }

    /**
     * get the temporary path of the cropped image
     */
    public static String getTempCropImagePath() {
        if (createFolder(tempFolderPath)) {
            return tempFolderPath + TEMP_CROP_IMAGE_NAME;
        } else {
            return null;
        }
    }

    public static String getMimeType(String url) {
        CacheResult mCacheResult = CacheManager.getCacheFile(url, null);
        if (mCacheResult != null) {
            return mCacheResult.getMimeType();
        } else {
            return null;
        }
    }

    /**
     * 获取文件扩展名
     * 
     * @return suggested extension
     */
    public static final String guessFileExtension(String url, String mimeType) {
        String filename = null;
        String extension = null;

        // If all the other http-related approaches failed, use the plain uri
        if (filename == null) {
            String decodedUrl = Uri.decode(url);
            if (decodedUrl != null) {
                int queryIndex = decodedUrl.indexOf('?');
                // If there is a query string strip it, same as desktop browsers
                if (queryIndex > 0) {
                    decodedUrl = decodedUrl.substring(0, queryIndex);
                }
                if (!decodedUrl.endsWith("/")) {
                    int index = decodedUrl.lastIndexOf('/') + 1;
                    if (index > 0) {
                        filename = decodedUrl.substring(index);
                    }
                }
            }
        }

        // Finally, if couldn't get filename from URI, get a generic filename
        if (filename == null) {
            filename = "downloadfile";
        }

        // Split filename between base and extension
        // Add an extension if filename does not have one
        int dotIndex = filename.indexOf('.');
        if (dotIndex < 0) {
            if (mimeType != null) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (extension != null) {
                    extension = "." + extension;
                }
            }
            if (extension == null) {
                if ((mimeType != null) && mimeType.toLowerCase().startsWith("text/")) {
                    if (mimeType.equalsIgnoreCase("text/html")) {
                        extension = ".html";
                    } else {
                        extension = ".txt";
                    }
                }
                if ((mimeType != null) && mimeType.toLowerCase().startsWith("image/")) {
                    if (mimeType.equalsIgnoreCase("image/jpeg")) {
                        extension = ".jpg";
                    } else if (mimeType.equalsIgnoreCase("image/png")) {
                        extension = ".png";
                    } else if (mimeType.equalsIgnoreCase("image/gif")) {
                        extension = ".gif";
                    }
                }
            }
        } else {
            if (mimeType != null) {
                // Compare the last segment of the extension against the mime type.
                // If there's a mismatch, discard the entire extension.
                int lastDotIndex = filename.lastIndexOf('.');
                String typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        filename.substring(lastDotIndex + 1));
                if ((typeFromExt != null) && !typeFromExt.equalsIgnoreCase(mimeType)) {
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    if (extension != null) {
                        extension = "." + extension;
                    }
                }
            }
            if (extension == null) {
                extension = filename.substring(dotIndex);
            }
        }

        return extension;
    }

    /**
     * 判断指定路径的文件是否存在
     */
    public static boolean isFileExist(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建目录 return: 如果目录已经存在，或者目录创建成功，返回true；如果目录创建失败，返回false
     */
    public static boolean createFolder(String folderPath) {
        boolean success = false;
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 创建指定文件的目录
     */
    public static void createFileFolder(String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动指定文件到指定的路径
     */
    public static boolean copyFile(String fromPath, String toPath) {
        boolean success;
        // get channels
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fis = new FileInputStream(fromPath);
            fos = new FileOutputStream(toPath);
            fcin = fis.getChannel();
            fcout = fos.getChannel();

            // do the file copy
            fcin.transferTo(0, fcin.size(), fcout);
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                // finish up
                if (fcin != null) {
                    fcin.close();
                }
                if (fcout != null) {
                    fcout.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }

    /**
     * 移动指定文件到指定的路径
     */
    public static boolean moveFile(String fromPath, String toPath) {
        try {
            File fromFile = new File(fromPath);
            File toFile = new File(toPath);
            if (fromFile.exists()) {
                return fromFile.renameTo(toFile);
            } else {
                return false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定路径的文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            return new File(filePath).delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定文件夹中的全部文件
     */
    public static boolean cleanDirectory(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }
        try {
            for (File tempFile : new File(folderPath).listFiles()) {
                if (tempFile.isDirectory()) {
                    cleanDirectory(tempFile.getPath());
                }
                tempFile.delete();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存浏览器中的图片
     */
    public static boolean saveImageFromWebCache(String url, String savePath) {

        // 缓存的文件路径
        String cachePath = null;;
        CacheResult mCacheResult = CacheManager.getCacheFile(url, null);
        if (mCacheResult != null) {
            String fileName = mCacheResult.getLocalPath();
            if (!TextUtils.isEmpty(fileName)) {
                cachePath = CacheManager.getCacheFileBaseDir() + File.separator + fileName;
                // 如果存在缓存，则直接从缓存复制保持图片
                if (FileUtils.copyFile(cachePath, savePath)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 这个是手机内存的总空间大小
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    // 这个是手机内存的可用空间大小
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    // 这个是外部存储的总空间大小
    public static long getAvailableExternalMemorySize(Context context) {
        long availableExternalMemorySize = 0;
        if (isSdcardValid(context)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableExternalMemorySize = availableBlocks * blockSize;
        } else {
            availableExternalMemorySize = -1;
        }
        return availableExternalMemorySize;
    }

    // 这个是外部存储的总空间大小
    public static long getTotalExternalMemorySize(Context context) {
        long totalExternalMemorySize = 0;
        if (isSdcardValid(context)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalExternalMemorySize = totalBlocks * blockSize;
        } else {
            totalExternalMemorySize = -1;
        }

        return totalExternalMemorySize;
    }

    /* 返回为字符串数组[0]为大小[1]为单位KB或MB */
    public static String fileSize(long size) {
        String str = "";
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
            }
        }
        DecimalFormat formatter = new DecimalFormat();
        /* 每3个数字用,分隔如：1,000 */
        formatter.setGroupingSize(3);
        return formatter.format(size) + str;
    }

    /**
     * 将指定内容写入Log（指定目录）
     * */
    public static boolean writeFile(String filePath, String content, boolean append) {

        try {
            FileWriter fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从指定位置读取Log（指定目录）
     * */
    public static String readFile(String filePath) {

        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            // 将字符列表转换成字符串
            content = sb.toString();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 写测试日志数据
     * */
    public static void writeTestLog(String content) {
        if (isSdcardValidNoToast()) {
            writeFile(getAppFolderPath() + "http_log.txt", content.concat(","), true);
        }
    }

    /**
     * 读测试日志数据
     * */
    public static String readTestLog(Context context) {
        if (isSdcardValidNoToast()) {
            return readFile(getAppFolderPath() + "http_log.txt");
        }
        return null;
    }

    /**
     * 将Gzip压缩过的内容写入Log（指定文件名）
     * */
    public static boolean writeGzipFile(Context context, String filePath, String content) {
        File file;
        file = new File(filePath);

        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        try {
            fos = new FileOutputStream(file, false);
            gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            gos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gos.finish();
                gos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 写测试日志数据
     * */
    public static void deleteTestLog() {
        if (isSdcardValidNoToast()) {
            deleteFile(getAppFolderPath() + "http_log.txt");
        }
    }

    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file, long byteLen) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = byteLen;
        if (length == 0) {
            length = file.length();
        }

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("ensure that file is not larger than Integer.MAX_VALUE "
                    + file.getName());
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static void saveFile(String filePath, Bitmap bitmap) throws FileNotFoundException {

        File imageFile = new File(filePath);

        FileOutputStream fos = new FileOutputStream(imageFile);

        final BufferedOutputStream bos = new BufferedOutputStream(fos, 16384);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        try {
            bos.flush();
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<String> getAssetFileByLine(Context context, String filename) {
        InputStream file;
        try {
            file = context.getAssets().open(filename);
            if (file == null) {
                return null;
            }
            return parseConfigFile(new InputStreamReader(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 读配置文件，获取配置列表，可能为null */
    private static List<String> parseConfigFile(Reader in) {
        List<String> configList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(in, 1024);
            String line;
            while ((line = br.readLine()) != null) {
                configList.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            configList = null;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                // ignore
            }
        }

        if (configList.size() > 0)
            return configList;
        else
            return null;
    }
}
