package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ViewInterface;

public class TextMessage extends MessageToClient{
    private String message;

    public TextMessage(String message){
        super(false);
        this.message = message;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayMessage(message);
    }
}
