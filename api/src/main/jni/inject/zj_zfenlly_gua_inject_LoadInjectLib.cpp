/*
 ============================================================================
 Name        : libinject.c
 Author      :  
 Version     :
 Copyright   : 
 Description : Android shared library inject helper
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <sys/exec_elf.h>
#include <dirent.h>
#include <unistd.h>
#include <errno.h>
#include "zj_zfenlly_gua_inject_LoadInjectLib.h"
#include "hook_funciton.h"

#define ENABLE_DEBUG 0


#if ENABLE_DEBUG
#define DEBUG_PRINT(format,args...) \
        LOGD(format, ##args)
#else
#define DEBUG_PRINT(format, args...)
#endif

AddInfo *mallocAddinfo(u_int32_t addrs, u_int32_t addre) {
    AddInfo *addr;
    addr = (AddInfo *) malloc(sizeof(AddInfo));
    addr->start_addr = addrs;
    addr->sizen = addre - addrs;
    addr->next = NULL;
    if (addr->start_addr == 0x8000)
        addr->start_addr = 0;
    return addr;
}

AddInfo *get_module_base(pid_t pid, const char *module_name) {
    FILE *fp;
    AddInfo *addr = NULL;
    AddInfo *addrinfo = NULL;
    AddInfo *addrf = NULL;
    AddInfo *addrp = NULL;
//    AddInfo *addrn = NULL;
    char *pch;
    char *pend;
    int found = 0;
    u_int32_t addrs = 0;
    u_int32_t addre = 0;
    char filename[32];
    char line[1024];

    if (pid < 0) {
        /* self process */
        snprintf(filename, sizeof(filename), "/proc/self/maps", pid);
    } else {
        snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    }

    fp = fopen(filename, "r");

    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, module_name)) {
                DL_DEBUG("_____ %s", line);
                pch = strtok(line, "-");
                addrs = strtoul(pch, NULL, 16);
                pend = strtok(NULL, " ");
                addre = strtoul(pend, NULL, 16);
                if (addrinfo != NULL) {
                    addrf = addrinfo;
                    do {
                        addrp = addrf;
                        if (addrf->start_addr != addrs) {
                            addrf = addrf->next;
                        } else {
                            found = 1;
                            break;
                        }
                    } while (addrf != NULL);
                    if (found == 0) {
                        addr = mallocAddinfo(addrs, addre);
                        if (addrp != NULL)
                            addrp->next = addr;
                    }
                    found = 0;
                } else {
                    if (found == 0) {
                        addr = mallocAddinfo(addrs, addre);
                        if (addrp != NULL)
                            addrp->next = addr;
                    }
                    addrinfo = addr;
                }
            }
        }
        fclose(fp);
    }

    return addrinfo;
}

void releaseAddInfo(AddInfo *pAddInfo) {
    AddInfo *p1;
    AddInfo *p2;
    p1 = pAddInfo;
    while (p1 != NULL) {
        if (p1->next != NULL) {
            p2 = p1->next;
            free(p1);
            p1 = p2;
        } else {
            free(p1);
            p1 = NULL;
        }
    }
}

void printAddInfo(AddInfo *pAddInfo) {
    AddInfo *p;
    if (pAddInfo != NULL) {

        p = pAddInfo;
        do {
            DL_DEBUG("0x%x --- 0x%x", p->start_addr, p->sizen);
            p = p->next;
        } while (p != NULL);
    } else {
        DL_DEBUG("The AddInfo list is null");
    }
}


AddInfo *getLibMemAddr(pid_t target_pid, const char *module_name) {
    AddInfo *local_handle;
    AddInfo *remote_handle;

//    local_handle = get_module_base(-1, module_name);
    remote_handle = get_module_base(target_pid, module_name);
    printAddInfo(remote_handle);
    DL_DEBUG("[+] get_remote_addr: local[0x%x], remote[0x%x]\n", (unsigned int) local_handle,
             (unsigned int) remote_handle);
    return remote_handle;
//    return (void *) ((uint32_t) local_addr + (uint32_t) remote_handle - (uint32_t) local_handle);
}


int find_pid_of(const char *process_name) {
    int id;
    pid_t pid = -1;
    DIR *dir;
    FILE *fp;
    char filename[32];
    char cmdline[256];

    struct dirent *entry;

    if (process_name == NULL)
        return -1;

    dir = opendir("/proc");
    if (dir == NULL)
        return -1;

    while ((entry = readdir(dir)) != NULL) {
        id = atoi(entry->d_name);
        if (id != 0) {
            sprintf(filename, "/proc/%d/cmdline", id);
            fp = fopen(filename, "r");
            if (fp) {
                fgets(cmdline, sizeof(cmdline), fp);
                fclose(fp);
                if (strcmp(process_name, cmdline) == 0) {
                    /* process found */
                    pid = id;
                    break;
                }
            }
        }
    }

    closedir(dir);

    return pid;
}


int changeLibFuncAddr(AddInfo *addr, const char *dlib, const char *symbol, void *replace_func,
                      void **old_func);

int injectLibFunc(pid_t target_pid, const char *soname, const char *symbol, void *replace_func,
                  void **old_func) {
    void *addr;
    int i;
    AddInfo *base_addr;
    base_addr = getLibMemAddr(target_pid, soname);
    i = changeLibFuncAddr(base_addr, soname, symbol, replace_func, old_func);
    releaseAddInfo(base_addr);
    return i;
}


#define PAGE_START(addr) (~(getpagesize() - 1) & (addr))

static int modifyMemAccess(void *addr, int prots) {
    void *page_start_addr = (void *) PAGE_START((uint32_t) addr);
    return mprotect(page_start_addr, getpagesize(), prots);
}

static int clearCache(void *addr, size_t len) {
    void *end = (uint8_t *) addr + len;
//    syscall(0xf0002, addr, end);
    return 0;
}

static int replaceFunc(void *addr, void *replace_func, void **old_func) {
    int res = 1;

    if (*(void **) addr == replace_func) {
        DL_DEBUG("addr %p had been replace.", addr);
        goto fails;
    }

    if (!*old_func) {
        DL_DEBUG("old_func %p save", old_func);
        *old_func = *(void **) addr;
    }

    if (modifyMemAccess((void *) addr, PROT_READ | PROT_WRITE)) {
        DL_DEBUG("[-] modifymemAccess fails, error %s.", strerror(errno));
        res = 1;
        goto fails;
    }

    *(void **) addr = replace_func;

//    clearCache(addr, getpagesize());
    //goto fails;
    DL_DEBUG("[+] old_func is %p, replace_func is %p, new_func %p.", *old_func, replace_func,
             (void *) addr);
    res = 0;
    fails:
    return res;
}

#define R_ARM_ABS32 0x02
#define R_ARM_GLOB_DAT 0x15
#define R_ARM_JUMP_SLOT 0x16

void getLibSegmentInfo() {

}

JNIEXPORT jstring JNICALL Java_zj_zfenlly_gua_LoadInjectLib_setTime
        (JNIEnv *, jobject, int time) {
    DL_DEBUG("Java_zj_zfenlly_gua_LoadInjectLib_setTime\n");
    return NULL;
}

JNIEXPORT jstring JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_addHalfHour(JNIEnv *env, jobject obj, jstring j_str) {
    halfHourFlag += 1;
    DL_DEBUG("Java_zj_zfenlly_gua_LoadInjectLib_addHalfHour\n");
    return NULL;
}

JNIEXPORT jstring JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_decHalfHour(JNIEnv *env, jobject obj, jstring j_str) {
    halfHourFlag -= 1;
    DL_DEBUG("Java_zj_zfenlly_gua_LoadInjectLib_decHalfHour\n");
    return NULL;
}

JNIEXPORT jstring JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_addHour(JNIEnv *env, jobject obj, jstring j_str) {
    halfHourFlag += 2;
    DL_DEBUG("Java_zj_zfenlly_gua_LoadInjectLib_addHour\n");
    return NULL;
}

JNIEXPORT jstring JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_decHour(JNIEnv *env, jobject obj, jstring j_str) {
    halfHourFlag -= 2;
    DL_DEBUG("Java_zj_zfenlly_gua_LoadInjectLib_decHour\n");
    return NULL;
}

JNIEXPORT int JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_injectLib(JNIEnv *env, jobject obj, jstring j_str, jstring j_lib,
                                            jstring j_func) {
    const char *c_str = NULL;
    const char *c_str_libname = NULL;
    const char *c_str_fun = NULL;
    pid_t target_pid;
    jboolean isCopy;
    int i;
    c_str = env->GetStringUTFChars(j_str, &isCopy);
    if (c_str == NULL) {
        return NULL;
    }

    target_pid = find_pid_of(c_str);

    c_str_libname = env->GetStringUTFChars(j_lib, &isCopy);
    if (c_str_libname == NULL) {
        return NULL;
    }
    c_str_fun = env->GetStringUTFChars(j_func, &isCopy);
    if (c_str_fun == NULL) {
        return NULL;
    }

    DL_DEBUG("find  \"%d\" pid", target_pid);
    i = injectLibFunc(target_pid, c_str_libname, c_str_fun, (char *) clock_gettime_hook,
                      (void **) &old_test);

    return i;
}

int changeLibFuncAddr(AddInfo *addr, const char *dlib, const char *symbol, void *replace_func,
                      void **old_func) {
    Elf32_Ehdr *elf = (Elf32_Ehdr *) (addr->start_addr);
    int i;
    char *string_table;
//    Elf32_Phdr *phdr;
//    Elf32_Shdr *shdr;
    DL_DEBUG("++++++++++");
    ElfHandle *handle = (ElfHandle *) malloc(sizeof(ElfHandle));;
    ElfInfo elfInfo;
    handle->base = (void *) (addr->start_addr);
    elfInfo.handle = handle;
//    elfInfo.ehdr = (Elf32_Ehdr *)addr;
//    elfInfo.phdr = addr + elf->e_phoff;
//    elfInfo.shdr = addr + elf->e_shoff;
//    elfInfo.shstr = NULL;


    getElfInfoBySegmentView(elfInfo, handle);

    int shnum = elfInfo.ehdr->e_shnum;
    int shentsize = elfInfo.ehdr->e_shentsize;
    DL_DEBUG("%d %x", shnum, shentsize);

//    for(i = 0; i < shnum; i++){
//        Elf32_Shdr *s = elfInfo.shdr + i;
//        if(s->sh_type == SHT_PROGBITS){
//
//        }
//    }

//    string_table = (char *) (elfInfo.shstr);
//    printHex((void *) string_table);


    Elf32_Phdr *dynamic = NULL;
    Elf32_Word size = 0;

//    getSegmentInfo(elfInfo, PT_DYNAMIC, &dynamic, &size, &elfInfo.dyn);

    Elf32_Sym *sym = NULL;
    int symidx = 0;

    findSymByName(elfInfo, symbol, &sym, &symidx);

    if (!sym) {
        DL_DEBUG("[-] Could not find symbol %s", symbol);
        goto fails;
    } else {
        DL_DEBUG("[+] sym %x %x, symidx %d.", (int) sym, ((int) sym - (int) (elfInfo.elf_base)),
                 symidx);
    }
    DL_DEBUG("+++++++++++%d", elfInfo.relpltsz);

    for (int i = 0; i < elfInfo.relpltsz; i++) {
        Elf32_Rel &rel = elfInfo.relplt[i];
//        printHex((void *) rel.r_info);

        if (ELF32_R_SYM(rel.r_info) == symidx && ELF32_R_TYPE(rel.r_info) == R_ARM_JUMP_SLOT) {
//            printHex((__uint32_t *) rel.r_info);
            void *addr = (void *) (elfInfo.elf_base + rel.r_offset);
            DL_DEBUG("find symidx 0x%x", (unsigned int) addr);
            if (replaceFunc(addr, replace_func, old_func)) {
                DL_DEBUG("replace function error");
                return -1;
            } else {
                DL_DEBUG("replace function success");
            }
            break;
        }
    }

    //DL_DEBUG("------------%d", elfInfo.reldynsz);
    for (int i = 0; i < elfInfo.reldynsz; i++) {
        Elf32_Rel &rel = elfInfo.reldyn[i];
        //printHex((void *) rel.r_info);
        if (ELF32_R_SYM(rel.r_info) == symidx &&
            (ELF32_R_TYPE(rel.r_info) == R_ARM_ABS32
             || ELF32_R_TYPE(rel.r_info) == R_ARM_GLOB_DAT)) {
            DL_DEBUG("find glob");
            void *addr = (void *) (elfInfo.elf_base + rel.r_offset);
            if (replaceFunc(addr, replace_func, old_func)) {
                DL_DEBUG("replace function error2");
                return 1;
            }else {
                DL_DEBUG("replace function2 success");
            }
            break;
        }
    }

    fails:
    return 0;
//    void *p;
//    int n, i;
//    elf->e_phoff;
//    shdr = addr + elf->e_shoff;
//    n = elf->e_phnum;
//    p = addr + elf->e_phoff;
//    phdr = (Elf32_Phdr *) p;
//    printHex(elf->e_shoff);
//    printHex((void *) shdr - addr);
//    for (i = 0; i < n; phdr++, i++) {
//        //p += elf->e_phentsize;
////        printWordHex(phdr->p_type);
//        if (phdr->p_type != PT_LOAD)
//            continue;
////        printWordHex(phdr->)
//        printWordHex(phdr);
////        phdr++;
//
//    }
    DL_DEBUG("=========0x%x 0x%x 0x%x", elf->e_phoff, elf->e_phentsize, elf->e_phnum);
//    printHex(addr);
    return 0;
}

extern void testf(void);


