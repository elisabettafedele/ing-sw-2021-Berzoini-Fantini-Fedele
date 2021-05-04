package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.model.player.Player;

public class TurnController {
    private int numberOfLeaderActionsDone;
    private boolean standardActionDone;
    private Action nextAction;
    private Player currentPlayer;
    private Controller controller;
    private boolean interruptible;

    public Controller getController() {
        return controller;
    }

    public TurnController(Controller controller) {
        this.numberOfLeaderActionsDone = 0;
        this.standardActionDone = false;
        this.interruptible = controller.getGame().getGameMode() != GameMode.MULTI_PLAYER;
        this.controller = controller;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
