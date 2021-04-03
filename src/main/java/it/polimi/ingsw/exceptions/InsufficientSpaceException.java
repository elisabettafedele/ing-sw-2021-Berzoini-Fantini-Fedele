package it.polimi.ingsw.exceptions;

public class InsufficientSpaceException extends Exception{
    public InsufficientSpaceException(int wanted, int available){
        super("You added" + wanted +  "resource(s) to the depot, but only " + available + "slot(s) is (are) still available");
    }
}
