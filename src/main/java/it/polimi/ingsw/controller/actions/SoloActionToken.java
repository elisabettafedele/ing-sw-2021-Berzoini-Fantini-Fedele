package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.game_phases.SinglePlayerPlayPhase;

import java.util.Objects;

/**
 * This class represents the tokens used by Lorenzo il Magnifico in single player mode
 */
public abstract class SoloActionToken {

    private String pathImageFront;
    private String pathImageBack;
    private int id;

    public SoloActionToken(String pathImageFront, String pathImageBack) {
        this.pathImageFront = pathImageFront;
        this.pathImageBack = pathImageBack;
    }

    public String getPathImageFront() {
        return pathImageFront;
    }

    public String getPathImageBack() {
        return pathImageBack;
    }

    /**
     * Executes the action of Lorenzo il Magnifico when playing in single player game mode
     * @param singlePlayerPlayPhase the play phase related to the game
     */
    public abstract void useActionToken(SinglePlayerPlayPhase singlePlayerPlayPhase);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoloActionToken that = (SoloActionToken) o;
        return pathImageFront.equals(that.pathImageFront) && pathImageBack.equals(that.pathImageBack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathImageFront, pathImageBack);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
