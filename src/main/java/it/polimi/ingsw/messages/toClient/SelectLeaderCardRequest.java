package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

import java.util.List;

public class SelectLeaderCardRequest implements MessageToClient{
    List<Integer> leaderCardsIDs;

    public SelectLeaderCardRequest(List<Integer> leaderCardsIDs){
        this.leaderCardsIDs = leaderCardsIDs;
    }
    @Override
    public void handleMessage(VirtualView view) {
        view.displaySelectLeaderCardRequest(leaderCardsIDs);
    }
}
