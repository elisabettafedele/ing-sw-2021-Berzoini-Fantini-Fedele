package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

/**
 * Message to notify that Lorenzo has performed his action
 */
public class NotifyLorenzoAction extends MessageToClient {
    private int id;

    public NotifyLorenzoAction(int id){
        super(false);
        this.id = id;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayLorenzoAction(id);
    }

    public String toString(){
        return "sent notification of Lorenzo's action";
    }
}
