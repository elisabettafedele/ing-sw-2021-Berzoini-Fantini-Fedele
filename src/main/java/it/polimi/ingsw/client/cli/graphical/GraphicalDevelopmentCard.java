package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class GraphicalDevelopmentCard extends GraphicalCard{


    public GraphicalDevelopmentCard(LightDevelopmentCard ldc) {
        super(ldc);
    }

    //draw itsSelf
    public void drawCard(){
        reset();
        drawEdges();
        drawFlag();
        drawCost(1, lightCard.getCost());
        drawID();
        drawVictoryPoints();
        drawProductionCost();
        drawProductionOutput();
    }


    private void drawFlag() {
        Colour flagColor = getColor(lightCard.getFlagColor());
        int level = getLevel(lightCard);
        for (int j = 0; j <= level; j++){
            symbols[1+j][2] = '\u25CF';
            colours[1+j][2] = flagColor;
        }
    }

    private int getLevel(LightCard ldc){
        return Level.valueOf(ldc.getFlagLevel()).getValue();
    }


}
