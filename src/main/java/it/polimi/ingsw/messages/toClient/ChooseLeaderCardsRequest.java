package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.List;

public class ChooseLeaderCardsRequest implements MessageToClient{

    List<Integer> leaderCardsIDs;

    public ChooseLeaderCardsRequest(List<Integer> leaderCardsIDs){
        this.leaderCardsIDs = leaderCardsIDs;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseLeaderCardsRequest(leaderCardsIDs);
    }
}
