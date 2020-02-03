package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class Threads {

    private int activeThreadCount;
    private int peakThreadCount;
    private float cpuUsage;
}
