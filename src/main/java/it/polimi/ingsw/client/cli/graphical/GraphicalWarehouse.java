package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.enumerations.Resource;
import java.util.List;

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

    public static void printWarehouseGrid(List<Resource>[] depots){
        int width = 7;
        int height = 6;
        int[] startingX = new int[3];
        int[] startingY = new int[3];
        startingX[0] = 3;
        startingX[1] = 2;
        startingX[2] = 1;
        startingY[0] = 0;
        startingY[1] = 2;
        startingY[2] = 4;
        char[][] grid = new char[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++)
                grid[i][j] = ' ';
        }

        grid[1][2] = '═';
        grid[1][3] = '═';
        grid[1][4] = '═';
        grid[3][1] = '═';
        grid[3][2] = '═';
        grid[3][3] = '═';
        grid[3][4] = '═';
        grid[3][5] = '═';
        grid[5][0] = '═';
        grid[5][1] = '═';
        grid[5][2] = '═';
        grid[5][3] = '═';
        grid[5][4] = '═';
        grid[5][5] = '═';
        grid[5][6] = '═';
        for (int i = 0; i < depots.length; i++){
            if (!depots[i].isEmpty()){
                for (int j=0; j < depots[i].size(); j++)
                    grid[startingY[i]][startingX[i]+2*j] = depots[i].get(0).symbol.charAt(0);
            }
        }

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                System.out.print(grid[i][j]);
            }
            System.out.print("\n");
        }
    }
}
