package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        super(188, 26);
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();
        reset();
    }

    public void setClientToDisplay(String nickname){
        this.nickname = nickname;
    }

    public void displayStandardView(){
        nickname = MatchData.getInstance().getCurrentViewNickname();
        //clearConsole();
        System.out.flush();
        reset();
        drawAllElements();
        display();
    }

    private void clearConsole()
    {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
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
        drawDepotsNumbers(warehouse_x_anchor + 1, warehouse_y_anchor + gw.getWidth() + 1);
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
        drawLabel(devCardSlots_x_anchor + GraphicalDevelopmentCardGrid.cardHeight, devCardSlots_y_anchor + 10, "Development Cards Slots");
    }

    private void drawLabel(int x, int y, String label) {
        for(int i = 0; i < label.length(); i++){
            symbols[x][y + i] = label.charAt(i);
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

    public void displayCardSelection(List<Integer> IDs, List<Value> basicProduction){
        reset();
        int x_anchor = height - GraphicalDevelopmentCardGrid.cardHeight;
        int y_anchor = 0;
        int y_step = GraphicalDevelopmentCardGrid.cardWidth + 1;
        LightCard lc;
        GraphicalCard gc;
        boolean basicProd = false;

        if(IDs.contains(0)){
            drawBasicProduction(y_step*(IDs.size()-1), x_anchor, basicProduction);
            IDs.remove(Integer.valueOf(0));
            basicProd = true;
        }

        for(Integer ID : IDs){
            if(ID < 49){
                lc = MatchData.getInstance().getDevelopmentCardByID(ID);
                gc = new GraphicalDevelopmentCard(lc, this.nickname);
            }else{
                lc = MatchData.getInstance().getLeaderCardByID(ID);
                gc = new GraphicalLeaderCard(lc, this.nickname);
            }
            gc.drawCard();
            drawElement(GraphicalDevelopmentCardGrid.cardHeight, GraphicalDevelopmentCardGrid.cardWidth, gc.getColours(), gc.getSymbols(),
                    gc.getBackGroundColours(), x_anchor, y_anchor);

            y_anchor += y_step;
        }
        if(basicProd)
            IDs.add(0);
        int x_start = height - GraphicalDevelopmentCardGrid.cardHeight;
        int x_end = height;
        int y_start = 0;
        int y_end = width;
        displayASection(x_start, x_end, y_start, y_end);
    }

    private void drawBasicProduction(int y_anchor, int x_anchor, List<Value> basicProduction) {
        String a = "BASIC  ID:0";
        String b = "PRODUCTION";
        String c = "  POWER";
        List<String> name = new ArrayList<>();
        name.add(a);
        name.add(b);
        name.add(c);

        for(int i = x_anchor; i < x_anchor + 3; i++){
            for(int j = y_anchor; j < y_anchor + name.get(i-x_anchor).length(); j++){
                symbols[i][j+1] = name.get(i-x_anchor).charAt(j-y_anchor);
                colours[i][j+1] = Colour.ANSI_BLUE;
            }
        }
        try {
            if(basicProduction.get(0).getResourceValue().containsKey(Resource.ANY)){
                drawUnselectedBasicProductionPower(x_anchor, y_anchor);
            }else{
                drawSelectedBasicProductionPower(x_anchor, y_anchor, basicProduction);
            }
        } catch (ValueNotPresentException e) {
            e.printStackTrace();
        }
    }

    private void drawSelectedBasicProductionPower(int x_anchor, int y_anchor, List<Value> basicProduction) throws ValueNotPresentException {
        Map<Resource, Integer> cost = basicProduction.get(0).getResourceValue();
        int start = 4;
        symbols[x_anchor + 4][y_anchor + 3] = '1';
        symbols[x_anchor + 4][y_anchor + 5] = '1';
        for(Map.Entry<Resource, Integer> entry : cost.entrySet()){
            symbols[x_anchor + 4][y_anchor + start] = entry.getKey().symbol.charAt(0);
            colours[x_anchor + 4][y_anchor + start] = Colour.getColourByResource(entry.getKey());
            start += 2;
        }
        symbols[x_anchor + 5][y_anchor + 4] = '1';
        start = 5;
        cost = basicProduction.get(1).getResourceValue();
        for(Map.Entry<Resource, Integer> entry : cost.entrySet()){
            symbols[x_anchor + 5][y_anchor + start] = entry.getKey().symbol.charAt(0);
            colours[x_anchor + 5][y_anchor + start] = Colour.getColourByResource(entry.getKey());
            start += 2;
        }
    }


    private void drawUnselectedBasicProductionPower(int x_anchor, int y_anchor) {
        symbols[x_anchor + 4][y_anchor + 3] = '1';
        symbols[x_anchor + 4][y_anchor + 4] = '?';
        symbols[x_anchor + 4][y_anchor + 5] = '1';
        symbols[x_anchor + 4][y_anchor + 6] = '?';

        symbols[x_anchor + 5][y_anchor + 4] = '1';
        symbols[x_anchor + 5][y_anchor + 5] = '?';

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
        //drawDepotsNumbers(gw.getHeight(), gw.getWidth());
        //displayASection(height-gw.getHeight()-2, height, 0, width);
    }

    private void drawDepotsNumbers(int h, int w) {
        String[] depots = new String[]{"◄ First Depot", "◄ Second Depot", "◄ Third Depot"};

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < depots[i].length(); j++){
                symbols[h + i*2][w+j] = depots[i].charAt(j);
            }
        }
    }


}
