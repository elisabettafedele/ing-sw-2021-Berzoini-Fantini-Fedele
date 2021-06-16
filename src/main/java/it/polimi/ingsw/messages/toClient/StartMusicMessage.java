package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class StartMusicMessage extends MessageToClient{

    public StartMusicMessage() {
        super(false);
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.startMusic();
    }
}
