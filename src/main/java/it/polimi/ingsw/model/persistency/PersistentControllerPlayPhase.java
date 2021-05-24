package it.polimi.ingsw.model.persistency;

public class PersistentControllerPlayPhase {
    private PersistentGame game;
    private String lastPlayer;
    private int controllerID;
    private boolean endTriggered;

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
