package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toClient.SelectCardRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.messages.toServer.SelectCardResponse;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;

import java.util.*;

public class LeaderCardAction implements Action{
    private Player player;
    private ClientHandler clientHandler;
    boolean activateORdiscard;
    List<Integer> leaderCardsIDs =null;
    TurnController turnController;

    public LeaderCardAction(TurnController turnController, boolean activateORdiscard){
        this.player = turnController.getCurrentPlayer();
        this.turnController=turnController;
        this.clientHandler = turnController.getController().getConnectionByNickname(player.getNickname());
        this.activateORdiscard=activateORdiscard;
    }

    @Override
    public void reset(Player currentPlayer) {
        this.player = currentPlayer;
        this.clientHandler = turnController.getController().getConnectionByNickname(currentPlayer.getNickname());
        leaderCardsIDs=new ArrayList<>();
    }

    @Override
    public void execute() {
        clientHandler.setCurrentAction(this);
        clientHandler.sendMessageToClient(new SelectCardRequest(leaderCardsIDs,true));
        turnController.incrementNumberOfLeaderActionDone();
        turnController.checkFaithTrack();
    }

    @Override
    public boolean isExecutable() {
        leaderCardsIDs=new ArrayList<>();
        for(LeaderCard lc : player.getPersonalBoard().getLeaderCards()){
            if(!lc.isActive()){
                if(activateORdiscard){
                    if(isActivable(lc)){
                        leaderCardsIDs.add(lc.getID());
                    }
                }
                else {
                    leaderCardsIDs.add(lc.getID());
                }
            }
        }
        return !leaderCardsIDs.isEmpty();
    }

    @Override
    public void handleMessage(MessageToServer message) {
        for(LeaderCard lc : player.getPersonalBoard().getLeaderCards()){
            if(!lc.isActive()){
                if(lc.getID()==((SelectCardResponse) message).getSelectedCard()){
                    if(activateORdiscard) {lc.activate();}
                    else {
                        player.getPersonalBoard().removeLeaderCard(lc.getID());
                        try {
                            player.getPersonalBoard().moveMarker(1);
                        } catch (InvalidArgumentException e) {
                            //moveMarker's parameter is a constant so the exception won't be launched
                        }

                    }
                    }
                }
            }
        }

    /**
     *
     * @param lc the {@link LeaderCard} that needs to be checked if it's activable
     * @return true if activable, false if lc's activation cost is not satisfied
     */
    private boolean isActivable(LeaderCard lc){
        try {
            int i=0;
            HashMap<Flag,Integer> cost= (HashMap<Flag, Integer>) lc.getCost().getFlagValue();
            HashMap<Flag,Integer> possessedByUser=new HashMap<>();
            List<DevelopmentCard> developmentCards = player.getPersonalBoard().getDevelopmentCards();
            for(Map.Entry<Flag, Integer> entry : cost.entrySet()){
                i=0;
                while(entry.getValue()>0){
                    if(i>=developmentCards.size()){
                    return false;
                    }
                   if(developmentCards.get(i).getFlag().getFlagColor().equals(entry.getKey().getFlagColor())){
                       if(entry.getKey().getFlagLevel().equals(Level.ANY)){
                           entry.setValue(entry.getValue()-1);
                       }
                       else if(entry.getKey().getFlagLevel().equals(developmentCards.get(i).getFlag().getFlagLevel())){
                           entry.setValue(entry.getValue()-1);
                       }
                   }
                   i++;
                }
            }
        } catch (ValueNotPresentException e) {
        }
        return true;

    }
}
