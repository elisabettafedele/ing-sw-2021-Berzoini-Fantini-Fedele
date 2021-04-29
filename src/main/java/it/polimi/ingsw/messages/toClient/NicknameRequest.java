package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
//TODO: This MessageToClient needs a MessageToServer... Need to be corrected?
import it.polimi.ingsw.messages.toServer.NicknameResponse;

public class NicknameRequest implements MessageToClient {
    private String message;
    private boolean isRetry;
    private boolean alreadyTaken;

    public NicknameRequest(boolean isRetry, boolean alreadyTaken){
        this.isRetry = isRetry;
        this.alreadyTaken = alreadyTaken;
    }

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {
        view.displayNicknameRequest(isRetry, alreadyTaken);
        client.sendMessageToServer(new NicknameResponse(view.getNickname()));
    }

}
