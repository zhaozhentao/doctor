package com.zzt.doctor.controller;

import cn.hutool.core.io.IoUtil;
import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.jconsole.JConsoleContext;
import com.zzt.doctor.entity.*;
import com.zzt.doctor.helper.VmConnector;
import com.zzt.doctor.vm.ProxyClient;
import org.springframework.web.bind.annotation.*;
import sun.jvmstat.monitor.*;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.jconsole.LocalVirtualMachine;
import sun.tools.jconsole.Messages;
import sun.tools.jconsole.Resources;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaotao
 */
@RestController
@RequestMapping("/api")
public class Controller {

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    private float KB = 1000;

    private ConcurrentHashMap<String, CpuInfo> map = new ConcurrentHashMap<>();

    @GetMapping("/jvms")
    public Object main() {
        Set<String> ids = ProxyClient.getCache().keySet();

        return LocalVirtualMachine.getAllVirtualMachines().values().stream()
            .map(JInfo::new)
            .peek(info -> info.setConnected(ids.contains(String.valueOf(info.getVmid()))))
            .collect(Collectors.toList());
    }

    @GetMapping("/jvms/{id}/vm")
    public Object mv(@PathVariable("id") String id) throws IOException {
        ProxyClient client = getProxyClient(id);

        RuntimeMXBean runtimeMXBean = client.getRuntimeMXBean();
        VMDetail vmDetail = new VMDetail();
        vmDetail.setMainClass(client.getDisplayName());
        vmDetail.setVmArgs(runtimeMXBean.getInputArguments());

        vmDetail.setClassPaths(runtimeMXBean.getClassPath().split(":"));
        vmDetail.setLibraryPaths(runtimeMXBean.getLibraryPath().split(":"));
        vmDetail.setBootstrapClassPaths(runtimeMXBean.getBootClassPath().split(":"));
        vmDetail.setUpTime(runtimeMXBean.getUptime());
        vmDetail.setVmName(runtimeMXBean.getVmName());
        vmDetail.setVmVersion(runtimeMXBean.getVmVersion());

        // 系统信息
        OperatingSystemMXBean ob = client.getSunOperatingSystemMXBean();
        vmDetail.setProgressCpuTime(ob.getProcessCpuTime() / 1000000);
        vmDetail.setAvailableProcessors(ob.getAvailableProcessors());
        vmDetail.setTotalPhysicalMemorySize(ob.getTotalPhysicalMemorySize() / KB / KB);
        vmDetail.setFreePhysicalMemorySize(ob.getFreePhysicalMemorySize() / KB / KB);
        vmDetail.setTotalSwapSpaceSize(ob.getTotalSwapSpaceSize() / KB / KB);
        vmDetail.setFreeSwapSpaceSize(ob.getFreeSwapSpaceSize() / KB / KB);

        MemoryMXBean mxBean = client.getMemoryMXBean();
        MemoryUsage heap = mxBean.getHeapMemoryUsage();

        // 堆内存使用情况
        vmDetail.setHeapUsed(heap.getUsed() / KB / KB);
        vmDetail.setHeapMax(heap.getMax() / KB / KB);
        vmDetail.setHeapCommitted(heap.getCommitted() / KB / KB);

        // gc情况
        Collection<GarbageCollectorMXBean> garbageCollectorMXBeans = client.getGarbageCollectorMXBeans();

        vmDetail.setGarbageCollectInfos(garbageCollectorMXBeans.stream().map(i -> {
            VMDetail.GarbageCollectInfo info = new VMDetail.GarbageCollectInfo();
            info.setName(i.getName());
            info.setCount(i.getCollectionCount());
            info.setTime(i.getCollectionTime());
            return info;
        }).collect(Collectors.toList()));

        client.flush();

        return vmDetail;
    }

    @GetMapping("/jvms/{id}/memory")
    public Object memory(@PathVariable("id") String pid) throws IOException {
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
    public void gc(@PathVariable("id") String pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);

        proxyClient.getMemoryMXBean().gc();
    }

    @GetMapping("/jvms/{id}/threads_summary")
    public Object threadsSummary(@PathVariable("id") String pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);
        ThreadMXBean threadMXBean = proxyClient.getThreadMXBean();

        Threads threads = new Threads();
        threads.setActiveThreadCount(threadMXBean.getThreadCount());
        threads.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        threads.setCpuUsage(cpu(proxyClient, pid));

        proxyClient.flush();

        return threads;
    }

    @GetMapping("/jvms/{id}/threads")
    private Object threads(@PathVariable("id") String pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);
        ThreadMXBean threadMXBean = proxyClient.getThreadMXBean();

        List<DThreadInfo> threadInfos = Stream.of(threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds()))
            .map(DThreadInfo::new).collect(Collectors.toList());

        proxyClient.flush();

        return threadInfos;
    }

    @GetMapping("/jvms/{id}/threads/{thread_id}")
    public Object threadInfo(@PathVariable("id") String pid, @PathVariable("thread_id") Integer tid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);

        ThreadMXBean tb = proxyClient.getThreadMXBean();
        ThreadInfo ti = null;
        MonitorInfo[] monitors = null;
        StringBuilder sb = new StringBuilder();

        if (proxyClient.isLockUsageSupported() && tb.isObjectMonitorUsageSupported()) {
            // VMs that support the monitor usage monitoring
            ThreadInfo[] infos = tb.dumpAllThreads(true, false);
            for (ThreadInfo info : infos) {
                if (info.getThreadId() == tid) {
                    ti = info;
                    monitors = info.getLockedMonitors();
                    break;
                }
            }
        } else {
            // VM doesn't support monitor usage monitoring
            ti = tb.getThreadInfo(tid, Integer.MAX_VALUE);
        }

        if (ti != null) {
            if (ti.getLockName() == null) {
                sb.append(Resources.format(Messages.NAME_STATE,
                    ti.getThreadName(),
                    ti.getThreadState().toString()));
            } else if (ti.getLockOwnerName() == null) {
                sb.append(Resources.format(Messages.NAME_STATE_LOCK_NAME,
                    ti.getThreadName(),
                    ti.getThreadState().toString(),
                    ti.getLockName()));
            } else {
                sb.append(Resources.format(Messages.NAME_STATE_LOCK_NAME_LOCK_OWNER,
                    ti.getThreadName(),
                    ti.getThreadState().toString(),
                    ti.getLockName(),
                    ti.getLockOwnerName()));
            }
            sb.append(Resources.format(Messages.BLOCKED_COUNT_WAITED_COUNT,
                ti.getBlockedCount(),
                ti.getWaitedCount()));
            sb.append(Messages.STACK_TRACE);
            int index = 0;
            for (StackTraceElement e : ti.getStackTrace()) {
                sb.append(e.toString()).append("\n");
                if (monitors != null) {
                    for (MonitorInfo mi : monitors) {
                        if (mi.getLockedStackDepth() == index) {
                            sb.append(Resources.format(Messages.MONITOR_LOCKED, mi.toString()));
                        }
                    }
                }
                index++;
            }
        }

        proxyClient.flush();

        return sb.toString();
    }

    @GetMapping("/jvms/{id}/deadlock")
    public Object deadLocks(@PathVariable("id") String pid) throws IOException {
        ProxyClient proxyClient = getProxyClient(pid);
        ThreadMXBean threadMBean = proxyClient.getThreadMXBean();

        Long[][] deadlockedThreadIdsArray = getDeadlockedThreadIds(proxyClient, threadMBean);

        List<List<DeadLockThread>> deadLocks;
        if (deadlockedThreadIdsArray == null) {
            deadLocks = new ArrayList<>();
        } else {
            deadLocks = Stream.of(deadlockedThreadIdsArray)
                .map(item -> Stream.of(item)
                    .map(j -> new DeadLockThread(threadMBean.getThreadInfo(j)))
                    .collect(Collectors.toList()))
                .collect(Collectors.toList());
        }

        proxyClient.flush();
        return deadLocks;
    }

    @GetMapping("/jvms/{id}/objects")
    public Object objects(@PathVariable("id") String pid) throws AttachNotSupportedException, IOException {
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));

        InputStream inputStream = ((HotSpotVirtualMachine) vm).heapHisto("-all");
        BufferedReader reader = IoUtil.getReader(inputStream, "UTF-8");

        Matcher matcher = PATTERN.matcher("");
        StringBuilder total = new StringBuilder();

        ArrayList<HistogramBean> list = new ArrayList<>();
        reader.lines().forEach(line -> {
            HistogramBean bean = parseHistogramBean(matcher, line);
            if (bean != null) {
                list.add(bean);
                return;
            }

            if (line.contains("Total")) {
                total.append(line);
            }
        });

        reader.close();
        inputStream.close();
        vm.detach();

        return new ObjectsInfo(total.toString(), list);
    }

    @DeleteMapping("/jvms/{id}")
    public void disConnect(@PathVariable("id") String pid) throws IOException {
        ProxyClient client = getProxyClient(pid);

        client.disconnect();
    }

    @PostMapping("/jvms")
    public String connect(@RequestParam("host") String host, @RequestParam("userName") String userName, @RequestParam("password") String password) throws IOException {
        String[] hosts = host.split(":");
        return getRemoteClient(hosts[0], Integer.parseInt(hosts[1]), userName, password).key;
    }

    private float cpu(ProxyClient proxyClient, String vmKey) throws IOException {
        RuntimeMXBean rb = proxyClient.getRuntimeMXBean();
        OperatingSystemMXBean sb = proxyClient.getSunOperatingSystemMXBean();
        java.lang.management.OperatingSystemMXBean ob = proxyClient.getOperatingSystemMXBean();

        if (sb == null) {
            return 0;
        }

        CpuInfo cpuInfo = map.get(vmKey);
        if (cpuInfo == null) {
            map.put(vmKey, new CpuInfo(rb.getUptime(), sb.getProcessCpuTime()));
            return 0;
        }

        long processCpuTime = sb.getProcessCpuTime();
        long upTime = rb.getUptime();

        float cpuUsage = 0;
        if (upTime > cpuInfo.getUpTime()) {
            long elapsedCpu = processCpuTime - cpuInfo.getProcessCpuTime();
            long elapsedTime = upTime - cpuInfo.getUpTime();

            // cpuUsage could go higher than 100% because elapsedTime
            // and elapsedCpu are not fetched simultaneously. Limit to
            // 99% to avoid Plotter showing a scale from 0% to 200%.
            cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * ob.getAvailableProcessors()));
        }

        cpuInfo.setProcessCpuTime(processCpuTime);
        cpuInfo.setUpTime(upTime);

        return cpuUsage;
    }

    private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+):{1}\\s+(\\d+)\\s+(\\d+)\\s+(.+)");

    private HistogramBean parseHistogramBean(Matcher matcher, String line) {
        matcher.reset(line);
        if (matcher.matches()) {
            return new HistogramBean(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        return null;
    }

    public Long[][] getDeadlockedThreadIds(ProxyClient proxyClient, ThreadMXBean threadMBean) throws IOException {
        long[] ids = proxyClient.findDeadlockedThreads();
        if (ids == null) {
            return null;
        }
        ThreadInfo[] infos = threadMBean.getThreadInfo(ids, Integer.MAX_VALUE);

        List<Long[]> dcycles = new ArrayList<Long[]>();
        List<Long> cycle = new ArrayList<Long>();

        // keep track of which thread is visited
        // one thread can only be in one cycle
        boolean[] visited = new boolean[ids.length];

        int deadlockedThread = -1; // Index into arrays
        while (true) {
            if (deadlockedThread < 0) {
                if (cycle.size() > 0) {
                    // a cycle found
                    dcycles.add(cycle.toArray(new Long[0]));
                    cycle = new ArrayList<Long>();
                }
                // start a new cycle from a non-visited thread
                for (int j = 0; j < ids.length; j++) {
                    if (!visited[j]) {
                        deadlockedThread = j;
                        visited[j] = true;
                        break;
                    }
                }
                if (deadlockedThread < 0) {
                    // done
                    break;
                }
            }

            cycle.add(ids[deadlockedThread]);
            long nextThreadId = infos[deadlockedThread].getLockOwnerId();
            for (int j = 0; j < ids.length; j++) {
                ThreadInfo ti = infos[j];
                if (ti.getThreadId() == nextThreadId) {
                    if (visited[j]) {
                        deadlockedThread = -1;
                    } else {
                        deadlockedThread = j;
                        visited[j] = true;
                    }
                    break;
                }
            }
        }
        return dcycles.toArray(new Long[0][0]);
    }

    private ProxyClient getProxyClient(String pid) {
        try {
            int id = Integer.parseInt(pid);
            return getLocalClient(id);
        } catch (Exception e) {
            return ProxyClient.getCache().get(pid);
        }
    }

    private ProxyClient getLocalClient(Integer pid) throws IOException {
        LocalVirtualMachine machine = LocalVirtualMachine.getAllVirtualMachines().values()
            .stream()
            .filter(m -> m.vmid() == pid)
            .findFirst().get();

        ProxyClient proxyClient = ProxyClient.getProxyClient(machine);
        checkConnect(proxyClient);

        return proxyClient;
    }

    private ProxyClient getRemoteClient(String hostName, int port, String userName, String password) throws IOException {
        ProxyClient client = ProxyClient.getProxyClient(hostName, port, userName, password);
        checkConnect(client);
        return client;
    }

    private void checkConnect(ProxyClient client) {
        if (client.getConnectionState() != JConsoleContext.ConnectionState.CONNECTED) {
            client.connect(false);
        }
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

