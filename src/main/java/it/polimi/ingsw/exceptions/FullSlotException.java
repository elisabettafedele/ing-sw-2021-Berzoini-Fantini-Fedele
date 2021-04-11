package it.polimi.ingsw.exceptions;

public class FullSlotException extends Exception{
    public FullSlotException() {
        super("The Development Cards Slot already contains three development cards");
    }
}
