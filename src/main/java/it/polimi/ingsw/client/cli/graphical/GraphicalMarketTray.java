package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Marble;
import javafx.css.Match;

public class GraphicalMarketTray {

    private final int width = 18;
    private final int height = 5;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];
    private final BackColour[][] backGroundColours = new BackColour[height][width];

    public GraphicalMarketTray(){
        reset();
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

    public void displayMarketTray(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    public void drawMarketTray(){
        reset();
        Marble[][] marketTray = MatchData.getInstance().getMarketTray();
        Marble slideMarble = MatchData.getInstance().getSlideMarble();
        drawMarket(marketTray);
        drawArrowsAndNumbers();
        symbols[3][11] = '\u25CF';
        colours[3][11] = Colour.getColourByMarble(slideMarble);
    }

    private void drawArrowsAndNumbers() {
        for(int i = 0; i < 3; i++){
            symbols[i][11] = '←';
            colours[i][11] = Colour.ANSI_WHITE;
            symbols[i][13] = String.valueOf(i+1).charAt(0);
            colours[i][13] = Colour.ANSI_WHITE;
        }
        for(int j = 0; j < 4; j++){
            symbols[3][j*3] = '↑';
            colours[3][j*3] = Colour.ANSI_WHITE;
            symbols[4][j*3] = String.valueOf(j*(-1)+7).charAt(0);
            colours[4][j*3] = Colour.ANSI_WHITE;
        }
    }

    private void drawMarket(Marble[][] marketTray) {
        for(int i = 0; i < marketTray.length; i++){
            for(int j = 0; j < marketTray[i].length; j++){
                symbols[i][j*3] = '\u25CF';
                colours[i][j*3] = Colour.getColourByMarble(marketTray[i][j]);
            }
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
