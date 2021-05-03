package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;


public class NicknameRequest implements MessageToClient {
    private String message;
    private boolean isRetry;
    private boolean alreadyTaken;

    public NicknameRequest(boolean isRetry, boolean alreadyTaken){
        this.isRetry = isRetry;
        this.alreadyTaken = alreadyTaken;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayNicknameRequest(isRetry, alreadyTaken);
    }

}
