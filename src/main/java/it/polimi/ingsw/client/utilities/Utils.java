package it.polimi.ingsw.client.utilities;

import java.util.regex.Pattern;

/**
 * Utility class to check connection parameters
 */
public class Utils {
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = "^(" + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + ")$";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    public static boolean IPAddressIsValid(String IP){
        return IP != null && IP_PATTERN.matcher(IP).matches();
    }
    public static boolean portIsValid(int port){
        return port >= 1024 && port <= 65535;
    }

}
