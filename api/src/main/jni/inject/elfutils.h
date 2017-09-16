//
// Created by Administrator on 2017/7/29.
//

#ifndef LOADLIB_ELFUTILS_H
#define LOADLIB_ELFUTILS_H

#include <stdint.h>
#include <sys/exec_elf.h>
#include <android/log.h>

#define DL_ERR(...) \
    __android_log_print(ANDROID_LOG_ERROR, "ZTAG", __VA_ARGS__)

#define DL_DEBUG(...) \
    __android_log_print(ANDROID_LOG_ERROR, "ZTAG", __VA_ARGS__)
#ifdef __cplusplus
extern "C" {
#endif
typedef struct ElfHandle {
    void *base;
    size_t space_size;

} ElfHandle;
typedef struct ElfInfo {
    const ElfHandle *handle;
    uint8_t *elf_base;
    Elf32_Ehdr *ehdr;
    Elf32_Phdr *phdr;
    Elf32_Shdr *shdr;

    Elf32_Dyn *dyn;
    Elf32_Word dynsz;

    Elf32_Sym *sym;
    Elf32_Word symsz;

    Elf32_Rel *relplt;
    Elf32_Word relpltsz;
    Elf32_Rel *reldyn;
    Elf32_Word reldynsz;

    uint32_t nbucket;
    uint32_t nchain;

    uint32_t *bucket;
    uint32_t *chain;

    const char *shstr;
    const char *symstr;

} ElfInfo;

typedef  struct AddInfo{
    uint32_t start_addr;
    uint32_t sizen;
    AddInfo* next;
} AddInfo;

/**
 * 符号hash函数
 */
unsigned elf_hash(const char *name);

/**
 * 从section视图获取info
 */
void getElfInfoBySectionView(ElfInfo &info, const ElfHandle *handle);

/**
 * 从segment视图获取info
 */
void getElfInfoBySegmentView(ElfInfo &info, const ElfHandle *handle);


/**
 * 根据符号名寻找Sym
 */
void findSymByName(ElfInfo &info, const char *symbol, Elf32_Sym **sym, int *symidx);

/**
 * 打印section信息
 */
void printSections(ElfInfo &info);


/**
 * 打印segment信息
 */
void printSegments(ElfInfo &info);

/**
 * 打印dynamic信息
 */
void printfDynamics(ElfInfo &info);

/**
 * 打印所有符号信息
 */
void printfSymbols(ElfInfo &info);

/**
 * 打印重定位信息
 */
void printfRelInfo(ElfInfo &info);


void printWordHex2(__uint32_t *addr);

#ifdef __cplusplus
}
#endif
#endif //LOADLIB_ELFUTILS_H
