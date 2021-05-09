package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.actions.*;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.messages.toClient.ChooseActionRequest;
import it.polimi.ingsw.messages.toClient.ChooseProductionPowersRequest;
import it.polimi.ingsw.messages.toClient.TextMessage;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.VaticanReportSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TurnController {
    private ClientHandler clientHandler;
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
    private int actionChosen= -1;

    public Controller getController() {
        return controller;
    }

    public TurnController(Controller controller,Player currentPlayer) {
        this.currentPlayer=currentPlayer;
        this.clientHandler= controller.getConnectionByNickname(currentPlayer.getNickname());
        this.numberOfLeaderActionsDone = 0;
        this.standardActionDone = false;
        this.isInterruptible = controller.getGame().getGameMode() != GameMode.MULTI_PLAYER;
        this.controller = controller;
        this.possibleActions = new ArrayList<>();
        buildActions();
        executableActions = new HashMap<>();
        executableActions.put(ActionType.ACTIVATE_LEADER_CARD,true);
        executableActions.put(ActionType.DISCARD_LEADER_CARD,true);
        executableActions.put(ActionType.ACTIVATE_PRODUCTION,true);
        executableActions.put(ActionType.BUY_DEVELOPMENT_CARD,true);
        executableActions.put(ActionType.TAKE_RESOURCE_FROM_MARKET,true);
    }

    public void start(Player currentPlayer){
        this.currentPlayer=currentPlayer;
        this.clientHandler= controller.getConnectionByNickname(currentPlayer.getNickname());
        reset();
        setNextAction();

    }

    public void setNextAction(){
        checkExecutableActions();
        if(isInterruptible && controller.getGame().getDevelopmentCardGrid().checkEmptyColumn()){
            endTurnImmediately=true;
        }
        if (!((endTrigger && isInterruptible) || endTurnImmediately || executableActions.values().stream().filter(x->x==true).collect(Collectors.toList()).isEmpty()))
            clientHandler.sendMessageToClient(new ChooseActionRequest(executableActions, standardActionDone));
    }

    public void doAction(int actionChosen){
        possibleActions.get(actionChosen).execute();
    }


    /*****************************************************
    public void start(Player currentPlayer){
        this.currentPlayer=currentPlayer;
        this.clientHandler= controller.getConnectionByNickname(currentPlayer.getNickname());
        reset();
        while(!((endTrigger && isInterruptible) || endTurnImmediately || executableActions.values().stream().filter(x->x==true).collect(Collectors.toList()).isEmpty())){
            checkExecutableActions();
            clientHandler.sendMessageToClient(new ChooseActionRequest(executableActions));
            while(actionChosen==-1){
                //da fare in un thread
            }
            if(executableActions.get(ActionType.valueOf(actionChosen))==true){
                possibleActions.get(actionChosen).execute();
            }
            actionChosen=-1;
            if(isInterruptible&&controller.getGame().getDevelopmentCardGrid().checkEmptyColumn()){
                endTurnImmediately=true;
            }
        }

    }****************************************************/
    private void reset(){
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

    public void setActionChosen(int actionChosen){
        this.actionChosen=actionChosen;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void checkFaithTrack(){
        if(controller.getGame().getFaithTrack().isVaticanReport(currentPlayer.getPersonalBoard().getMarkerPosition())){
            VaticanReportSection vaticanReportSection=controller.getGame().getFaithTrack().getCurrentSection();
            if(!isInterruptible){
                try {
                    for(Player p: controller.getGame().getPlayers()){
                        if(p.getPersonalBoard().getMarkerPosition()>= vaticanReportSection.getStart()){
                            try {
                                p.addVictoryPoints(vaticanReportSection.getPopeFavorPoints());
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (InvalidMethodException e) {
                    e.printStackTrace();
                } catch (ZeroPlayerException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    currentPlayer.addVictoryPoints(vaticanReportSection.getPopeFavorPoints());
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
            if(currentPlayer.getPersonalBoard().getMarkerPosition()==controller.getGame().getFaithTrack().getLength()){
                endTrigger=true;
            }

        }

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

    public void endTurn(){
        clientHandler.sendMessageToClient(new TextMessage("Turn ended"));
        ((PlayPhase) controller.getGamePhase()).nextTurn();
    }

}
