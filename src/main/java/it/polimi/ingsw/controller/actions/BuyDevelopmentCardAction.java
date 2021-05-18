package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.messages.toClient.matchData.NotifyDevelopmentCardBought;
import it.polimi.ingsw.messages.toClient.matchData.UpdateDepotsStatus;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.game.SelectCardRequest;
import it.polimi.ingsw.messages.toClient.game.SelectDevelopmentCardSlotRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.messages.toServer.game.SelectCardResponse;
import it.polimi.ingsw.messages.toServer.game.SelectDevelopmentCardSlotResponse;
import it.polimi.ingsw.messages.toServer.game.SelectStorageResponse;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.RemoveResources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuyDevelopmentCardAction implements Action{
    private ClientHandler clientHandler;
    private Player currentPlayer;
    private TurnController turnController;
    private List<Integer> buyableCardsIDs;
    private DevelopmentCard developmentCardChosen;
    private DevelopmentCard newCard;
    private int slot;




    public BuyDevelopmentCardAction( TurnController turnController){
        this.turnController=turnController;
        this.currentPlayer = turnController.getCurrentPlayer();
        this.clientHandler = turnController.getController().getConnectionByNickname(currentPlayer.getNickname());

    }


    @Override
    public void execute() {
        clientHandler.setCurrentAction(this);
        clientHandler.sendMessageToClient(new SelectCardRequest(buyableCardsIDs,false));
    }

    @Override
    public void reset(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        this.clientHandler = turnController.getController().getConnectionByNickname(currentPlayer.getNickname());
        buyableCardsIDs=new ArrayList<>();
        developmentCardChosen=null;
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
        possessions = currentPlayer.getPersonalBoard().countResources();
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
        buyableCardsIDs=new ArrayList<>();
        List<DevelopmentCard> availableCards = turnController.getController().getGame().getDevelopmentCardGrid().getAvailableCards();
        for (DevelopmentCard card : availableCards) {
            if (enoughResourcesAvailable(card) && currentPlayer.getPersonalBoard().cardInsertionIsLegal(card)) {
                buyableCardsIDs.add(card.getID());
            }
        }
        return !buyableCardsIDs.isEmpty();
    }

    @Override
    public void handleMessage(MessageToServer message) {
        if(message instanceof SelectCardResponse) {
            for (DevelopmentCard dc : turnController.getController().getGame().getDevelopmentCardGrid().getAvailableCards()) {
                if (dc.getID() == ((SelectCardResponse) message).getSelectedCard()) {
                    developmentCardChosen = dc;
                }
            }
            clientHandler.sendMessageToClient(new SelectDevelopmentCardSlotRequest(currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,0),currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,1),currentPlayer.getPersonalBoard().cardInsertionIsLegal(developmentCardChosen,2)));
        }
        if(message instanceof SelectDevelopmentCardSlotResponse){
            try {
                newCard = turnController.getController().getGame().getDevelopmentCardGrid().removeCard(developmentCardChosen);
                Map<Resource, Integer> cost = developmentCardChosen.getDiscountedCost(this.getDiscountedResources());
                RemoveResources.removeResources(cost,clientHandler,currentPlayer);
                currentPlayer.getPersonalBoard().addDevelopmentCard(developmentCardChosen,((SelectDevelopmentCardSlotResponse) message).getSlotSelected());

            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (InvalidSlotException e) {
                e.printStackTrace();
            }
            turnController.setStandardActionDoneToTrue();
            if(currentPlayer.getPersonalBoard().getNumOfDevelopmentCards()==7){
                turnController.setEndTriggerToTrue();
            }
            turnController.getController().sendMessageToAll(new UpdateDepotsStatus(currentPlayer.getNickname(), currentPlayer.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), currentPlayer.getPersonalBoard().getStrongboxStatus(), currentPlayer.getPersonalBoard().getLeaderStatus()));
            turnController.setNextAction();
        }
        if(message instanceof SelectStorageResponse){
            currentPlayer.getPersonalBoard().isResourceAvailableAndRemove( ((SelectStorageResponse) message).getResourceStorageType(),((SelectStorageResponse) message).getResource(),1,true);
        }
    }

}
