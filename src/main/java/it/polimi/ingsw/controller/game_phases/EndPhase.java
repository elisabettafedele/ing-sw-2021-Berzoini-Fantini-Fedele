package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.toServer.MessageToServer;
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

    /**
     * Every message in the end phase is ignored, it should not be received
     * @param message the message to hanlde
     * @param clientHandler the {@link ClientHandler} related to the client that has sent the message
     */
    public void handleMessage(MessageToServer message, ClientHandler clientHandler){
        //ignored
    }

    /**
     * Abstract method to notify results to the players.
     * It is handled in a different way depending on the game mode chosen (single player or multiplayer)
     */
    public abstract void notifyResults();

    public Controller getController(){
        return controller;
    }

    public String toString(){
        return "End Phase";
    }
}
