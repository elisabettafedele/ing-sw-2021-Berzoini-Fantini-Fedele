package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import java.util.List;


public class PlayersReadyToStartMessage implements MessageToClient {
    List<String> nicknames;
    public PlayersReadyToStartMessage(List<String> nicknames){
        this.nicknames = nicknames;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayPlayersReadyToStartMessage(nicknames);
    }
}
