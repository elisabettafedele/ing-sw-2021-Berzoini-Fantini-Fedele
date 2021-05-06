package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.controller.actions.BuyDevelopmentCardAction;

public class SelectDevelopmentCardSlotResponse implements MessageToServer{
    private int slotSelected;
    public SelectDevelopmentCardSlotResponse(int slotSelected) {
        this.slotSelected=slotSelected;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        ((BuyDevelopmentCardAction) clientHandler.getCurrentAction()).insertCard(slotSelected);
    }
}
