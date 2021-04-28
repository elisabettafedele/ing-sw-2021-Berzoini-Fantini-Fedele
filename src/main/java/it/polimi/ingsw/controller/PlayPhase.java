package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;

public interface PlayPhase {
    public void handleResourceDiscard() throws InvalidMethodException, ZeroPlayerException, InvalidArgumentException;

}
