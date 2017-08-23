//
// Created by Administrator on 2017/8/12.
//
#include "hook_funciton.h"


volatile int miniteFlag = 0;

typedef int (*clock_gettime_fun)(int clock_id, timespec *tp);

clock_gettime_fun old_clock_gettime = NULL;

typedef int (*gettimeofday_fun)(struct timeval *tv, struct timezone *tz);

gettimeofday_fun old_gettimeofday = NULL;

typedef time_t (*time_fun)(time_t *timer);

time_fun old_time = NULL;

time_t time_hook(time_t *timer) {

    time_t t = old_time(timer);
    t += miniteFlag * 60;
//    DL_DEBUG("clock_gettime_hook   %d", t);
    return t;
}

int clock_gettime_hook(int clock_id, timespec *tp) {
    timespec t;
    int flag;
//    static int n = 0;
//    static time_t m;


    flag = old_clock_gettime(clock_id, &t);
    tp->tv_nsec = t.tv_nsec;
    tp->tv_sec = t.tv_sec + (miniteFlag * 60/* - 5*/);
//    if (m != tp->tv_sec) {
//        m = tp->tv_sec;
//        if (n++ > 3) {
//            n = 0;
//            DL_DEBUG("clock_gettime_hook   %d", tp->tv_sec);
//        }
//    }
    return flag;
}

int gettimeofday_hook(struct timeval *tv, struct timezone *tz) {
    struct timeval tvn;

    int flag = old_gettimeofday(&tvn, tz);

    tv->tv_sec = tvn.tv_sec + (miniteFlag * 60);
    tv->tv_usec = tvn.tv_usec;

    return flag;
}

//////////////////////////////
Hook_Entry hook_entry11 = {"clock_gettime",
                           (void *) clock_gettime_hook,
                           (void **) &old_clock_gettime};
Hook_Entry hook_entry12 = {"gettimeofday",
                           (void *) gettimeofday_hook,
                           (void **) &old_gettimeofday};
Hook_Entry hook_entry13 = {"time",
                           (void *) time_hook,
                           (void **) &old_time};
Hook_Entry *hook_entries1[] = {/*&hook_entry11,*/
                               &hook_entry12,
                               &hook_entry13};

Hook_Funs hook_fun1 = {
        2,//
        hook_entries1
};
Hook_Lib hook_lib1 = {
        "libs3e_android.so",
        &hook_fun1
};

/////////////////////////////////////////////////////////////
Hook_Entry hook_entry21 = {"clock_gettime",
                           (void *) clock_gettime_hook,
                           (void **) &old_clock_gettime};

Hook_Entry *hook_entries2[] = {
        &hook_entry21
};
Hook_Funs hook_fun2 = {
        1,
        hook_entries2
};
Hook_Lib hook_lib2 = {"libs3eOpenAl.so",
                      &hook_fun2
};

//////////////////////////////////////////
Hook_Lib *hook_entries[] = {
        &hook_lib1,
        &hook_lib2
};

Hook_Libs hook_libs={
        2,
        hook_entries
};