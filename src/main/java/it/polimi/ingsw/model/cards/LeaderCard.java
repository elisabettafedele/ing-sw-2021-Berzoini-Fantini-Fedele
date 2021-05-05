package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.Objects;

/**
 * The class represents the Leader Cards of the game
 */
public class LeaderCard extends Card {

    private static final long serialVersionUID = -5121414682747056044L;
    private boolean active;
    private Effect effect;

    /**
     *
     * @param victoryPoints the number of victory points obtained at the end of the game
     * @param cost the number and type of {@link Flag} or the number of Faith points needed to activate the card
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

    @Override
    public String toString() {
        return "LeaderCard{" +
                super.toString() +
                "active=" + active +
                ", effect=" + effect +
                '}';
    }

    /**
     * @return the {@link Effect} of the card
     */
    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LeaderCard that = (LeaderCard) o;
        return active == that.active && effect.equals(that.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), active, effect);
    }
}
