//
// Created by Administrator on 2017/7/29.
//
#include <sys/mman.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/exec_elf.h>

#include "elfutils.h"

static inline Elf32_Shdr *findSectionByName(ElfInfo &info, const
char *sname
) {
    Elf32_Shdr *target = NULL;
    Elf32_Shdr *shdr = info.shdr;
    for (
            int i = 0;
            i < info.ehdr->
                    e_shnum;
            i++) {
        const char *name = (const char *) (shdr[i].sh_name + info.shstr);
        if (!
                strncmp(name, sname, strlen(sname)
                )) {
            target = (Elf32_Shdr *) (shdr + i);
            break;
        }
    }

    return
            target;
}

static inline Elf32_Phdr *findSegmentByType(ElfInfo &info, const Elf32_Word type) {
    Elf32_Phdr *target = NULL;
    Elf32_Phdr *phdr = info.phdr;

    for (int i = 0; i < info.ehdr->e_phnum; i++) {
        if (phdr[i].p_type == type) {
            target = phdr + i;
            break;
        }
    }

    return target;
}


#define SAFE_SET_VALUE(t, v) if(t) *(t) = (v)

template<class T>
static inline void
getSectionInfo(ElfInfo &info, const char *name, Elf32_Word *pSize, Elf32_Shdr **ppShdr, T *data) {
    Elf32_Shdr *_shdr = findSectionByName(info, name);

    if (_shdr) {
        SAFE_SET_VALUE(pSize, _shdr->sh_size / _shdr->sh_entsize);
        SAFE_SET_VALUE(data, reinterpret_cast<T>(info.elf_base + _shdr->sh_offset));
    } else {
        DL_DEBUG("[-] Could not found section %s\n", name);
        exit(-1);
    }

    SAFE_SET_VALUE(ppShdr, _shdr);
}

template<class T>
static void
getSegmentInfo(ElfInfo &info, const Elf32_Word type, Elf32_Phdr **ppPhdr, Elf32_Word *pSize,
               T *data) {
    Elf32_Phdr *_phdr = findSegmentByType(info, type);


    if (_phdr) {
//        if (info.handle->fromfile) { //文件读取
//            SAFE_SET_VALUE(data, reinterpret_cast<T>(info.elf_base + _phdr->p_offset));
//            SAFE_SET_VALUE(pSize, _phdr->p_filesz);
//        } else { //从内存读取
        SAFE_SET_VALUE(data, reinterpret_cast<T>(info.elf_base + _phdr->p_vaddr));
        SAFE_SET_VALUE(pSize, _phdr->p_memsz);
//        }

    } else {
        DL_DEBUG("[-] Could not found segment type is %d\n", type);
        exit(-1);
    }

    SAFE_SET_VALUE(ppPhdr, _phdr);
}

unsigned elf_hash(const char *name) {
    const unsigned char *tmp = (const unsigned char *) name;
    unsigned h = 0, g;

    while (*tmp) {
        h = (h << 4) + *tmp++;
        g = h & 0xf0000000;
        h ^= g;
        h ^= g >> 24;
    }
    return h;
}

void getElfInfoByHeader(ElfInfo &info, const ElfHandle *handle) {
    info.handle = handle;
    info.elf_base = (uint8_t *) handle->base;
//    DL_DEBUG("-----------------");
//    printWordHex(info.elf_base);
    info.ehdr = reinterpret_cast<Elf32_Ehdr *>(info.elf_base);
    info.shdr = reinterpret_cast<Elf32_Shdr *>(info.elf_base + info.ehdr->e_shoff);
    info.phdr = reinterpret_cast<Elf32_Phdr *>(info.elf_base + info.ehdr->e_phoff);

//    Elf32_Shdr *shstr = (Elf32_Shdr *) (info.shdr + info.ehdr->e_shstrndx);
//    printHex(info.shdr);
//    printHex(shstr);printWordHex(info.elf_base+0x20c0);
//    info.shstr = reinterpret_cast<char *>(info.elf_base + shstr->sh_offset);
//
//    printHex(info.shstr);
//    DL_DEBUG("+++++++++++++++++");
}

void getElfInfoBySectionView(ElfInfo &info, const ElfHandle *handle) {

    getElfInfoByHeader(info, handle);

    getSectionInfo(info, ".dynstr", NULL, NULL, &info.symstr);
    getSectionInfo(info, ".dynamic", &info.dynsz, NULL, &info.dyn);
    getSectionInfo(info, ".dynsym", &info.symsz, NULL, &info.sym);
    getSectionInfo(info, ".rel.dyn", &info.reldynsz, NULL, &info.reldyn);
    getSectionInfo(info, ".rel.plt", &info.relpltsz, NULL, &info.relplt);

    Elf32_Shdr *hash = findSectionByName(info, ".hash");
    if (hash) {
        uint32_t *rawdata = reinterpret_cast<uint32_t *>(info.elf_base + hash->sh_offset);
        info.nbucket = rawdata[0];
        info.nchain = rawdata[1];
        info.bucket = rawdata + 2;
        info.chain = info.bucket + info.nbucket;
    }
}

#define PAGE_START(x)  ((x) & PAGE_MASK)

// Returns the offset of address 'x' in its page.
#define PAGE_OFFSET(x) ((x) & ~PAGE_MASK)

// Returns the address of the next page after address 'x', unless 'x' is
// itself at the start of a page.
#define PAGE_END(x)    PAGE_START((x) + (PAGE_SIZE-1))

void printWordHex2(__uint32_t *addr) {
    DL_DEBUG("0x%x: 0x%08X(   %02X %02X %02X %02X)", (int) addr, *addr, *(uint8_t *) addr,
             *((uint8_t *) addr + 1), *((uint8_t *) addr + 2), *((uint8_t *) addr + 3));
}

void printPTLoad(ElfInfo &info) {
    Elf32_Phdr *phdr = info.phdr;

    for (int i = 0; i < info.ehdr->e_phnum; i++) {
        if (phdr[i].p_type == PT_LOAD) {
            DL_DEBUG("file: [0x%x 0x%x] ====> mem: 0x%x  *  [0x%x 0x%x]  0  0x%x",
                     (unsigned int)phdr[i].p_offset, (unsigned int)(phdr[i].p_offset + phdr[i].p_filesz),
                     (unsigned int)(info.elf_base + PAGE_START(phdr[i].p_vaddr)),
                     (unsigned int)(info.elf_base + phdr[i].p_vaddr),
                     (unsigned int)(info.elf_base + phdr[i].p_vaddr + phdr[i].p_filesz),
                     (unsigned int)(info.elf_base + PAGE_END(phdr[i].p_vaddr + phdr[i].p_filesz)));
//            printWordHex2((u_int32_t *) (info.elf_base + phdr[i].p_vaddr));
        }
    }


}

void getElfInfoBySegmentView(ElfInfo &info, const ElfHandle *handle) {

    getElfInfoByHeader(info, handle);

    Elf32_Phdr *dynamic = NULL;
    Elf32_Word size = 0;

    printPTLoad(info);


    getSegmentInfo(info, PT_DYNAMIC, &dynamic, &size, &info.dyn);
    if (!dynamic) {
        DL_DEBUG("[-] could't find PT_DYNAMIC segment");
        exit(-1);
    }
//    DL_DEBUG("PT_DYNAMIC(%d) type ===== 0x%x", type, (int) _phdr - (int) (info.elf_base));

    info.dynsz = size / sizeof(Elf32_Dyn);

    Elf32_Dyn *dyn = info.dyn;
    DL_DEBUG("======[0x%x]0x%x %d %d 0x%x %d", dynamic->p_offset, dynamic->p_vaddr,
             dynamic->p_memsz, size, (unsigned int)info.dyn, info.dynsz);
    //======0x1cbb0 304 304 0xa3101bb0 38
    for (int i = 0; i < info.dynsz; i++, dyn++) {
//        DL_DEBUG("----  0x%x:    0x%x", (unsigned int)dyn, dyn->d_tag);
        switch (dyn->d_tag) {
            case DT_SYMTAB:
                info.sym = reinterpret_cast<Elf32_Sym *>(info.elf_base + dyn->d_un.d_ptr);
                break;
            case DT_STRTAB:
                info.symstr = reinterpret_cast<const char *>(info.elf_base + dyn->d_un.d_ptr);
                break;
            case DT_REL:
                info.reldyn = reinterpret_cast<Elf32_Rel *>(info.elf_base + dyn->d_un.d_ptr);
                break;
            case DT_RELSZ:
                info.reldynsz = dyn->d_un.d_val / sizeof(Elf32_Rel);
                break;
            case DT_JMPREL:
                info.relplt = reinterpret_cast<Elf32_Rel *>(info.elf_base + dyn->d_un.d_ptr);
                break;
            case DT_PLTRELSZ:
                info.relpltsz = dyn->d_un.d_val / sizeof(Elf32_Rel);
                break;
            case DT_HASH:
                uint32_t *rawdata = reinterpret_cast<uint32_t *>(info.elf_base + dyn->d_un.d_ptr);
                info.nbucket = rawdata[0];
                info.nchain = rawdata[1];
                info.bucket = rawdata + 2;
                info.chain = info.bucket + info.nbucket;
                info.symsz = info.nchain;
//                printWordHex2(rawdata);
//                DL_DEBUG("hash 0x%x 0x%x", info.nbucket, info.nchain);
                break;
        }
    }
}

void findSymByName(ElfInfo &info, const char *symbol, Elf32_Sym **sym, int *symidx) {
    Elf32_Sym *target = NULL;

    unsigned hash = elf_hash(symbol);
    uint32_t index = info.bucket[hash % info.nbucket];

    if (!strcmp(info.symstr + info.sym[index].st_name, symbol)) {
        target = info.sym + index;
    }

    if (!target) {
        do {
            index = info.chain[index];
            if (!strcmp(info.symstr + info.sym[index].st_name, symbol)) {
                target = info.sym + index;
                break;
            }

        } while (index != 0);
    }

    if (target) {
        SAFE_SET_VALUE(sym, target);
        SAFE_SET_VALUE(symidx, index);
    }
}

void printSections(ElfInfo &info) {
    Elf32_Half shnum = info.ehdr->e_shnum;
    Elf32_Shdr *shdr = info.shdr;

    DL_DEBUG("Sections: \n");
    for (int i = 0; i < shnum; i++, shdr++) {
        const char *name =
                shdr->sh_name == 0 || !info.shstr ? "UNKOWN" : (const char *) (shdr->sh_name +
                                                                               info.shstr);
        DL_DEBUG("[%.2d] %-20s 0x%.8x\n", i, name, shdr->sh_addr);
    }
}

void printSegments(ElfInfo &info) {
    Elf32_Phdr *phdr = info.phdr;
    Elf32_Half phnum = info.ehdr->e_phnum;

    DL_DEBUG("Segments: \n");
    for (int i = 0; i < phnum; i++) {
        DL_DEBUG("[%.2d] %-20d 0x%-.8x 0x%-.8x %-8d %-8d\n", i,
                 phdr[i].p_type, phdr[i].p_vaddr,
                 phdr[i].p_paddr, phdr[i].p_filesz,
                 phdr[i].p_memsz);
    }
}

void printfDynamics(ElfInfo &info) {
    Elf32_Dyn *dyn = info.dyn;

    DL_DEBUG(".dynamic section info:\n");
    const char *type = NULL;

    for (int i = 0; i < info.dynsz; i++) {
        switch (dyn[i].d_tag) {
            case DT_INIT:
                type = "DT_INIT";
                break;
            case DT_FINI:
                type = "DT_FINI";
                break;
            case DT_NEEDED:
                type = "DT_NEEDED";
                break;
            case DT_SYMTAB:
                type = "DT_SYMTAB";
                break;
            case DT_SYMENT:
                type = "DT_SYMENT";
                break;
            case DT_NULL:
                type = "DT_NULL";
                break;
            case DT_STRTAB:
                type = "DT_STRTAB";
                break;
            case DT_REL:
                type = "DT_REL";
                break;
            case DT_SONAME:
                type = "DT_SONAME";
                break;
            case DT_HASH:
                type = "DT_HASH";
                break;
            default:
                type = NULL;
                break;
        }

        // we only printf that we need.
        if (type) {
            DL_DEBUG("[%.2d] %-10s 0x%-.8x 0x%-.8x\n", i, type, dyn[i].d_tag, dyn[i].d_un.d_val);
        }

        if (dyn[i].d_tag == DT_NULL) {
            break;
        }
    }
}

void printfSymbols(ElfInfo &info) {
    Elf32_Sym *sym = info.sym;

    DL_DEBUG("dynsym section info:\n");
    for (int i = 0; i < info.symsz; i++) {
        DL_DEBUG("[%2d] %-20s\n", i, sym[i].st_name + info.symstr);
    }
}


void printfRelInfo(ElfInfo &info) {
    Elf32_Rel *rels[] = {info.reldyn, info.relplt};
    Elf32_Word resszs[] = {info.reldynsz, info.relpltsz};

    Elf32_Sym *sym = info.sym;

    DL_DEBUG("rel section info:\n");
    for (int i = 0; i < sizeof(rels) / sizeof(rels[0]); i++) {
        Elf32_Rel *rel = rels[i];
        Elf32_Word relsz = resszs[i];

        for (int j = 0; j < relsz; j++) {
            const char *name = sym[ELF32_R_SYM(rel[j].r_info)].st_name + info.symstr;
            DL_DEBUG("[%.2d-%.4d] 0x%-.8x 0x%-.8x %-10s\n", i, j, rel[j].r_offset, rel[j].r_info,
                     name);
        }
    }
}