package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * The class represents the Leader Cards of the game
 */
public class LeaderCard extends Card{

    private boolean active;
    private Effect effect;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param ID the unique ID of the card
     * @param cost the number and type of {@link Flag} or the numebr of Faith points needed to activate the card
     * @param effect the {@link Effect} of the card (production, discount, white marble conversion or extra depot)
     * @throws InvalidArgumentException
     */
    public LeaderCard(int victoryPoints, int ID, Value cost, Effect effect) throws InvalidArgumentException {
        super(victoryPoints, ID, cost);
        if (effect == null){
            throw new InvalidArgumentException();
        }
        this.active = false;
        this.effect = effect;
    }

    /**
     *
     * @return true if the card is active and his effect can be used in this turn.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activate the card in order to be used in the next turn of the player.
     * @return true if the card was not active.
     */
    public boolean activate() {
        if(isActive()){
            return false;
        }
        this.active = true;
        return true;
    }

    public Effect getEffect() throws InactiveCardException {
        if(!isActive()){
            throw new InactiveCardException();
        }
        return this.effect;
    }
}
