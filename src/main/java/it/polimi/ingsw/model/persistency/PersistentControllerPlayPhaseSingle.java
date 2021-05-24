package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.controller.actions.SoloActionToken;

import java.util.List;
import java.util.Queue;

public class PersistentControllerPlayPhaseSingle extends PersistentControllerPlayPhase{
    private List<Integer> tokens;
    private int blackCrossPosition;

    public PersistentControllerPlayPhaseSingle(PersistentGame game, String lastPlayer, int controllerID, boolean endTriggered, List<Integer> tokens, int blackCrossPosition) {
        super(game, lastPlayer, controllerID, endTriggered);
        this.tokens = tokens;
        this.blackCrossPosition = blackCrossPosition;
    }

    public List<Integer> getTokens() {
        return tokens;
    }

    public int getBlackCrossPosition() {
        return blackCrossPosition;
    }
}
