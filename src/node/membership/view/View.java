package node.membership.view;

import java.nio.ByteBuffer;
import java.util.*;

public class View {
    private final Map<String, ViewEntry> entries;

    public View() {
        this.entries = new HashMap<String, ViewEntry>();
    }

    public Map<String, ViewEntry> getEntries() {
        return entries;
    }

    public List<Byte> toBytes() {
        List<Byte> asBytes = new ArrayList<>();

        for (Map.Entry<String, ViewEntry> viewEntry : entries.entrySet()) {
            byte[] key = viewEntry.getKey().getBytes();
            for (byte b: key) {
                asBytes.add(b);
            }

            asBytes.add((byte) ';');

            byte[] counter = ByteBuffer.allocate(4).putInt(viewEntry.getValue().getCounter()).array();

            for (byte b : counter) {
                asBytes.add(b);
            }

            asBytes.add((byte) ';');

            byte[] epoch = ByteBuffer.allocate(4).putInt(viewEntry.getValue().getEpoch()).array();

            for (byte b : epoch) {
                asBytes.add(b);
            }

            asBytes.add((byte) ' ');

        }

        return asBytes;
    }

    public void copyView(View view) {
        for(Map.Entry<String, ViewEntry> entry: view.getEntries().entrySet()) {
            this.addEntry(entry.getKey(), entry.getValue());
        }
    }

    public void addEntry(String nodeId, ViewEntry logEntry) {
        if (entries.containsKey(nodeId)) {
            ViewEntry currentEntry = entries.get(nodeId);

            if (currentEntry.getEpoch() < logEntry.getEpoch() || currentEntry.getCounter() < logEntry.getCounter()) {
                return;
            }
        }

        entries.put(nodeId, logEntry);
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
            ViewEntry value = entry.getValue();
            toString.append(entry.getKey()).append(";").append(value.getCounter()).append(";").append(value.getEpoch()).append("\n");
        }

        return toString.toString();
    }
}
