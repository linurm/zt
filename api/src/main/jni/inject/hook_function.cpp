//
// Created by Administrator on 2017/8/12.
//
#include "hook_funciton.h"
#include "elfutils.h"


test_fun old_test = NULL;
int halfHourFlag = 0;

int clock_gettime_hook(int clock_id, timespec *tp) {
    timespec t;
    int flag;
    static int n = 0;
    static time_t m;


    flag = old_test(clock_id, &t);
    tp->tv_nsec = t.tv_nsec;
    tp->tv_sec = t.tv_sec + (halfHourFlag * 30 * 60/* - 5*/);
    if (m != tp->tv_sec) {
        m = tp->tv_sec;
        if (n++ > 3) {
            n = 0;
            DL_DEBUG("clock_gettime_hook   %d", tp->tv_sec);
        }
    }
    return flag;
}