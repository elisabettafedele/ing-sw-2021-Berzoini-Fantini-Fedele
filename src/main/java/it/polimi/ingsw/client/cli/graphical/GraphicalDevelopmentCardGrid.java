package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphicalDevelopmentCardGrid extends GraphicalElement{

    private static final int h_space = 1; //horizontal_space between cards
    private static final int v_space = 0; //vertical_space between cards

    public static final int cardWidth = 14;
    public static final int cardHeight = 8;

    List<LightDevelopmentCard> cardsToDisplay;

    public GraphicalDevelopmentCardGrid() {
        super(cardWidth*4 + h_space*3, cardHeight*3 + v_space*3);
    }

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
            GraphicalDevelopmentCard gdc = new GraphicalDevelopmentCard(ldc, null);
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
}
