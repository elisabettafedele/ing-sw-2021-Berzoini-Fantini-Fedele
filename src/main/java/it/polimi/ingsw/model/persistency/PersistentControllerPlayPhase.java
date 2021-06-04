package it.polimi.ingsw.model.persistency;

public class PersistentControllerPlayPhase {
    private PersistentGame game;
    private String lastPlayer;
    private int controllerID;
    private boolean endTriggered;

    /**
     * Constructor of the class which represent the play phase of a multiplayer {@link it.polimi.ingsw.model.game.Game}.
     * It will be saved in a json file and retrieved if needed
     * @param game the {@link it.polimi.ingsw.model.game.Game} to be saved
     * @param lastPlayer the nickname of the last {@link it.polimi.ingsw.model.player.Player} that has completed his turn
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} related to the game
     * @param endTriggered whether the game is end triggered
     */
    public PersistentControllerPlayPhase(PersistentGame game, String lastPlayer, int controllerID, boolean endTriggered){
        this.game = game;
        this.lastPlayer = lastPlayer;
        this.controllerID = controllerID;
        this.endTriggered = endTriggered;
    }

    public int getControllerID() {
        return controllerID;
    }

    public PersistentGame getGame() {
        return game;
    }

    public String getLastPlayer() {
        return lastPlayer;
    }

    public boolean isEndTriggered() {
        return endTriggered;
    }
}
