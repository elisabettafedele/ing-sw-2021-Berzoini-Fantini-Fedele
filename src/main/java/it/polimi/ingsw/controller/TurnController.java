package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.controller.game_phases.SinglePlayerPlayPhase;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toClient.game.SelectStorageRequest;
import it.polimi.ingsw.messages.toClient.matchData.NotifyTakenPopesFavorTile;
import it.polimi.ingsw.messages.toClient.matchData.UpdateDepotsStatus;
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
import it.polimi.ingsw.model.game.VaticanReportSection;

import java.util.*;
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
    private Map<Resource, Integer> resourcesToRemove;

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
        this.currentPlayer = currentPlayer;
        if (!currentPlayer.isActive()) {
            ((PlayPhase) controller.getGamePhase()).nextTurn();
        } else {
            this.clientHandler = controller.getConnectionByNickname(currentPlayer.getNickname());
            reset();
            setNextAction();
        }
    }

    public void setNextAction(){
        checkFaithTrack();
        checkExecutableActions();
        if(isInterruptible && (controller.getGame().getDevelopmentCardGrid().checkEmptyColumn() || endTrigger)){
            controller.endMatch();
        }
        if (executableActions.values().stream().noneMatch(x -> x))
            endTurn();
        else
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
                                p.getPersonalBoard().setPopesTileStates(controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), true);
                                controller.sendMessageToAll(new NotifyTakenPopesFavorTile(p.getNickname(), controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), true));
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                        } else {
                            p.getPersonalBoard().setPopesTileStates(controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), false);
                            controller.sendMessageToAll(new NotifyTakenPopesFavorTile(p.getNickname(), controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), false));
                        }
                    }
                } catch (InvalidMethodException | ZeroPlayerException e) {
                    e.printStackTrace();
                }
            }
            else{ //singleplayer
                try {
                    if(currentPlayer.getPersonalBoard().getMarkerPosition()>=vaticanReportSection.getStart()){
                        currentPlayer.getPersonalBoard().setPopesTileStates(controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), true);
                        currentPlayer.addVictoryPoints(vaticanReportSection.getPopeFavorPoints());
                        controller.sendMessageToAll(new NotifyTakenPopesFavorTile(currentPlayer.getNickname(), controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), true));
                    }
                    else{
                        currentPlayer.getPersonalBoard().setPopesTileStates(controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), false);
                        controller.sendMessageToAll(new NotifyTakenPopesFavorTile(currentPlayer.getNickname(), controller.getGame().getFaithTrack().getVaticanReportSectionNumberByStart(vaticanReportSection.getStart()), false));
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

    public void removeResources(Map<Resource, Integer> resourcesToRemove){
        this.resourcesToRemove = resourcesToRemove;
        handleRemoveResources();
    }

    /**
     * Method to handle the remove of the next resource from the resourcesToRemove. If there are not more resources to remove, it ends the action
     */
    private void handleRemoveResources(){
        //If I do not have any other resource to remove
        boolean end = true;
        for (Resource resource: resourcesToRemove.keySet())
            if (resourcesToRemove.get(resource) > 0)
                end = false;
        //If I do not have any other resource to remove
        if (end){
            getController().sendMessageToAll(new UpdateDepotsStatus(currentPlayer.getNickname(), currentPlayer.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), currentPlayer.getPersonalBoard().getStrongboxStatus(), currentPlayer.getPersonalBoard().getLeaderStatus()));
            setStandardActionDoneToTrue();
            setNextAction();
            return;
        }
        for (Resource resource: resourcesToRemove.keySet()){
            if (resourcesToRemove.get(resource) > 0){
                //Automatic remove
                if (currentPlayer.getPersonalBoard().countResources().get(resource).equals(resourcesToRemove.get(resource))) {
                    currentPlayer.getPersonalBoard().removeAll(resource);
                    resourcesToRemove.replace(resource, 0);
                } else {
                    clientHandler.sendMessageToClient(new SelectStorageRequest(resource, currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.WAREHOUSE, resource, 1, false), currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX, resource, 1, false), currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.LEADER_DEPOT, resource, 1, false)));
                    return;
                }
            }
        }
        getController().sendMessageToAll(new UpdateDepotsStatus(currentPlayer.getNickname(), currentPlayer.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), currentPlayer.getPersonalBoard().getStrongboxStatus(), currentPlayer.getPersonalBoard().getLeaderStatus()));
        setStandardActionDoneToTrue();
        setNextAction();
    }

    public void removeResource(ResourceStorageType resourceStorageType, Resource resource){
        int previousValue = resourcesToRemove.get(resource);
        assert (previousValue > 0);
        resourcesToRemove.replace(resource, previousValue-1);
        currentPlayer.getPersonalBoard().isResourceAvailableAndRemove( resourceStorageType,resource,1,true);
        handleRemoveResources();
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
        //I set a copy of the game at the end of each turn
        ((PlayPhase) controller.getGamePhase()).setLastTurnGameCopy(controller.getGame());
        clientHandler.sendMessageToClient(new TextMessage("Turn ended"));
        ((PlayPhase) controller.getGamePhase()).nextTurn();
    }

}
