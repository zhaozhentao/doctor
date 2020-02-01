package com.zzt.doctor.controller;

import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.vip.vjtools.vjtop.data.PerfData;
import com.zzt.doctor.entity.JInfo;
import com.zzt.doctor.entity.VMDetail;
import com.zzt.doctor.helper.VmConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.jvmstat.monitor.*;
import sun.management.counter.Counter;
import sun.tools.jconsole.LocalVirtualMachine;

import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
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
    public Object memory(@PathVariable("id") Integer pid) {
        PerfData prefData = PerfData.connect(pid);
        Map<String, Counter> counters = prefData.getAllCounters();

        HashMap<String, Object> map = new HashMap<>();
        map.put("EdenUsed", getValue(counters, "sun.gc.generation.0.space.0.used") / KB / KB);
        map.put("Survivor0Used", getValue(counters, "sun.gc.generation.0.space.1.used") / KB / KB);
        map.put("Survivor1Used", getValue(counters, "sun.gc.generation.0.space.2.used") / KB / KB);
        map.put("TenuredUsed", getValue(counters, "sun.gc.generation.1.space.0.used") / KB / KB);
        map.put("PermUsed", getValue(counters, "sun.gc.generation.2.space.0.used") / KB / KB);
        return map;
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

    private Long getValue(Map<String, Counter> counters, String key) {
        Counter counter = counters.get(key);
        if (counter != null && counter.getValue() != null) {
            return Long.valueOf(counter.getValue().toString());
        }
        return 0L;
    }
}

