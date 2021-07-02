package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ViewInterface;

/**
 * Message to notify that the timeout has expired
 */
public class TimeoutExpiredMessage extends MessageToClient{

    public TimeoutExpiredMessage() {
        super(false);
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayTimeoutExpiredMessage();
    }
}
