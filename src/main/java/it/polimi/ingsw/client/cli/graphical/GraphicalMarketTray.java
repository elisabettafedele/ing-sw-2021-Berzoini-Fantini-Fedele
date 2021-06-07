package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.Marble;
import javafx.css.Match;

/**
 * Class to represents the market in the CLI
 */
public class GraphicalMarketTray extends GraphicalElement{

    public GraphicalMarketTray(){
        super(18, 5);
        reset();
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

    /**
     * Draws the possible position with relatives numbers where the slide marble
     * can be inserted
     */
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

    /**
     * Fills the matrix of chars with the marbles
     * @param marketTray
     */
    private void drawMarket(Marble[][] marketTray) {
        for(int i = 0; i < marketTray.length; i++){
            for(int j = 0; j < marketTray[i].length; j++){
                symbols[i][j*3] = '\u25CF';
                colours[i][j*3] = Colour.getColourByMarble(marketTray[i][j]);
            }
        }
    }
}
