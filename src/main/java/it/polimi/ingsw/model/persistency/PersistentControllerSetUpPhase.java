package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.Map;

public class PersistentControllerSetUpPhase {
    private PersistentGame game;
    private int controllerID;
    private Map<String, List<Resource>> resourcesToStore;

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
