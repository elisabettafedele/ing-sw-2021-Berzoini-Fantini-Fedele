package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Marble;

import java.util.List;

public class NotifyMarbleTaken implements MessageToClient{
    private List<Marble> marbleTaken;
    private boolean needToChooseConversion;

    public NotifyMarbleTaken(List<Marble> marbleTaken, boolean needToChooseConversion){
        this.marbleTaken = marbleTaken;
        this.needToChooseConversion = needToChooseConversion;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayMarblesTaken(marbleTaken, needToChooseConversion);
    }
}
