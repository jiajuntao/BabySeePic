package cn.babysee.picture.draw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.TextView;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.utils.FileUtils;

import com.baidu.mobstat.StatService;

public class DrawBoardActivity extends GraphicsActivity implements View.OnClickListener {

    private String TAG = "DrawBoardActivity";
    private boolean DEBUG = AppEnv.DEBUG;

    private static final int DIALOG_TEXT_ENTRY = 0;
    private Context mContext;

    private DrawBoardView myView = null;

    private String filePath = FileUtils.getImageFolderPath();

    private String fileName;

    private EditText picName;
    private GridView colorView;
    private SlidingDrawer slidingDrawer;

    public static enum ColorType {
        PEN, BG, NONE
    };

    private ColorType mColorType;
    private ColorsAdapter mColorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppEnv.initScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_board);
        mContext = getApplicationContext();
        myView = (DrawBoardView) findViewById(R.id.draw_board);
        findViewById(R.id.draw_pen).setOnClickListener(this);
        findViewById(R.id.draw_bg).setOnClickListener(this);
        findViewById(R.id.draw_board).setOnClickListener(this);
        findViewById(R.id.draw_clear).setOnClickListener(this);
        findViewById(R.id.draw_eraser).setOnClickListener(this);
        findViewById(R.id.draw_list).setOnClickListener(this);
        findViewById(R.id.draw_save).setOnClickListener(this);
//        findViewById(R.id.draw_style).setOnClickListener(this);

        mColorsAdapter = new ColorsAdapter(this);
        colorView = (GridView) findViewById(R.id.colors);
        colorView.setAdapter(mColorsAdapter);
        colorView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int color = mColorsAdapter.getItem(position);
                if (mColorType == ColorType.PEN) {
                    myView.setPaintColor(color);
                } else if (mColorType == ColorType.BG) {
                    myView.setCanvasColor(color);
                }
                slidingDrawer.toggle();
                mColorType = ColorType.NONE;
                toggleColorView();
            }
        });

        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            
            @Override
            public void onDrawerClosed() {
                colorView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_TEXT_ENTRY:
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
            final TextView picPath = (TextView) textEntryView.findViewById(R.id.pic_path);
            picName = (EditText) textEntryView.findViewById(R.id.pic_name);
            picPath.setText(filePath);
            fileName = String.valueOf(System.currentTimeMillis());
            picName.setText(fileName);
            return new AlertDialog.Builder(DrawBoardActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            slidingDrawer.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.draw_pen:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BRUSH_COLOR_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BRUSH_COLOR_LABEL, 1);

            if (toggleColorView()) {
                mColorType = ColorType.PEN;
            }
            
            //            new ColorPickerDialog(this, new OnColorChangedListener() {
            //
            //                @Override
            //                public void colorChanged(int color) {
            //                    myView.setPaintColor(color);
            //                }
            //            }, myView.getPaintColor()).show();
            break;
        case R.id.draw_bg:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_LABEL, 1);
            if (toggleColorView()) {
                mColorType = ColorType.BG;
            }

            //            new ColorPickerDialog(this, new OnColorChangedListener() {
            //
            //                @Override
            //                public void colorChanged(int color) {
            //                    myView.setCanvasColor(color);
            //                }
            //
            //            }, Color.WHITE).show();
            break;
        //        case EMBOSS_MENU_ID:
        //            myView.setMaskFilterEmboss();
        //            break;
        //        case BLUR_MENU_ID:
        //            myView.setMaskFilterBlur();
        //            break;
        case R.id.draw_eraser:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_ERASER_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_ERASER_LABEL, 1);
            myView.setXfermodeClear();
            break;
        case R.id.draw_save:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_SAVE_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_SAVE_LABEL, 1);
            showDialog(DIALOG_TEXT_ENTRY);
            break;
        case R.id.draw_list:
            StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BABYWORKS_EVENT_ID,
                    StatServiceEnv.DRAWBOARD_MENU_BABYWORKS_LABEL, 1);
            startActivity(new Intent(mContext, DrawListActivity.class));
            break;

        default:
            break;
        }
    }

    private boolean toggleColorView() {
        boolean isOpen = true;
        if (colorView.getVisibility() == View.VISIBLE) {
            colorView.setVisibility(View.GONE);
            isOpen = false;
        } else {
            colorView.setVisibility(View.VISIBLE);
        }
        return isOpen;
    }
}
