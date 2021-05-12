package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.enumerations.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphicalWarehouse {

    public static void printWarehouse(List<Resource>[] depots){
        //print first row
        for (int i = 0; i < depots.length; i++) {
            System.out.print(i+1 + " ");
            for (int j = 0; j < 2 - i; j++)
                System.out.print(" ");
            depots[i].forEach(x -> System.out.print(Colour.getResourceColour(x) + x.symbol + " " + Colour.ANSI_RESET));
            System.out.print("\n");
        }

    }
}
