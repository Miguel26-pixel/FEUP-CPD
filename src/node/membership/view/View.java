package node.membership.view;

import node.membership.log.Log;

import java.nio.ByteBuffer;
import java.util.*;

public class View {
    private final Map<String, ViewEntry> entries;

    public View() {
        this.entries = new TreeMap<>();
    }

    public Map<String, ViewEntry> getEntries() {
        return entries;
    }

    public List<Byte> toBytes() {
        List<Byte> asBytes = new ArrayList<>();

        for (Byte b: this.toString().getBytes()) {
            asBytes.add(b);
        }

        return asBytes;
    }

    public void copyView(View view) {
        synchronized (entries) {
            for(Map.Entry<String, ViewEntry> entry: view.getEntries().entrySet()) {
                this.addEntry(entry.getKey(), entry.getValue());
            }
        }
    }

    public void copyView(View view, boolean updateLog) {
        synchronized (entries) {
            for(Map.Entry<String, ViewEntry> entry: view.getEntries().entrySet()) {
                this.addEntry(entry.getKey(), entry.getValue(), updateLog);
            }
        }
    }

    public void addEntry(String nodeId, ViewEntry logEntry) {
        synchronized (entries) {
            if (entries.containsKey(nodeId)) {
                ViewEntry currentEntry = entries.get(nodeId);

                if (currentEntry.getEpoch() < logEntry.getEpoch() || currentEntry.getCounter() < logEntry.getCounter()) {
                    return;
                }
            }

            entries.put(nodeId, logEntry);
        }
    }

    public void addEntry(String nodeId, ViewEntry logEntry, boolean updateLog) {
        synchronized (entries) {
            addEntry(nodeId, logEntry);

            if (updateLog) {
                Log.update(this);
            }
        }
    }

    public View getMostRecentEntries(int subsetSize) {
        List<Map.Entry<String, ViewEntry>> entryList = new ArrayList<>(new ArrayList<>(entries.entrySet()));

        entryList.sort(Comparator.comparing(entry -> entry.getValue().getEpoch()));

        entryList.subList(0, subsetSize);

        View recentView = new View();
        for (Map.Entry<String, ViewEntry> entry: entryList) {
            recentView.addEntry(entry.getKey(), entry.getValue());
        }

        return recentView;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        for (Map.Entry<String, ViewEntry> entry: entries.entrySet()) {
            toString.append(entry.getKey()).append(";").append(entry.getValue().toString()).append("\n");
        }

        return toString.toString();
    }
}
