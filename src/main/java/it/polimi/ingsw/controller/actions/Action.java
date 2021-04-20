package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.TurnController;

public interface Action {
    public void execute(TurnController turnController);
    public boolean isExecutable();
    public void reset();
}
