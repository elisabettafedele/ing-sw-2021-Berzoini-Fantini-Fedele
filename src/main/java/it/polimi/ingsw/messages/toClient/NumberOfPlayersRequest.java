package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class NumberOfPlayersRequest implements MessageToClient {
    private boolean isRetry;

    public NumberOfPlayersRequest(boolean isRetry){
        this.isRetry = isRetry;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayNumberOfPlayersRequest(isRetry);
    }
}
