package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class LoadDevelopmentCardsMessage implements MessageToClient {

    private List<LightDevelopmentCard> lightDevCards;

    public LoadDevelopmentCardsMessage(List<LightDevelopmentCard> lightDevCards) {
        this.lightDevCards = lightDevCards;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.loadDevelopmentCards(lightDevCards);
    }
}
