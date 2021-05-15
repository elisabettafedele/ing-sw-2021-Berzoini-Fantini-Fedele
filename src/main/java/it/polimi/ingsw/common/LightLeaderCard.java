package it.polimi.ingsw.common;

import java.util.List;

public class LightLeaderCard extends LightCard{

    private String effectType;
    private List<String> effectDescription;
    private List<String> effectDescription2; //easier management in case of production effect to split input and output

    //the cost of Leader cards can be either resources or flags
    public LightLeaderCard(List<String> cost, int victoryPoints, boolean used, int ID, String effectType,
                           List<String> effectDescription, List<String> effectDescription2) {
        super(cost, victoryPoints, used, ID);
        this.effectType = effectType;
        this.effectDescription = effectDescription;
        this.effectDescription2 = effectDescription2;
    }

    public String getEffectType() {
        return effectType;
    }

    public List<String> getEffectDescription() {
        return effectDescription;
    }
}
