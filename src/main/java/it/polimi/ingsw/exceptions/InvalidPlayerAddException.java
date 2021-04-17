package it.polimi.ingsw.exceptions;

public class InvalidPlayerAddException extends Exception {
    public InvalidPlayerAddException(String motivation) {
        super(motivation);
    }
}
