package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class SelectCardRequest extends MessageToClient {
    List<Integer> CardsIDs;
    boolean leaderORdevelopment;
    public SelectCardRequest(List<Integer> CardsIDs, boolean leaderORdevelopment){
        super(true);
        this.CardsIDs = CardsIDs;
        this.leaderORdevelopment=leaderORdevelopment;
    }
    @Override
    public void handleMessage(ViewInterface view) {
        view.displaySelectCardRequest(CardsIDs,leaderORdevelopment);
    }

    public String toString(){
        return "asking to choose a " + (leaderORdevelopment ? "leader" : "development") + " card";
    }
}
