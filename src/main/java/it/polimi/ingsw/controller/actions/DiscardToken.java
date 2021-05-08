package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.SinglePlayerPlayPhase;
import it.polimi.ingsw.enumerations.FlagColor;

import java.util.Objects;

public class DiscardToken extends SoloActionToken{

    private int numOfCardRemoved;
    private FlagColor flagColor;

    public DiscardToken(String pathImageFront, String pathImageBack, int numOfCardRemoved, FlagColor flagColor) {
        super(pathImageFront, pathImageBack);
        this.numOfCardRemoved = numOfCardRemoved;
        this.flagColor = flagColor;
    }

    @Override
    public void useActionToken(SinglePlayerPlayPhase singlePlayerPlayPhase) {
        singlePlayerPlayPhase.discardDevelopmentCards(this.numOfCardRemoved, this.flagColor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DiscardToken that = (DiscardToken) o;
        return numOfCardRemoved == that.numOfCardRemoved && flagColor == that.flagColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numOfCardRemoved, flagColor);
    }
}
