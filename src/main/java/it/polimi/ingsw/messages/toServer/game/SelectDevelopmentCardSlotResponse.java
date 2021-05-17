package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;

public class SelectDevelopmentCardSlotResponse implements MessageToServer {
    private int slotSelected;
    public SelectDevelopmentCardSlotResponse(int slotSelected) {
        this.slotSelected=slotSelected;
    }

    public int getSlotSelected() {
        return slotSelected;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
