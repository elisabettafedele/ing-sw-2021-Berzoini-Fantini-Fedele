package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class SendPlayerNicknamesMessage implements MessageToClient {

    String playerNickname;
    List<String> otherPlayersNicknames;

    public SendPlayerNicknamesMessage(String playerNickname, List<String> otherPlayersNicknames) {
        this.playerNickname = playerNickname;
        this.otherPlayersNicknames = otherPlayersNicknames;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.setNicknames(playerNickname, otherPlayersNicknames);
        otherPlayersNicknames.add(playerNickname);
        view.displayPlayersReadyToStartMessage(otherPlayersNicknames);
    }
}
