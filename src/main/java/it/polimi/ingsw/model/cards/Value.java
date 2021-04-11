package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;

import java.util.Map;
import java.util.Objects;

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

    /**
     * Get the {@link Flag} part of {@link Card}'s cost
     * @return the {@link Flag} part of {@link Card}'s cost
     * @throws ValueNotPresentException if this value has no flags
     */
    public Map<Flag, Integer> getFlagValue() throws ValueNotPresentException {
        if(flagValue == null){
            throw new ValueNotPresentException("flag");
        }
        return flagValue;
    }

    /**
     * Get the {@link Resource} part of {@link Card}'s cost
     * @return the cost in terms of {@link Resource}
     * @throws ValueNotPresentException if this value has no resources
     */
    public Map<Resource, Integer> getResourceValue() throws ValueNotPresentException {
        if(resourceValue == null){
            throw new ValueNotPresentException("resources");
        }
        return resourceValue;
    }

    /**
     * Get the Faith Value part of {@link Card}'s cost
     * @return the cost in terms of faith points
     * @throws ValueNotPresentException if this value has no faith points
     */
    public int getFaithValue() throws ValueNotPresentException {
        if(faithValue == 0){
            throw new ValueNotPresentException("faith points");
        }
        return faithValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return faithValue == value.faithValue && ((flagValue==null && value.flagValue== null) ||flagValue.equals(value.flagValue)) && ((resourceValue==null && value.resourceValue==null) || resourceValue.equals(value.resourceValue));
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagValue, resourceValue, faithValue);
    }
}
