package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Resource;

import java.util.ArrayList;
import java.util.List;

public class GraphicalWarehouse extends GraphicalElement{

    private String nickname;

    public GraphicalWarehouse(String nickname){
        super(7,7);
        this.nickname = nickname;
        reset();
    }

    public void drawWarehouse(){
        reset();
        drawDepots();
        fillDepots();
    }

    private void fillDepots() {
        int[] y_coords = new int[]{3, 2, 4, 1, 3, 5};
        int coord = 0;
        List<Resource>[] depots = MatchData.getInstance().getLightClientByNickname(this.nickname).getWarehouse();
        for(int i = 0; i < depots.length; i++){
            for(int j = 0; j < i+1; j++){
                try{
                    Resource r = depots[i].get(j);
                    symbols[i*2+1][y_coords[coord]] = r.symbol.charAt(0);
                    colours[i*2+1][y_coords[coord]] = Colour.getColourByResource(r);
                }catch (IndexOutOfBoundsException e){
                    //skip
                }
                coord++;
            }
        }
    }

    private void drawDepots() {
        drawBox(0, 2);
        drawBox(2, 1);
        drawBox(2, 3);
        drawBox(4, 0);
        drawBox(4, 2);
        drawBox(4, 4);
        symbols[0][2] = '┌';
        symbols[0][3] = '─';
        symbols[2][1] = '┌';
        symbols[4][0] = '┌';
        symbols[6][0] = '└';
        symbols[6][6] = '┘';
        symbols[6][1] = '─';
        symbols[6][3] = '─';
        symbols[6][5] = '─';
    }

    private void drawBox(int x, int y) {
        symbols[x][y] = '┬';
        symbols[x][y+1] = '┴';
        symbols[x][y+2] = '┐';
        symbols[x+1][y+2] = '│';
        symbols[x+1][y] = '│';
        symbols[x+2][y] = '┴';
        symbols[x+2][y+2] = '┴';
    }

}
