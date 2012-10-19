/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.babysee.utils;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Color.WHITE;
import static android.graphics.PorterDuff.Mode.DST_IN;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * Image utilities
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * Get a bitmap from the image path
     *
     * @param imagePath
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final String imagePath) {
        return getBitmap(imagePath, 1);
    }

    /**
     * Get a bitmap from the image path
     *
     * @param imagePath
     * @param sampleSize
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final String imagePath, int sampleSize) {
        final Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            return BitmapFactory.decodeFileDescriptor(file.getFD(), null,
                    options);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
        }
    }

    /**
     * Get size of image
     *
     * @param imagePath
     * @return size
     */
    public static Point getSize(final String imagePath) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            BitmapFactory.decodeFileDescriptor(file.getFD(), null, options);
            return new Point(options.outWidth, options.outHeight);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                }
        }
    }

    /**
     * Get bitmap with maximum height or width
     *
     * @param imagePath
     * @param width
     * @param height
     * @return image
     */
    public static Bitmap getBitmap(final String imagePath, int width, int height) {
        Point size = getSize(imagePath);
        int currWidth = size.x;
        int currHeight = size.y;

        int scale = 1;
        while (currWidth >= width || currHeight >= height) {
            currWidth /= 2;
            currHeight /= 2;
            scale *= 2;
        }

        return getBitmap(imagePath, scale);
    }

    /**
     * Get bitmap with maximum height or width
     *
     * @param image
     * @param width
     * @param height
     * @return image
     */
    public static Bitmap getBitmap(final File image, int width, int height) {
        return getBitmap(image.getAbsolutePath(), width, height);
    }

    /**
     * Get a bitmap from the image file
     *
     * @param image
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final File image) {
        return getBitmap(image.getAbsolutePath());
    }

    /**
     * Load a {@link Bitmap} from the given path and set it on the given
     * {@link ImageView}
     *
     * @param imagePath
     * @param view
     */
    public static void setImage(final String imagePath, final ImageView view) {
        setImage(new File(imagePath), view);
    }

    /**
     * Load a {@link Bitmap} from the given {@link File} and set it on the given
     * {@link ImageView}
     *
     * @param image
     * @param view
     */
    public static void setImage(final File image, final ImageView view) {
        Bitmap bitmap = getBitmap(image);
        if (bitmap != null)
            view.setImageBitmap(bitmap);
    }

    /**
     * Round the corners of a {@link Bitmap}
     *
     * @param source
     * @param radius
     * @return rounded corner bitmap
     */
    public static Bitmap roundCorners(final Bitmap source, final float radius) {
        int width = source.getWidth();
        int height = source.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(WHITE);

        Bitmap clipped = Bitmap.createBitmap(width, height, ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
                paint);
        paint.setXfermode(new PorterDuffXfermode(DST_IN));

        Bitmap rounded = Bitmap.createBitmap(width, height, ARGB_8888);
        canvas = new Canvas(rounded);
        canvas.drawBitmap(source, 0, 0, null);
        canvas.drawBitmap(clipped, 0, 0, paint);

        source.recycle();
        clipped.recycle();

        return rounded;
    }
    
    //start
    static final int THUMBNAIL_TARGET_SIZE = 320;

    static final int MINI_THUMB_TARGET_SIZE = 96;

    static final int THUMBNAIL_MAX_NUM_PIXELS = 512 * 384;

    static final int MINI_THUMB_MAX_NUM_PIXELS = 128 * 128;

    static final int UNCONSTRAINED = -1;

    public static final boolean ROTATE_AS_NEEDED = true;

    public static final boolean NO_ROTATE = false;

    public static final boolean USE_NATIVE = true;

    public static final boolean NO_NATIVE = false;

    public static final int DIRECTION_LEFT = 0;

    public static final int DIRECTION_RIGHT = 1;

    public static final int DIRECTION_UP = 2;

    public static final int DIRECTION_DOWN = 3;

    private static OnClickListener sNullOnClickListener;

    private ImageUtils() {
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees) {
        if ((degrees != 0) && (b != null)) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    /*
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage.
     * 
     * The function returns a sample size based on the constraints. Both size
     * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
     * indicates no care of the corresponding constraint. The functions prefers
     * returning a sample size that generates a smaller bitmap, unless
     * minSideLength = IImage.UNCONSTRAINED.
     * 
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    // Whether we should recycle the input (unless the output is the input).
    public static final boolean RECYCLE_INPUT = true;

    public static final boolean NO_RECYCLE_INPUT = false;

    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight,
            boolean scaleUp, boolean recycle) {
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && ((deltaX < 0) || (deltaY < 0))) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target. Transform it by placing as much of the image as
             * possible into the target and leaving the top/bottom or left/right
             * (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
                    + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if ((scale < .9F) || (scale > 1F)) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if ((scale < .9F) || (scale > 1F)) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler,
                    true);
        } else {
            b1 = source;
        }

        if (recycle && (b1 != source)) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        if (b2 != b1) {
            if (recycle || (b1 != source)) {
                b1.recycle();
            }
        }

        return b2;
    }

    public static <T> int indexOf(T[] array, T s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public static void closeSilently(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void closeSilently(ParcelFileDescriptor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    /**
     * Make a mini bitmap from a given filePath.
     * 
     * @param uri
     * @throws Exception
     */
    public static Bitmap makeMiNiBitmap(String filePath) throws Exception {

        return resampleImage(filePath, THUMBNAIL_TARGET_SIZE);
    }

    /**
     * Make a bitmap from a given Uri.
     * 
     * @param uri
     */
    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri,
            ContentResolver cr, boolean useNative) {
        ParcelFileDescriptor input = null;
        try {
            input = cr.openFileDescriptor(uri, "r");
            BitmapFactory.Options options = null;
            if (useNative) {
                options = createNativeAllocOptions();
            }
            return makeBitmap(minSideLength, maxNumOfPixels, uri, cr, input, options);
        } catch (IOException ex) {
            return null;
        } finally {
            closeSilently(input);
        }
    }

    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels,
            ParcelFileDescriptor pfd, boolean useNative) {
        BitmapFactory.Options options = null;
        if (useNative) {
            options = createNativeAllocOptions();
        }
        return makeBitmap(minSideLength, maxNumOfPixels, null, null, pfd, options);
    }

    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri,
            ContentResolver cr, ParcelFileDescriptor pfd, BitmapFactory.Options options) {
        try {
            if (pfd == null) {
                pfd = makeInputStream(uri, cr);
            }
            if (pfd == null) {
                return null;
            }
            if (options == null) {
                options = new BitmapFactory.Options();
            }

            FileDescriptor fd = pfd.getFileDescriptor();
            options.inJustDecodeBounds = true;
            BitmapManager.instance().decodeFileDescriptor(fd, options);
            if (options.mCancel || (options.outWidth == -1) || (options.outHeight == -1)) {
                return null;
            }
            options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapManager.instance().decodeFileDescriptor(fd, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            closeSilently(pfd);
        }
        return null;
    }

    private static ParcelFileDescriptor makeInputStream(Uri uri, ContentResolver cr) {
        try {
            return cr.openFileDescriptor(uri, "r");
        } catch (IOException ex) {
            return null;
        }
    }

    public static synchronized OnClickListener getNullOnClickListener() {
        if (sNullOnClickListener == null) {
            sNullOnClickListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            };
        }
        return sNullOnClickListener;
    }

    public static void Assert(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    public static boolean equals(String a, String b) {
        // return true if both string are null or the content equals
        return (a == b) || a.equals(b);
    }

    // Returns Options that set the puregeable flag for Bitmap decode.
    public static BitmapFactory.Options createNativeAllocOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inNativeAlloc = true;
        return options;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = (float) height / (float) width;
        int newHeight = (int) (newWidth * temp);
        float scaleWidth = (float) newWidth / width;
        float scaleHeight = (float) newHeight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, newHeight, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static int getResizeBitmapHeight(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return getResizeBitmapHeight(width, height, newWidth);
    }

    public static int getResizeBitmapHeight(int width, int height, int newWidth) {
        float temp = (float) height / (float) width;
        return (int) (newWidth * temp);
    }

    public static Bitmap getBitmapFormFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    public static void resampleImageAndSaveToNewLocation(String pathInput, String pathOutput)
            throws Exception {
        Bitmap bmp = resampleImage(pathInput, 640);

        OutputStream out = new FileOutputStream(pathOutput);
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
    }

    public static void resampleImageAndSaveToNewLocation(String pathInput, String pathOutput,
            int maxDim, int quality) throws Exception {
        Bitmap bmp = resampleImageByWidth(pathInput, maxDim);

        OutputStream out = new FileOutputStream(pathOutput);
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, out);
        out.flush();
        out.close();
    }

    //width 500px, height 900px
    private static int getClosestResampleSizeByWidth(int cx, int cy, int maxDim) {

        if ((cx < 500) && (cy < 900)) {
            return 1;
        }

        int max = Math.max(cx, cy);
        if ((cx > 500) && (cy <= 900)) {
            max = cx;
            maxDim = 500;
        } else if ((cx <= 500) && (cy > 900)) {
            max = cy;
            maxDim = 900;
        } else if ((cx > 500) && (cy > 900)) {
            float f = (float) cy / cx;
            if (f >= 1.8) {
                max = cy;
                maxDim = 900;
            } else {
                max = cx;
                maxDim = 500;
            }
        }

        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    public static boolean isBitmap(String path) {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);
        if (bfo.outHeight > 0 && bfo.outWidth > 0) {
            return true;
        }

        return false;
    }

    public static Bitmap resampleImageByWidth(String path, int maxDim) throws Exception {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSizeByWidth(bfo.outWidth, bfo.outHeight,
                maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if ((bmpt.getWidth() > maxDim) || (bmpt.getHeight() > maxDim)) {
            BitmapFactory.Options optsScale = getResamplingByWidth(bmpt.getWidth(),
                    bmpt.getHeight(), maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk > 4) {
            int rotation = ExifUtils.getExifRotation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }

    private static BitmapFactory.Options getResamplingByWidth(int cx, int cy, int maxDim) {

        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        if ((cx > 500) && (cy <= 900)) {
            scaleVal = (float) 500 / (float) cx;
        } else if ((cx <= 500) && (cy > 900)) {
            scaleVal = (float) 900 / (float) cy;
        } else if ((cx > 500) && (cy > 900)) {
            float f = (float) cy / cx;
            if (f >= 1.8) {
                scaleVal = (float) 900 / (float) cy;
            } else {
                scaleVal = (float) 500 / (float) cx;
            }
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    public static Bitmap resampleImage(String path, int maxDim) throws Exception {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if ((bmpt.getWidth() > maxDim) || (bmpt.getHeight() > maxDim)) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(),
                    maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk > 4) {
            int rotation = ExifUtils.getExifRotation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }

    public void writeFileEx(String sourceFilePath, File outFileDir, int maxLength) {
        try {
            //获取源图片的大小
            Bitmap bm;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //当opts不为null时，但decodeFile返回空，不为图片分配内存，只获取图片的大小，并保存在opts的outWidth和outHeight
            BitmapFactory.decodeFile(sourceFilePath, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int destWidth = 0;
            int destHeight = 0;
            //缩放的比例
            double ratio = 0.0;
            //tvInfo是一个TextView用于显示图片的尺寸信息
            System.out.println("Width:" + srcWidth + " Height:" + srcHeight);
            //按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
            if (srcWidth > srcHeight) {
                ratio = srcWidth / maxLength;
                destWidth = maxLength;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = srcHeight / maxLength;
                destHeight = maxLength;
                destWidth = (int) (srcWidth / ratio);
            }
            //对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            //inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            //设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            //添加尺寸信息，
            System.out.println("\nWidth:" + newOpts.outWidth + " Height:" + newOpts.outHeight);
            //获取缩放后图片
            Bitmap destBm = BitmapFactory.decodeFile(sourceFilePath, newOpts);

            if (destBm == null) {
                System.out.println("Create Failed");
            } else {
                //文件命名，通过GUID可避免命名的重复
                String fileName = java.util.UUID.randomUUID().toString() + ".jpg";
                //另外定义：
                //ConfigManager.photoDir = getFileStreamPath(photoDirName) 
                //String photoDirName = "photo";要注意是根目录
                File destFile = new File(outFileDir, fileName);
                //创建文件输出流
                OutputStream os = new FileOutputStream(destFile);
                //存储
                destBm.compress(CompressFormat.JPEG, 100, os);
                //关闭流
                os.close();
                //显示图片
                //setImgView(fileName);
                //setDrawable(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        } else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        } else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    private static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);

        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    public static BitmapFactory.Options getBitmapDims(String path) throws Exception {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);
        return bfo;
    }

    private static final long POLY64REV = 0x95AC9329AC4BC9B5L;

    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

    private static boolean init = false;

    private static long[] CRCTable = new long[256];

    /**
     * A function thats returns a 64-bit crc for string
     * 
     * @param in : input string
     * @return 64-bit crc value
     */
    public static final long Crc64Long(String in) {
        if (in == null || in.length() == 0) {
            return 0;
        }
        // http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
        long crc = INITIALCRC, part;
        if (!init) {
            for (int i = 0; i < 256; i++) {
                part = i;
                for (int j = 0; j < 8; j++) {
                    int value = ((int) part & 1);
                    if (value != 0) part = (part >> 1) ^ POLY64REV;
                    else part >>= 1;
                }
                CRCTable[i] = part;
            }
            init = true;
        }
        int length = in.length();
        for (int k = 0; k < length; ++k) {
            char c = in.charAt(k);
            crc = CRCTable[(((int) crc) ^ c) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }

    /**
     * detect bytes's image type
     * 
     * @param bytes 2~8 byte at beginning of the image file
     * @return image mimetype or null if the file is not image
     */
    public static String getImageType(String filePath) {

        if (filePath == null) {
            return null;
        }

        File file = new File(filePath);

        if (file.exists()) {
            try {
                return getImageType(FileUtils.getBytesFromFile(file, 10));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * detect bytes's image type
     * 
     * @param bytes 2~8 byte at beginning of the image file
     * @return image mimetype or null if the file is not image
     */
    public static String getImageType(byte[] bytes) {
        if (isJPEG(bytes)) {
            return "image/jpeg";
        }
        if (isGIF(bytes)) {
            return "image/gif";
        }
        if (isPNG(bytes)) {
            return "image/png";
        }
        if (isBMP(bytes)) {
            return "application/x-bmp";
        }
        return null;
    }

    private static boolean isJPEG(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    public static boolean isGIF(String type) {

        return "image/gif".equals(type);
    }

    private static boolean isGIF(byte[] b) {
        if (b.length < 6) {
            return false;
        }
        return (b[0] == 'G') && (b[1] == 'I') && (b[2] == 'F') && (b[3] == '8')
                && ((b[4] == '7') || (b[4] == '9')) && (b[5] == 'a');
    }

    private static boolean isPNG(byte[] b) {
        if (b.length < 8) {
            return false;
        }
        return ((b[0] == (byte) 137) && (b[1] == (byte) 80) && (b[2] == (byte) 78)
                && (b[3] == (byte) 71) && (b[4] == (byte) 13) && (b[5] == (byte) 10)
                && (b[6] == (byte) 26) && (b[7] == (byte) 10));
    }

    private static boolean isBMP(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }
}
