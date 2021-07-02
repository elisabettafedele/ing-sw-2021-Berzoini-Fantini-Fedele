package it.polimi.ingsw.common;

import java.io.Serializable;
import java.util.List;

/**
 * Class to build light version of {@link it.polimi.ingsw.model.cards.Card} to send through messages to the client/server
 */
public abstract class LightCard implements Serializable {
    private List<String> cost;
    private int victoryPoints;
    private boolean used;
    private int ID;

    public LightCard(List<String> cost, int victoryPoints, boolean used, int ID) {
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.used = used;
        this.ID = ID;
    }

    public List<String> getCost() {
        return cost;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public boolean isUsed() {
        return used;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "LightCard{" +
                "ID=" + ID +
                '}';
    }

    public abstract List<String> getProductionCost();

    public abstract List<String> getProductionOutput();

    public abstract String getFlagColor();

    public abstract String getFlagLevel();

    public abstract String getEffectType();

    public abstract String getCostType();

    public abstract List<String> getEffectDescription();

    public abstract List<String> getEffectDescription2();

}
