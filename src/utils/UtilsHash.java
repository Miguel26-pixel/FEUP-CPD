package utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class UtilsHash {
    public static String hashSHA256(String input) {
        byte[] bytes;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hash exception: " + e);
            return null;
        }
        BigInteger number = new BigInteger(1, bytes);

        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) { hexString.insert(0, '0'); }

        return hexString.toString();
    }

    public static int compareSHA256(String first, String second) throws Exception {
        if (first.length() < second.length()) {
            throw new Exception("Both strings should be of equal size");
        }

        HexFormat hexFormat = HexFormat.of();

        byte[] firstHex = hexFormat.parseHex(first);
        byte[] secondHex = hexFormat.parseHex(second);

        for (int i = 0; i < firstHex.length; i++) {
            if (firstHex[i] > secondHex[i]) {
                return 1;
            } else if (firstHex[i] < secondHex[i]) {
                return -1;
            }
        }

        return 0;
    }
}
