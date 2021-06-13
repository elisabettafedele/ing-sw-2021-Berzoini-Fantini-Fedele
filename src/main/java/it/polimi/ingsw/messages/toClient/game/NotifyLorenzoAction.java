package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NotifyLorenzoAction extends MessageToClient {
    private int id;

    public NotifyLorenzoAction(int id){
        super(false);
        this.id = id;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayLorenzoAction(id);
    }

    public String toString(){
        return "sent notification of Lorenzo's action";
    }
}
