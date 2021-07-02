package it.polimi.ingsw.client.utilities;

import java.util.List;

/**
 * Class to print any given numeric list
 */
public class UtilityPrinter {

    /**
     * Given a list it prints the list with a number associated with every element
     * @param elements the elements to be printed
     */
    public static void printNumericList(List<String> elements) {
        for (int i = 1; i <= elements.size(); i++)
            System.out.println(i + ". " + elements.get(i - 1).replace("_", " "));
    }
}
