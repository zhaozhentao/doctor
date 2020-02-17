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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            return printFullClass((JavaClass) thing, snapshot);
        } else if (thing instanceof JavaValueArray) {
            map.put("type", "Value");
            map.put("isValue", true);
            map.put("value", ((JavaValueArray) thing).valueString(true));
            map.put("referencesTo", referencesTo(thing, snapshot));
            return map;
        } else if (thing instanceof JavaObjectArray) {
            return "";
        } else if (thing instanceof JavaObject) {
            ObjectInstance instance = new ObjectInstance(thing.toString(), thing.getClazz().getIdString(), thing.getSize());

            map.put("type", "JavaObject");
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

    private Object printFullClass(JavaClass clazz, Snapshot snapshot) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", "JavaClass");

        result.put("className", clazz.toString());

        result.put("Superclass", printClass(clazz.getSuperclass()));

        result.put("ClassLoader", thingDetail(clazz.getLoader(), snapshot));

        result.put("Signers", thingDetail(clazz.getSigners(), snapshot));

        result.put("Protection Domain", thingDetail(clazz.getProtectionDomain(), snapshot));

        ArrayList<Object> subclasses = new ArrayList<>();
        result.put("Subclasses", subclasses);
        JavaClass[] sc = clazz.getSubclasses();
        for (JavaClass javaClass : sc) {
            subclasses.add(printClass(javaClass));
        }

        ArrayList<Object> instanceMembers = new ArrayList<>();
        result.put("Instance Data Members", instanceMembers);
        JavaField[] ff = clazz.getFields().clone();
        ArraySorter.sort(ff, new Comparer() {
            @Override
            public int compare(Object lhs, Object rhs) {
                JavaField left = (JavaField) lhs;
                JavaField right = (JavaField) rhs;
                return left.getName().compareTo(right.getName());
            }
        });
        for (JavaField javaField : ff) {
            instanceMembers.add(printField(javaField));
        }

        ArrayList<Object> staticMembers = new ArrayList<>();
        result.put("Static Data Members", staticMembers);
        JavaStatic[] ss = clazz.getStatics();
        for (JavaStatic s : ss) {
            staticMembers.add(printStatic(s, snapshot));
        }

        result.put("instances", instances(clazz, snapshot));

//        if (snapshot.getHasNewSet()) {
//            out.println("<h2>New Instances</h2>");
//
//            printAnchorStart();
//            print("newInstances/" + encodeForURL(clazz));
//            out.print("\">");
//            out.println("Exclude subclasses</a><br>");
//
//            printAnchorStart();
//            print("allNewInstances/" + encodeForURL(clazz));
//            out.print("\">");
//            out.println("Include subclasses</a><br>");
//        }

        result.put("referencesToThisObject", referencesTo(clazz, snapshot));

        return result;
    }

    protected Object printStatic(JavaStatic member, Snapshot snapshot) {
        HashMap<String, Object> result = new HashMap<>(4);

        JavaField f = member.getField();
        result.put("field", printField(f));
        if (f.hasId()) {
            JavaThing t = member.getValue();
            result.put("objectValue", thingDetail(t, snapshot));
        } else {
            result.put("value", member.getValue().toString());
        }

        return result;
    }

    private Object instances(JavaClass clazz, Snapshot snapshot) {
        ArrayList<Object> instances = new ArrayList<>();

        Enumeration objects = clazz.getInstances(false);
        while (objects.hasMoreElements()) {
            JavaHeapObject obj = (JavaHeapObject) objects.nextElement();

            instances.add(thingDetail(obj, snapshot));
        }

        return instances;
    }

    protected Object printClass(JavaClass clazz) {
        if (clazz == null) {
            return null;
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("class", clazz.toString());
        result.put("classAddress", encodeForURL(clazz));

        return result;
    }

    protected String encodeForURL(JavaClass clazz) {
        if (clazz.getId() == -1) {
            return encodeForURL(clazz.getName());
        } else {
            return clazz.getIdString();
        }
    }

    protected String encodeForURL(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            // Should never happen
            ex.printStackTrace();
        }
        return s;
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

    protected Object printField(JavaField field) {
        HashMap<String, Object> result = new HashMap<>(4);

        result.put("fieldName", field.getName());
        result.put("signature", field.getSignature());

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

