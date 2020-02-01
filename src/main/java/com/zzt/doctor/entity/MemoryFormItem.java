package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class MemoryFormItem {

    private String name;
    private boolean isHeap = false;
    private float used;
    private float committed;
}
