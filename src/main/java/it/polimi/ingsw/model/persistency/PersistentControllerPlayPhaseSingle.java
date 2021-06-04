package it.polimi.ingsw.model.persistency;


import java.util.List;

public class PersistentControllerPlayPhaseSingle extends PersistentControllerPlayPhase{
    private List<Integer> tokens;
    private int blackCrossPosition;

    /**
     * Constructor of the class which represent the play phase of a single player {@link it.polimi.ingsw.model.game.Game}.
     * It will be saved in a json file and retrieved if needed
     * @param game the {@link it.polimi.ingsw.model.game.Game} to be saved
     * @param lastPlayer nickname of the last {@link it.polimi.ingsw.model.player.Player} that has completed his turn (Lorenzo or the single player)
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} related to the game
     * @param endTriggered whether the game is end triggered
     * @param tokens the ordered list of the IDs of the {@link it.polimi.ingsw.controller.actions.SoloActionToken}
     * @param blackCrossPosition the position of the black cross in the faith track
     */
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
