package it.polimi.ingsw.exceptions;

public class ZeroPlayerException extends Exception {
    public ZeroPlayerException(String motivation) {
        super(motivation);
    }
}
