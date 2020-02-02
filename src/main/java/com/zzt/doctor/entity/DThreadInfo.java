package com.zzt.doctor.entity;

import lombok.Data;

import java.lang.management.ThreadInfo;

/**
 * @author zhaotao
 */
@Data
public class DThreadInfo {

    private long id;
    private String name;
    private Thread.State state;

    public DThreadInfo(ThreadInfo info) {
        id = info.getThreadId();
        name = info.getThreadName();
        state =  info.getThreadState();
    }
}
