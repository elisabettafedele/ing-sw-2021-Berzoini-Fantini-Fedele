package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.actions.*;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TurnController {
    private int numberOfLeaderActionsDone=0;
    private boolean standardActionDone=false;
    private Action nextAction;
    private Player currentPlayer;
    private Controller controller;
    private boolean isInterruptible;
    private boolean endTurnImmediately=false;
    private boolean endTrigger=false;
    private List<Action> possibleActions;
    private Map<ActionType, Boolean> executableActions;

    public Controller getController() {
        return controller;
    }

    public TurnController(Controller controller,Player currentPlayer) {
        this.currentPlayer=currentPlayer;
        this.numberOfLeaderActionsDone = 0;
        this.standardActionDone = false;
        this.isInterruptible = controller.getGame().getGameMode() != GameMode.MULTI_PLAYER;
        this.controller = controller;
        this.possibleActions = new ArrayList<>();
        buildActions();
        executableActions.put(ActionType.ACTIVATE_LEADER_CARD,true);
        executableActions.put(ActionType.DISCARD_LEADER_CARD,true);
        executableActions.put(ActionType.ACTIVATE_PRODUCTION,true);
        executableActions.put(ActionType.BUY_DEVELOPMENT_CARD,true);
        executableActions.put(ActionType.TAKE_RESOURCE_FROM_MARKET,true);
    }

    public void start(Player currentPlayer){
        this.currentPlayer=currentPlayer;
        reset();
        while(!((endTrigger && isInterruptible) || endTurnImmediately || executableActions.values().stream().filter(x->x==true).collect(Collectors.toList()).isEmpty())){
            checkExecutableActions();
        }

    }

    public void reset(){
        numberOfLeaderActionsDone=0;
        standardActionDone=false;
        endTurnImmediately=false;
        endTrigger=false;
        for(Action action : possibleActions){
            action.reset(currentPlayer);
        }
        checkExecutableActions();

    }

    public void checkExecutableActions(){
        for(ActionType actionType: executableActions.keySet()){
            executableActions.replace(actionType,true);
        }
        if(standardActionDone){
            executableActions.replace(ActionType.BUY_DEVELOPMENT_CARD,false);
            executableActions.replace(ActionType.TAKE_RESOURCE_FROM_MARKET,false);
            executableActions.replace(ActionType.ACTIVATE_PRODUCTION,false);
        }
        if(numberOfLeaderActionsDone==2){
            executableActions.replace(ActionType.ACTIVATE_LEADER_CARD,false);
            executableActions.replace(ActionType.DISCARD_LEADER_CARD,false);
        }
        for(ActionType actionType: executableActions.keySet()){
            if(executableActions.get(actionType)==true&&!(possibleActions.get(actionType.getValue()).isExecutable())){
                executableActions.replace(actionType,false);
            }
        }
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
        //the order is important, don't change it, it's the same as in the enum
        possibleActions.add(new TakeResourcesFromMarketAction(this));
        possibleActions.add(new BuyDevelopmentCardAction(this));
        possibleActions.add(new ActivateProductionAction(this));
        possibleActions.add(new LeaderCardAction(this, true));
        possibleActions.add(new LeaderCardAction(this, false));
    }



    public boolean isEndTriggered(){
        return this.endTrigger;
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
