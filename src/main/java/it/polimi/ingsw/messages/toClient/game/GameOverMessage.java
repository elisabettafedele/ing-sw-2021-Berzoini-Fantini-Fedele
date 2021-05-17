package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.Map;

public class GameOverMessage implements MessageToClient {
    private Map<String, Integer> results;

    public GameOverMessage(Map<String, Integer> results) {
        this.results = results;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayResults(results);
    }
}
