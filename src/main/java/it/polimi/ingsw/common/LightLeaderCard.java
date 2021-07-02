package it.polimi.ingsw.common;

import java.util.List;

/**
 * Light representation of a {@link it.polimi.ingsw.model.cards.LeaderCard}
 */
public class LightLeaderCard extends LightCard{

    private String costType;
    private String effectType;
    private List<String> effectDescription;
    private List<String> effectDescription2; //easier management in case of production effect to split input and output

    //the cost of Leader cards can be either resources or flags
    public LightLeaderCard(List<String> cost, int victoryPoints, boolean used, int ID, String effectType,
                           List<String> effectDescription, List<String> effectDescription2, String costType) {
        super(cost, victoryPoints, used, ID);
        this.effectType = effectType;
        this.effectDescription = effectDescription;
        this.effectDescription2 = effectDescription2;
        this.costType = costType;
    }

    public String getEffectType() {
        return effectType;
    }

    @Override
    public String getCostType() {
        return costType;
    }

    @Override
    public List<String> getEffectDescription() {
        return effectDescription;
    }

    @Override
    public List<String> getEffectDescription2() {
        return effectDescription2;
    }

    @Override
    public List<String> getProductionCost() {
        return null;
    }

    @Override
    public List<String> getProductionOutput() {
        return null;
    }

    @Override
    public String getFlagColor() {
        return null;
    }

    @Override
    public String getFlagLevel() {
        return null;
    }
}
