package com.zzt.doctor.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class CpuInfo {

    private long upTime;

    private long processCpuTime;

    public CpuInfo(long upTime, long processCpuTime) {
        this.upTime = upTime;
        this.processCpuTime = processCpuTime;
    }
}
