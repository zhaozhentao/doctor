package com.zzt.doctor.entity;

import lombok.Data;

import java.util.List;

/**
 * @author zhaotao
 */
@Data
public class ObjectsInfo {

    private int totalCount;

    private long totalBytes;

    private List<HistogramBean> beans;

    public ObjectsInfo(int totalCount, long totalBytes, List<HistogramBean> beanList) {
        this.totalCount = totalCount;
        this.totalBytes = totalBytes;
        this.beans = beanList;
    }
}
