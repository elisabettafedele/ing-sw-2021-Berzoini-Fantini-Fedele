package it.polimi.ingsw.exceptions;

public class InvalidArgumentException extends Exception{
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("a");
    }
}
