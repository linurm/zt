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

//    time_hook(NULL);

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
    if (addrinfo == NULL) {
        DL_DEBUG("there is no  %s", module_name);
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
//            DL_DEBUG("0x%x --- 0x%x", p->start_addr, p->sizen);
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
    if (remote_handle == NULL) {
        goto fails;
    }
    printAddInfo(remote_handle);
    DL_DEBUG("[+] get_remote_addr: local[0x%x], remote[0x%x]\n", (unsigned int) local_handle,
             (unsigned int) remote_handle);
    fails:
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


int changeLibFuncAddr(AddInfo *addr, const char *symbol, void *replace_func,
                      void **old_func);

int injectLibFunc(pid_t target_pid, const char *soname, const char *symbol, void *replace_func,
                  void **old_func) {
//    void *addr;
    int i;
    AddInfo *base_addr;
    base_addr = getLibMemAddr(target_pid, soname);
    if (base_addr == NULL)
        return -3;
    i = changeLibFuncAddr(base_addr, symbol, replace_func, old_func);
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

    if (addr == replace_func) {
        DL_DEBUG("addr %p had been replace.", addr);
        goto fails;
    }

    if (!*old_func) {
        *old_func = *(void **) addr;
        DL_DEBUG("old_func %p save", *old_func);
    }

    if (modifyMemAccess(addr, PROT_READ | PROT_WRITE)) {
        DL_DEBUG("[-] modifymemAccess fails, error %s.", strerror(errno));
        res = 1;
        goto fails;
    }

    *(void **) addr = replace_func;

    DL_DEBUG("[+] new_func  is %p, replace_func is %p, old_func %p.", addr, replace_func,
             *old_func);
    res = 0;
    fails:
    return res;
}

#define R_ARM_ABS32 0x02
#define R_ARM_GLOB_DAT 0x15
#define R_ARM_JUMP_SLOT 0x16

JNIEXPORT void JNICALL Java_zj_zfenlly_gua_LoadInjectLib_setTime
        (JNIEnv *, jobject, int time) {
    miniteFlag += time;

}

JNIEXPORT int JNICALL
Java_zj_zfenlly_gua_LoadInjectLib_injectLib(JNIEnv *env, jobject obj, jstring j_pkg_name) {
    const char *c_pkg_name = NULL;
    int i;
    pid_t target_pid;
    jboolean isCopy;
    int len;
    int res = 1;

    c_pkg_name = env->GetStringUTFChars(j_pkg_name, &isCopy);
    if (c_pkg_name == NULL) {
        return NULL;
    }

    target_pid = find_pid_of(c_pkg_name);


    size_t length = hook_libs.len;

    DL_DEBUG("find  \"%d\" %d  entry %d  lib %d", target_pid, length, sizeof(Hook_Entry),
             sizeof(Hook_Lib));
    for (int j = 0; j < length; j++) {
        len = hook_libs.lib_entry[j]->funs_entry->len;
        for (i = 0; i < len; i++) {
            DL_DEBUG("hook     \"%s\"   %s %p %p", hook_libs.lib_entry[j]->lib_name,
                     hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->func_name,
                     hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->fn,
                     hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->old_fn);
            res = injectLibFunc(target_pid, hook_libs.lib_entry[j]->lib_name,
                                hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->func_name,
                                hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->fn,
                                hook_libs.lib_entry[j]->funs_entry->fun_entry[i]->old_fn);
            if (res != 0) {
                break;
            }
        }
    }
    return res;
}

int changeLibFuncAddr(AddInfo *addr, const char *symbol, void *replace_func,
                      void **old_func) {
//    Elf32_Ehdr *elf = (Elf32_Ehdr *) (addr->start_addr);
//    int i;
//    char *string_table;

//    DL_DEBUG("++++++++++");
    ElfHandle *handle = (ElfHandle *) malloc(sizeof(ElfHandle));;
    ElfInfo elfInfo;
    handle->base = (void *) (addr->start_addr);
    elfInfo.handle = handle;


    getElfInfoBySegmentView(elfInfo, handle);

//    int shnum = elfInfo.ehdr->e_shnum;
//    int shentsize = elfInfo.ehdr->e_shentsize;
////    DL_DEBUG("%d %x", shnum, shentsize);
//
//    Elf32_Phdr *dynamic = NULL;
//    Elf32_Word size = 0;

    Elf32_Sym *sym = NULL;
    int symidx = 0;

    findSymByName(elfInfo, symbol, &sym, &symidx);

    if (!sym) {
        DL_DEBUG("[-] Could not find symbol %s", symbol);
        return -2;
    } else {
        DL_DEBUG("[+] sym %x %x, symidx %d.", (int) sym, ((int) sym - (int) (elfInfo.elf_base)),
                 symidx);
    }
//    DL_DEBUG("+++++++++++%d", elfInfo.relpltsz);

    for (int i = 0; i < elfInfo.relpltsz; i++) {
        Elf32_Rel &rel = elfInfo.relplt[i];

        if (ELF32_R_SYM(rel.r_info) == symidx && ELF32_R_TYPE(rel.r_info) == R_ARM_JUMP_SLOT) {
//            printHex((__uint32_t *) rel.r_info);
            void *addr = (void *) (elfInfo.elf_base + rel.r_offset);
            DL_DEBUG("find symidx %p %p %p", addr, replace_func, *old_func);
            if (replaceFunc(addr, replace_func, old_func)) {
                DL_DEBUG("replace function error");
                return -1;
            } else {
                DL_DEBUG("replace %s function success", symbol);
            }
            break;
        }
    }

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
            } else {
                DL_DEBUG("replace function2 success");
            }
            break;
        }
    }

    return 0;
}


