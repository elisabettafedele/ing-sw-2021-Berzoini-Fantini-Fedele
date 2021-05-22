package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Resource;

public class GraphicalStrongbox extends GraphicalElement{

    private String nickname;

    public GraphicalStrongbox(String nickname){
        super(7,9);
        this.nickname = nickname;
        reset();
    }

    public void drawStrongbox(){
        drawEdges();
        drawSeparators();
        drawResources();
    }

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
        }
    }

    private void drawEdges(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 0 && j == 0) {
                    symbols[i][j] = '╔';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == 0 && j == width - 1) {
                    symbols[i][j] = '╗';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if ((i == 0 || i == height - 1) && j > 0 && j < width -1)
                {
                    symbols[i][j] = '═';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == height - 1 && j == 0) {
                    symbols[i][j] = '╚';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == height - 1 && j == width - 1) {
                    symbols[i][j] = '╝';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i > 0 && i < height-1 && (j == 0 || j == width - 1)) {
                    symbols[i][j] = '║';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
            }
        }
    }
}
