package node.membership.log;

import java.util.HashMap;
import java.util.Map;

public class Log {
    private final Map<String, LogEntry> entries;

    public Log() {
        this.entries = new HashMap<String, LogEntry>();
    }

    public Map<String, LogEntry> getEntries() {
        return entries;
    }
}
