package node.membership.log;

import java.util.HashMap;
import java.util.Map;

public class Log {
    private final Map<int, LogEntry> entries;

    public Log() {
        this.entries = new HashMap<int, LogEntry>();
    }
}
