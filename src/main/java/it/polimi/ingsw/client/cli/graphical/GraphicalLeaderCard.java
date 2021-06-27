package it.polimi.ingsw.client.cli.graphical;


import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.Map;

/**
 * Class to represents a leader card in the cli
 */
public class GraphicalLeaderCard extends GraphicalCard{

    public GraphicalLeaderCard(LightCard ldc, String nickname) {
        super(ldc, nickname);
    }

    @Override
    public void drawCard(){
        reset();
        drawEdges(this.height, this.width);
        drawID();
        drawVictoryPoints();
        drawActivationCost();
        drawEffect();
        boolean active;
        try {
            active = MatchData.getInstance().getLightClientByNickname(this.nickname).
                    leaderCardIsActive(this.lightCard.getID());
        }catch(Exception e){
            active = false;
        }
        drawActive(active);
    }

    /**
     * Paints the ID green if the card is active, red if it's not
     * @param active True if the card is active
     */
    private void drawActive(boolean active) {
        if(active){
            colours[1][width - 4] = Colour.ANSI_BRIGHT_GREEN;
            colours[1][width - 3] = Colour.ANSI_BRIGHT_GREEN;
        }else{
            colours[1][width - 4] = Colour.ANSI_BRIGHT_RED;
            colours[1][width - 3] = Colour.ANSI_BRIGHT_RED;
        }

    }

    /**
     * Draw the effect fo the card
     */
    private void drawEffect() {
        if(lightCard.getEffectType().equals("PRODUCTION")){
            drawProductionCost();
            drawProductionOutput();
        }else if(lightCard.getEffectType().equals("DISCOUNT")){
            drawDiscountEffect();
        }else if(lightCard.getEffectType().equals(("WHITE_MARBLE"))){
            drawConversionEffect();
        }else
            drawExtraDepotEffect();
    }

    /**
     * Draw the representation of the leader depots
     */
    private void drawExtraDepotEffect() {
        int center = width /2;
        int begin = center - 4;
        String s = "DEPOT";
        for(int i = 0; i < s.length(); i++){
            symbols[height -3][begin+i] = s.charAt(i);
            colours[height -3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[height -3][begin+s.length()] = ':';
        colours[height -3][begin+s.length()] = Colour.ANSI_BRIGHT_WHITE;
        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        Colour c = getResourceColor(r);
        symbols[height -3][begin+s.length()+2] = r.symbol.charAt(0);
        colours[height -3][begin+s.length()+2] = c;

        drawResourceSlots(r, c);
    }

    /**
     * Draw the slot of leader depots
     * @param r the type of resource to be stored
     * @param c the colour of the depots
     */
    private void drawResourceSlots(Resource r, Colour c) {
        int center = width /2;
        Map<Integer, Integer> leaderDepots = MatchData.getInstance().getLightClientByNickname(this.nickname).getLeaderDepots();
        int quantity;
        try {
            quantity = leaderDepots.get(this.lightCard.getID());
        }catch(NullPointerException e){
            quantity = 0;
        }
        symbols[height -2][center-1] = '□';
        symbols[height -2][center+1] = '□';
        if(quantity > 0)
            symbols[height -2][center-1] = '■';
        if(quantity > 1)
            symbols[height -2][center+1] = '■';

        colours[height -2][center-1] = c;
        colours[height -2][center+1] = c;
    }

    /**
     * Draw the graphical representation of the conversion of the white marble
     */
    private void drawConversionEffect() {
        int center = width /2;
        int begin = center - 5;
        String s = "CONVERSION";
        for(int i = 0; i < s.length(); i++){
            symbols[height -3][begin+i] = s.charAt(i);
            colours[height -3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[height -2][center-2] = '\u25CF';
        colours[height -2][center-2] = Colour.ANSI_BRIGHT_WHITE;
        symbols[height -2][center] = '→';
        colours[height -2][center] = Colour.ANSI_WHITE;

        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        Colour c = getResourceColor(r);
        symbols[height -2][center+2] = r.symbol.charAt(0);
        colours[height -2][center+2] = c;
    }

    /**
     * Draw the representation of the discount effect of the leader cards
     */
    private void drawDiscountEffect() {
        int center = width /2;
        int begin = center - 4;
        String s = "DISCOUNT";
        for(int i = 0; i < s.length(); i++){
            symbols[height -3][begin+i] = s.charAt(i);
            colours[height -3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }

        begin = center;

        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        symbols[height -2][begin] = r.symbol.charAt(0);
        Colour c = getResourceColor(r);
        colours[height -2][begin] = c;

    }

    /**
     * Draw the activation cost of the leader card (flags or resources)
     */
    private void drawActivationCost() {
        if(lightCard.getCostType().equals("RESOURCE"))
            drawCost(1, lightCard.getCost());
        else{
            drawFlagCost();
        }
    }

    /**
     * Draw the activation cost in terms of flag
     */
    private void drawFlagCost() {
        List<String> cost = lightCard.getCost();

        assert cost.size()%3 == 0;

        for(int i = 0; i < cost.size()/3; i++){
            String quantity = cost.get(i*3);
            Colour flagColor = getColor(cost.get(1+i*3));
            int level = getLevel(cost.get(2+i*3));
            level = level == 3 ? 0 : level;
            for (int j = 0; j <= level; j++){
                symbols[1][2+i*2] = quantity.charAt(0);
                colours[1][2+i*2] = Colour.ANSI_BRIGHT_WHITE;
                symbols[1+j][3+i*2] = '\u25CF';
                colours[1+j][3+i*2] = flagColor;
            }
        }
    }

    private int getLevel(String level) {
        return Level.valueOf(level).getValue();
    }

    @Override
    public void display() {
        super.display();
    }

    /**
     * Draw the back of the card (only the edges)
     */
    public void drawHiddenCard() {
        boolean active;
        try {
            active = MatchData.getInstance().getLightClientByNickname(this.nickname).
                    leaderCardIsActive(this.lightCard.getID());
        }catch(Exception e){
            active = false;
        }
        if(active){
            drawCard();
        }else{
            reset();
            drawEdges(this.height, this.width);
        }
    }
}
