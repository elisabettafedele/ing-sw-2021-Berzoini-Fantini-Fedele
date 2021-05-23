package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class Screen extends GraphicalElement{

    private final int devCardGrid_x_anchor = 0;
    private final int devCardGrid_y_anchor = 0;

    private final int faith_track_x_anchor = 0;
    private final int faith_track_y_anchor = 62;

    private final int devCardSlots_x_anchor = 16;
    private final int devCardSlots_y_anchor = 62;

    private final int ownedLeader_x_anchor = 16;
    private final int ownedLeader_y_anchor = 122;

    private final int market_x_anchor = 1;
    private final int market_y_anchor = 123;

    private final int scoreBoard_x_anchor = 1;
    private final int scoreBoard_y_anchor = 140;

    private final int warehouse_x_anchor = 9;
    private final int warehouse_y_anchor = 131;

    private final int strongbox_x_anchor = 7;
    private final int strongbox_y_anchor = 122;

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
        super(188, 25);
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();
        reset();
    }

    public void setClientToDisplay(String nickname){
        this.nickname = nickname;
    }

    public void displayStandardView(){
        reset();
        drawAllElements();
        display();
    }

    private void drawAllElements() {
        drawDevelopmentCardGrid();
        drawFaithTrack();
        if(MatchData.getInstance().getLightClientByNickname(this.nickname).getOwnedLeaderCards().size()<3)
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
        int yStep = GraphicalDevelopmentCardGrid.cardWidth +1;
        Stack<Integer>[] developmentCardSlots = MatchData.getInstance().getLightClientByNickname(this.nickname).getDevelopmentCardSlots();

        for(int i = 0; i < developmentCardSlots.length; i++){
            for(int j = 0; j < developmentCardSlots[i].size(); j++){
                LightDevelopmentCard ldc = MatchData.getInstance().getDevelopmentCardByID(developmentCardSlots[i].get(j));
                GraphicalDevelopmentCard gdc = new GraphicalDevelopmentCard(ldc, this.nickname);
                gdc.drawCard();

                int x_anchor = this.devCardSlots_x_anchor + (developmentCardSlots[i].size()*(-2) + 2) + j*2;
                drawElement(gdc.getHeight(), gdc.getWidth(), gdc.getColours(), gdc.getSymbols(), gdc.getBackGroundColours(),
                        x_anchor, this.devCardSlots_y_anchor+i*yStep);

                if(developmentCardSlots[i].size() > 1 && j != 0){
                    symbols[x_anchor][this.devCardSlots_y_anchor+i*yStep] = '╠';
                    symbols[x_anchor][this.devCardSlots_y_anchor+i*yStep+gdc.getWidth()-1] = '╣';

                }
            }
        }
    }

    private void drawOwnedLeaderCards() {
        int yStep = GraphicalDevelopmentCardGrid.cardWidth +1;
        List<Integer> leaderCards = MatchData.getInstance().getLightClientByNickname(this.nickname).getOwnedLeaderCards();

        for(int i = 0; i < leaderCards.size(); i++){
            LightLeaderCard llc = MatchData.getInstance().getLeaderCardByID(leaderCards.get(i));
            GraphicalLeaderCard glc = new GraphicalLeaderCard(llc, this.nickname);
            if(!this.nickname.equals(MatchData.getInstance().getThisClientNickname()))
                glc.drawHiddenCard();
            else
                glc.drawCard();
            drawElement(GraphicalDevelopmentCardGrid.cardHeight, GraphicalDevelopmentCardGrid.cardWidth, glc.getColours(), glc.getSymbols(),
                    glc.getBackGroundColours(), this.ownedLeader_x_anchor, this.ownedLeader_y_anchor + i*yStep);
        }

    }

    private void drawCompositeElement(GraphicalElement ge, int x_anchor, int y_anchor){
        int geWidth = ge.getWidth();
        int geHeight = ge.getHeight();

        Colour[][] colours = ge.getColours();
        char[][] symbols = ge.getSymbols();
        BackColour[][] backColours = ge.getBackGroundColours();

        drawElement(geHeight, geWidth, colours, symbols, backColours, x_anchor, y_anchor);
    }

    private void drawDevelopmentCardGrid(){
        graphicalDevelopmentCardGrid.drawDevelopmentCardGrid(MatchData.getInstance().getDevelopmentCardGrid());
        drawCompositeElement(graphicalDevelopmentCardGrid, devCardGrid_x_anchor, devCardGrid_y_anchor);
    }

    private void drawFaithTrack() {
        this.graphicalFaithTrack = new GraphicalFaithTrack(this.nickname);
        graphicalFaithTrack.drawFaithTrack();
        drawCompositeElement(graphicalFaithTrack, faith_track_x_anchor, faith_track_y_anchor);
    }

    private void drawElement(int height, int width, Colour[][] colours, char[][] symbols, BackColour[][] backColours, int x_anchor, int y_anchor){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                this.symbols[i + x_anchor][j + y_anchor] = symbols[i][j];
                this.colours[i + x_anchor][j + y_anchor] = colours[i][j];
                this.backGroundColours[i + x_anchor][j + y_anchor] = backColours[i][j];
            }
        }
    }

    public void displaySetUpLeaderCardSelection(List<Integer> IDs){
        reset();
        int x_anchor = height - GraphicalDevelopmentCardGrid.cardHeight;
        int y_anchor = 0;
        int y_step = GraphicalDevelopmentCardGrid.cardWidth + 1;

        for(Integer ID : IDs){
            LightLeaderCard llc = MatchData.getInstance().getLeaderCardByID(ID);
            GraphicalLeaderCard glc = new GraphicalLeaderCard(llc, null);
            glc.drawCard();
            drawElement(GraphicalDevelopmentCardGrid.cardHeight, GraphicalDevelopmentCardGrid.cardWidth, glc.getColours(), glc.getSymbols(),
                    glc.getBackGroundColours(), x_anchor, y_anchor);

            y_anchor += y_step;
        }
        int x_start = height - GraphicalDevelopmentCardGrid.cardHeight;
        int x_end = height;
        int y_start = 0;
        int y_end = width;
        displayASection(x_start, x_end, y_start, y_end);
    }

    private void displayASection(int x_start, int x_end, int y_start, int y_end) {
        for(int i = x_start; i < x_end; i++){
            for(int j = y_start; j < y_end; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    public void displayWarehouse(){
        reset();
        GraphicalWarehouse gw = new GraphicalWarehouse(this.nickname);
        gw.drawWarehouse();
        drawElement(gw.getHeight(), gw.getWidth(), gw.getColours(), gw.getSymbols(), gw.getBackGroundColours(),
                height-gw.getHeight()-1, 0);
        drawDepotsNumbers(gw.getHeight(), gw.getWidth());
        displayASection(height-gw.getHeight()-2, height, 0, width);
    }

    private void drawDepotsNumbers(int h, int w) {
        String[] depots = new String[]{"◄ First Depot", "◄ Second Depot", "◄ Third Depot"};

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < depots[i].length(); j++){
                symbols[height - h + i*2][w+j] = depots[i].charAt(j);
            }
        }
    }


}
