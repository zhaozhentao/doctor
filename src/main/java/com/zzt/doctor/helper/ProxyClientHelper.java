package com.zzt.doctor.helper;

import com.sun.tools.jconsole.JConsoleContext;
import com.zzt.doctor.vm.ProxyClient;
import sun.tools.jconsole.LocalVirtualMachine;

import java.io.IOException;

/**
 * @author zhaotao
 */
public class ProxyClientHelper {

    public static ProxyClient getProxyClient(String pid) {
        try {
            int id = Integer.parseInt(pid);
            return getLocalClient(id);
        } catch (Exception e) {
            return ProxyClient.getCache().get(pid);
        }
    }

    public static ProxyClient getLocalClient(Integer pid) throws IOException {
        LocalVirtualMachine machine = LocalVirtualMachine.getAllVirtualMachines().values()
            .stream()
            .filter(m -> m.vmid() == pid)
            .findFirst().get();

        ProxyClient proxyClient = ProxyClient.getProxyClient(machine);
        checkConnect(proxyClient);

        return proxyClient;
    }

    public static ProxyClient getRemoteClient(String hostName, int port, String userName, String password) throws IOException {
        ProxyClient client = ProxyClient.getProxyClient(hostName, port, userName, password);
        checkConnect(client);
        return client;
    }

    public static void checkConnect(ProxyClient client) {
        if (client.getConnectionState() != JConsoleContext.ConnectionState.CONNECTED) {
            client.connect(false);
        }
    }
}
