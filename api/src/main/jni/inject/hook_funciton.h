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

typedef int (*test_fun)(int clock_id, timespec *tp);

extern test_fun old_test;
extern int halfHourFlag;
int clock_gettime_hook(int clock_id, timespec *tp);


#ifdef __cplusplus
}
#endif
#endif //LOADLIB_HOOK_FUNCITON_H
