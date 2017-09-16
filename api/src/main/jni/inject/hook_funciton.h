//
// Created by Administrator on 2017/8/12.
//

#ifndef LOADLIB_HOOK_FUNCITON_H
#define LOADLIB_HOOK_FUNCITON_H

#include <sys/time.h>
#include <time.h>
#ifdef __cplusplus
extern "C"
{
#endif

extern volatile  int miniteFlag;
typedef struct hook_entry {
    const char *func_name;
    void *fn;
    void ** old_fn;
} Hook_Entry;

typedef struct hook_funs{
    int len;
    Hook_Entry **fun_entry;
}Hook_Funs;

typedef struct hook_lib{
    const char *lib_name;
    Hook_Funs *funs_entry;
}Hook_Lib;

typedef struct hook_libs{
    int len;
    Hook_Lib **lib_entry;
}Hook_Libs;

extern Hook_Libs hook_libs;

time_t time_hook(time_t *timer);

#ifdef __cplusplus
}
#endif
#endif //LOADLIB_HOOK_FUNCITON_H
