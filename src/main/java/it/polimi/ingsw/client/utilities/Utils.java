package it.polimi.ingsw.client.utilities;

import java.util.List;
import java.util.regex.Pattern;

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
    public static boolean isACorrectReorganizeDepotCommand(String command, List<String> availableDepots){
        String commands [] = command.split(" ");
        String name = commands[0];
        if (!commands[0].equals("swap") && !commands[0].equals("move") && !commands[0].equals("-swap") && !commands[0].equals("-move"))
            return false;
        if ("-swap".contains(name))
            return (commands.length == 3 && !commands[1].equals(commands[2]) && availableDepots.contains(commands[1]) && availableDepots.contains(commands[2]));
        return (commands.length == 4 && !commands[1].equals(commands[2]) && availableDepots.contains(commands[1]) && availableDepots.contains(commands[2]) && isInteger(commands[2]));
    }

    public static boolean isInteger(String string){
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException | NullPointerException e){
            return false;
        }
        return true;
    }
}
