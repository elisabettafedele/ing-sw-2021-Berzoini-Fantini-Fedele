package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.util.List;

public class SelectLeaderCardResponse implements  MessageToServer {
    Integer selectedLeaderCard;
    public SelectLeaderCardResponse(Integer selectedLeaderCard){
        this.selectedLeaderCard = selectedLeaderCard;
    }

    public Integer getSelectedLeaderCard() {
        return selectedLeaderCard;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
