package node.membership.log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Log {
    private final Map<String, LogEntry> entries;

    public Log() {
        this.entries = new HashMap<String, LogEntry>();
    }

    public Map<String, LogEntry> getEntries() {
        return entries;
    }

    public List<Byte> toBytes() {
        List<Byte> asBytes = new ArrayList<>();

        for (Map.Entry<String, LogEntry> logEntry : entries.entrySet()) {
            byte[] key = logEntry.getKey().getBytes();
            for (byte b: key) {
                asBytes.add(b);
            }

            asBytes.add((byte) ';');

            byte[] counter = ByteBuffer.allocate(4).putInt(logEntry.getValue().getCounter()).array();

            for (byte b : counter) {
                asBytes.add(b);
            }

            asBytes.add((byte) ';');

            byte[] epoch = ByteBuffer.allocate(4).putInt(logEntry.getValue().getEpoch()).array();

            for (byte b : epoch) {
                asBytes.add(b);
            }

            asBytes.add((byte) ' ');

        }

        return asBytes;
    }
}
