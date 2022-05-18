package node.membership.log;

import java.nio.ByteBuffer;
import java.util.*;

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

    public void addEntry(String nodeId, LogEntry logEntry) {
        if (entries.containsKey(nodeId)) {
            LogEntry currentEntry = entries.get(nodeId);

            if (currentEntry.getEpoch() < logEntry.getEpoch() || currentEntry.getCounter() < logEntry.getCounter()) {
                return;
            }
        }

        entries.put(nodeId, logEntry);
    }

    public Map<String, LogEntry> getMostRecentEntries(int subsetSize) {
        List<Map.Entry<String, LogEntry>> entryList = entries.entrySet().stream().toList();

        entryList.sort(Comparator.comparing(entry -> entry.getValue().getEpoch()));

        entryList.subList(0, subsetSize);

        Map<String, LogEntry> recentEntries = new HashMap<>();

        for (Map.Entry<String, LogEntry> entry: entryList) {
            recentEntries.put(entry.getKey(), entry.getValue());
        }

        return recentEntries;
    }
}
