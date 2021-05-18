package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;


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
        System.out.println(this.toString());
        view.displayNicknameRequest(isRetry, alreadyTaken);
    }

}
