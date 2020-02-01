package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class MemoryFormItem {

    private String name;
    private boolean isHeap = false;
    private long used;
    private long committed;
    private long max;
}
