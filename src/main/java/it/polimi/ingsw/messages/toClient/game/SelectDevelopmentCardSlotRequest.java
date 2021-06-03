package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class SelectDevelopmentCardSlotRequest implements MessageToClient {

    private boolean firstSlotAvailable;
    private boolean secondSlotAvailable;
    private boolean thirdSlotAvailable;
    public SelectDevelopmentCardSlotRequest(boolean firstSlotAvailable,boolean secondSlotAvailable,boolean thirdSlotAvailable){
        this.firstSlotAvailable=firstSlotAvailable;
        this.secondSlotAvailable=secondSlotAvailable;
        this.thirdSlotAvailable=thirdSlotAvailable;
    }
    @Override
    public void handleMessage(VirtualView view) {
        view.displaySelectDevelopmentCardSlotRequest(firstSlotAvailable,secondSlotAvailable,thirdSlotAvailable);
    }

    public String toString(){
        return "asking to choose a development card slot for the development card just bought";
    }
}
