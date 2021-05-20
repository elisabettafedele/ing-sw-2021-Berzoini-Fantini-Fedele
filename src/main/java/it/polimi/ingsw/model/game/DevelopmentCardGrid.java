package it.polimi.ingsw.model.game;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.player.Player;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to represent the disposition of the {@link DevelopmentCard} purchasable in the game
 */
public class DevelopmentCardGrid implements Serializable {

    private Stack<DevelopmentCard>[][] cardGrid;
    private final static int LEVEL = 3, COLOR = 4;

    /**
     * Class constructor. Generates, randomly, the 12 decks of 4 cards according to their {@link Flag}
     * @throws UnsupportedEncodingException
     * @throws InvalidArgumentException
     */
    public DevelopmentCardGrid() throws UnsupportedEncodingException, InvalidArgumentException {
        cardGrid = new Stack[LEVEL][COLOR];
        List<DevelopmentCard> cards = DevelopmentCardParser.parseCards();
        Random rand = new Random();
        for(int i = 0; i < LEVEL; i++){
            for(int j = 0; j < COLOR; j++){
                int finalJ = j;
                int finalI = i;
                List<DevelopmentCard> actualCellCards = cards.stream().filter(c ->
                        c.getFlag().getFlagColor().getValue() == finalJ).filter(c ->
                        c.getFlag().getFlagLevel().getValue() == finalI *(-1)+2).
                        collect(Collectors.toList());
                cardGrid[i][j] = new Stack<DevelopmentCard>();
                int size = actualCellCards.size();
                for(int k = 0; k < size; k++){
                    cardGrid[i][j].push(actualCellCards.get(rand.nextInt(actualCellCards.size())));
                    actualCellCards.remove(cardGrid[i][j].peek());
                }
            }
        }
    }

    /**
     * Remove the bought {@link DevelopmentCard} from the grid
     * @param card the card that a {@link Player} have bought
     * @return null if there is no card under the one I have just removed
     */
    public DevelopmentCard removeCard(DevelopmentCard card) throws InvalidArgumentException {
        if(!cardGrid[card.getFlag().getFlagLevel().getValue()*(-1)+2][card.getFlag().getFlagColor().getValue()].peek().equals(card))
            throw new InvalidArgumentException();
        this.cardGrid[card.getFlag().getFlagLevel().getValue()*(-1)+2][card.getFlag().getFlagColor().getValue()].pop();
        if (cardGrid[card.getFlag().getFlagLevel().getValue()*(-1)+2][card.getFlag().getFlagColor().getValue()].isEmpty())
            return null;
        return cardGrid[card.getFlag().getFlagLevel().getValue()*(-1)+2][card.getFlag().getFlagColor().getValue()].peek();
    }

    /**
     * Get the available {@link DevelopmentCard} from the market, that is the first {@link DevelopmentCard} of each deck
     * @return the available {@link DevelopmentCard} from the market, that is the first {@link DevelopmentCard} of each deck
     */
    public List<DevelopmentCard> getAvailableCards(){
        List<DevelopmentCard> availableCards = new ArrayList<>();
        for(int i = 0; i < LEVEL; i++) {
            for (int j = 0; j < COLOR; j++) {
                if(!cardGrid[i][j].empty()) availableCards.add(cardGrid[i][j].peek());
            }
        }
        return availableCards;
    }

    /**
     * Method for single player mode: if all the {@link DevelopmentCard} of one type has been taken the
     * game ends
     * @return true if all the {@link DevelopmentCard} of one type has been taken
     */
    public boolean checkEmptyColumn(){
        for(int j = 0; j < COLOR; j++){
            if(cardGrid[0][j].empty() && cardGrid[1][j].empty() && cardGrid[2][j].empty())
                return true;
        }
        return false;
    }
}
