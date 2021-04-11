package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * Abstract {@link Card} is the generic representation of all the game's cards. It includes all the attributes that every card has.
 */
public abstract class Card {

    private final int victoryPoints;
    private final Value cost;
    private String pathImageFront;
    private String pathImageBack;
    private boolean used;

    /**
     * Constructs a Card made of
     * @param victoryPoints the number of Victory points obtained at the end of the game
     * @param cost the cost of the card represented with the {@link Value} class
     * @param pathImageFront path to the front side of the card
     * @param pathImageBack path to the back side of the card
     * @throws InvalidArgumentException if victory points are negative or cost is null
     */
    public Card(int victoryPoints, Value cost, String pathImageFront, String pathImageBack) throws InvalidArgumentException {
        if (victoryPoints < 0 || cost == null){
            throw new InvalidArgumentException();
        }
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        this.pathImageFront = pathImageFront;
        this.pathImageBack = pathImageBack;
        this.used = false;
    }

    /**
     * Get the victory points of the card
     * @return the victory points of the card
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Get the cost ({@link Value}) of the card to be purchased/activate
     * @return the cost ({@link Value}) of the card to be purchased/activate
     */
    public Value getCost() {
        return cost;
    }

    /**
     * @return the path to the front side of the card
     */
    public String getPathImageFront() {
        return pathImageFront;
    }

    /**
     * @return the path to the back side of the card
     */
    public String getPathImageBack() {
        return pathImageBack;
    }

    /**
     * Check if the card has already been used in the current turn
     * @return true if the card has already been used in the current turn
     */
    public boolean alreadyUsed() {
        return used;
    }

    /**
     * Use the card in the turn (execute effect)
     * @return true if the usage of the card was possible
     */
    public boolean use(){
        if(this.used == false){
            this.used = true;
            return true;
        }else {
            return false;
        }
    }

    /**
     * Reset the used variable of the card, to be used at the end of each turn
     */
    public void resetUsed(){
        this.used = false;
    }
}
