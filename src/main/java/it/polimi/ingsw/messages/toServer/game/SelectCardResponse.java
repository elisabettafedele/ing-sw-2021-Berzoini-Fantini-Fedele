package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.List;
import java.util.logging.Level;

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

    public String toString(){
        return "chosen the card with ID " + selectedCard;
    }

}
