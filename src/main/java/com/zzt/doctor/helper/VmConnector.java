package com.zzt.doctor.helper;

import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhaotao
 */
public class VmConnector {
    private final JMXConnector connector;

    public VmConnector(JMXConnector connector) {
        this.connector = connector;
    }

    public MBeanServerConnection getConnection() throws IOException {
        return connector.getMBeanServerConnection();
    }

    public RuntimeMXBean getRuntimeMXBean() throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
    }

    public OperatingSystemMXBean getOperatingSystemMXBean() throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
    }

    public MemoryMXBean getMemoryMXBean() throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    }

    public ThreadMXBean getThreadMXBean() throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
    }

    public ClassLoadingMXBean getClassLoadingMXBean() throws IOException {
        return ManagementFactory.newPlatformMXBeanProxy(getConnection(), ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);
    }

    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() throws IOException, MalformedObjectNameException {
        Set<ObjectName> gcNames = getConnection().queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        List<GarbageCollectorMXBean> gcMxBeans = new ArrayList<>();
        for (ObjectName gc : gcNames) {
            gcMxBeans.add(ManagementFactory.newPlatformMXBeanProxy(getConnection(), gc.toString(), GarbageCollectorMXBean.class));
        }
        return gcMxBeans;
    }

    public List<MemoryPoolMXBean> getMemoryPoolMXBeans() throws Exception {
        Set<ObjectName> gcNames = getConnection().queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        List<MemoryPoolMXBean> memoryPoolMXBeans = new ArrayList<>();
        for (ObjectName gc : gcNames) {
            memoryPoolMXBeans.add(ManagementFactory.newPlatformMXBeanProxy(getConnection(), gc.toString(), MemoryPoolMXBean.class));
        }
        return memoryPoolMXBeans;
    }
}
