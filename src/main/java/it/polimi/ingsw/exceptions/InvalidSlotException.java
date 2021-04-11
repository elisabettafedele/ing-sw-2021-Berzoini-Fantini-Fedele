package it.polimi.ingsw.exceptions;

public class InvalidSlotException extends Exception {
    public InvalidSlotException(int level) {
        super("The Development Cards Slot contains a level " + level + " Card, only a level " + (level+1) + " Card can be added");
    }
}
