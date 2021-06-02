package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.messages.toClient.game.GameOverMessage;
import it.polimi.ingsw.model.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MultiplayerEndPhase extends EndPhase {
    @Override
    public void notifyResults() {
        getController().sendMessageToAll(getEndMessage(false));
        getController().getServer().gameEnded(getController(), getEndMessage(true));
    }

    public GameOverMessage getEndMessage(boolean readyForAnotherGame){
        List<Player> sortedPlayers = getController().getPlayers().stream().sorted(Comparator.comparingInt(Player::getVictoryPoints).reversed()).collect(Collectors.toList());
        Map<String, Integer> results = new LinkedHashMap<>();

        for (Player player : sortedPlayers){
            results.put(player.getNickname(), player.getVictoryPoints());
        }
        return new GameOverMessage(results, readyForAnotherGame);
    }
}
