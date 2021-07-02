package it.polimi.ingsw.exceptions;

/**
 * Exception thrown when an invalid argument is received by a method
 */
public class InvalidArgumentException extends Exception{
    public InvalidArgumentException(){
        super("Invalid argument");
    }

    public InvalidArgumentException(String string){
        super("Invalid argument: " + string);
    }
}
