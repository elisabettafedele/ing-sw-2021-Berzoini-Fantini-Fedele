package it.polimi.ingsw.exceptions;

public class ValueNotPresentException extends Exception{
    public ValueNotPresentException(String valueType){
        super("This Value has not " + valueType + " among his values");
    }
}
