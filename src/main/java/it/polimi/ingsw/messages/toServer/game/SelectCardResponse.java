package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;

import java.util.List;

public class SelectCardResponse implements MessageToServer {
    Integer selectedCard;
    public SelectCardResponse(Integer selectedCard){
        this.selectedCard = selectedCard;
    }

    public Integer getSelectedCard() {
        return selectedCard;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
