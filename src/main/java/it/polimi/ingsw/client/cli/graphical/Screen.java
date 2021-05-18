package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;

import java.util.ArrayList;
import java.util.List;

public class Screen {

    private final static int screen_width = 160;
    private final static int screen_height = 25;

    private final int devCardGrid_x_anchor = 0;
    private final int devCardGrid_y_anchor = 0;

    private final int faith_track_x_anchor = 0;
    private final int faith_track_y_anchor = 60;

    private final int devCardSlots_x_anchor = 16;
    private final int devCardSlots_y_anchor = 82;

    private final int market_x_anchor = 8;
    private final int market_y_anchor = 120;

    private final int scoreBoard_x_anchor = 1;
    private final int scoreBoard_y_anchor = 122;

    private final int warehouse_x_anchor = 17;
    private final int warehouse_y_anchor = 72;

    private final int strongbox_x_anchor = 15;
    private final int strongbox_y_anchor = 62;

    private final char[][] screen = new char[screen_height][screen_width];
    private final Colour[][] colours = new Colour[screen_height][screen_width];
    private final BackColour[][] backGroundColours = new BackColour[screen_height][screen_width];

    GraphicalDevelopmentCardGrid graphicalDevelopmentCardGrid;
    List<Integer> developmentCardGridCardsToDisplay;

    GraphicalFaithTrack graphicalFaithTrack;

    private String nickname;

    private static Screen instance;

    public static Screen getInstance(){
        if(instance == null)
            instance = new Screen();
        return instance;
    }

    private Screen() {
        //TODO: remove the following two lines, leave only reset()
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();
        reset();
    }

    public void updateInfo(List<Integer> developmentCardGridCardsToDisplay){
        this.developmentCardGridCardsToDisplay = developmentCardGridCardsToDisplay;
    }

    public void setClientToDisplay(String nickname){
        this.nickname = nickname;
    }

    public void displayStandardView(){
        reset();
        drawAllElements();
        display();
    }

    //TODO: la display è uguale in tutti gli elementi grafici
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
        drawOwnedLeaderCards();
        drawDevelopmentCardSlots();
        drawMarket();
        drawScoreBoard();
        drawWarehouse();
        drawStrongbox();
    }

    private void drawStrongbox() {
        GraphicalStrongbox gs = new GraphicalStrongbox(this.nickname);
        gs.drawStrongbox();
        drawElement(gs.getHeight(), gs.getWidth(), gs.getColours(), gs.getSymbols(), gs.getBackGroundColours(),
                strongbox_x_anchor, strongbox_y_anchor);
    }

    private void drawWarehouse() {
        GraphicalWarehouse gw = new GraphicalWarehouse(this.nickname);
        gw.drawWarehouse();
        drawElement(gw.getHeight(), gw.getWidth(), gw.getColours(), gw.getSymbols(), gw.getBackGroundColours(),
                warehouse_x_anchor, warehouse_y_anchor);
    }

    private void drawScoreBoard() {
        GraphicalScoreBoard gsb = new GraphicalScoreBoard();
        gsb.drawScoreBoard();
        drawElement(gsb.getHeight(), gsb.getWidth(), gsb.getColours(), gsb.getSymbols(), gsb.getBackGroundColours(),
                scoreBoard_x_anchor, scoreBoard_y_anchor);
    }

    private void drawMarket() {
        GraphicalMarketTray gmt = new GraphicalMarketTray();
        gmt.drawMarketTray();
        drawElement(gmt.getHeight(), gmt.getWidth(), gmt.getColours(), gmt.getSymbols(), gmt.getBackGroundColours(),
                market_x_anchor, market_y_anchor);
    }

    private void drawDevelopmentCardSlots() {
        int yStep = GraphicalCard.CardWidth+1;
        int[] developmentCards = MatchData.getInstance().getLightClientByNickname(this.nickname).getOwnedDevelopmentCards();
        for(int i = 0; i < developmentCards.length; i++){
            if(developmentCards[i] != MatchData.EMPTY_SLOT){
                LightDevelopmentCard ldc = MatchData.getInstance().getDevelopmentCardByID(developmentCards[i]);
                GraphicalDevelopmentCard gd = new GraphicalDevelopmentCard(ldc, this.nickname);
                gd.drawCard();
                drawElement(GraphicalCard.CardHeight, GraphicalCard.CardWidth, gd.getColours(), gd.getSymbols(),
                        gd.getBackGroundColours(), this.devCardSlots_x_anchor, this.devCardSlots_y_anchor + i*yStep);
            }

        }
    }

    private void drawOwnedLeaderCards() {
        int yStep = GraphicalCard.CardWidth+1;
        List<Integer> leaderCards = MatchData.getInstance().getLightClientByNickname(this.nickname).getOwnedLeaderCards();

        for(int i = 0; i < leaderCards.size(); i++){
            LightLeaderCard llc = MatchData.getInstance().getLeaderCardByID(leaderCards.get(i));
            GraphicalLeaderCard glc = new GraphicalLeaderCard(llc, this.nickname);
            glc.drawCard();
            drawElement(GraphicalCard.CardHeight, GraphicalCard.CardWidth, glc.getColours(), glc.getSymbols(),
                    glc.getBackGroundColours(), this.devCardSlots_x_anchor, this.devCardSlots_y_anchor + (i+3)*yStep);
        }

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
        this.graphicalFaithTrack = new GraphicalFaithTrack(this.nickname);
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
            GraphicalLeaderCard glc = new GraphicalLeaderCard(llc, null);
            glc.drawCard();
            drawElement(GraphicalCard.CardHeight, GraphicalCard.CardWidth, glc.getColours(), glc.getSymbols(),
                    glc.getBackGroundColours(), x_anchor, y_anchor);

            y_anchor += y_step;
        }
        display();
    }
}
