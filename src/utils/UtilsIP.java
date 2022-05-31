package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsIP {
    final static String numIPRegex = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";

    final static String regex = numIPRegex + "\\." + numIPRegex + "\\." + numIPRegex + "\\." + numIPRegex;
    private static final Pattern patternIP = Pattern.compile(regex);

    public static boolean isIPValid(final String ip) {
        Matcher matcher = patternIP.matcher(ip);
        return !matcher.matches();
    }

    public static boolean isPortValid(final String port) {
        try {
            int p = Integer.parseInt(port);
            return (p < 0 || p > 65535);
        } catch (NumberFormatException e) {
            System.out.println("Exception: " + e);
            return true;
        }
    }
}
