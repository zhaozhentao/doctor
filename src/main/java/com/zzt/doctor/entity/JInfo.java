package com.zzt.doctor.entity;

import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class JInfo {
    private Integer id;

    private String mainClass;

    private String mainArgs;

    private String vmArgs;

    private String vmFlags;

    private String commandLine;

    private String vmVersion;
}
