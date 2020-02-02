package com.zzt.doctor.controller;

import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.jconsole.JConsoleContext;
import com.zzt.doctor.entity.JInfo;
import com.zzt.doctor.entity.MemoryFormItem;
import com.zzt.doctor.entity.VMDetail;
import com.zzt.doctor.helper.VmConnector;
import com.zzt.doctor.vm.ProxyClient;
import org.springframework.web.bind.annotation.*;
import sun.jvmstat.monitor.*;
import sun.tools.jconsole.LocalVirtualMachine;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author zhaotao
 */
@RestController
@RequestMapping("/api")
public class Controller {

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    private float KB = 1000;

    @GetMapping("/jvms")
    public Object main() {
        return LocalVirtualMachine.getAllVirtualMachines().values().stream()
            .map(JInfo::new)
            .collect(Collectors.toList());
    }

    @GetMapping("/jvms/{id}/vm")
    public Object mv(@PathVariable("id") Integer id) throws URISyntaxException, MonitorException, IOException, AttachNotSupportedException, MalformedObjectNameException {
        HostIdentifier hostId = new HostIdentifier((String) null);
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);

        String vmidString = "//" + id + "?mode=r";
        MonitoredVm vm = monitoredHost.getMonitoredVm(new VmIdentifier(vmidString), 0);

        VMDetail vmDetail = new VMDetail();
        vmDetail.setMainClass(MonitoredVmUtil.mainClass(vm, true));
        vmDetail.setVmArgs(MonitoredVmUtil.jvmArgs(vm).split(" "));

        VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(id));
        final String address = getLocalConnectorAddress(virtualMachine);
        JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(address));
        VmConnector vmConnector = new VmConnector(connector);

        RuntimeMXBean runtimeMXBean = vmConnector.getRuntimeMXBean();
        vmDetail.setClassPaths(runtimeMXBean.getClassPath().split(":"));
        vmDetail.setLibraryPaths(runtimeMXBean.getLibraryPath().split(":"));
        vmDetail.setBootstrapClassPaths(runtimeMXBean.getBootClassPath().split(":"));
        vmDetail.setUpTime(runtimeMXBean.getUptime());
        vmDetail.setVmName(runtimeMXBean.getVmName());
        vmDetail.setVmVersion(runtimeMXBean.getVmVersion());

        // 系统信息
        OperatingSystemMXBean ob = vmConnector.getOperatingSystemMXBean();
        vmDetail.setProgressCpuTime(ob.getProcessCpuTime() / 1000000);
        vmDetail.setAvailableProcessors(ob.getAvailableProcessors());
        vmDetail.setTotalPhysicalMemorySize(ob.getTotalPhysicalMemorySize() / KB / KB);
        vmDetail.setFreePhysicalMemorySize(ob.getFreePhysicalMemorySize() / KB / KB);
        vmDetail.setTotalSwapSpaceSize(ob.getTotalSwapSpaceSize() / KB / KB);
        vmDetail.setFreeSwapSpaceSize(ob.getFreeSwapSpaceSize() / KB / KB);

        MemoryMXBean mxBean = vmConnector.getMemoryMXBean();
        MemoryUsage heap = mxBean.getHeapMemoryUsage();

        // 堆内存使用情况
        vmDetail.setHeapUsed(heap.getUsed() / KB / KB);
        vmDetail.setHeapMax(heap.getMax() / KB / KB);
        vmDetail.setHeapCommitted(heap.getCommitted() / KB / KB);

        // gc情况
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = vmConnector.getGarbageCollectorMXBeans();

        vmDetail.setGarbageCollectInfos(garbageCollectorMXBeans.stream().map(i -> {
            VMDetail.GarbageCollectInfo info = new VMDetail.GarbageCollectInfo();
            info.setName(i.getName());
            info.setCount(i.getCollectionCount());
            info.setTime(i.getCollectionTime());
            return info;
        }).collect(Collectors.toList()));

        connector.close();
        virtualMachine.detach();

        return vmDetail;
    }

    @GetMapping("/jvms/{id}/memory")
    public Object memory(@PathVariable("id") Integer pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);

        Map<ObjectName, MBeanInfo> mBeanMap = proxyClient.getMBeans("java.lang");

        MemoryUsage heapMemoryUsage = proxyClient.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = proxyClient.getMemoryMXBean().getNonHeapMemoryUsage();

        MemoryFormItem heapItem = new MemoryFormItem();
        heapItem.setName("Heap");
        heapItem.setMax(heapMemoryUsage.getMax());
        heapItem.setUsed(heapMemoryUsage.getUsed());
        heapItem.setCommitted(heapMemoryUsage.getCommitted());

        MemoryFormItem nonHeapItem = new MemoryFormItem();
        nonHeapItem.setName("NonHeap");
        nonHeapItem.setMax(nonHeapMemoryUsage.getMax());
        nonHeapItem.setUsed(nonHeapMemoryUsage.getUsed());
        nonHeapItem.setCommitted(nonHeapMemoryUsage.getCommitted());

        List<MemoryFormItem> items = mBeanMap.keySet().stream()
            .filter(objectName -> "MemoryPool".equals(objectName.getKeyProperty("type")))
            .map(objectName -> {
                MemoryFormItem item = new MemoryFormItem();

                item.setName(objectName.getKeyProperty("name"));
                // Heap or non-heap?
                boolean isHeap = false;
                try {
                    AttributeList al = proxyClient.getAttributes(objectName, new String[]{"Type", "Usage", "UsageThreshold"});
                    if (al.size() > 0) {
                        isHeap = MemoryType.HEAP.name().equals(((Attribute) al.get(0)).getValue());
                    }
                    item.setHeap(isHeap);

                    if (al.size() > 1) {
                        CompositeData cd = (CompositeData) ((Attribute) al.get(1)).getValue();
                        MemoryUsage mu = MemoryUsage.from(cd);
                        item.setUsed(mu.getUsed());
                        item.setCommitted(mu.getCommitted());
                        item.setMax(mu.getMax());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return item;
            })
            .collect(Collectors.toList());

        items.add(0, heapItem);
        items.add(1, nonHeapItem);

        proxyClient.flush();

        return items;
    }

    @PostMapping("/jvms/{id}/gc")
    public void gc(@PathVariable("id") Integer pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);

        proxyClient.getMemoryMXBean().gc();
    }

    @GetMapping("/jvms/{id}/threads_summary")
    public Object threads(@PathVariable("id") Integer pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("activeThreadCount", proxyClient.getThreadMXBean().getThreadCount());
        map.put("peakThreadCount", proxyClient.getThreadMXBean().getPeakThreadCount());
        proxyClient.flush();

        return map;
    }

    private ProxyClient getProxyClient(Integer pid) throws IOException {
        LocalVirtualMachine machine = LocalVirtualMachine.getAllVirtualMachines().values()
            .stream()
            .filter(m -> m.vmid() == pid)
            .findFirst().get();

        ProxyClient proxyClient = ProxyClient.getProxyClient(machine);
        if (proxyClient.getConnectionState() != JConsoleContext.ConnectionState.CONNECTED) {
            proxyClient.connect(false);
        }

        return proxyClient;
    }

    private String getLocalConnectorAddress(VirtualMachine vm) throws IOException {
        // 1. 检查smartAgent是否已启动
        Properties agentProps = vm.getAgentProperties();
        String address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);

        if (address != null) {
            return address;
        }

        // 2. 未启动，尝试启动
        // JDK8后有更直接的vm.startLocalManagementAgent()方法
        String home = vm.getSystemProperties().getProperty("java.home");

        // Normally in ${java.home}/jre/lib/management-agent.jar but might
        // be in ${java.home}/lib in build environments.

        String agentPath = home + File.separator + "jre" + File.separator + "lib" + File.separator
            + "management-agent.jar";
        File f = new File(agentPath);
        if (!f.exists()) {
            agentPath = home + File.separator + "lib" + File.separator + "management-agent.jar";
            f = new File(agentPath);
            if (!f.exists()) {
                throw new IOException("Management agent not found");
            }
        }

        agentPath = f.getCanonicalPath();
        try {
            vm.loadAgent(agentPath, "com.sun.management.jmxremote");
        } catch (AgentLoadException | AgentInitializationException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }

        // 3. 再次获取connector address
        agentProps = vm.getAgentProperties();
        address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);

        if (address == null) {
            throw new IOException("Fails to find connector address");
        }

        return address;
    }
}

