package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class MultiplayerEndPhase extends EndPhase{
    @Override
    public void notifyResults() {
        List<Player> players = getController().getPlayers();
        for (Player player : players){

        }
    }
}
