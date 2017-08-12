# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library(inject
            SHARED
            zj_zfenlly_gua_inject_LoadInjectLib.cpp
            elfutils.cpp
            hook_function.cpp
)

target_link_libraries(inject
        log
)
#set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../distribution)

#target_link_libraries(inject
#        log
#)
#message(${PROJECT_BINARY_DIR})
#MESSAGE("++++++++++++++++++++++++++++++++++")
#set_target_properties(inject
#                      PROPERTIES
#                      LIBRARY_OUTPUT_DIRECTORY
#                      "${distribution_DIR}/lib/${ANDROID_ABI}")

#add_custom_command(TARGET inject POST_BUILD
#                   COMMAND "${CMAKE_COMMAND}" -E
#                   copy "${CMAKE_CURRENT_SOURCE_DIR}/inject.h"
#                   "${distribution_DIR}/include/inject.h"
#                   **** the following 2 lines are for potential future debug purpose ****
#                   COMMAND "${CMAKE_COMMAND}" -E
#                   remove_directory "${CMAKE_CURRENT_BINARY_DIR}"
#                   COMMENT "Copying gmath to output directory")