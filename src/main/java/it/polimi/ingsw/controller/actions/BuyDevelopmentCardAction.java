package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;

import java.util.*;

public class BuyDevelopmentCardAction implements Action{
    private DevelopmentCard cardToBuy;
    private Player currentPlayer;
    private DevelopmentCardGrid developmentCardGrid;
    private PersonalBoard personalBoard;
    private Map<Resource, Integer> playersPossession;
    private boolean done;


    public BuyDevelopmentCardAction(DevelopmentCard card, Player currentPlayer, DevelopmentCardGrid developmentCardGrid){
        this.currentPlayer = currentPlayer;
        this.developmentCardGrid = developmentCardGrid;
        this.personalBoard = currentPlayer.getPersonalBoard();
        this.done = false;
    }



    private void removeResources(Map<Resource, Integer> resourcesToRemove){
        Set<Resource> resourcesTypeToRemove = resourcesToRemove.keySet();
        ResourceStorageType positionChosen;
        int quantityChosen;
        for (Resource resource : resourcesTypeToRemove){
            if (resourcesToRemove.isEmpty())
                return;
            if (resourcesToRemove.get(resource) == 0)
                resourcesToRemove.remove(resource);
            else{
                //TODO Provide the client the depots from where he can take the resources



            }

        }
    }

    private void addCardToPersonalBoard(DevelopmentCard cardToBeAdded){
        int slotNumber;
        int slotTest = 2;
        slotNumber = slotTest;
        //TODO ask to the client where to add the card and write the slot chosen in slotNumber, the value given above is just a test
        try {
            personalBoard.addDevelopmentCard(cardToBeAdded, slotNumber);
        }catch (InvalidSlotException e1){
            //TODO Tell to the client that his choice was not possible
            //Then, call the function again
            addCardToPersonalBoard(cardToBeAdded);
        }catch (InvalidArgumentException e2){
            //TODO Tell the client the number of the depot is not in the correct range
            addCardToPersonalBoard(cardToBeAdded);
        }

    }


    private void buyCard(Stack<DevelopmentCard> stack){
        DevelopmentCard chosenCard = stack.peek();
        Map<Resource, Integer> cost = chosenCard.getDiscountedCost(this.getDiscountedResources());

        //First, the client pays
        removeResources(cost);

        //Second, I remove the card from the grid
        stack.remove(chosenCard);

        //Third, I add the card to the personal board, making the client choose where to add it
        addCardToPersonalBoard(chosenCard);


    }


    @Override
    public void execute(TurnController turnController) {
        while (!done) {
            //Show available cards through stacks
            int chosenStackNumberTest = 1;
            Stack<DevelopmentCard> chosenStack = personalBoard.getDevelopmentCards()[chosenStackNumberTest]; //!!!!!This is just an example
            DevelopmentCard chosenCard = chosenStack.peek();
            if (enoughResourcesAvailable(chosenCard) && personalBoard.cardInsertionIsLegal(chosenCard))
                buyCard(chosenStack);
        }

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
            if (enoughResourcesAvailable(card) && personalBoard.cardInsertionIsLegal(card))
                return true;
        }
        return false;
    }

    @Override
    public void handleMessage(MessageToServer message) {

    }


}
