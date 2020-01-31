package com.zzt.doctor.entity;

import lombok.Data;

import java.util.List;

/**
 * @author zhaotao
 */
@Data
public class VMDetail {
    private String mainClass;

    private String[] vmArgs;

    private String[] classPaths;

    private String[] libraryPaths;

    private String[] bootstrapClassPaths;

    private long upTime;

    private String vmName;

    private String vmVersion;

    private int availableProcessors;

    private float totalPhysicalMemorySize;

    private float freePhysicalMemorySize;

    private float totalSwapSpaceSize;

    private float freeSwapSpaceSize;

    private long progressCpuTime;

    private double heapUsed;

    private double heapMax;

    private double heapCommitted;

    private List<GarbageCollectInfo> garbageCollectInfos;

    @Data
    public static class GarbageCollectInfo {
        private String name;

        private long count;

        private long time;
    }
}
