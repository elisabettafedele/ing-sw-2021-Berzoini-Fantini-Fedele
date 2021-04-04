package it.polimi.ingsw.exceptions;

public class InactiveCardException extends Exception{
    public InactiveCardException(){
        super("This card is inactive, effect not accessible");
    }
}
