package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;


public class NicknameRequest extends MessageToClient {
    private boolean isRetry;
    private boolean alreadyTaken;

    public NicknameRequest(boolean isRetry, boolean alreadyTaken){
        super(true);
        this.isRetry = isRetry;
        this.alreadyTaken = alreadyTaken;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayNicknameRequest(isRetry, alreadyTaken);
    }

    public String toString(){
        return "asking nickname" + ((alreadyTaken) ? " because the old one was already taken" : "");
    }

}
