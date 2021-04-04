package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.enumerations.EffectType;

public class DifferentEffectTypeException extends Exception{
    public DifferentEffectTypeException(EffectType expectedType, EffectType actualType){
        super("You wanted a " + actualType + " effect, but this is a " + expectedType + " effect");
    }
}
