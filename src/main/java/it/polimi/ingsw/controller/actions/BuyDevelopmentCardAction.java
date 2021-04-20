package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuyDevelopmentCardAction implements Action{
    private DevelopmentCard cardToBuy;
    private Player currentPlayer;
    private DevelopmentCardGrid developmentCardGrid;
    private PersonalBoard personalBoard;
    private Map<Resource, Integer> playersPossession;


    public BuyDevelopmentCardAction(DevelopmentCard card, Player currentPlayer, DevelopmentCardGrid developmentCardGrid){
        this.currentPlayer = currentPlayer;
        this.developmentCardGrid = developmentCardGrid;
        this.personalBoard = currentPlayer.getPersonalBoard();
    }

    @Override
    public void execute(TurnController turnController) {
        //Show available cards
        //get the card and verify whether it is possible to buy and stored it

    }

    /**
     * Private method used to get the discounted resources of the player,
     * @return an empty list if the player has not any discount, a list of the discounted resources otherwise
     */
    private List<Resource> getDiscountedResources(){
        List <Resource> discounts = new ArrayList<>();
        List <Effect> discountEffects = personalBoard.getAvailableEffects(EffectType.DISCOUNT);
        if (!discountEffects.isEmpty()) {
            for (Effect effect : discountEffects) {
                try {
                    discounts.add(effect.getDiscountEffect());
                } catch (DifferentEffectTypeException e) {
                    e.printStackTrace();
                }
            }
        }
        return discounts;
    }




    /**
     *Private method to check if the player has enough resources to buy a card
     * @param card
     * @return
     * @throws ValueNotPresentException
     * @throws InactiveCardException
     * @throws InvalidArgumentException
     * @throws DifferentEffectTypeException
     */
    private boolean enoughResourcesAvailable(DevelopmentCard card)  {
        //First, I get the players possessions
        Map<Resource, Integer> possessions = null;
        try {
            possessions = currentPlayer.getPersonalBoard().countResources();
        } catch (InactiveCardException e) {
            e.printStackTrace();
        } catch (DifferentEffectTypeException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        assert(possessions != null);
        //Then, I get the available Discount Effects, if any
        List <Resource> discounts = getDiscountedResources();

        //Finally, I check if my possessions are enough
        Map<Resource, Integer> cost = card.getDiscountedCost(discounts);
        for (Resource resource : cost.keySet()) {
            if (possessions.get(resource) < cost.get(resource))
                return false;
        }
        return true;
    }



    /**
     * This function check if there is at least one card that can be bought by the user
     * @return true if there is at least one card that can bought
     */
    @Override
    public boolean isExecutable() {
        List<DevelopmentCard> availableCards = developmentCardGrid.getAvailableCards();
        for (DevelopmentCard card : availableCards) {
            if (enoughResourcesAvailable(card))
                return true;
        }
        return false;
    }

    @Override
    public void reset() {

    }
}
