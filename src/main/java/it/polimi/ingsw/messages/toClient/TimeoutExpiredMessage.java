package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class TimeoutExpiredMessage extends MessageToClient{

    public TimeoutExpiredMessage() {
        super(false);
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayTimeoutExpiredMessage();
    }
}
