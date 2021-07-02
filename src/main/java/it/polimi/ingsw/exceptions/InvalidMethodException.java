package it.polimi.ingsw.exceptions;


public class InvalidMethodException extends Exception{
    public InvalidMethodException(String motivation){
        super(motivation);
    }
}
