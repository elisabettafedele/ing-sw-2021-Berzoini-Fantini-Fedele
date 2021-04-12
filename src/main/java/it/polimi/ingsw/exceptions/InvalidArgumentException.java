package it.polimi.ingsw.exceptions;

public class InvalidArgumentException extends Exception{
    public InvalidArgumentException(){
        super("Invalid argument");
    }

    public InvalidArgumentException(String string){
        super("Invalid argument: " + string);
    }
}
