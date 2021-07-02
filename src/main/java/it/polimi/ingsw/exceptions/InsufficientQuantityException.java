package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when getting a number of resources that is not available
 */
public class InsufficientQuantityException extends Exception{
    public InsufficientQuantityException(int wanted, int available){
        super("You want to remove " + wanted + " resource(s), but just "+ available + " is (are) available");
    }
}
