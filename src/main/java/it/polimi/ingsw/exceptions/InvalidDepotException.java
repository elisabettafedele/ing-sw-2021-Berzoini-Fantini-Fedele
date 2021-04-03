package it.polimi.ingsw.exceptions;

public class InvalidDepotException extends Exception{

    public InvalidDepotException(){
        super("The depot has no resource type, select it before adding resources");
    }
}
