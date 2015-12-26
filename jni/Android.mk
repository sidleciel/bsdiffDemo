LOCAL_PATH := $(call my-dir)

#>>>>> nativehelper section
include $(CLEAR_VARS)
LOCAL_MODULE := libnativehelper
LIB_NATIVEHELPER_PATH:= $(LOCAL_PATH)/libnativehelper
LOCAL_MODULE_TAGS := user
LOCAL_ARM_MODE := arm
LOCAL_SRC_FILES := libnativehelper/JNIHelp.c		
LOCAL_C_INCLUDES := \
    $(LIB_NATIVEHELPER_PATH)/include/	\
LOCAL_LDLIBS := -llog

include $(BUILD_STATIC_LIBRARY)


#>>>>> bzip2 section
include $(CLEAR_VARS)
LOCAL_MODULE := libbzip2
BZIP2_FILES := \
    bzip2/bzip2recover.c    \
    bzip2/compress.c        \
    bzip2/unzcrash.c        \
    bzip2/bzip2.c           \
    bzip2/mk251.c           \
    bzip2/randtable.c       \
    bzip2/crctable.c        \
    bzip2/decompress.c      \
    bzip2/huffman.c         \
    bzip2/blocksort.c       \
    bzip2/spewG.c           \
    bzip2/dlltest.c         \
    bzip2/bzlib.c           \

LOCAL_SRC_FILES := $(BZIP2_FILES)
include $(BUILD_STATIC_LIBRARY)


#>>>>> diff/patch section
include $(CLEAR_VARS)
LOCAL_MODULE := libdiffpatch
LOCAL_SRC_FILES := \
    bsdiff.c    \
    bspatch.c   \
    bsentry.cpp

LOCAL_STATIC_LIBRARIES := \
    libbzip2    \
    libnativehelper
LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/libnativehelper/include                   \
    $(JNI_H_INCLUDE) external/bzip2                         \
    $(LOCAL_PATH)/bzip2                                     \
    $(LOCAL_PATH)

LOCAL_LDLIBS := -llog
LOCAL_CFLAGS += -O3 -fvisibility=hidden

include $(BUILD_SHARED_LIBRARY)