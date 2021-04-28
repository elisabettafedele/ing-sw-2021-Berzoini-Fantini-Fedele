package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.messages.toServer.NumberOfPlayersResponse;

import java.io.Serializable;

public class NumberOfPlayersRequest implements MessageToClient, Serializable {
    private boolean isRetry;

    public NumberOfPlayersRequest(boolean isRetry){
        this.isRetry = isRetry;
    }

    @Override
    public void handleMessage(View view, Client client) {
        view.displayNumberOfPlayersRequest(isRetry);
        client.sendMessageToServer(new NumberOfPlayersResponse(InputParser.getInt("Invalid number of players: please insert an integer number between 2 and 4")));
    }
}
