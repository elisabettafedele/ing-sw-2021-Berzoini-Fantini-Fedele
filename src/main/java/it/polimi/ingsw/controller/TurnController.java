package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.actions.*;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TurnController {
    private int numberOfLeaderActionsDone=0;
    private boolean standardActionDone=false;
    private Action nextAction;
    private Player currentPlayer;
    private Controller controller;
    private boolean interruptible;
    private boolean endTrigger=false;
    private List<Action> possibleActions;

    public Controller getController() {
        return controller;
    }

    public TurnController(Controller controller) {
        this.numberOfLeaderActionsDone = 0;
        this.standardActionDone = false;
        this.interruptible = controller.getGame().getGameMode() != GameMode.MULTI_PLAYER;
        this.controller = controller;
        this.possibleActions = new ArrayList<>();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void checkFaithTrack(){

    }

    public void nextActionManager(){
        //TODO
    }

    public void chooseAction(){

    }

    public List<Action> getAvailableActions(){
        return possibleActions.stream().filter(Action::isExecutable).collect(Collectors.toList());
    }

    private void buildActions(){
        possibleActions.add(new TakeResourcesFromMarketAction(this));
        possibleActions.add(new BuyDevelopmentCardAction(this));
        possibleActions.add(new ActivateProductionAction(this));
        possibleActions.add(new LeaderCardAction(this, true));
        possibleActions.add(new LeaderCardAction(this, false));
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
