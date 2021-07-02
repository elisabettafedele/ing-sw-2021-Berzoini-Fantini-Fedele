package it.polimi.ingsw.messages.toClient.matchData;

/**
 * Message to notify the purchase of a {@link it.polimi.ingsw.model.cards.DevelopmentCard}
 */
public class NotifyDevelopmentCardBought extends MatchDataMessage{
    private final int cardBought;
    private final int newCardOnGrid;
    private final int slot;
    private final int victoryPoints;

    public NotifyDevelopmentCardBought(String nickname, int cardBought, int newCardOnGrid, int slot, int victoryPoints) {
        super(nickname);
        this.cardBought = cardBought;
        this.newCardOnGrid = newCardOnGrid;
        this.slot = slot;
        this.victoryPoints = victoryPoints;
    }

    public int getCardBought() {
        return cardBought;
    }

    public int getNewCardOnGrid() {
        return newCardOnGrid;
    }

    public int getSlot() {
        return slot;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
