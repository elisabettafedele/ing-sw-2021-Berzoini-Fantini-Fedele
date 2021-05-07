package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.SinglePlayerPlayPhase;

import java.util.Objects;

public class BlackCrossMoveToken extends SoloActionToken{

    private int numOfMoves;
    private boolean shuffle;

    public BlackCrossMoveToken(String pathImageFront, String pathImageBack, int numOfMoves, boolean shuffle) {
        super(pathImageFront, pathImageBack);
        this.numOfMoves = numOfMoves;
        this.shuffle = shuffle;
    }

    @Override
    public void useActionToken(SinglePlayerPlayPhase singlePlayerPlayPhase) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BlackCrossMoveToken that = (BlackCrossMoveToken) o;
        return numOfMoves == that.numOfMoves && shuffle == that.shuffle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numOfMoves, shuffle);
    }
}
