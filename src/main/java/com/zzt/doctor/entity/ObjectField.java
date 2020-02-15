package com.zzt.doctor.entity;

import com.sun.tools.hat.internal.model.JavaField;
import com.sun.tools.hat.internal.model.JavaHeapObject;
import com.sun.tools.hat.internal.model.JavaThing;
import lombok.Data;

/**
 * @author zhaotao
 */
@Data
public class ObjectField {

    private String name;
    private String signature;

    private boolean hasId;
    private String value;

    private String idString;
    private int size;

    public ObjectField(JavaField f, JavaThing thing) {
        name = f.getName();
        signature = f.getSignature();
        hasId = f.hasId();
        value = thing.toString();

        if (f.hasId()) {
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
