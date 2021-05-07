package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.SelectCardRequest;
import it.polimi.ingsw.messages.toClient.SelectDevelopmentCardSlotRequest;
import it.polimi.ingsw.messages.toClient.SelectStorageRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.messages.toServer.SelectCardResponse;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.RemoveResources;

import java.util.*;

public class BuyDevelopmentCardAction implements Action{
    private ClientHandler clientHandler;
    private Player currentPlayer;
    private TurnController turnController;
    private List<Integer> buyableCardsIDs;
    private DevelopmentCard developmentCardChosen;




    public BuyDevelopmentCardAction(Player currentPlayer, ClientHandler clientHandler, TurnController turnController){
        this.currentPlayer = currentPlayer;
        this.clientHandler = clientHandler;
        this.turnController=turnController;//gli serve già alla isExecutable();
    }


    @Override
    public void execute(TurnController turnController) {
        clientHandler.setCurrentAction(this);
        this.turnController=turnController;
        clientHandler.sendMessageToClient(new SelectCardRequest(buyableCardsIDs,false));
        clientHandler.sendMessageToClient(new SelectDevelopmentCardSlotRequest(currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,0),currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,1),currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,2)));
        turnController.setStandardActionDoneToTrue();
        if(currentPlayer.getPersonalBoard().getNumOfDevelopmentCards()==7){
            turnController.setEndTriggerToTrue();
        }

    }

    /**
     * Private method used to get the discounted resources of the player,
     * @return an empty list if the player has not any discount, a list of the discounted resources otherwise
     */
    private List<Resource> getDiscountedResources(){
        List <Resource> discounts = new ArrayList<>();
        List <Effect> discountEffects = currentPlayer.getPersonalBoard().getAvailableEffects(EffectType.DISCOUNT);
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
        List<DevelopmentCard> availableCards = turnController.getController().getGame().getDevelopmentCardGrid().getAvailableCards();
        for (DevelopmentCard card : availableCards) {
            if (enoughResourcesAvailable(card) && currentPlayer.getPersonalBoard().cardInsertionIsLegal(card));
                buyableCardsIDs.add(card.getID());
        }
        return !availableCards.isEmpty();
    }

    @Override
    public void handleMessage(MessageToServer message) {
            for(DevelopmentCard dc:turnController.getController().getGame().getDevelopmentCardGrid().getAvailableCards()){
                if(dc.getID()==((SelectCardResponse) message).getSelectedCard()){
                    developmentCardChosen=dc;
                }
            }
    }

    public void insertCard(int slot){
        try {
            turnController.getController().getGame().getDevelopmentCardGrid().removeCard(developmentCardChosen);
            Map<Resource, Integer> cost = developmentCardChosen.getDiscountedCost(this.getDiscountedResources());
            RemoveResources.removeResources(cost,clientHandler,currentPlayer);
            currentPlayer.getPersonalBoard().addDevelopmentCard(developmentCardChosen,slot);

        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (InvalidSlotException e) {
            e.printStackTrace();
        }
    }


}
