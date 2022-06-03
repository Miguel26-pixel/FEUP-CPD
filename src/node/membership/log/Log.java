package node.membership.log;

import node.membership.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Log {
    private final static int SAVE_SIZE = 32;
    private final String logPath;

    public Log(String folderPath) {
        this.logPath = folderPath + "dynamo.log";
    }

    public void update(View view) {
        View recentEntries = view.getMostRecentEntries(SAVE_SIZE);

        File logFile = new File(this.logPath);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileWriter logFileWriter = new FileWriter(this.logPath);
            logFileWriter.write(recentEntries.toString());
            logFileWriter.close();
        } catch (IOException ignored) {}
    }

    public String getLog() {
        StringBuilder log = new StringBuilder();

        File logFile = new File(this.logPath);
        try {
            Scanner fileReader = new Scanner(logFile);
            while (fileReader.hasNext()) {
                log.append(fileReader.nextLine()).append("\n");
            }
        } catch (IOException ignored) {}

        return log.toString();
    }

    public List<Byte> toBytes() {
        List<Byte> asBytes = new ArrayList<>();

        for (Byte b: getLog().getBytes()) {
            asBytes.add(b);
        }

        return asBytes;
    }

    public String getLogPath() {
        return logPath;
    }
}
