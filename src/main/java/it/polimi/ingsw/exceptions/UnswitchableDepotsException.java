package it.polimi.ingsw.exceptions;

public class UnswitchableDepotsException extends Exception{
    public UnswitchableDepotsException(){
        super("The two depots are not switchable");
    }
}
