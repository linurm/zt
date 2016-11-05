#include <string.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stddef.h>
#include "linker_phdr.h"
#include "fcntl.h"

#ifdef __cplusplus
extern "C" {
#endif
int g_a = 42;
int a_array[8] = {23, 334, 54, 65, 786, 87, 989, 98};
int func_add(int a, int b) {

    int i;

    for (i = a; i < b; i++) {
        g_a = a_array[i % 8] + g_a;
    }


    return g_a;
}

int func_test() {
    char *s1 = "string 123";
    char *s2 = "string 456";
    char buff[128] = {0};
    char buff2[128] = {0};
    //char * s2="string 456";
    sprintf(buff, "test %s", s1);
    sprintf(buff2, "test %s", s2);
    int i = func_add(12, 56);

    return i;
}

void test(const char *name) {
    int fd = (open(name, O_RDONLY | O_CLOEXEC));
    ElfReader elf_reader(name, fd);
    if (!elf_reader.Load()) {
        ;//printf("elf load error\n");//return NULL;
    }
    ;//printf("elf load success!!!\n");
    close(fd);
}

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

    int m = func_add(123, 456);
    //printf("C_str: %s \n", c_str);
    test(c_str);
    sprintf(buff, "hello %s %d", c_str, m);
    env->ReleaseStringUTFChars(j_str, c_str);
    jstring a = env->NewStringUTF(buff);
    return a;

}


#ifdef __cplusplus
}
#endif
