package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NotifyLorenzoAction implements MessageToClient {
    private int id;

    public NotifyLorenzoAction(int id){
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
