package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.Map;

public class GameOverMessage implements MessageToClient {
    private Map<String, Integer> results;
    private boolean readyForAnotherGame;

    public GameOverMessage(Map<String, Integer> results, boolean readyForAnotherGame) {
        this.results = results;
        this.readyForAnotherGame = readyForAnotherGame;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayResults(results, readyForAnotherGame);
    }

    public String toString(){
        return "sending results";
    }
}
