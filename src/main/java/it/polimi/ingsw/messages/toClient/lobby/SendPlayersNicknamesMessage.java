package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class SendPlayersNicknamesMessage extends MessageToClient {

    String playerNickname;
    List<String> otherPlayersNicknames;

    public SendPlayersNicknamesMessage(String playerNickname, List<String> otherPlayersNicknames) {
        super(false);
        this.playerNickname = playerNickname;
        this.otherPlayersNicknames = otherPlayersNicknames;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.setNicknames(playerNickname, otherPlayersNicknames);
        otherPlayersNicknames.add(playerNickname);
        view.displayPlayersReadyToStartMessage(otherPlayersNicknames);
    }

    public String toString(){
        return "sending info about players in game";
    }
}
