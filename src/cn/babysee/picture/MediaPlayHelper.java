package cn.babysee.picture;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 播放音乐和音效的类
 * 
 * @author Administrator
 * 
 */
public class MediaPlayHelper {

    private int[] soundIds = null;

    // 引用mideaPlayer和SoundPool  
    //    MediaPlayer mMediaPlayer;

    SoundPool soundPool;

    HashMap<Integer, Integer> soundPoolMap;

    private Context context;

    public MediaPlayHelper(Context context) {
        this.context = context;
        //        mMediaPlayer = MediaPlayer.create(context, R.raw.animal_1_duck);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
    }

    /**
     * 初始化声音的方法
     */
    public void setSounds(int[] sounds) {
        this.soundIds = sounds;
        if (soundIds == null) {
            return;
        }

        int len = soundIds.length;
        for (int i = 0; i < len; i++) {
            soundPoolMap.put(i, soundPool.load(context, soundIds[i], 1));
        }
    }

    /**
     * 播放音效的方法
     */
    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
    }

    public void playSound(int id) {
        //        if (id == 1) {
        ////            if (!mMediaPlayer.isPlaying()) {
        ////                mMediaPlayer.start();
        ////            }
        //            this.playSound(1, 0);
        //        } else {
        //        }
        this.playSound(id, 0);
    }
}
