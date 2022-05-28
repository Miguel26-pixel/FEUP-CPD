package node.membership.view;

import node.membership.log.Log;

import java.util.*;

public class View {
    private final Map<String, ViewEntry> entries;
    private final Map<String, ViewEntry> upEntries;

    public View() {
        this.entries = new TreeMap<>();
        this.upEntries = new TreeMap<>();
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

    public boolean addEntry(String key, ViewEntry viewEntry) {
        synchronized (entries) {
            if (entries.containsKey(key)) {
                ViewEntry currentEntry = entries.get(key);

                if (currentEntry.getEpoch() < viewEntry.getEpoch() || currentEntry.getCounter() < viewEntry.getCounter()) {
                    return false;
                }
            }

            entries.put(key, viewEntry);

            if (viewEntry.getCounter() % 2 == 0) {
                upEntries.put(key, viewEntry);
            } else {
                upEntries.remove(key);
            }

            return true;
        }
    }

    public void addEntry(String key, ViewEntry viewEntry, boolean updateLog) {
        synchronized (entries) {
            if (addEntry(key, viewEntry) && updateLog) {
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
