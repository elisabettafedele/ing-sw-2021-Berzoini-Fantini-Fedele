package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.ValueType;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * The class represents the activation power that produce Faith Points
 */
public class FaithValue extends Value{

    public FaithValue(int quantity) throws InvalidArgumentException {
        super(quantity, ValueType.FAITH);
    }

}
