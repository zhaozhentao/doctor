package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class HistogramBean {

    private int num;
    private long count;
    private long bytes;
    private String className;

    public HistogramBean(int num, long count, long bytes, String className) {
        this.num = num;
        this.count = count;
        this.bytes = bytes;
        this.className = className;
    }

    public HistogramBean(String num, String count, String bytes, String className) {
        this(Integer.parseInt(num), Long.parseLong(count), Long.parseLong(bytes), className);
    }
}
