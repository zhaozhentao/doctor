package com.zzt.doctor.entity;

import lombok.Data;

import java.lang.management.ThreadInfo;

/**
 * @author zhaotao
 */
@Data
public class DeadLockThread {

    private long threadId;
    private String name;

    public DeadLockThread(ThreadInfo threadInfo) {
        threadId = threadInfo.getThreadId();
        name = threadInfo.getThreadName();
    }
}
