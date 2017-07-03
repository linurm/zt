package com.apportable.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.zj.stock.R;

/**
 * Created by Administrator on 2017/6/30.
 */

public class NotifySound {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static NotifySound soundPlayUtils;
    // 上下文
    static Context mContext;
//    int i;
//    private SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
//    public NotifySound() {
//
//
//    }

    /**
     * 初始化
     *
     * @param context
     */
    public static NotifySound init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new NotifySound();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.beep, 1);// 1
        mSoundPlayer.load(mContext, R.raw.nfcsuccess, 1);// 2

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }
//
//    public void NotifyEnd(Context mContext) {
//
//        Log.e("55", "55555555555555");
//        i = soundPool.load(mContext, R.raw.beep, 1);
//        soundPool.play(i, 1, 1, 0, 0, 1);
//    }
}
