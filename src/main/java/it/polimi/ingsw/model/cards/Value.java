package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.Map;

/**
 * The class represents the {@link DevelopmentCard} and {@link LeaderCard} costs and the production powers
 */
public class Value {

    private Map<Flag, Integer> flagValue;
    private Map<Resource, Integer> resourceValue;
    private int faithValue;

    /**
     * Constructs a value associated with a cost or a production power.
     * @param flagValue the {@link Flag} of the activation cost
     * @param resourceValue the type and quantity of the {@link Resource}
     * @param faithValue the quantity of Faith Points
     * @throws InvalidArgumentException
     */
    public Value(Map<Flag, Integer> flagValue, Map<Resource, Integer> resourceValue, int faithValue) throws InvalidArgumentException {
        if (faithValue < 0 ){
            throw new InvalidArgumentException();
        }
        this.flagValue = (flagValue == null || flagValue.isEmpty()) ? null : flagValue;
        this.resourceValue = (resourceValue == null || resourceValue.isEmpty()) ? null : resourceValue;
        this.faithValue = faithValue;
    }

    public Map<Flag, Integer> getFlagValue() {
        return flagValue;
    }

    public Map<Resource, Integer> getResourceValue() {
        return resourceValue;
    }

    public int getFaithValue() {
        return faithValue;
    }
}
