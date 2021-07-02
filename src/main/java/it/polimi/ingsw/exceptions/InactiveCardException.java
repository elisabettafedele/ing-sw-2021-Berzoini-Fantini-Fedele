package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when trying to retrieve an effect from an inactive card
 */
public class InactiveCardException extends Exception{
    public InactiveCardException(){
        super("This card is inactive, effect not accessible");
    }
}
