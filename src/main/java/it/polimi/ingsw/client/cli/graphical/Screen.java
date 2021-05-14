package it.polimi.ingsw.client.cli.graphical;

import java.util.ArrayList;
import java.util.List;

public class Screen {

    private final static int screen_width = 160;
    private final static int screen_height = 25;

    private int devCardGrid_x_anchor = 0;
    private int devCardGrid_y_anchor = 0;

    private int faith_track_x_anchor;
    private int faith_track_y_anchor;

    private final char[][] screen = new char[screen_height][screen_width];
    private final Colour[][] colours = new Colour[screen_height][screen_width];

    GraphicalDevelopmentCardGrid graphicalDevelopmentCardGrid;
    List<Integer> developmentCardGridCardsToDisplay;

    public Screen() {
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();

        this.faith_track_x_anchor = 0;
        this.faith_track_y_anchor = graphicalDevelopmentCardGrid.getWidth() + 1;

        reset();
    }

    public void updateInfo(List<Integer> developmentCardGridCardsToDisplay){
        this.developmentCardGridCardsToDisplay = developmentCardGridCardsToDisplay;
    }

    public void displayStandardView(){
        reset();
        drawAllElements();
        for(int i = 0; i < screen_height; i++){
            for(int j = 0; j < screen_width; j++){
                System.out.print(colours[i][j].getCode() + screen[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    private void drawAllElements() {
        drawDevelopmentCardGrid();
    }

    private void reset(){
        for(int i = 0; i < screen_height; i++) {
            for (int j = 0; j < screen_width; j++) {
                screen[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
            }
        }
    }

    private void drawDevelopmentCardGrid(){
        graphicalDevelopmentCardGrid.drawDevelopmentCardGrid(developmentCardGridCardsToDisplay);

        int devCardGridWidth = graphicalDevelopmentCardGrid.getWidth();
        int devCardGridHeight = graphicalDevelopmentCardGrid.getHeight();

        Colour[][] colours = graphicalDevelopmentCardGrid.getColours();
        char[][] symbols = graphicalDevelopmentCardGrid.getSymbols();

        for(int i = 0; i < devCardGridHeight; i++){
            for(int j = 0; j < devCardGridWidth; j++){
                this.screen[i + devCardGrid_x_anchor][j + devCardGrid_y_anchor] = symbols[i][j];
                this.colours[i + devCardGrid_x_anchor][j + devCardGrid_y_anchor] = colours[i][j];
            }
        }
    }
}
