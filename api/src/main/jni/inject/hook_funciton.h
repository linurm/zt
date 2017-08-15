//
// Created by Administrator on 2017/8/12.
//

#ifndef LOADLIB_HOOK_FUNCITON_H
#define LOADLIB_HOOK_FUNCITON_H

#include <sys/time.h>

#ifdef __cplusplus
extern "C"
{
#endif

extern int miniteFlag;

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

extern Hook_Lib *hook_libs[2];



#ifdef __cplusplus
}
#endif
#endif //LOADLIB_HOOK_FUNCITON_H
