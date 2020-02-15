package com.zzt.doctor.controller;

import com.sun.tools.hat.internal.model.*;
import com.sun.tools.hat.internal.util.ArraySorter;
import com.sun.tools.hat.internal.util.Comparer;
import com.sun.tools.hat.internal.util.Misc;
import com.zzt.doctor.cache.SnapshotCache;
import com.zzt.doctor.entity.ObjectField;
import com.zzt.doctor.entity.ObjectInstance;
import com.zzt.doctor.vm.ProxyClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import static com.zzt.doctor.helper.ProxyClientHelper.getProxyClient;

/**
 * @author zhaotao
 */
@RestController
@RequestMapping("/api")
public class ObjectsController {

    @Resource
    private SnapshotCache snapshotCache;

    @GetMapping("/jvms/{vmId}/objects/{objectId}")
    public Object show(@PathVariable("vmId") String vmId, @PathVariable("objectId") String objectId) throws IOException {
        ProxyClient client = getProxyClient(vmId);

        Snapshot snapshot = snapshotCache.get(client);

        JavaHeapObject thing = snapshot.findThing(objectId);

        HashMap<String, Object> map = new HashMap<>(8);

        if (thing == null) {
            return null;
        } else if (thing instanceof JavaClass) {
            return "";
        } else if (thing instanceof JavaValueArray) {
            map.put("isValue", true);
            map.put("value", ((JavaValueArray) thing).valueString(true));
            map.put("referencesTo", referencesTo(thing, snapshot));
            return map;
        } else if (thing instanceof JavaObjectArray) {
            return "";
        } else if (thing instanceof JavaObject) {
            ObjectInstance instance = new ObjectInstance(thing.toString(), thing.getClazz().getIdString(), thing.getSize());

            map.put("instance", instance);
            map.put("class", thing.getClazz().getName());
            map.put("members", objectMembers((JavaObject) thing));
            map.put("referencesTo", referencesTo(thing, snapshot));

            return map;
        } else {
            // We should never get here
            return "";
        }
    }

    protected Object referencesTo(JavaHeapObject obj, Snapshot snapshot) {
        HashMap<String, Object> result = new HashMap<>();

        if (obj.getId() == -1) {
            return result;
        }

        ArrayList<Object> referencesToThisObject = new ArrayList<>();
        result.put("referencesToThisObject", referencesToThisObject);
        Enumeration referers = obj.getReferers();
        while (referers.hasMoreElements()) {
            HashMap<String, Object> item = new HashMap<>(4);
            JavaHeapObject ref = (JavaHeapObject) referers.nextElement();

            item.put("thing", thingDetail(ref, snapshot));
            item.put("describe", ref.describeReferenceTo(obj, snapshot));

            referencesToThisObject.add(item);
        }

//        out.println("<h2>Other Queries</h2>");
//        out.println("Reference Chains from Rootset");
//        long id = obj.getId();
//
//        out.print("<ul><li>");
//        printAnchorStart();
//        out.print("roots/");
//        printHex(id);
//        out.print("\">");
//        out.println("Exclude weak refs</a>");
//
//        out.print("<li>");
//        printAnchorStart();
//        out.print("allRoots/");
//        printHex(id);
//        out.print("\">");
//        out.println("Include weak refs</a></ul>");
//
//        printAnchorStart();
//        out.print("reachableFrom/");
//        printHex(id);
//        out.print("\">");
//        out.println("Objects reachable from here</a><br>");

        return result;
    }

    protected Object thingDetail(JavaThing thing, Snapshot snapshot) {
        HashMap<String, Object> result = new HashMap<>();

        if (thing == null) {
            return result;
        }

        if (thing instanceof JavaHeapObject) {
            JavaHeapObject ho = (JavaHeapObject) thing;
            long id = ho.getId();
            if (id != -1L) {
                result.put("objectAddress", printHex(id, snapshot));
            }

            if (id != -1) {
                result.put("size", ho.getSize());
            }
        }

        result.put("valueString", thing.toString());
        return result;
    }

    protected String printHex(long addr, Snapshot snapshot) {
        if (snapshot.getIdentifierSize() == 4) {
            return Misc.toHex((int) addr);
        } else {
            return Misc.toHex(addr);
        }
    }

    private ArrayList<ObjectField> objectMembers(JavaObject obj) {
        final JavaThing[] things = obj.getFields();
        final JavaField[] fields = obj.getClazz().getFieldsForInstance();
        Integer[] hack = new Integer[things.length];
        for (int i = 0; i < things.length; i++) {
            hack[i] = i;
        }
        ArraySorter.sort(hack, new Comparer() {
            @Override
            public int compare(Object lhs, Object rhs) {
                JavaField left = fields[(Integer) lhs];
                JavaField right = fields[(Integer) rhs];
                return left.getName().compareTo(right.getName());
            }
        });

        ArrayList<ObjectField> objectFields = new ArrayList<>();
        for (int i = 0; i < things.length; i++) {
            int index = hack[i];
            objectFields.add(new ObjectField(fields[index], things[index]));
        }

        return objectFields;
    }
}

