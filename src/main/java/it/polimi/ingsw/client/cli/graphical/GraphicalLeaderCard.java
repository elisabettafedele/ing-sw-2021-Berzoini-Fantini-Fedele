package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class GraphicalLeaderCard extends GraphicalCard{

    public GraphicalLeaderCard(LightLeaderCard ldc) {
        super(ldc);
    }

    public void drawCard(){
        reset();
        drawEdges();
        drawID();
        drawVictoryPoints();
        drawActivationCost();
        drawEffect();
        //TODO: draw active/inactive
    }

    private void drawEffect() {
        if(lightCard.getEffectType().equals("PRODUCTION")){
            drawProductionCost();
            drawProductionOutput();
        }else if(lightCard.getEffectType().equals("DISCOUNT")){
            drawDiscountEffect();
        }
    }

    private void drawDiscountEffect() {
        int center = CardWidth/2;
        int begin = center - 4;
        String s = "DISCOUNT";
        for(int i = 0; i < s.length(); i++){
            symbols[CardHeight-3][begin+i] = s.charAt(i);
            colours[CardHeight-3][begin+i] = Colour.ANSI_BRIGHT_WHITE;
        }

        center = CardWidth/2;
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

        //assert cost.size()%3 == 0;

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
