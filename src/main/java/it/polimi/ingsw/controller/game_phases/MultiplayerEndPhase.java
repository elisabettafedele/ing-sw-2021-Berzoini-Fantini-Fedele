package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.messages.toClient.game.GameOverMessage;
import it.polimi.ingsw.model.player.Player;
import java.util.*;
import java.util.stream.Collectors;

public class MultiplayerEndPhase extends EndPhase {

    /**
     * Method to notify the results of the game to all the client connected.
     * If some clients are no longer connected they will receive a message at their return.
     */
    @Override
    public void notifyResults() {
        getController().sendMessageToAll(getEndMessage(false));
        getController().getServer().gameEnded(getController(), getEndMessage(true));
    }

    /**
     * Method to create a {@link GameOverMessage} which contains all the results
     * @param readyForAnotherGame whether the player I am sending the message to is ready to play another game (true only if the player is receiving the results of an old match)
     * @return a {@link GameOverMessage} which contains all the results
     */
    public GameOverMessage getEndMessage(boolean readyForAnotherGame){
        List<Player> sortedPlayers = getController().getPlayers().stream().sorted(Comparator.comparingInt(Player::countPoints).reversed()).collect(Collectors.toList());
        Map<String, Integer> results = new LinkedHashMap<>();

        for (Player player : sortedPlayers){
            results.put(player.getNickname(), player.countPoints());
        }
        return new GameOverMessage(results, readyForAnotherGame);
    }
}
