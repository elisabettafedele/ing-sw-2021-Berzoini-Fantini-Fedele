package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Marble;

import java.util.List;

public class ChooseWhiteMarbleConversionRequest implements MessageToClient{

    public ChooseWhiteMarbleConversionRequest(List<Marble> conversions){

    }

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {

    }
}
