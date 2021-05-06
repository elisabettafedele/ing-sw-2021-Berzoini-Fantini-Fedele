package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class TextMessage implements MessageToClient{
    private String message;

    public TextMessage(String message){
        this.message = message;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayMessage(message);
    }
}
