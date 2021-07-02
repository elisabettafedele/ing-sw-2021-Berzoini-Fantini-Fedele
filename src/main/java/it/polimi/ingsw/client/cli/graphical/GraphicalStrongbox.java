package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Resource;

/**
 * Class to represents the strongbox
 */
public class GraphicalStrongbox extends GraphicalElement{

    private String nickname;

    public GraphicalStrongbox(String nickname){
        super(7,9);
        this.nickname = nickname;
        reset();
    }

    /**
     * Draw all the elements of the strongbox
     */
    public void drawStrongbox(){
        drawEdges(this.height, this.width);
        drawSeparators();
        drawResources();
    }

    /**
     * Draws the resources with the relative quantity
     */
    private void drawResources() {
        int[] strongbox = MatchData.getInstance().getLightClientByNickname(this.nickname).getStrongbox();
        for(int i = 0; i < strongbox.length; i++){
            int quantity = strongbox[i];
            Resource r = Resource.valueOf(i);
            symbols[i*2+1][1] = r.symbol.charAt(0);
            colours[i*2+1][1] = Colour.getColourByResource(r);
            symbols[i*2+1][3] = 'x';
            if(quantity > 9)
                symbols[i*2+1][4] = String.valueOf(quantity/10).charAt(0);
            symbols[i*2+1][5] = String.valueOf(quantity%10).charAt(0);
        }
    }

    private void drawSeparators() {
        for(int i = 0; i < 3; i++){
            for(int j = 1; j < width - 1; j++){
                symbols[i*2+2][j] = '═';
            }
            symbols[i*2+2][0] = '╠';
            symbols[i*2+2][width-1] = '╣';
        }
    }
}
