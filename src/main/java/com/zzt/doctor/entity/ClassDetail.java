package com.zzt.doctor.entity;

import com.sun.tools.hat.internal.model.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author zhaotao
 */
@Data
public class ClassDetail {

    private String className;
    private String superClassId;
    private String superClassName;
    private JavaField[] fields;
    private ObjectField[] staticFields;
    private ArrayList<ObjectInstance> instances = new ArrayList<>();

    public ClassDetail(JavaClass aClass) {
        className = aClass.getName();

        JavaClass superclass = aClass.getSuperclass();
        superClassId = superclass.getIdString();
        superClassName = superclass.getName();
        fields = aClass.getFields();

        JavaStatic[] statics = aClass.getStatics();

        if (statics != null) {
            staticFields = new ObjectField[statics.length];

            for (int i = 0; i < statics.length; i++) {
                staticFields[i] = new ObjectField(statics[i].getField(), statics[i].getValue());
            }
        }

        Enumeration objects = aClass.getInstances(false);
        while (objects.hasMoreElements()) {
            JavaHeapObject obj = (JavaHeapObject) objects.nextElement();

            instances.add(new ObjectInstance(obj.toString(), obj.getIdString(), obj.getSize()));
        }
    }
}
