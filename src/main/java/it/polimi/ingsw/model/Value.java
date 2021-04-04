package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.List;
import java.util.Map;

/**
 * The class represents the {@link DevelopmentCard} and {@link LeaderCard} costs and the production powers
 */
public class Value {

    private List<Flag> flagValue;
    private Map<Resource, Integer> resourceValue;
    private int faithValue;

    /**
     * Constructs a value associated with a cost or a production power.
     * @param flagValue the {@link Flag} of the activation cost
     * @param resourceValue the type and quantity of the {@link Resource}
     * @param faithValue the quantity of Faith Points
     * @throws InvalidArgumentException
     */
    public Value(List<Flag> flagValue, Map<Resource, Integer> resourceValue, int faithValue) throws InvalidArgumentException {
        if (faithValue < 0 || flagValue == null || resourceValue == null){
            throw new InvalidArgumentException();
        }
        this.flagValue = flagValue;
        this.resourceValue = resourceValue;
        this.faithValue = faithValue;
    }
    /**
     * Constructs a value associated with a cost or a production power.
     * @param flagValue the {@link Flag} of the activation cost
     * @param resourceValue the type and quantity of the {@link Resource}
     * @throws InvalidArgumentException
     */
    public Value(List<Flag> flagValue, Map<Resource, Integer> resourceValue) throws InvalidArgumentException {
        if (flagValue == null || resourceValue == null){
            throw new InvalidArgumentException();
        }
        this.flagValue = flagValue;
        this.resourceValue = resourceValue;
        this.faithValue = 0;
    }

    /**
     * Constructs a value associated with a cost or a production power.
     * @param flagValue the {@link Flag} of the activation cost
     * @param faithValue the quantity of Faith Points
     * @throws InvalidArgumentException
     */
    public Value(List<Flag> flagValue, int faithValue) throws InvalidArgumentException {
        if (faithValue < 0 || flagValue == null){
            throw new InvalidArgumentException();
        }
        this.flagValue = flagValue;
        this.resourceValue = null;
        this.faithValue = faithValue;
    }
    /**
     * Constructs a value associated with a cost or a production power.
     * @param resourceValue the type and quantity of the {@link Resource}
     * @param faithValue the quantity of Faith Points
     * @throws InvalidArgumentException
     */
    public Value(Map<Resource, Integer> resourceValue, int faithValue) throws InvalidArgumentException {
        if (faithValue < 0 || resourceValue == null){
            throw new InvalidArgumentException();
        }
        this.flagValue = null;
        this.resourceValue = resourceValue;
        this.faithValue = faithValue;
    }

    /**
     * Constructs a value associated with a cost or a production power.
     * @param flagValue the {@link Flag} of the activation cost
     * @throws InvalidArgumentException
     */
    public Value(List<Flag> flagValue) throws InvalidArgumentException {
        if (flagValue == null){
            throw new InvalidArgumentException();
        }
        this.flagValue = flagValue;
        this.resourceValue = null;
        this.faithValue = 0;
    }

    /**
     * Constructs a value associated with a cost or a production power.
     * @param resourceValue the type and quantity of the {@link Resource}
     * @throws InvalidArgumentException
     */
    public Value(Map<Resource, Integer> resourceValue) throws InvalidArgumentException {
        if(resourceValue==null){
            throw new InvalidArgumentException();
        }
        this.resourceValue = resourceValue;
        this.flagValue = null;
        this.faithValue = 0;
    }

    /**
     * Constructs a value associated with a cost or a production power.
     * @param faithValue the quantity of Faith Points
     * @throws InvalidArgumentException
     */
    public Value(int faithValue) throws InvalidArgumentException {
        if (faithValue < 0){
            throw new InvalidArgumentException();
        }
        this.faithValue = faithValue;
        this.flagValue = null;
        this.resourceValue = null;
    }

    public List<Flag> getFlagValue() {
        return flagValue;
    }

    public Map<Resource, Integer> getResourceValue() {
        return resourceValue;
    }

    public int getFaithValue() {
        return faithValue;
    }
}
