package it.polimi.ingsw.client.utilities;

import java.util.List;

public class UtilityPrinter {

    public static void printNumericList(List<String> elements){
        for (int i = 1; i <= elements.size(); i++)
            System.out.println(i + ". " + elements.get(i-1));
    }
}
