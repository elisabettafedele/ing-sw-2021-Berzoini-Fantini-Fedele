package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

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
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has requested to place his development card on the slot number " + slotSelected);
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
