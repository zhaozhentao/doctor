package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class ObjectInstance {

    public String name;
    public String objectId;
    public int size;

    public ObjectInstance(String name, String objectId, int size) {
        this.name = name;
        this.objectId = objectId;
        this.size = size;
    }
}
