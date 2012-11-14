package cn.babysee.picture.draw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.draw.ColorPickerDialog.OnColorChangedListener;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.utils.FileUtils;

import com.baidu.mobstat.StatService;

public class DrawBoradActivity extends GraphicsActivity {

    private Context mContext;

    private DrawBoradView myView = null;

    private static final int DIALOG_TEXT_ENTRY = 0;

    private String filePath = FileUtils.getImageFolderPath();

    private String fileName;

    private EditText picName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        super.onCreate(savedInstanceState);

        DisplayMetrics DM = new DisplayMetrics();
        //获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
        getWindowManager().getDefaultDisplay().getMetrics(DM);
        AppEnv.screenWeight = DM.widthPixels;
        AppEnv.screenHeight = DM.heightPixels;

        mContext = getApplicationContext();
        myView = new DrawBoradView(this);
        setContentView(myView);

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
            return new AlertDialog.Builder(DrawBoradActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.dialog_save_pic_title).setView(textEntryView)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (!TextUtils.isEmpty(picName.getText())) {
                                fileName = picName.getText().toString().trim();
                            }
                            myView.savePic(filePath + fileName + ".jpg");
                        }
                    }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

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
        menu.add(0, COLOR_MENU_CANVAS_ID, 0, getString(R.string.menu_color_canvas)).setShortcut('3', 'c');
        //        menu.add(0, EMBOSS_MENU_ID, 0, getString(R.string.menu_emboss)).setShortcut('4', 's');
        //        menu.add(0, BLUR_MENU_ID, 0, getString(R.string.menu_blur)).setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, getString(R.string.menu_erase)).setShortcut('5', 'z');
        //        menu.add(0, SRCATOP_MENU_ID, 0, getString(R.string.menu_srcatop)).setShortcut('5', 'z');
        menu.add(0, SAVE_PIC_MENU_ID, 0, getString(R.string.menu_save_pic)).setShortcut('5', 's');
        menu.add(0, SAVE_PIC_LIST_MENU_ID, 0, getString(R.string.menu_save_pic_list)).setShortcut('5', 's');

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
        //        mPaint.setXfermode(null);
        //        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
        case COLOR_MENU_ID:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BRUSH_COLOR_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BRUSH_COLOR_LABEL, 1);
            new ColorPickerDialog(this, new OnColorChangedListener() {

                @Override
                public void colorChanged(int color) {
                    myView.setPaintColor(color);
                }
            }, myView.getPaintColor()).show();
            return true;
        case COLOR_MENU_CANVAS_ID:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_LABEL, 1);
            new ColorPickerDialog(this, new OnColorChangedListener() {

                @Override
                public void colorChanged(int color) {
                    myView.setCanvasColor(color);
                }

            }, Color.WHITE).show();
            return true;
        case EMBOSS_MENU_ID:
            myView.setMaskFilterEmboss();
            return true;
        case BLUR_MENU_ID:
            myView.setMaskFilterBlur();
            return true;
        case ERASE_MENU_ID:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_ERASER_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_ERASER_LABEL, 1);
            myView.setXfermodeClear();
            return true;
            //        case SRCATOP_MENU_ID:
            //            //画完就清除
            //            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            //            mPaint.setAlpha(0x80);
            //            return true;
        case SAVE_PIC_MENU_ID:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_SAVE_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_SAVE_LABEL, 1);
            showDialog(DIALOG_TEXT_ENTRY);
            return true;
        case SAVE_PIC_LIST_MENU_ID:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BABYWORKS_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BABYWORKS_LABEL, 1);
            startActivity(new Intent(mContext, DrawListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
