package messages.toClient;

import client.Client;
import client.View;
import messages.toServer.NicknameResponse;

import java.io.Serializable;

public class NicknameRequest implements MessageToClient, Serializable {
    private String message;
    private boolean isRetry;
    private boolean alreadyTaken;

    public NicknameRequest(boolean isRetry, boolean alreadyTaken){
        this.isRetry = isRetry;
        this.alreadyTaken = alreadyTaken;
    }

    @Override
    public void handleMessage(View view, Client client) {
        view.displayNicknameRequest(isRetry, alreadyTaken);
        client.sendMessageToServer(new NicknameResponse(view.getNickname()));
    }

}
