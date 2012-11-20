package cn.babysee.picture.draw;

import java.io.FileNotFoundException;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cn.babysee.picture.MediaPlayHelper;
import cn.babysee.picture.ResourcesHelper;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class DrawBoardView extends View {
    private String TAG = "DrawBoardView";
    private boolean DEBUG = AppEnv.DEBUG;

    private Paint mPaint;

    private static final float MINP = 0.25f;

    private static final float MAXP = 0.75f;

    private MaskFilter mEmboss;

    private MaskFilter mBlur;

    private Path mPath;

    private boolean isClearMode = false;

    private Context mContext;
    private MediaPlayHelper mediaPlay;

    private int mBgColor = Color.WHITE;
    private int mPaintColor = Color.RED;

    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Bitmap tempBitmap;
    private Canvas temptCanvas;
    private int[] colors = AppEnv.COLORS;
    //声音开关
    private boolean isSoundEnabled = true;
    private int mStrokeWidth = 14;
    private int soundIndex;

    public DrawBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private int soundsLen = 0;
    private Random random;
    private void init(Context context) {
        mContext = context;
        //产生随机背景
        random = new Random(System.currentTimeMillis());
        mBgColor = colors[random.nextInt(11)];
        mPaintColor = colors[random.nextInt(11)];
        if (mBgColor == mPaintColor) {
            mPaintColor = colors[random.nextInt(11)];
        }

        if (DEBUG)
            Log.d(TAG, "" + AppEnv.screenWeight + " " + AppEnv.screenHeight);
        
        newDraw();

        mediaPlay = new MediaPlayHelper(mContext);
        
        int[] soundIds = ResourcesHelper.getSoundList(3);
        mediaPlay.setSounds(soundIds);
        soundsLen = soundIds.length-1;
    }

    private void newDraw() {
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mBitmap = Bitmap.createBitmap(AppEnv.screenWeight, AppEnv.screenHeight, Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBgColor);

        tempBitmap = Bitmap.createBitmap(AppEnv.screenWeight, AppEnv.screenHeight, Config.ARGB_8888);
        temptCanvas = new Canvas(tempBitmap);
        temptCanvas.drawColor(Color.TRANSPARENT);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);

        mPath = new Path();
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }
    
    public void setStrokeWidth(int size) {
        mStrokeWidth = size;
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        mCanvas.drawColor(mBgColor);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;

    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        radomPlaySound();

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mCanvas.drawColor(mBgColor);

        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        //        mCanvas.drawPath(mPath, mPaint);
        temptCanvas.drawPath(mPath, mPaint);
        mCanvas.drawBitmap(tempBitmap, 0, 0, null);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            touch_up();
            invalidate();
            break;
        }
        return true;
    }

    //设置画笔颜色
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    //获取画笔颜色
    public int getPaintColor() {
        return mPaint.getColor();
    }

    //设置背景颜色
    public void setCanvasColor(int color) {
        mBgColor = color;
        mCanvas.drawColor(mBgColor);
        mCanvas.drawBitmap(tempBitmap, 0, 0, null);
        invalidate();
    }
    
    //清除
    public void clear() {
        newDraw();
        invalidate();
    }

    //    如果现在要画橡皮的痕迹，那么先要设置画笔的颜色mPaint.setColor(Color.BLACK);这里只要不设置成Color.TRANSPARENT透明色就行，
    //    颜色任意；再设置画笔的模式paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));这一步非常重要，它的作用是用此画笔后，画笔划过的痕迹就变成透明色了。
    //    画笔设置好了后，就可以调用该画笔进行橡皮痕迹的绘制了，例如temptCanvas.drawPath(eraPath,mPaint);
    //橡皮
    public void setXfermodeClear() {
        if (isClearMode) {
            mPaint.setXfermode(null);
            isClearMode = false;
            mPaint.setColor(mPaintColor);
        } else {
            isClearMode = true;
            //            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mPaint.setColor(Color.RED);
        }
    }
    
    public boolean isClearMode() {
        return isClearMode;
    }

    public void setMaskFilterEmboss() {
        if (mPaint.getMaskFilter() != mEmboss) {
            mPaint.setMaskFilter(mEmboss);
//        } else {
//            mPaint.setMaskFilter(null);
        }
    }

    public void setMaskFilterBlur() {
        if (mPaint.getMaskFilter() != mBlur) {
            mPaint.setMaskFilter(mBlur);
//        } else {
//            mPaint.setMaskFilter(null);
        }
    }
    
    public void setNormal() {
        mPaint.setMaskFilter(null);
        mPaint.setXfermode(null);
        isClearMode = false;
        mPaint.setColor(mPaintColor);
    }
    
    public void setSoundEnabled(boolean enabled) {
        isSoundEnabled = enabled;
    }
    
    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    //随机播放声音
    private void radomPlaySound() {
        if (!isSoundEnabled) {
            return;
        }
        soundIndex = random.nextInt((soundsLen));
        mediaPlay.playSound(soundIndex);
    }

    //保存画图
    public void savePic(String filePath) {
        if (DEBUG)
            Log.d(TAG, "savePic: " + filePath);

        if (FileUtils.isSdcardValid(mContext)) {
            try {
                FileUtils.saveFile(filePath, mBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
