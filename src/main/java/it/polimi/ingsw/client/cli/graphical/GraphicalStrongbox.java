package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Resource;

public class GraphicalStrongbox {

    private final int width = 7;
    private final int height = 9;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];
    private final BackColour[][] backGroundColours = new BackColour[height][width];

    private String nickname;

    public GraphicalStrongbox(String nickname){
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

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }

    public void displayStrongbox() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    char[][] getSymbols() {
        return symbols;
    }

    Colour[][] getColours() {
        return colours;
    }

    BackColour[][] getBackGroundColours() {
        return backGroundColours;
    }
}
