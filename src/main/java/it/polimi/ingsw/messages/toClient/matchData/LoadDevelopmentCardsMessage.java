package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

/**
 * Message to load all the development cards client-side
 */
public class LoadDevelopmentCardsMessage extends MessageToClient {

    private List<LightDevelopmentCard> lightDevCards;

    public LoadDevelopmentCardsMessage(List<LightDevelopmentCard> lightDevCards) {
        super(false);
        this.lightDevCards = lightDevCards;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.loadDevelopmentCards(lightDevCards);
    }
}
