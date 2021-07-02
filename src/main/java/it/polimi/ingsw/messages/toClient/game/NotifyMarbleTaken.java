package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

/**
 * Message to communicate the marble taken from the market
 */
public class NotifyMarbleTaken extends MessageToClient {
    private List<Marble> marbleTaken;
    private boolean needToChooseConversion;

    public NotifyMarbleTaken(List<Marble> marbleTaken, boolean needToChooseConversion){
        super(false);
        this.marbleTaken = marbleTaken;
        this.needToChooseConversion = needToChooseConversion;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayMarblesTaken(marbleTaken, needToChooseConversion);
    }

    public String toString(){
        return "sending marbles taken";
    }
}
