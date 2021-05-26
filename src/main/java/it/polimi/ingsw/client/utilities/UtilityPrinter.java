package it.polimi.ingsw.client.utilities;

import java.util.List;

public class UtilityPrinter {

    public static void printNumericList(List<String> elements) {
        for (int i = 1; i <= elements.size(); i++)
            System.out.println(i + ". " + elements.get(i - 1));
    }

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
