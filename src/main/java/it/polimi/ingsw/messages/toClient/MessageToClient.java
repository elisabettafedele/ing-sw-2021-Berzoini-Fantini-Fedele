package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

import java.io.Serializable;

public abstract class MessageToClient implements Serializable {
    private boolean timer;

    public MessageToClient(boolean timer){
        this.timer = timer;
    }

    public abstract void handleMessage(VirtualView view);

    public boolean hasTimer() {
        return timer;
    }
}
