package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.PlayPhase;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.messages.toClient.SelectLeaderCardRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.messages.toServer.SelectLeaderCardResponse;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ActivateLeaderCardAction implements Action{
    private Player player;
    private ClientHandler clientHandler;
    public ActivateLeaderCardAction(Player player, ClientHandler clientHandler){
        this.player = player;
        this.clientHandler = clientHandler;
    }
    @Override
    public void execute(TurnController turnController) {
        clientHandler.setCurrentAction(this);
        List<Integer> leaderCardsIDs =new ArrayList<>();
        for(LeaderCard lc : player.getPersonalBoard().getLeaderCards()){
            if(!lc.isActive()){
                leaderCardsIDs.add(lc.getID());
            }
        }
        clientHandler.sendMessageToClient(new SelectLeaderCardRequest(leaderCardsIDs));
    }

    @Override
    public boolean isExecutable() {
        for(LeaderCard lc : player.getPersonalBoard().getLeaderCards()){
            if(!lc.isActive()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleMessage(MessageToServer message) {
        for(LeaderCard lc : player.getPersonalBoard().getLeaderCards()){
            if(!lc.isActive()){
                if(lc.getID()==((SelectLeaderCardResponse) message).getSelectedLeaderCard()){
                    lc.activate();
                }
            }
        }
    }
}
