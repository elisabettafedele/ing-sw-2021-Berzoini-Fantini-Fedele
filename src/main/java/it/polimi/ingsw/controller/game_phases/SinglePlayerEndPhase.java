package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.game_phases.EndPhase;
import it.polimi.ingsw.messages.toClient.GameOverMessage;
import it.polimi.ingsw.messages.toClient.NotifyPointsSinglePlayer;
import it.polimi.ingsw.messages.toClient.TextMessage;

public class SinglePlayerEndPhase extends EndPhase {
    @Override
    public void notifyResults() {
        getController().getConnectionByNickname(getController().getPlayers().get(0).getNickname()).sendMessageToClient(new NotifyPointsSinglePlayer(getController().getPlayers().get(0).getVictoryPoints()));
    }
}
