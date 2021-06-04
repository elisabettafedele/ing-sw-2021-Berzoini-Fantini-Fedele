package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.Map;

public class PersistentControllerSetUpPhase {
    private PersistentGame game;
    private int controllerID;
    private Map<String, List<Resource>> resourcesToStore;

    /**
     * Constructor of the class which represent the set up phase a {@link it.polimi.ingsw.model.game.Game}.
     * It will be saved in a json file and retrieved if needed
     * @param game the {@link it.polimi.ingsw.model.game.Game} to be saved
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} related to the game
     * @param resourcesToStore a map which associates each player's nickname to the resources the player still have to decide where to store
     */
    public PersistentControllerSetUpPhase(PersistentGame game, int controllerID, Map<String, List<Resource>> resourcesToStore) {
        this.game = game;
        this.controllerID = controllerID;
        this.resourcesToStore = resourcesToStore;
    }

    public PersistentGame getGame() {
        return game;
    }

    public int getControllerID() {
        return controllerID;
    }

    public Map<String, List<Resource>> getResourcesToStore() {
        return resourcesToStore;
    }
}
