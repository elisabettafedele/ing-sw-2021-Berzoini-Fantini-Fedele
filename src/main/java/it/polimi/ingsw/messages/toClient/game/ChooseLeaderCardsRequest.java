package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class ChooseLeaderCardsRequest extends MessageToClient {

    /**
     * Message used to ask the client to choose 2 out of the 4 leader cards assigned
     */

    List<Integer> leaderCardsIDs;

    public ChooseLeaderCardsRequest(List<Integer> leaderCardsIDs){
        super(true);
        this.leaderCardsIDs = leaderCardsIDs;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayChooseLeaderCardsRequest(leaderCardsIDs);
    }

    public String toString(){
        return "sending leader cards to choose";
    }
}
