package cn.babysee.picture.draw;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.TextView;
import cn.babysee.base.BaseStatActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.utils.FileUtils;
import cn.babysee.utils.Utils;

import com.baidu.mobstat.StatService;

public class DrawBoardActivity extends BaseStatActivity implements View.OnClickListener {

    private String TAG = "DrawBoardActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private static final int DIALOG_TEXT_ENTRY = 0;

    private DrawBoardView mDrawBoardView = null;

    private String filePath = FileUtils.getImageFolderPath();

    private String fileName;

    private EditText picName;

    private GridView mColorView;

    private View mSizeView;

    private View mStyleView;

    private SlidingDrawer slidingDrawer;

    private View mSoundIcView;

    private View mBrushView;

    private View mBgColorView;

    private View mEraserView;

    private SeekBar mSeekBar;

    private TextView mBrushSize;

    public static enum ColorType {
        PEN, BG, NONE
    };

    public static enum BrushType {
        NORMAL, EMBOSS, BLUR
    };

    private BrushType mBrushType = BrushType.NORMAL;

    private ColorType mColorType = ColorType.NONE;

    private ColorsAdapter mColorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppEnv.initScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_board);
        mDrawBoardView = (DrawBoardView) findViewById(R.id.draw_board);
        mSoundIcView = findViewById(R.id.draw_sound_ic);
        mBrushSize = (TextView) findViewById(R.id.draw_brush_size);
        mSeekBar = (SeekBar) findViewById(R.id.draw_brush_size_seekBar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBrushSize.setText(String.valueOf(progress));
                mDrawBoardView.setStrokeWidth(progress);
            }
        });

        mSizeView = findViewById(R.id.draw_brush_size_view);
        mStyleView = findViewById(R.id.draw_style_view);

        mBrushView = findViewById(R.id.draw_pen);
        mBgColorView = findViewById(R.id.draw_bg);
        mEraserView = findViewById(R.id.draw_eraser);

        mBrushView.setOnClickListener(this);
        mBgColorView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        findViewById(R.id.draw_board).setOnClickListener(this);
        findViewById(R.id.draw_clear).setOnClickListener(this);
        findViewById(R.id.draw_list).setOnClickListener(this);
        findViewById(R.id.draw_save).setOnClickListener(this);
        findViewById(R.id.style_normal).setOnClickListener(this);
        findViewById(R.id.style_emboss).setOnClickListener(this);
        findViewById(R.id.style_blur).setOnClickListener(this);
        findViewById(R.id.draw_sound).setOnClickListener(this);

        mColorsAdapter = new ColorsAdapter(this);
        mColorView = (GridView) findViewById(R.id.colors);
        mColorView.setAdapter(mColorsAdapter);
        mColorView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int color = mColorsAdapter.getItem(position);
                if (mColorType == ColorType.PEN) {
                    mDrawBoardView.setPaintColor(color);
                } else if (mColorType == ColorType.BG) {
                    mDrawBoardView.setCanvasColor(color);
                }
                //                slidingDrawer.toggle();
                //                mColorType = ColorType.NONE;
                //
                //                mColorView.setVisibility(View.GONE);
                //                mStyleView.setVisibility(View.GONE);
                //                mSizeView.setVisibility(View.GONE);
                //                mBrushView.setBackgroundDrawable(null);
                //                mBgColorView.setBackgroundDrawable(null);
            }
        });

        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                //                mColorView.setVisibility(View.GONE);
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
                return new AlertDialog.Builder(DrawBoardActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dialog_save_pic_title)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.dialog_ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if (!TextUtils.isEmpty(picName.getText())) {
                                            fileName = picName.getText().toString().trim();
                                        }
                                        mDrawBoardView.savePic(filePath + fileName + ".jpg");
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
                fileName = Utils.getFileName();
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
                if (mColorType == ColorType.BG) {
                    mColorType = ColorType.NONE;
                }

                mBgColorView.setBackgroundDrawable(null);
                mEraserView.setBackgroundDrawable(null);
                //取消橡皮
                if (mDrawBoardView.isClearMode()) {
                    mDrawBoardView.setXfermodeClear();
                }

                if (mColorType == ColorType.NONE) {
                    mColorType = ColorType.PEN;
                    v.setBackgroundResource(R.drawable.ic_draw_selected_bg);
                    mColorView.setVisibility(View.VISIBLE);
                    mStyleView.setVisibility(View.VISIBLE);
                    mSizeView.setVisibility(View.VISIBLE);
                } else {
                    v.setBackgroundDrawable(null);
                    mColorType = ColorType.NONE;
                    mColorView.setVisibility(View.GONE);
                    mStyleView.setVisibility(View.GONE);
                    mSizeView.setVisibility(View.GONE);
                }
                break;
            case R.id.draw_bg:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_BG_COLOR_LABEL, 1);

                mBrushView.setBackgroundDrawable(null);
                mEraserView.setBackgroundDrawable(null);
                mStyleView.setVisibility(View.GONE);
                mSizeView.setVisibility(View.GONE);

                if (mColorType == ColorType.PEN) {
                    mColorType = ColorType.NONE;
                }

                if (mColorType == ColorType.NONE) {
                    mColorType = ColorType.BG;
                    v.setBackgroundResource(R.drawable.ic_draw_selected_bg);
                    mColorView.setVisibility(View.VISIBLE);
                } else {
                    mColorView.setVisibility(View.GONE);
                    mColorType = ColorType.NONE;
                    v.setBackgroundDrawable(null);
                }
                break;
            case R.id.draw_eraser:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_ERASER_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_ERASER_LABEL, 1);

                mBrushView.setBackgroundDrawable(null);
                mBgColorView.setBackgroundDrawable(null);
                mColorView.setVisibility(View.GONE);
                mStyleView.setVisibility(View.GONE);
                mSizeView.setVisibility(View.GONE);

                mDrawBoardView.setXfermodeClear();
                if (mDrawBoardView.isClearMode()) {
                    v.setBackgroundResource(R.drawable.ic_draw_selected_bg);
                    mSizeView.setVisibility(View.VISIBLE);
                } else {
                    v.setBackgroundDrawable(null);
                    mSizeView.setVisibility(View.GONE);
                }
                break;
                
            case R.id.style_normal:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_STYLE_NORMAL_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_STYLE_NORMAL_LABEL, 1);
                mDrawBoardView.setNormal();
                break;
            case R.id.style_emboss:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_STYLE_EMBOSS_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_STYLE_EMBOSS_LABEL, 1);
                mDrawBoardView.setMaskFilterEmboss();
                break;
            case R.id.style_blur:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_STYLE_BLUR_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_STYLE_BLUR_LABEL, 1);
                mDrawBoardView.setMaskFilterBlur();
                break;
            case R.id.draw_clear:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_DRAW_CLEAR_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_DRAW_CLEAR_LABEL, 1);
                mDrawBoardView.clear();
                break;
            case R.id.draw_sound:
                StatService.onEvent(mContext, StatServiceEnv.DRAWBOARD_MENU_DRAW_SOUND_EVENT_ID,
                        StatServiceEnv.DRAWBOARD_MENU_DRAW_SOUND_LABEL, 1);

                if (mDrawBoardView.isSoundEnabled()) {
                    mSoundIcView.setEnabled(false);
                    mDrawBoardView.setSoundEnabled(false);
                } else {
                    mSoundIcView.setEnabled(true);
                    mDrawBoardView.setSoundEnabled(true);
                }

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
}
