package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class LoadDevelopmentCardGrid implements MessageToClient {
    private List<Integer> availableCardsIds;

    public LoadDevelopmentCardGrid(List<Integer> availableCardsIds) {
        this.availableCardsIds = availableCardsIds;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.loadDevelopmentCardGrid(availableCardsIds);
    }
}
