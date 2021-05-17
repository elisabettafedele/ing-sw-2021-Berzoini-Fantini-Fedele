package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphicalDevelopmentCardGrid {
    private final int h_space = 1; //horizontal_space between cards
    private final int v_space = 0; //vertical_space between cards

    private final int cardWidth = GraphicalDevelopmentCard.CardWidth;
    private final int cardHeight = GraphicalDevelopmentCard.CardHeight;

    private final int width = cardWidth*4 + h_space *3;
    private final int height = cardHeight*3 + v_space *3;

    List<LightDevelopmentCard> cardsToDisplay;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];
    private final BackColour[][] backGroundColours = new BackColour[height][width];


    private void setCardsToDisplay(List<Integer> cardsToDisplay){
        this.cardsToDisplay = new ArrayList<>();
        for(Integer ID : cardsToDisplay){
            this.cardsToDisplay.add(MatchData.getInstance().getDevelopmentCardByID(ID));
        }
    }

    public void drawDevelopmentCardGrid(List<Integer> cardsToDisplay){
        setCardsToDisplay(cardsToDisplay);
        reset();
        int x_coord = 0;
        int y_coord = 0;
        for(LightDevelopmentCard ldc : this.cardsToDisplay){
            List<Integer> coordinates = retrieveCoordinates(ldc);
            x_coord = coordinates.get(0);
            y_coord = coordinates.get(1);
            GraphicalDevelopmentCard gdc = new GraphicalDevelopmentCard(ldc);
            gdc.drawCard();
            drawCard(gdc, x_coord, y_coord);
        }
    }

    private void drawCard(GraphicalDevelopmentCard gdc, int x_coord, int y_coord) {

        char[][] cardSymbols = gdc.getSymbols();
        Colour[][] cardColours = gdc.getColours();
        BackColour[][] cardBackColours = gdc.getBackGroundColours();

        for(int i = 0; i < cardHeight; i++){
            for(int j = 0; j < cardWidth; j++){
                symbols[x_coord+i][y_coord+j] = cardSymbols[i][j];
                colours[x_coord+i][y_coord+j] = cardColours[i][j];
                backGroundColours[x_coord+i][y_coord+j] = cardBackColours[i][j];
            }
        }
    }

    private List<Integer> retrieveCoordinates(LightDevelopmentCard ldc) {
        int x = 0;
        int y = 0;
        x = (Level.valueOf(ldc.getFlagLevel()).getValue() * - 1) + 2;
        y = FlagColor.valueOf(ldc.getFlagColor()).getValue();
        x = x*(cardHeight + v_space);
        y = y*(cardWidth + h_space);
        return new ArrayList<>(Arrays.asList(x, y));
    }

    public void displayDevelopmentCardGrid() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_DEFAULT;
                backGroundColours[i][j] = BackColour.ANSI_BG_BLACK;
            }
        }
    }

    char[][] getSymbols() {
        return symbols;
    }

    Colour[][] getColours() {
        return colours;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BackColour[][] getBackGroundColours() {
        return backGroundColours;
    }
}
