package com.zzt.doctor.cache;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.parser.Reader;
import com.zzt.doctor.vm.ProxyClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaotao
 */
@Component
public class SnapshotCache {

    private ConcurrentHashMap<String, Snapshot> map = new ConcurrentHashMap<>();

    public Snapshot get(ProxyClient client) throws IOException {
        Snapshot cache = map.get(client.key);
        if (cache == null) {
            cache = createSnapshot(client);
        }
        return cache;
    }

    public void put(String pid, Snapshot s) {
        map.put(pid, s);
    }

    private Snapshot createSnapshot(ProxyClient client) throws IOException {
        String filePath = client.key + ".hprof";
        File dumpFile = new File(filePath);

        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = client.getHotSpotDiagnosticMXBean();
        hotSpotDiagnosticMXBean.dumpHeap(dumpFile.getAbsolutePath(), false);
        Snapshot snapshot = Reader.readFile(dumpFile.getAbsolutePath(), true, 0);
        snapshot.resolve(true);
        dumpFile.delete();

        map.put(client.key, snapshot);
        return snapshot;
    }

    public void remove(String key) {
        map.remove(key);
    }
}
