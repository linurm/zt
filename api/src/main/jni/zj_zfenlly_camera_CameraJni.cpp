#include <zj_zfenlly_camera_CameraJni.h>
#include <string.h>
#include <android/log.h>

typedef int (*strlen_fun)(const char *);
strlen_fun global_strlen1 = (strlen_fun)strlen;
strlen_fun global_strlen2 = (strlen_fun)strlen;
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native", __VA_ARGS__))
#define SHOW(x) LOGI("%s is %d", #x, x)
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_zj_zfenlly_camera_CameraJni_getCLanguageString
        (JNIEnv *env, jobject obj) {
    const char *str = "helloworld";

    strlen_fun local_strlen1 = (strlen_fun)strlen;
    strlen_fun local_strlen2 = (strlen_fun)strlen;

    int len0 = global_strlen1(str);
    int len1 = global_strlen2(str);
    int len2 = local_strlen1(str);
    int len3 = local_strlen2(str);
    int len4 = strlen(str);
    int len5 = strlen(str);

    SHOW(len0);
    SHOW(len1);
    SHOW(len2);
    SHOW(len3);
    SHOW(len4);
    SHOW(len5);

    return 0;
}
#ifdef __cplusplus
}
#endif