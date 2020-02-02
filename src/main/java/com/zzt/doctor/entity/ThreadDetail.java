package com.zzt.doctor.entity;

import lombok.Data;

import java.lang.management.ThreadInfo;

/**
 * @author zhaotao
 */
@Data
public class ThreadDetail {

    private long tid;
    private final StackTraceElement[] stackTraceElements;

    public ThreadDetail(ThreadInfo ti) {
        stackTraceElements = ti.getStackTrace();
    }
}
