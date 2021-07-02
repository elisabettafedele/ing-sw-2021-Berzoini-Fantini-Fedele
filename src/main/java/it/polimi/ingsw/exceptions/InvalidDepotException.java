package it.polimi.ingsw.exceptions;


public class InvalidDepotException extends Exception{
    public InvalidDepotException(String additionalInformation){
        super(additionalInformation);
    }
}
