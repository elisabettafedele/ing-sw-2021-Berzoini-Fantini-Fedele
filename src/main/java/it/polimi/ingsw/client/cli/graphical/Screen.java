package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.Value;

import java.io.IOException;
import java.util.*;

/**
 * Class to collect all the graphical element together
 */
public class Screen extends GraphicalElement{

    private final int devCardGridXAnchor = 0;
    private final int devCardGridYAnchor = 0;

    private final int faithTrackXAnchor = 0;
    private final int faithTrackYAnchor = 62;

    private final int devCardSlotsXAnchor = 16;
    private final int devCardSlots_y_anchor = 62;

    private final int ownedLeaderXAnchor = 16;
    private final int ownedLeaderYAnchor = 122;

    private final int marketXAnchor = 1;
    private final int marketYAnchor = 123;

    private final int scoreBoardXAnchor = 1;
    private final int scoreBoardYAnchor = 140;

    private final int warehouseXAnchor = 9;
    private final int warehouseYAnchor = 131;

    private final int strongboxXAnchor = 7;
    private final int strongboxYAnchor = 122;

    GraphicalDevelopmentCardGrid graphicalDevelopmentCardGrid;
    List<Integer> developmentCardGridCardsToDisplay;

    GraphicalFaithTrack graphicalFaithTrack;

    private String nickname;

    private static Screen instance;
    private int longestNickname;

    public static Screen getInstance(){
        if(instance == null)
            instance = new Screen();
        return instance;
    }

    private Screen() {
        super(201, 26);
        graphicalDevelopmentCardGrid = new GraphicalDevelopmentCardGrid();
        developmentCardGridCardsToDisplay = new ArrayList<>();
        reset();
    }

    public void setClientToDisplay(String nickname){
        this.nickname = nickname;
    }

    /**
     * Display the view of a player with all the graphical elements
     */
    public void displayStandardView(){
        nickname = MatchData.getInstance().getCurrentViewNickname();
        //clearConsole();
        System.out.println("\033[H\033[2J");
        System.out.println("\033[H\033[3J");
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

    /**
     * Draw all the graphical elements
     */
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
        drawSideInformations();

    }

    private void drawSideInformations() {
        int currentViewNicknameYAnchor = scoreBoardYAnchor + longestNickname + 11;
        String s1 = "You are viewing the personal";
        String s2 = "board of " + MatchData.getInstance().getCurrentViewNickname();
        String s3 = "Type <-pb + nickname> to see";
        String s4 = "other players' personal board";
        List<String> text= new ArrayList<>(Arrays.asList(s1, s2, s3, s4));

        int i = 1;
        for (String s : text){
            for(int j = 0; j < s.length(); j++){
                symbols[i][j + currentViewNicknameYAnchor] = s.charAt(j);
            }
            i++;
        }
        drawResourceLegend(currentViewNicknameYAnchor, i + 1);
    }

    private void drawResourceLegend(int currentViewNicknameYAnchor, int i) {
        List<Resource> resources = Resource.realValues();
        String s1 = "The resources present in the";
        String s2 = "game are:";
        List<String> text= new ArrayList<>(Arrays.asList(s1, s2));
        for (String s : text){
            for(int j = 0; j < s.length(); j++){
                symbols[i][j + currentViewNicknameYAnchor] = s.charAt(j);
            }
            i++;
        }
        for(Resource r : resources){
            symbols[i][currentViewNicknameYAnchor] = '>';
            symbols[i][currentViewNicknameYAnchor + 2] = r.symbol.charAt(0);
            colours[i][currentViewNicknameYAnchor + 2] = Colour.getColourByResource(r);
            for(int j = 0; j < r.toString().length(); j++){
                symbols[i][currentViewNicknameYAnchor + 4 + j] = r.toString().charAt(j);
            }
            i++;
        }
    }

    private void drawStrongbox() {
        GraphicalStrongbox gs = new GraphicalStrongbox(this.nickname);
        gs.drawStrongbox();
        drawElement(gs.getHeight(), gs.getWidth(), gs.getColours(), gs.getSymbols(), gs.getBackGroundColours(),
                strongboxXAnchor, strongboxYAnchor);
        String label = "Strongbox";
        drawVerticalLabel(strongboxXAnchor, strongboxYAnchor - 2, label);
    }

    private void drawVerticalLabel(int x, int y, String label) {
        for(int i = 0; i < label.length(); i++){
            symbols[x+ i][y] = label.charAt(i);
            colours[x+ i][y] = Colour.ANSI_BRIGHT_BLUE;
        }
    }

    private void drawWarehouse() {
        GraphicalWarehouse gw = new GraphicalWarehouse(this.nickname);
        gw.drawWarehouse();
        drawElement(gw.getHeight(), gw.getWidth(), gw.getColours(), gw.getSymbols(), gw.getBackGroundColours(),
                warehouseXAnchor, warehouseYAnchor);
        drawDepotsNumbers(warehouseXAnchor + 1, warehouseYAnchor + gw.getWidth() + 1);
        String label = "Warehouse";
        drawHorizontalLabel(warehouseXAnchor - 1, warehouseYAnchor - 1, label);
    }

    private void drawScoreBoard() {
        GraphicalScoreBoard gsb = new GraphicalScoreBoard();
        int maxLength = gsb.drawScoreBoard();
        this.longestNickname = maxLength;
        drawElement(gsb.getHeight(), gsb.getWidth(), gsb.getColours(), gsb.getSymbols(), gsb.getBackGroundColours(),
                scoreBoardXAnchor, scoreBoardYAnchor);
        String label = "Scoreboard";
        drawHorizontalLabel(scoreBoardXAnchor - 1, scoreBoardYAnchor + ((maxLength + 9)/2 - 5), label);
    }

    private void drawMarket() {
        GraphicalMarketTray gmt = new GraphicalMarketTray();
        gmt.drawMarketTray();
        drawElement(gmt.getHeight(), gmt.getWidth(), gmt.getColours(), gmt.getSymbols(), gmt.getBackGroundColours(),
                marketXAnchor, marketYAnchor);
        String label = "Market";
        drawHorizontalLabel(marketXAnchor - 1, marketYAnchor + 4, label);
    }

    private void drawDevelopmentCardSlots() {
        int yStep = GraphicalDevelopmentCardGrid.cardWidth +1;
        Stack<Integer>[] developmentCardSlots = MatchData.getInstance().getLightClientByNickname(this.nickname).getDevelopmentCardSlots();

        for(int i = 0; i < developmentCardSlots.length; i++){
            for(int j = 0; j < developmentCardSlots[i].size(); j++){
                LightDevelopmentCard ldc = MatchData.getInstance().getDevelopmentCardByID(developmentCardSlots[i].get(j));
                GraphicalDevelopmentCard gdc = new GraphicalDevelopmentCard(ldc, this.nickname);
                gdc.drawCard();

                int x_anchor = this.devCardSlotsXAnchor + (developmentCardSlots[i].size()*(-2) + 2) + j*2;
                drawElement(gdc.getHeight(), gdc.getWidth(), gdc.getColours(), gdc.getSymbols(), gdc.getBackGroundColours(),
                        x_anchor, this.devCardSlots_y_anchor+i*yStep);

                if(developmentCardSlots[i].size() > 1 && j != 0){
                    symbols[x_anchor][this.devCardSlots_y_anchor+i*yStep] = '╠';
                    symbols[x_anchor][this.devCardSlots_y_anchor+i*yStep+gdc.getWidth()-1] = '╣';

                }
            }
        }
        drawHorizontalLabel(devCardSlotsXAnchor + GraphicalDevelopmentCardGrid.cardHeight, devCardSlots_y_anchor + 10, "Development Cards Slots");
    }

    private void drawHorizontalLabel(int x, int y, String label) {
        for(int i = 0; i < label.length(); i++){
            symbols[x][y + i] = label.charAt(i);
            colours[x][y + i] = Colour.ANSI_BRIGHT_BLUE;
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
                    glc.getBackGroundColours(), this.ownedLeaderXAnchor, this.ownedLeaderYAnchor + i*yStep);
        }
        String label = "Your leader cards";
        drawHorizontalLabel(ownedLeaderXAnchor + GraphicalDevelopmentCardGrid.cardHeight, ownedLeaderYAnchor + 6, label);

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
        drawCompositeElement(graphicalDevelopmentCardGrid, devCardGridXAnchor, devCardGridYAnchor);
        String label = "Development Card Grid";
        drawHorizontalLabel(devCardGridXAnchor + GraphicalDevelopmentCardGrid.cardHeight*3, 19, label);
    }

    private void drawFaithTrack() {
        this.graphicalFaithTrack = new GraphicalFaithTrack(this.nickname);
        graphicalFaithTrack.drawFaithTrack();
        drawCompositeElement(graphicalFaithTrack, faithTrackXAnchor, faithTrackYAnchor);
        String label = "Faith Track";
        drawHorizontalLabel(faithTrackXAnchor + graphicalFaithTrack.getHeight() - 1, faithTrackYAnchor + 10, label);
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

    /**
     * Method to display a list of card outside of the standard view
     * @param IDs the IDs to be displayed
     * @param basicProduction the eventual basic production if ID == 0 is present in IDs
     */
    public void displayCardSelection(List<Integer> IDs, List<Value> basicProduction){
        reset();
        int x_anchor = height - GraphicalDevelopmentCardGrid.cardHeight;
        int y_anchor = 0;
        int y_step = GraphicalDevelopmentCardGrid.cardWidth + 1;
        LightCard lc;
        GraphicalCard gc;
        boolean basicProd = false;

        if(IDs.contains(0)){
            drawBasicProduction(y_step*(IDs.size()-1), x_anchor + 1, basicProduction);
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
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j] + Colour.ANSI_RESET); //
            }
            System.out.print("\n");
        }
    }

    /**
     * Method to display the warehouse outside the standard view
     */
    public void displayWarehouse(){
        reset();
        GraphicalWarehouse gw = new GraphicalWarehouse(this.nickname);
        gw.drawWarehouse();
        drawElement(gw.getHeight(), gw.getWidth(), gw.getColours(), gw.getSymbols(), gw.getBackGroundColours(),
                height-gw.getHeight()-1, 0);
        //drawDepotsNumbers(gw.getHeight(), gw.getWidth());
        //displayASection(height-gw.getHeight()-2, height, 0, width);
    }

    /**
     * Draws the label to indicate the three depots of the warehouse
     * @param h x coordinate
     * @param w y coordinate
     */
    private void drawDepotsNumbers(int h, int w) {
        List<String> depots = new ArrayList<>(Arrays.asList("◄ First Depot", "◄ Second Depot", "◄ Third Depot"));
        int i = 0;
        if(MatchData.getInstance().getAllNicknames().size() > 3){
            depots.remove(0);
            i = 1;
        }

        for(String s : depots){
            for(int j = 0; j < s.length(); j++){
                symbols[h + i*2][w+j] = s.charAt(j);
            }
            i++;
        }
    }


}
