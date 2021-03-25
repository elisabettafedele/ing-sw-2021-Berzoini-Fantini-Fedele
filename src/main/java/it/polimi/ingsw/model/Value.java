package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.ValueType;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

import static it.polimi.ingsw.enumerations.ValueType.*;

/**
 * The class represents the Development and Leader Card costs and the production powers
 */
public abstract class Value {
    int quantity;
    ValueType type;

    public Value(int quantity, ValueType type) throws InvalidArgumentException {
        if(quantity<0){
            throw new InvalidArgumentException();
        }
        this.quantity = quantity;
        this.type = type;
    }

    public int getQuantity(){
        return quantity;
    }

    public ValueType getType() {
        return type;
    }
}
