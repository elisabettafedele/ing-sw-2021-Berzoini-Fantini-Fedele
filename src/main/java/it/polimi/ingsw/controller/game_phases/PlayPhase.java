package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.jsonParsers.GameCloneThroughJson;
import it.polimi.ingsw.messages.toClient.matchData.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.messages.toServer.game.ChooseActionResponse;
import it.polimi.ingsw.messages.toServer.game.EndTurnRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PlayPhase {
    private Controller controller;
    private TurnController turnController;
    private Player player;
    private Game lastTurnGameCopy;

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

    public abstract void handleResourceDiscard(String nickname);

    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseActionResponse)
            getTurnController().doAction(((ChooseActionResponse) message).getActionChosen());
        if (message instanceof EndTurnRequest)
            getTurnController().endTurn();
    }

    public abstract void handleEndTriggered();
    public String toString(){
        return "Play Phase";
    }

    public void saveGameCopy(Game game){
        lastTurnGameCopy = GameCloneThroughJson.clone(game);
    }

    public void reloadGameCopy(boolean disconnection){
        controller.sendMatchData(lastTurnGameCopy, disconnection);
    }

    public Game getLastTurnGameCopy() {
        return lastTurnGameCopy;
    }

    public void setLastTurnGameCopy(Game lastTurnGameCopy) {
        this.lastTurnGameCopy = lastTurnGameCopy;
    }
}
