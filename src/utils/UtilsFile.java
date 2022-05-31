package utils;

import message.messages.GetMessageReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class UtilsFile {
    public static void fileToBytes(File file, List<Byte> buf) {
        if (file == null) { return; }
        try (FileInputStream in = new FileInputStream(file)) {
            int n;
            byte[] bytes = new byte[1024];
            while ((n = in.read(bytes)) != -1)  {
                for (int i = 0; i < n; i++) {
                    buf.add(bytes[i]);
                }
            }
        } catch (IOException e) {
            System.err.println("Build PutMessage exception: " + e);
        }
    }

    public static File stringToFile(String body, String filepath) {
        File file= new File(filepath);

        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(body.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
