package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class LoadLeaderCardsMessage extends MessageToClient {

    private List<LightLeaderCard> leaderCards;
    public LoadLeaderCardsMessage (List<LightLeaderCard> leaderCards){
        super(false);
        this.leaderCards = leaderCards;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.loadLeaderCards(leaderCards);
    }
}
