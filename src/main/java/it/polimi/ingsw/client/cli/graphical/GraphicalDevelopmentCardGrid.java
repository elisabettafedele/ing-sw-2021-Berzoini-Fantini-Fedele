package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;

import java.util.*;

public class GraphicalDevelopmentCardGrid {


    private final int h_space = 3; //horizontal_space beetween cards
    private final int v_space = 1; //vertical_space beetween cards

    private final int cardWidth = GraphicalCard.CardWidth;
    private final int cardHeight = GraphicalCard.CardHeight;

    private final int width = cardWidth*4 + h_space *3;
    private final int height = cardHeight*4 + v_space *3;

    List<LightDevelopmentCard> cardsToDisplay;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];

    //Note that x move vertically and y move horizontally
    public void addPixel(int x, int y, Colour colour, char symbol){
        symbols[x][y] = symbol;
        colours[x][y] = colour;
    }

    //TODO: check if the IDs order respect the actual displaying order, should be right
    public void setCardsToDisplay(List<Integer> cardsToDisplay){
        this.cardsToDisplay = new ArrayList<>();
        Collections.sort(cardsToDisplay, Collections.reverseOrder()); //the IDs of the cards are in order of level and color
        for(Integer ID : cardsToDisplay){
            //this.cardsToDisplay.add("A");
            this.cardsToDisplay.add(MatchData.getInstance().getDevelopmentCardByID(ID));
        }
    }

    public void drawDevelopmentCardGrid(){
        reset();
        int x_coord = - cardHeight - v_space;
        int y_coord = - cardWidth - h_space;
        int count = 0;
        for(LightDevelopmentCard ldc : cardsToDisplay){
            y_coord += cardWidth + h_space;
            if(count%4 == 0){
                x_coord += cardHeight + v_space;
                y_coord = 0;
            }
            GraphicalCard gc = new GraphicalCard(this, ldc);
            gc.drawOnScreen(x_coord, y_coord);
            count ++;
        }
        displayGrid();
    }

    private void displayGrid() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
               System.out.print(colours[i][j].getCode() + symbols[i][j]);
            }
            System.out.print("\n");
        }
    }

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
            }
        }
    }

    /*private String flagColorParser(String s){

    }*/

    private List<String> cardInfoParse(String s){
        int index = s.indexOf("flagColor");
        return null;
    }
}
