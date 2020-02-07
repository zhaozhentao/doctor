package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class HistogramBean {

    private int num;
    private int count;
    private long bytes;
    private String className;
}
