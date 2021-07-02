package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ViewInterface;

import java.io.Serializable;

/**
 * General features of a message from the server to the client
 */
public abstract class MessageToClient implements Serializable {
    private boolean timer;

    public MessageToClient(boolean timer){
        this.timer = timer;
    }

    public abstract void handleMessage(ViewInterface view);

    public boolean hasTimer() {
        return timer;
    }
}
