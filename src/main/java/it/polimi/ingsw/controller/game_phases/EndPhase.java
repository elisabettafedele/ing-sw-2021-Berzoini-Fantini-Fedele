package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.server.ClientHandler;

public abstract class EndPhase implements GamePhase {
    /**
     * Abstract class to manage the end phase
     */
    private Controller controller;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        notifyResults();
    }

    public void handleMessage(MessageToServer message, ClientHandler clientHandler){
        //ignored
    }

    public abstract void notifyResults();

    public Controller getController(){
        return controller;
    }

    public String toString(){
        return "End Phase";
    }
}
