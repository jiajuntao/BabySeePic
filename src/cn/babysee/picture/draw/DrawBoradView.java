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
import android.view.MotionEvent;
import android.view.View;
import cn.babysee.picture.MediaPlayHelper;
import cn.babysee.picture.ResourcesHelper;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class DrawBoradView extends View {
    private Paint mPaint;

    private static final float MINP = 0.25f;

    private static final float MAXP = 0.75f;

    private MaskFilter mEmboss;

    private MaskFilter mBlur;

    private Path mPath;

    private boolean isClearMode = false;

    private Context mContext;
    private MediaPlayHelper mediaPlay;
    private int[] colors = { Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY,
            Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW };

    private int mBgColor = Color.WHITE;
    private int mPaintColor = Color.RED;
    /*    1.将背景图片在屏幕的canvas画布上先画好，例如canvas.drawBitmap(bgBitmap,0,0,null);
    
        2.新建一个Bitmap，例如Bitmap tempBitmap=Bitmap.createBitmap(100,100,Config.ARGB_4444);并以此Bitmap新建一个临时画布canvas例如：Canvas temptCanvas=new Canvas(tempBitmap);
    然后再执行一步把tempBitmap的背景色画成透明的temptCanvas.drawColor(Color.TRANSPARENT);这样做的目的是把新建的那个临时画布的目标定位在哪个tempBitmap上，这样做以后，
    调用temptCanvas的一切draw函数，都会把相应的图像画在临时的tempBitmap上，而不是
        在原先的屏幕上。
    
        3.临时画布temptCanvas和临时Bitmap建好后，下面就是开始绘画了，要注意的是现在的画点，画线什么的都是调用temptCanvas而不是原先屏幕上的canvas，
        比如应该是temptCanvas.drawPoint ,temptCanvasRect, temptCanvas.drawLine等等，如果现在要画橡皮的痕迹，那么先要设置画笔的颜色mPaint.setColor(Color.BLACK);这里只要不设置成Color.TRANSPARENT透明色就行，
        颜色任意；再设置画笔的模式paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));这一步非常重要，它的作用是用此画笔后，画笔划过的痕迹就变成透明色了。
        画笔设置好了后，就可以调用该画笔进行橡皮痕迹的绘制了，例如temptCanvas.drawPath(eraPath,mPaint);
    
        4.在所有的画笔痕迹和橡皮痕迹绘制完成后，执行最后一步，canvas.drawBitmap(tempBitmap,0,0,null);这里要注意的是canvas而不是temptCanvas了！temptCanvas负责的是将各种画笔痕迹画在tempBitmap上，
        而canvas负责将tempBitmap绘制到屏幕上。
        */
    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Bitmap tempBitmap;
    private Canvas temptCanvas;

    public DrawBoradView(Context c) {
        super(c);
        mContext = c;
        //产生随机背景
        Random random = new Random(System.currentTimeMillis());
        mBgColor = colors[random.nextInt(11)];
        mPaintColor = colors[random.nextInt(11)];
        if (mBgColor == mPaintColor) {
            mPaintColor = colors[random.nextInt(11)];
        }

        mBitmap = Bitmap.createBitmap(AppEnv.screenWeight, AppEnv.screenHeight, Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBgColor);

        tempBitmap = Bitmap.createBitmap(AppEnv.screenWeight, AppEnv.screenHeight, Config.ARGB_8888);
        temptCanvas = new Canvas(tempBitmap);
        temptCanvas.drawColor(Color.TRANSPARENT);

        //        this.setBackgroundColor(mBgColor);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(14);

        mPath = new Path();
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        mediaPlay = new MediaPlayHelper(mContext);
        mediaPlay.setSounds(ResourcesHelper.getSoundList(3));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

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
        radomPlaySound();

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

        mCanvas.drawColor(color);
        mCanvas.drawBitmap(tempBitmap, 0, 0, null);
        invalidate();
    }

    public void setXfermodeClear() {
        if (isClearMode) {
            mPaint.setXfermode(null);
            isClearMode = false;
        } else {
            isClearMode = true;
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    public void setMaskFilterEmboss() {
        if (mPaint.getMaskFilter() != mEmboss) {
            mPaint.setMaskFilter(mEmboss);
        } else {
            mPaint.setMaskFilter(null);
        }
    }

    public void setMaskFilterBlur() {
        if (mPaint.getMaskFilter() != mBlur) {
            mPaint.setMaskFilter(mBlur);
        } else {
            mPaint.setMaskFilter(null);
        }
    }

    //随机播放声音
    private void radomPlaySound() {
        Random random = new Random();
        mediaPlay.playSound(random.nextInt(10));
    }

    //保存画图
    public void savePic(String filePath) {

        if (FileUtils.isSdcardValid(mContext)) {
            try {
                FileUtils.saveFile(filePath, mBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
