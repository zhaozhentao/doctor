package com.zzt.doctor.entity;

import lombok.Data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaotao
 */
@Data
public class ObjectsInfo {

    private long totalCount;

    private long totalBytes;

    private List<HistogramBean> beans;

    public ObjectsInfo(String total, List<HistogramBean> beanList) {
        Pattern pattern = Pattern.compile("\\s*Total\\s*(\\d+)\\s*(\\d+)");
        Matcher matcher = pattern.matcher(total);
        matcher.matches();
        this.totalCount = Long.parseLong(matcher.group(1));
        this.totalBytes = Long.parseLong(matcher.group(2));
        this.beans = beanList;
    }
}
