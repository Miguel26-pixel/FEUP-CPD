package node.membership.log;

import node.membership.view.View;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Log {
    private final static String LOG_PATH = "./dynamo.log";
    private final static int SAVE_SIZE = 32;

    public static void update(View view) {
        View recentEntries = view.getMostRecentEntries(SAVE_SIZE);

        File logFile = new File(LOG_PATH);
        try {
            logFile.createNewFile();

            FileWriter logFileWriter = new FileWriter(LOG_PATH);
            logFileWriter.write(recentEntries.toString());
            logFileWriter.close();
        } catch (IOException ignored) {
        }
    }

    public static String getLog() {
        StringBuilder log = new StringBuilder();

        File logFile = new File(LOG_PATH);
        try {
            Scanner fileReader = new Scanner(logFile);
            while (fileReader.hasNext()) {
                log.append(fileReader.nextLine());
            }
        } catch (IOException ignored) {
        }

        return log.toString();
    }
}
