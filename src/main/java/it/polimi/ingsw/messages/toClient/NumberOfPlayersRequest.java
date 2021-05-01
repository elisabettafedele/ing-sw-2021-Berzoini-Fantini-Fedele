package it.polimi.ingsw.messages.toClient;

//TODO: InputParser it's in the client package, move to the common or adjust the NumberOfPlayerResponse constructor?
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
//TODO: This MessageToClient needs a MessageToServer... Need to be corrected?
import it.polimi.ingsw.messages.toServer.NumberOfPlayersResponse;

public class NumberOfPlayersRequest implements MessageToClient {
    private boolean isRetry;

    public NumberOfPlayersRequest(boolean isRetry){
        this.isRetry = isRetry;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayNumberOfPlayersRequest(isRetry);
    }
}
