package cn.babysee.picture.draw;

import java.io.FileNotFoundException;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.babysee.picture.MediaPlay;
import cn.babysee.picture.R;
import cn.babysee.picture.ResourcesHelper;
import cn.babysee.picture.draw.ColorPickerDialog.OnColorChangedListener;
import cn.babysee.utils.FileUtils;

public class FingerPaint extends GraphicsActivity {

    private Context mContext;

    private MyView myView = null;

    private MediaPlay mediaPlay;

    private int screenWeight;

    private int screenHeight;

    private static final int DIALOG_TEXT_ENTRY = 0;

    private String filePath = FileUtils.getImageFolderPath();

    private String fileName;

    private EditText picName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mediaPlay = new MediaPlay(mContext);
        mediaPlay.setSounds(ResourcesHelper.getSoundList(3));
        DisplayMetrics DM = new DisplayMetrics();
        //获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
        getWindowManager().getDefaultDisplay().getMetrics(DM);
        screenWeight = DM.widthPixels;
        screenHeight = DM.heightPixels;
        myView = new MyView(this);
        setContentView(myView);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setStrokeWidth(14);

        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_TEXT_ENTRY:
                // This example shows how to add a custom layout to an AlertDialog
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
                final TextView picPath = (TextView) textEntryView.findViewById(R.id.pic_path);
                picName = (EditText) textEntryView.findViewById(R.id.pic_name);
                picPath.setText(filePath);
                fileName = String.valueOf(System.currentTimeMillis());
                picName.setText(fileName);
                return new AlertDialog.Builder(FingerPaint.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_save_pic_title)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.dialog_ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if (!TextUtils.isEmpty(picName.getText())) {
                                            fileName = picName.getText().toString().trim();
                                        }
                                        myView.savePic(filePath + fileName + ".jpg");
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dismissDialog(DIALOG_TEXT_ENTRY);
                                    }
                                }).create();
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_TEXT_ENTRY:
                fileName = String.valueOf(System.currentTimeMillis());
                picName.setText(fileName);
                break;
        }
    }

    private Paint mPaint;

    private MaskFilter mEmboss;

    private MaskFilter mBlur;

    private Canvas mCanvas;

    public class MyView extends View {

        private static final float MINP = 0.25f;

        private static final float MAXP = 0.75f;

        private Bitmap mBitmap;

        private Path mPath;

        private Paint mBitmapPaint;

        public MyView(Context c) {
            super(c);

            mBitmap = Bitmap.createBitmap(screenWeight, screenHeight, Bitmap.Config.ARGB_8888);

            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

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
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        //随机播放声音
        private void radomPlaySound() {
            Random random = new Random();
            mediaPlay.playSound(random.nextInt(10));
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

    private static final int COLOR_MENU_ID = Menu.FIRST;

    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;

    private static final int BLUR_MENU_ID = Menu.FIRST + 2;

    private static final int ERASE_MENU_ID = Menu.FIRST + 3;

    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

    private static final int SAVE_PIC_MENU_ID = Menu.FIRST + 5;

    private static final int SAVE_PIC_LIST_MENU_ID = Menu.FIRST + 6;

    private static final int COLOR_MENU_CANVAS_ID = Menu.FIRST + 7;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, COLOR_MENU_ID, 0, getString(R.string.menu_color)).setShortcut('3', 'c');
        menu.add(0, COLOR_MENU_CANVAS_ID, 0, getString(R.string.menu_color_canvas)).setShortcut(
                '3', 'c');
        //        menu.add(0, EMBOSS_MENU_ID, 0, getString(R.string.menu_emboss)).setShortcut('4', 's');
        //        menu.add(0, BLUR_MENU_ID, 0, getString(R.string.menu_blur)).setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, getString(R.string.menu_erase)).setShortcut('5', 'z');
        //        menu.add(0, SRCATOP_MENU_ID, 0, getString(R.string.menu_srcatop)).setShortcut('5', 'z');
        menu.add(0, SAVE_PIC_MENU_ID, 0, getString(R.string.menu_save_pic)).setShortcut('5', 's');
        menu.add(0, SAVE_PIC_LIST_MENU_ID, 0, getString(R.string.menu_save_pic_list)).setShortcut(
                '5', 's');

        /****
         * Is this the mechanism to extend with filter effects? Intent
         * intent = new Intent(null, getIntent().getData());
         * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
         * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new
         * ComponentName(this, NotesList.class), null, intent, 0, null);
         *****/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, new OnColorChangedListener() {
                    
                    @Override
                    public void colorChanged(int color) {
                        mPaint.setColor(color);
                    }
                }, mPaint.getColor()).show();
                return true;
            case COLOR_MENU_CANVAS_ID:
                new ColorPickerDialog(this, new OnColorChangedListener() {

                    @Override
                    public void colorChanged(int color) {
                        mCanvas.drawColor(color);
//                        myView.invalidate();
                    }

                }, Color.WHITE).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                return true;
            case SRCATOP_MENU_ID:
                //画完就清除
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                mPaint.setAlpha(0x80);
                return true;
            case SAVE_PIC_MENU_ID:
                showDialog(DIALOG_TEXT_ENTRY);
                return true;
            case SAVE_PIC_LIST_MENU_ID:
                startActivity(new Intent(mContext, DrawPicListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
