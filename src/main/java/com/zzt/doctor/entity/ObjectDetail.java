package com.zzt.doctor.entity;

import com.sun.tools.hat.internal.model.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author zhaotao
 */
@Data
public class ObjectDetail {

    private String className;
    private String superClassId;
    private String superClassName;
    private JavaField[] fields;
    private StaticField[] staticFields;
    private ArrayList<Instance> instances = new ArrayList<>();

    public ObjectDetail(JavaClass aClass) {
        className = aClass.getName();

        JavaClass superclass = aClass.getSuperclass();
        superClassId = superclass.getIdString();
        superClassName = superclass.getName();
        fields = aClass.getFields();

        JavaStatic[] statics = aClass.getStatics();

        if (statics != null) {
            staticFields = new StaticField[statics.length];

            for (int i = 0; i < statics.length; i++) {
                staticFields[i] = new StaticField(statics[i]);
            }
        }

        Enumeration objects = aClass.getInstances(false);
        while (objects.hasMoreElements()) {
            JavaHeapObject obj = (JavaHeapObject) objects.nextElement();

            instances.add(new Instance(obj.toString(), obj.getIdString(), obj.getSize()));
        }
    }

    @Data
    public static class Instance {
        public String name;
        public String objectId;
        public int size;

        public Instance(String name, String objectId, int size) {
            this.name = name;
            this.objectId = objectId;
            this.size = size;
        }
    }

    @Data
    public static class StaticField {

        private String name;
        private String signature;

        private boolean hasId;
        private String value;

        private String idString;
        private int size;

        public StaticField(JavaStatic aStatic) {
            JavaField f = aStatic.getField();
            name = f.getName();
            signature = f.getSignature();
            hasId = f.hasId();
            this.value = aStatic.getValue().toString();

            if (f.hasId()) {
                JavaThing thing = aStatic.getValue();

                if (thing instanceof JavaHeapObject) {
                    JavaHeapObject ho = (JavaHeapObject) thing;

                    long id = ho.getId();
                    if (id != -1) {
                        size = ho.getSize();
                        idString = ho.getIdString();
                    }
                }
            }
        }
    }
}
