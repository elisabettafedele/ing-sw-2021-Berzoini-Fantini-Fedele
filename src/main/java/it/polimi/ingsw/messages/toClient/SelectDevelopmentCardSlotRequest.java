package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class SelectDevelopmentCardSlotRequest implements MessageToClient{

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
}
