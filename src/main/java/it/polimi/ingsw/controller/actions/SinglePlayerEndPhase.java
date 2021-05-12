package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.EndPhase;
import it.polimi.ingsw.messages.toClient.TextMessage;

public class SinglePlayerEndPhase extends EndPhase {
    @Override
    public void notifyResults() {
        getController().getConnectionByNickname(getController().getPlayers().get(0).getNickname()).sendMessageToClient(new TextMessage("Match ended, you got " + getController().getPlayers().get(0).getVictoryPoints() + " victory points"));
    }
}
