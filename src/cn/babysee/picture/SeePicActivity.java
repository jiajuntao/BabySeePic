package cn.babysee.picture;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.babysee.picture.base.BaseListNavigation;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.SharePref;
import cn.babysee.utils.Utils;

public class SeePicActivity extends BaseListNavigation implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private static final String TAG = "BabySeePicActivity";

    private int[] picIds = null;

    private int[] soundIds = null;

    private int picCount;

    private int currentIndex;

    private int currentPic1Index;

    private int currentPic2Index;

    private boolean isUp;

    private boolean isGoNext = false;

    private MediaPlayHelper mediaPlay;

    private ImageView imageView1;

    private ImageView imageView2;

    private ImageView up;

    private ImageView down;

    private ImageView[] dotViews = new ImageView[6];

    private Handler mHandler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_pic);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setOnClickListener(this);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setOnClickListener(this);
        up = (ImageView) findViewById(R.id.up);
        up.setOnClickListener(this);
        down = (ImageView) findViewById(R.id.down);
        down.setOnClickListener(this);

        dotViews[0] = (ImageView) findViewById(R.id.dot1);
        dotViews[1] = (ImageView) findViewById(R.id.dot2);
        dotViews[2] = (ImageView) findViewById(R.id.dot3);
        dotViews[3] = (ImageView) findViewById(R.id.dot4);
        dotViews[4] = (ImageView) findViewById(R.id.dot5);
        dotViews[5] = (ImageView) findViewById(R.id.dot6);

        int position = SharePref.getInt(mContext, SharePref.SEEPIC_PHASE, 0);
        getSupportActionBar().setSelectedNavigationItem(position);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SharePref.setInt(mContext, SharePref.SEEPIC_PHASE, mNavigationItemPosition);
    }

    @Override
    public void onClick(View v) {
        if (DEBUG) Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.imageView1:
                mediaPlay.playSound(currentPic1Index);
                break;
            case R.id.imageView2:
                mediaPlay.playSound(currentPic2Index);
                break;
            case R.id.up:
                up();
                break;
            case R.id.down:
                down();
                break;

            default:
                break;
        }
    }

    // 手势中坐标的计算
    private int downX, upX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // 只有遇到图片组时，才需要判断是否横向划屏（横向划屏会切换图片）
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                upX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                int totalMove = upX - downX;
                if (totalMove > Utils.dip2px(mContext, 100)) {
                    up();
                    // 向前
                    return true;
                } else if (totalMove < -Utils.dip2px(mContext, 100)) {
                    down();
                    // 向后
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void up() {
        isUp = true;
        if (DEBUG) Log.v(TAG, "up currentPic1Index:" + currentPic1Index);
        if (currentPic1Index == 2) {
            up.setVisibility(View.GONE);
        }
        if (currentPic1Index == 0) {
            return;
        }
        down.setVisibility(View.VISIBLE);
        setImageView(currentPic1Index - 2, currentPic1Index - 1);
        playRadomSound();

    }

    private void down() {
        isUp = false;
        if (DEBUG) Log.v(TAG, "down currentPic2Index:" + currentPic2Index);
        if (currentPic2Index == (picCount - 3)) {
            down.setVisibility(View.GONE);
        }
        if (currentPic2Index == (picCount - 1)) {
            return;
        }
        up.setVisibility(View.VISIBLE);
        setImageView(currentPic2Index + 1, currentPic2Index + 2);
        playRadomSound();
    }

    private void setImageView(int pic1Index, int pic2Index) {
        if (DEBUG) Log.d(TAG, "setImageView pic1Index:" + pic1Index + " pic2Index:" + pic2Index);

        currentPic1Index = pic1Index;
        currentPic2Index = pic2Index;
        imageView1.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        imageView1.setImageResource(picIds[pic1Index]);
        imageView2.setImageResource(picIds[pic2Index]);

        setDotView(currentPic1Index);
    }

    private void setDotView(int currentPic1Index) {
        int i = currentPic1Index / 2;
        for (int j = 0; j < 6; j++) {
            if (i == j) {
                dotViews[j].setImageResource(R.drawable.guide_dot_green);
            } else {
                dotViews[j].setImageResource(R.drawable.guide_dot_normal);
            }
        }
    }

    private void playRadomSound() {
        Random random = new Random();

        //用这种方式随机选择正确项目
        if ((random.nextInt(10) % 2) == 0) {
            currentIndex = currentPic1Index;
        } else {
            currentIndex = currentPic2Index;
        }

        mediaPlay.playSound(currentIndex);
    }

    private void goToNext() {
        if (isGoNext) {
            return;
        }
        isGoNext = true;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isUp) {
                    up();
                } else {
                    down();
                }
                isGoNext = false;
            }
        }, 3000);
    }

    @Override
    protected int getActionBarDropDownViewResource() {
        return R.array.seepics;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        super.onNavigationItemSelected(itemPosition, itemId);
        picIds = ResourcesHelper.getPicList(itemPosition);
        soundIds = ResourcesHelper.getSoundList(itemPosition);
        picCount = picIds.length;

        mediaPlay = new MediaPlayHelper(mContext);
        mediaPlay.setSounds(soundIds);

        setImageView(0, 1);
        up.setVisibility(View.GONE);
        playRadomSound();
        return true;
    }
}