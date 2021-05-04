package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.util.List;

public class ChooseLeaderCardsResponse implements MessageToServer{

    private List<Integer> discardedLeaderCards;

    public ChooseLeaderCardsResponse(List<Integer> chosenLeaderCards){
        this.discardedLeaderCards = chosenLeaderCards;
    }
    
    public List<Integer> getDiscardedLeaderCards(){
        return discardedLeaderCards;
    }


    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getController().handleMessage(this, clientHandler);
    }
}
