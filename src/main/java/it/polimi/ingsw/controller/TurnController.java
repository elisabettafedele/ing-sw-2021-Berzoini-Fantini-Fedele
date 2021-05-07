package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.model.player.Player;

public class TurnController {
    private int numberOfLeaderActionsDone=0;
    private boolean standardActionDone=false;
    private Action nextAction;
    private Player currentPlayer;
    private Controller controller;
    private boolean interruptible;
    private boolean endTrigger=false;

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

    public void checkFaithTrack(){

    }

    public void nextActionManager(){
        //TODO
    }



    public void incrementNumberOfLeaderActionDone(){
        numberOfLeaderActionsDone++;
    }

    public void setStandardActionDoneToTrue(){
        standardActionDone=true;
    }

    public void setEndTriggerToTrue() {
        endTrigger = true;
    }
}
