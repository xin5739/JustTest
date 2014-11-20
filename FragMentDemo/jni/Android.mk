LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := FragMentDemo
LOCAL_SRC_FILES := FragMentDemo.cpp

include $(BUILD_SHARED_LIBRARY)
