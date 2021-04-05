package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * Abstract {@link Card} is the generic representation of all the game's cards. It includes all the attributes that every card has.
 */
public abstract class Card {

    private int victoryPoints;
    private int ID;
    private Value cost;
    private boolean used;

    public Card(int victoryPoints, int ID, Value cost) throws InvalidArgumentException {
        if (victoryPoints < 0 || cost == null){
            throw new InvalidArgumentException();
        }
        this.victoryPoints = victoryPoints;
        this.ID = ID;
        this.cost = cost;
        this.used = false;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getID() {
        return ID;
    }

    public Value getCost() {
        return cost;
    }

    public boolean alreadyUsed() {
        return used;
    }

    public boolean use(){
        if(this.used == false){
            this.used = true;
            return true;
        }else {
            return false;
        }
    }
}
