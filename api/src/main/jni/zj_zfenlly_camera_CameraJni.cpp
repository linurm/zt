#include <string.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <zj_zfenlly_camera_CameraJni.h>
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring
JNICALL Java_zj_zfenlly_camera_CameraJni_getCLanguageString(JNIEnv *env, jobject obj,
                                                            jstring j_str) {
    const char *c_str = NULL;
    char buff[128] = {0};
    jboolean isCopy;    // 返回JNI_TRUE表示原字符串的拷贝，返回JNI_FALSE表示返回原字符串的指针
    c_str = env->GetStringUTFChars(j_str, &isCopy);
//printf("isCopy:%d\n", isCopy);
    if (c_str == NULL) {
        return NULL;
    }
//printf("C_str: %s \n", c_str);
    sprintf(buff, "hello %s", c_str);
    env->ReleaseStringUTFChars(j_str, c_str);
    jstring a = env->NewStringUTF(buff);
    return a;

}

#ifdef __cplusplus
}
#endif
