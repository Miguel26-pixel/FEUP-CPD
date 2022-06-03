package node.membership.view;

import node.membership.log.Log;
import utils.UtilsHash;

import java.util.*;

public class View {
    private final Map<String, ViewEntry> entries;
    private final Map<String, ViewEntry> upEntries;

    public View() {
        this.entries = new TreeMap<>();
        this.upEntries = new TreeMap<>();
    }

    public View(String asString) {
        this.entries = new TreeMap<>();
        this.upEntries = new TreeMap<>();

        for(String entryString: asString.split("\n")) {
            String[] entry = entryString.split(";");
            if (entry.length != 5) {
                continue;
            }
            String id = entry[0];
            String address = entry[1];
            int port = Integer.parseInt(entry[2]);
            String counter = entry[3];
            String epoch = entry[4];

            this.addEntry(id, new ViewEntry(port, address, Integer.parseInt(counter), Integer.parseInt(epoch)));
        }
    }

    public Map<String, ViewEntry> getEntries() {
        return entries;
    }

    public Map<String, ViewEntry> getUpEntries() {
        return upEntries;
    }

    public List<Byte> toBytes() {
        List<Byte> asBytes = new ArrayList<>();

        for (Byte b: this.toString().getBytes()) {
            asBytes.add(b);
        }

        return asBytes;
    }

    public void copyView(View view) {
        for(Map.Entry<String, ViewEntry> entry: view.getEntries().entrySet()) {
            this.addEntry(entry.getValue().getAddress(), entry.getValue());
        }
    }

    public void copyView(View view, boolean updateLog) {
        for(Map.Entry<String, ViewEntry> entry: view.getEntries().entrySet()) {
            this.addEntry(entry.getValue().getAddress(), entry.getValue(), updateLog);
        }
    }

    public boolean addEntry(String key, ViewEntry viewEntry) {
        synchronized (entries) {
            String hash = UtilsHash.hashSHA256(key);
            if (entries.containsKey(hash)) {
                ViewEntry currentEntry = entries.get(hash);

                if (currentEntry.getCounter() > viewEntry.getCounter()) {
                    return false;
                }
            }

            entries.put(hash, viewEntry);

            if (viewEntry.getCounter() % 2 == 0) {
                upEntries.put(hash, viewEntry);
            } else {
                upEntries.remove(hash);
            }

            return true;
        }
    }

    public void addEntry(String key, ViewEntry viewEntry, boolean updateLog) {
        if (addEntry(key, viewEntry) && updateLog) {
            Log.update(this);
        }
    }

    public View getMostRecentEntries(int subsetSize) {
        List<Map.Entry<String, ViewEntry>> entryList = new ArrayList<>(entries.entrySet());

        entryList.sort(Comparator.comparing(entry -> entry.getValue().getEpoch()));

        entryList.subList(0, Integer.min(subsetSize, entryList.size()));

        View recentView = new View();
        for (Map.Entry<String, ViewEntry> entry: entryList) {
            recentView.addEntry(entry.getKey(), entry.getValue());
        }

        return recentView;
    }

    public ViewEntry getNextUpEntry(String previousKey) {
        List<String> keyList = new ArrayList<>(upEntries.keySet());

        if (keyList.size() == 0) {
            return null;
        }

        Collections.sort(keyList);

        int previousKeyIndex = keyList.indexOf(previousKey);
        String nextKey = keyList.get((previousKeyIndex + 1) % keyList.size());

        return upEntries.get(nextKey);
    }

    public int compareTo(View other) {
        int score = 0;
        for (String key: this.entries.keySet()) {
            ViewEntry otherEntry = other.getEntry(key);
            if (otherEntry == null) {
                score += 1;
                continue;
            }

            ViewEntry thisEntry = this.getEntry(key);
            if (thisEntry.getCounter() < otherEntry.getCounter()) {
                score -= 1;
            } else if (thisEntry.getCounter() > otherEntry.getCounter()) {
                score += 1;
            }
        }

        for (String key: other.entries.keySet()) {
            ViewEntry thisEntry = this.getEntry(key);
            if (thisEntry == null) {
                score -= 1;
            }
        }

        return score;
    }

    private ViewEntry getEntry(String key) {
        if (this.entries.containsKey(key)) {
            return this.entries.get(key);
        }

        return null;
    }

    public void setDown(String nodeId, boolean updateLog) {
        String hash = UtilsHash.hashSHA256(nodeId);

        if (this.upEntries.containsKey(hash)) {
            ViewEntry entry = this.getEntry(hash);

            long secondsSinceEpoch = System.currentTimeMillis() / 1000;
            this.addEntry(nodeId, new ViewEntry(entry.getPort(), entry.getAddress(), entry.getCounter() + 1, secondsSinceEpoch), updateLog);
        }
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
