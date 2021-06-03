package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class SelectCardRequest implements MessageToClient {
    List<Integer> CardsIDs;
    boolean leaderORdevelopment;
    public SelectCardRequest(List<Integer> CardsIDs, boolean leaderORdevelopment){
        this.CardsIDs = CardsIDs;
        this.leaderORdevelopment=leaderORdevelopment;
    }
    @Override
    public void handleMessage(VirtualView view) {
        view.displaySelectCardRequest(CardsIDs,leaderORdevelopment);
    }

    public String toString(){
        return "asking to choose a " + (leaderORdevelopment ? "leader" : "development") + " card";
    }
}
