package zj.zfenlly.gua;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


/**
 * Created by Administrator on 2017/6/17.
 */

public class NotifySound {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static NotifySound soundPlayUtils;
    // 上下文
    static Context mContext;

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
        mSoundPlayer.load(mContext, Rfile.beep, 1);// 1
        mSoundPlayer.load(mContext, Rfile.nfcsuccess, 1);// 2
        return soundPlayUtils;
    }

    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

}
