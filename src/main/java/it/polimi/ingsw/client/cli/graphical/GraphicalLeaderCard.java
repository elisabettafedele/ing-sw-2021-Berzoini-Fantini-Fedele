package it.polimi.ingsw.client.cli.graphical;


import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.Map;

public class GraphicalLeaderCard extends GraphicalCard{

    public GraphicalLeaderCard(LightLeaderCard ldc, String nickname) {
        super(ldc, nickname);
    }

    public void drawCard(){
        reset();
        drawEdges();
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

    private void drawActive(boolean active) {
        if(active){
            colours[1][CardWidth - 4] = Colour.ANSI_BRIGHT_GREEN;
            colours[1][CardWidth - 3] = Colour.ANSI_BRIGHT_GREEN;
        }else{
            colours[1][CardWidth - 4] = Colour.ANSI_BRIGHT_RED;
            colours[1][CardWidth - 3] = Colour.ANSI_BRIGHT_RED;
        }

    }

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

    private void drawExtraDepotEffect() {
        int center = CardWidth/2;
        int begin = center - 4;
        String s = "DEPOT";
        for(int i = 0; i < s.length(); i++){
            symbols[CardHeight-3][begin+i] = s.charAt(i);
            colours[CardHeight-3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[CardHeight-3][begin+s.length()] = ':';
        colours[CardHeight-3][begin+s.length()] = Colour.ANSI_BRIGHT_WHITE;
        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        Colour c = getResourceColor(r);
        symbols[CardHeight-3][begin+s.length()+2] = r.symbol.charAt(0);
        colours[CardHeight-3][begin+s.length()+2] = c;

        drawResourceSlots(r, c);
    }

    private void drawResourceSlots(Resource r, Colour c) {
        int center = CardWidth/2;
        Map<Integer, Integer> leaderDepots = MatchData.getInstance().getLightClientByNickname(this.nickname).getLeaderDepots();
        int quantity;
        try {
            quantity = leaderDepots.get(this.lightCard.getID());
        }catch(NullPointerException e){
            quantity = 0;
        }
        symbols[CardHeight-2][center-1] = '□';
        symbols[CardHeight-2][center+1] = '□';
        if(quantity > 0)
            symbols[CardHeight-2][center-1] = '■';
        if(quantity > 1)
            symbols[CardHeight-2][center+1] = '■';

        colours[CardHeight-2][center-1] = c;
        colours[CardHeight-2][center+1] = c;
    }

    private void drawConversionEffect() {
        int center = CardWidth/2;
        int begin = center - 5;
        String s = "CONVERSION";
        for(int i = 0; i < s.length(); i++){
            symbols[CardHeight-3][begin+i] = s.charAt(i);
            colours[CardHeight-3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[CardHeight-2][center-2] = '\u25CF';
        symbols[CardHeight-2][center] = '→';
        colours[CardHeight-2][center] = Colour.ANSI_WHITE;

        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        Colour c = getResourceColor(r);
        symbols[CardHeight-2][center+2] = r.symbol.charAt(0);
        colours[CardHeight-2][center+2] = c;
    }

    private void drawDiscountEffect() {
        int center = CardWidth/2;
        int begin = center - 4;
        String s = "DISCOUNT";
        for(int i = 0; i < s.length(); i++){
            symbols[CardHeight-3][begin+i] = s.charAt(i);
            colours[CardHeight-3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }

        begin = center;

        Resource r = Resource.valueOf(lightCard.getEffectDescription().get(0));
        symbols[CardHeight-2][begin] = r.symbol.charAt(0);
        Colour c = getResourceColor(r);
        colours[CardHeight-2][begin] = c;

    }

    private void drawActivationCost() {
        if(lightCard.getCostType().equals("RESOURCE"))
            drawCost(1, lightCard.getCost());
        else{
            drawFlagCost();
        }
    }

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
    public void displayCard() {
        super.displayCard();
    }


}
