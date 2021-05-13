package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;

import java.util.*;

public class GraphicalDevelopmentCardGrid {


    private final int space = 3; //space beetween cards

    private final int cardWidth = GraphicalCard.CardWidth;
    private final int cardHeight = GraphicalCard.CardHeight;

    private final int width = cardWidth*4 + space*3;
    private final int height = cardHeight*4 + space*3;

    List<String> cardsToDisplay;

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
            this.cardsToDisplay.add("A");
            //this.cardsToDisplay.add(MatchData.getInstance().getDevelopmentCardByID(ID).get(0));
        }
    }

    public void drawDevelopmentCardGrid(){
        reset();
        int x_coord = - cardHeight;
        int y_coord = - cardWidth;
        for(String s : cardsToDisplay){
            GraphicalCard gc = new GraphicalCard(this, s);
            gc.drawOnScreen(x_coord, y_coord);
            x_coord += cardHeight + space;
            y_coord += cardWidth +space;
        }
        displayGrid();
    }

    private void displayGrid() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
               System.out.print(symbols[i][j]);
            }
            System.out.print("\n");
        }
    }

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
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
