package com.zzt.doctor.entity;

import lombok.Data;
import sun.tools.jconsole.LocalVirtualMachine;

/**
 * @author zhaotao
 */
@Data
public class JInfo {

    private String address;
    private String commandLine;
    private String displayName;
    private int vmid;
    private boolean isAttachSupported;

    public JInfo(LocalVirtualMachine m) {
        vmid = m.vmid();
        commandLine = m.toString();
        displayName = m.displayName();
        address = m.connectorAddress();
        isAttachSupported = m.isAttachable();
    }
}
