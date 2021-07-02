package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.messages.toClient.game.NotifyPointsSinglePlayer;

/**
 * Class to manage the end game in single player mode
 */
public class SinglePlayerEndPhase extends EndPhase {
    @Override
    public void notifyResults() {
        getController().getConnectionByNickname(getController().getPlayers().get(0).getNickname()).sendMessageToClient(new NotifyPointsSinglePlayer(getController().getPlayers().get(0).isWinner() ? getController().getPlayers().get(0).countPoints() : -1));
        getController().getServer().gameEnded(getController());
    }
}
