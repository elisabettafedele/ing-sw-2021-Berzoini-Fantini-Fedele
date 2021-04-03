package it.polimi.ingsw.exceptions;

public class InsufficientQuantityException extends Exception{
    public InsufficientQuantityException(int wanted, int available){
        super("You want to remove " + wanted + " resource(s), but just "+ available + " is (are) available");
    }
}
