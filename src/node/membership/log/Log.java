package node.membership.log;

import node.membership.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    private final static String LOG_PATH = "./dynamo.log";
    private final static int SAVE_SIZE = 32;

    public static void update(View view) {
        View recentEntries = view.getMostRecentEntries(SAVE_SIZE);

        File logFile = new File(LOG_PATH);
        try {
            logFile.createNewFile();

            FileWriter logFileWrite = new FileWriter(LOG_PATH);
            logFileWrite.write(view.toString());
            logFileWrite.close();
        } catch (IOException ignored) {
        }
    }
}
