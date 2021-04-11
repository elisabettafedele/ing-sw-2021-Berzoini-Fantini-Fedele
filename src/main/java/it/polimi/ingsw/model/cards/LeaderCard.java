package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * The class represents the Leader Cards of the game
 */
public class LeaderCard extends Card {

    private boolean active;
    private Effect effect;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param cost the number and type of {@link Flag} or the numebr of Faith points needed to activate the card
     * @param effect the {@link Effect} of the card (production, discount, white marble conversion or extra depot)
     * @throws InvalidArgumentException
     */
    public LeaderCard(int victoryPoints, Value cost, Effect effect, String pathImageFront, String pathImageBack) throws InvalidArgumentException {
        super(victoryPoints, cost, pathImageFront, pathImageBack);
        if (effect == null){
            throw new InvalidArgumentException();
        }
        this.active = false;
        this.effect = effect;
    }

    /**
     * Check if the card is active and, thus, usable
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

    /**
     * If the card is active return the {@link Effect}
     * @return the {@link Effect} of the card
     * @throws InactiveCardException if the card is inactive
     */
    public Effect getEffect() throws InactiveCardException {
        if(!isActive()){
            throw new InactiveCardException();
        }
        return this.effect;
    }
}
