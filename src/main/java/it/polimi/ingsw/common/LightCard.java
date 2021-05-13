package it.polimi.ingsw.common;

import java.io.Serializable;
import java.util.List;

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
}
