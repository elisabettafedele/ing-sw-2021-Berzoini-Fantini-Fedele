package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.messages.toServer.ChooseActionResponse;
import it.polimi.ingsw.messages.toServer.EndTurnRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

public abstract class PlayPhase {
    private Controller controller;
    private TurnController turnController;
    private Player player;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setTurnController(TurnController turnController) {
        this.turnController = turnController;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Controller getController() {
        return controller;
    }

    public TurnController getTurnController() {
        return turnController;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void nextTurn();

    public void handleResourceDiscard(String nickname){

    }
    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseActionResponse)
            getTurnController().doAction(((ChooseActionResponse) message).getActionChosen());
        if (message instanceof EndTurnRequest)
            getTurnController().endTurn();
    }

    public abstract void handleEndTriggered();

}
