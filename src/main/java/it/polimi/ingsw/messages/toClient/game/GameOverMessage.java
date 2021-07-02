package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.Map;

/**
 * Message to communicate the end of the game
 */
public class GameOverMessage extends MessageToClient {
    private Map<String, Integer> results;
    private boolean readyForAnotherGame;

    public GameOverMessage(Map<String, Integer> results, boolean readyForAnotherGame) {
        super(false);
        this.results = results;
        this.readyForAnotherGame = readyForAnotherGame;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayResults(results, readyForAnotherGame);
    }

    public String toString(){
        return "sending results";
    }
}
