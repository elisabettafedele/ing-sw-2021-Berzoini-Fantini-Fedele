package it.polimi.ingsw.model.persistency;

public class PersistentControllerPlayPhase {
    private PersistentGame game;
    private String lastPlayer;
    private int controllerID;

    public PersistentControllerPlayPhase(PersistentGame game, String lastPlayer, int controllerID){
        this.game = game;
        this.lastPlayer = lastPlayer;
        this.controllerID = controllerID;
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

}
