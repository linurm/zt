package zj.zfenlly.camera;

/**
 * Created by Administrator on 2016/1/29.
 */
public class CameraJni {

    public native String getCLanguageString();

    static {
        System.loadLibrary("JniCamera");   //defaultConfig.ndk.moduleName
    }
}
