package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.util.List;
import java.util.logging.Level;

public class ChooseLeaderCardsResponse implements MessageToServer {

    private List<Integer> discardedLeaderCards;

    public ChooseLeaderCardsResponse(List<Integer> chosenLeaderCards){
        this.discardedLeaderCards = chosenLeaderCards;
    }
    
    public List<Integer> getDiscardedLeaderCards(){
        return discardedLeaderCards;
    }


    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen his leader cards");
        if (clientHandler.getController() != null)
            clientHandler.getController().handleMessage(this, clientHandler);
    }
}
