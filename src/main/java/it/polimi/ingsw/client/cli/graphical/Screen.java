package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightLeaderCard;

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
    private final BackColour[][] backGroundColours = new BackColour[screen_height][screen_width];

    GraphicalDevelopmentCardGrid graphicalDevelopmentCardGrid;
    List<Integer> developmentCardGridCardsToDisplay;

    GraphicalFaithTrack graphicalFaithTrack;

    private static Screen instance;

    public static Screen getInstance(){
        if(instance == null)
            instance = new Screen();
        return instance;
    }

    private Screen() {
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();

        graphicalFaithTrack = new GraphicalFaithTrack();

        this.faith_track_x_anchor = 0;
        this.faith_track_y_anchor = graphicalDevelopmentCardGrid.getWidth() + 1;

        reset();
    }

    public void updateInfo(List<Integer> developmentCardGridCardsToDisplay){
        this.developmentCardGridCardsToDisplay = developmentCardGridCardsToDisplay;
    }

    //TODO: la display è uguale in tutti gli elementi grafici
    public void displayStandardView(){
        reset();
        drawAllElements();
        display();
    }

    private void display(){
        for(int i = 0; i < screen_height; i++){
            for(int j = 0; j < screen_width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + screen[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    private void drawAllElements() {
        drawDevelopmentCardGrid();
        drawFaithTrack();
    }

    private void reset(){
        for(int i = 0; i < screen_height; i++) {
            for (int j = 0; j < screen_width; j++) {
                screen[i][j] = ' ';
                colours[i][j] = Colour.ANSI_DEFAULT;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }

    private void drawDevelopmentCardGrid(){
        graphicalDevelopmentCardGrid.drawDevelopmentCardGrid(developmentCardGridCardsToDisplay);

        int devCardGridWidth = graphicalDevelopmentCardGrid.getWidth();
        int devCardGridHeight = graphicalDevelopmentCardGrid.getHeight();

        Colour[][] colours = graphicalDevelopmentCardGrid.getColours();
        char[][] symbols = graphicalDevelopmentCardGrid.getSymbols();
        BackColour[][] backColours = graphicalDevelopmentCardGrid.getBackGroundColours();

        drawElement(devCardGridHeight, devCardGridWidth, colours, symbols, backColours, devCardGrid_x_anchor, devCardGrid_y_anchor);
    }

    private void drawFaithTrack() {
        graphicalFaithTrack.drawFaithTrack();

        int faithTrackWidth = graphicalFaithTrack.getWidth();
        int faithTrackHeight = graphicalFaithTrack.getHeight();

        Colour[][] colours = graphicalFaithTrack.getColours();
        char[][] symbols = graphicalFaithTrack.getSymbols();
        BackColour[][] backColours = graphicalFaithTrack.getBackGroundColours();

        drawElement(faithTrackHeight, faithTrackWidth, colours, symbols, backColours, faith_track_x_anchor, faith_track_y_anchor);

    }

    private void drawElement(int height, int width, Colour[][] colours, char[][] symbols, BackColour[][] backColours, int x_anchor, int y_anchor){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                this.screen[i + x_anchor][j + y_anchor] = symbols[i][j];
                this.colours[i + x_anchor][j + y_anchor] = colours[i][j];
                this.backGroundColours[i + x_anchor][j + y_anchor] = backColours[i][j];
            }
        }
    }

    public void displaySetUpLeaderCardSelection(List<Integer> IDs){
        reset();
        int x_anchor = screen_height - GraphicalCard.CardHeight;
        int y_anchor = 0;
        int y_step = GraphicalCard.CardWidth + 1;

        for(Integer ID : IDs){
            LightLeaderCard llc = MatchData.getInstance().getLeaderCardByID(ID);
            //TODO: nicknames!
            GraphicalLeaderCard glc = new GraphicalLeaderCard(llc, "raffa");
            glc.drawCard();
            drawElement(GraphicalCard.CardHeight, GraphicalCard.CardWidth, glc.getColours(), glc.getSymbols(),
                    glc.getBackGroundColours(), x_anchor, y_anchor);

            y_anchor += y_step;
        }
        display();
    }
}
