package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.controller.game_phases.SinglePlayerPlayPhase;
import it.polimi.ingsw.messages.toClient.game.NotifyMarbleTaken;
import it.polimi.ingsw.messages.toClient.matchData.NotifyTakenPopesFavorTile;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.controller.actions.*;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.messages.toClient.game.ChooseActionRequest;
import it.polimi.ingsw.messages.toClient.TextMessage;
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
        checkFaithTrack();
        checkExecutableActions();
        if(isInterruptible && (controller.getGame().getDevelopmentCardGrid().checkEmptyColumn() || endTrigger)){
            controller.endMatch();
        }
        if (!((endTrigger && isInterruptible) || endTurnImmediately || executableActions.values().stream().filter(x->x==true).collect(Collectors.toList()).isEmpty()))
            clientHandler.sendMessageToClient(new ChooseActionRequest(executableActions, standardActionDone));
    }

    public void doAction(int actionChosen){
        possibleActions.get(actionChosen).execute();

    }

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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void checkFaithTrack(){
        boolean isVaticanReport=false;
        if (!isInterruptible) {
            try {
                for(Player p:controller.getGame().getPlayers()){
                    if(controller.getGame().getFaithTrack().isVaticanReport(p.getPersonalBoard().getMarkerPosition())){
                        isVaticanReport=true;
                        break;
                    }
                }
            } catch (InvalidMethodException e) {
                e.printStackTrace();
            } catch (ZeroPlayerException e) {
                e.printStackTrace();
            }
        }
        if(isInterruptible){
            isVaticanReport=controller.getGame().getFaithTrack().isVaticanReport(getCurrentPlayer().getPersonalBoard().getMarkerPosition());
            if(!isVaticanReport){
                isVaticanReport=controller.getGame().getFaithTrack().isVaticanReport(((SinglePlayerPlayPhase) getController().getGamePhase()).getBlackCrossPosition());
            }
        }

        if(isVaticanReport){
            VaticanReportSection vaticanReportSection=controller.getGame().getFaithTrack().getCurrentSection();
            if(!isInterruptible){// multiplayer
                try {
                    for(Player p: controller.getGame().getPlayers()){
                        if(p.getPersonalBoard().getMarkerPosition()>= vaticanReportSection.getStart()){
                            try {
                                p.addVictoryPoints(vaticanReportSection.getPopeFavorPoints());
                                controller.sendMessageToAll(new NotifyTakenPopesFavorTile(p.getNickname(), vaticanReportSection.getStart(), true));
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                        } else {
                            controller.sendMessageToAll(new NotifyTakenPopesFavorTile(p.getNickname(), vaticanReportSection.getStart(), false));
                        }
                    }
                } catch (InvalidMethodException | ZeroPlayerException e) {
                    e.printStackTrace();
                }
            }
            else{ //singleplayer
                try {
                    if(currentPlayer.getPersonalBoard().getMarkerPosition()>=vaticanReportSection.getStart()){
                        currentPlayer.addVictoryPoints(vaticanReportSection.getPopeFavorPoints());
                    }

                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        for(Player p: controller.getPlayers()){
            if(p.getPersonalBoard().getMarkerPosition()>=controller.getGame().getFaithTrack().getLength()){
                ((PlayPhase) controller.getGamePhase()).handleEndTriggered();
            }
        }
        if(isInterruptible&&((SinglePlayerPlayPhase)controller.getGamePhase()).getBlackCrossPosition()>=controller.getGame().getFaithTrack().getLength()){
            ((PlayPhase) controller.getGamePhase()).handleEndTriggered();
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
