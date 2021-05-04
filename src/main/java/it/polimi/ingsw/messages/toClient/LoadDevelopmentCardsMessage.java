package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

import java.util.List;
import java.util.Map;

public class LoadDevelopmentCardsMessage implements MessageToClient{

    private Map<Integer, List<String>> lightDevelopmentCards;

    public LoadDevelopmentCardsMessage(Map<Integer, List<String>> lightDevelopmentCards) {
        this.lightDevelopmentCards = lightDevelopmentCards;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.loadDevelopmentCards(lightDevelopmentCards);
    }
}
