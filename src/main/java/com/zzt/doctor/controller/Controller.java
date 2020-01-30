package com.zzt.doctor.controller;

import com.zzt.doctor.entity.JInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.jvmstat.monitor.*;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaotao
 */
@RestController
public class Controller {

    @GetMapping("/jvms")
    public Object main() throws URISyntaxException, MonitorException {
        HostIdentifier hostId = new HostIdentifier((String) null);

        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);
        Set<Integer> jvms = monitoredHost.activeVms();

        return jvms.stream().map(j -> {
            JInfo jInfo = new JInfo();
            jInfo.setId(j);

            try {
                String vmidString = "//" + j + "?mode=r";
                MonitoredVm vm = monitoredHost.getMonitoredVm(new VmIdentifier(vmidString), 0);
                jInfo.setMainClass(MonitoredVmUtil.mainClass(vm, true));
                jInfo.setMainArgs(MonitoredVmUtil.mainArgs(vm));
                jInfo.setVmArgs(MonitoredVmUtil.jvmArgs(vm));
                jInfo.setVmFlags(MonitoredVmUtil.jvmFlags(vm));
                jInfo.setCommandLine(MonitoredVmUtil.commandLine(vm));
                jInfo.setVmVersion(MonitoredVmUtil.vmVersion(vm));
            } catch (URISyntaxException | MonitorException e) {
                e.printStackTrace();
            }

            return jInfo;
        }).collect(Collectors.toList());
    }
}

