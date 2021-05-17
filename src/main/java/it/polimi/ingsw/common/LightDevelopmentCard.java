package it.polimi.ingsw.common;

import java.util.List;

public class LightDevelopmentCard extends LightCard{

    private String flagColor;
    private String flagLevel;

    private List<String> productionCost;
    private List<String> productionOutput;

    public LightDevelopmentCard(List<String> cost, int victoryPoints, boolean used, int ID, String flagColor, String flagLevel,
                                List<String> productionCost, List<String> productionOutput) {
        super(cost, victoryPoints, used, ID);
        this.flagColor = flagColor;
        this.flagLevel = flagLevel;
        this.productionCost = productionCost;
        this.productionOutput = productionOutput;
    }

    public String getFlagColor() {
        return flagColor;
    }

    public String getFlagLevel() {
        return flagLevel;
    }

    @Override
    public String getEffectType() {
        return "PRODUCTION";
    }

    @Override
    public String getCostType() {
        return "FLAG";
    }

    @Override
    public List<String> getEffectDescription() {
        return productionCost;
    }

    @Override
    public List<String> getEffectDescription2() {
        return productionOutput;
    }

    public List<String> getProductionCost() {
        return productionCost;
    }

    public List<String> getProductionOutput() {
        return productionOutput;
    }

}
