#include <zj_zfenlly_camera_CameraJni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_zj_zfenlly_camera_CameraJni_getCLanguageString
        (JNIEnv *env, jobject obj) {
        //LOGE("log string from ndk.");
        return env->NewStringUTF("Hello From JNI!");

}
#ifdef __cplusplus
}
#endif