package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class ChooseLeaderCardsRequest implements MessageToClient {

    List<Integer> leaderCardsIDs;

    public ChooseLeaderCardsRequest(List<Integer> leaderCardsIDs){
        this.leaderCardsIDs = leaderCardsIDs;
    }

    @Override
    public void handleMessage(VirtualView view) {

        //System.out.println(this.toString());
        view.displayChooseLeaderCardsRequest(leaderCardsIDs);
    }
}
