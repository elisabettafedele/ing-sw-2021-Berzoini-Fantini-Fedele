package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;

public class LoadLeaderCardsMessage implements MessageToClient{

    private static final long serialVersionUID = -7962181283165725506L;
    private List<LightLeaderCard> leaderCards;
    public LoadLeaderCardsMessage (List<LightLeaderCard> leaderCards){
        this.leaderCards = leaderCards;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.loadLeaderCards(leaderCards);
    }
}
