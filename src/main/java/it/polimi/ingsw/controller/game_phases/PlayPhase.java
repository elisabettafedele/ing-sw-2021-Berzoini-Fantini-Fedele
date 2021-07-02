package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.messages.toServer.game.ChooseActionResponse;
import it.polimi.ingsw.messages.toServer.game.EndTurnRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

/**
 * Abstract class with the common feature of the playphase
 */
public abstract class PlayPhase implements GamePhase{
    private Controller controller;
    private TurnController turnController;
    private Player player;
    private PersistentGame lastTurnGameCopy;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setTurnController(TurnController turnController) {
        this.turnController = turnController;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setLastTurnGameCopy(PersistentGame lastTurnGameCopy) {
        this.lastTurnGameCopy = lastTurnGameCopy;
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

    public PersistentGame getLastTurnGameCopy() {
        return lastTurnGameCopy;
    }

    public abstract void nextTurn();

    /**
     * Method to handle the discard of a {@link it.polimi.ingsw.enumerations.Resource} by a player
     * @param nickname the nickname of the player that has discarded a resource
     */
    public abstract void handleResourceDiscard(String nickname);

    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseActionResponse && (clientHandler.getNickname().equals(getTurnController().getCurrentPlayer().getNickname()))) {
            getTurnController().doAction(((ChooseActionResponse) message).getActionChosen());
        }
        if (message instanceof EndTurnRequest && (clientHandler.getNickname().equals(getTurnController().getCurrentPlayer().getNickname())))
            getTurnController().endTurn();
    }

    //TODO
    public abstract void handleEndTriggered();

    //TODO
    public void saveGameCopy(Game game){
        lastTurnGameCopy = new PersistentGame(game);
    }
    //TODO
    public void reloadGameCopy(boolean disconnection){
        controller.sendMatchData(controller.getGame(), disconnection);
    }

    //TODO
    public abstract void saveGame();
    //TODO
    public abstract void restartLastTurn();

    public String toString(){
        return "Play Phase";
    }
}
