package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.List;

public class LoadLeaderCardsMessage implements MessageToClient{

    private List<LeaderCard> leaderCards;
    public LoadLeaderCardsMessage (List<LeaderCard> leaderCards){
        this.leaderCards = leaderCards;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.loadLeaderCards(leaderCards);
    }
}
