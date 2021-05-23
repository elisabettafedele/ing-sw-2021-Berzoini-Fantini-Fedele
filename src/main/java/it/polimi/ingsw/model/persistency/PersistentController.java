package it.polimi.ingsw.model.persistency;

public class PersistentController {
    private PersistentGame game;
    private String lastPlayer;
    private int controllerID;
    private String gamePhase;

    public PersistentController(PersistentGame game, String lastPlayer, int controllerID, String gamePhase){
        this.game = game;
        this.lastPlayer = lastPlayer;
        this.controllerID = controllerID;
        this.gamePhase = gamePhase;
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

    public String getGamePhase() {
        return gamePhase;
    }
}
