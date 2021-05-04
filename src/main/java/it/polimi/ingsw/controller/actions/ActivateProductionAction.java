package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ActivateProductionAction implements Action{

    private Player player;
    private PersonalBoard personalBoard;
    private Map<Resource, Integer> availableResources;
    private List<LeaderCard> availableProductionLeaderCards;
    private List<DevelopmentCard> availableDevelopmentCards;
    private ClientHandler clientHandler;

    public ActivateProductionAction(Player player, ClientHandler clientHandler){
        this.clientHandler = clientHandler;
        this.player = player;
        this.personalBoard = this.player.getPersonalBoard();
        //TODO: manage this exceptions
        try {
            this.availableResources = this.personalBoard.countResources();
        } catch (InactiveCardException | InvalidArgumentException | DifferentEffectTypeException e) {
            e.printStackTrace();
        }
        this.availableProductionLeaderCards = this.personalBoard.availableLeaderCards();
        this.availableDevelopmentCards = this.personalBoard.availableDevelopmentCards();
        this.availableProductionLeaderCards = this.availableProductionLeaderCards.stream().filter(
                lc -> lc.getEffect().getEffectType() == EffectType.PRODUCTION).collect(Collectors.toList());
    }

    @Override
    public boolean isExecutable() {

        Map<Resource, Integer> activationCost = null;
        DevelopmentCard developmentCard = null;
        Iterator<DevelopmentCard> developmentCardIterator = availableDevelopmentCards.iterator();

        while (developmentCardIterator.hasNext()){
            try {
                developmentCard = developmentCardIterator.next();
                activationCost = developmentCard.getProduction().getProductionPower().get(0).getResourceValue();
            } catch (ValueNotPresentException e) {
                e.printStackTrace();
                //TODO: manage better
                Server.SERVER_LOGGER.log(Level.WARNING, "The Development card " + developmentCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
               developmentCardIterator.remove();
            }

            if (hasResourcesForThisProduction(activationCost)){
                return true;
            }
        }

        Iterator<LeaderCard> leaderCardIterator = availableProductionLeaderCards.iterator();
        LeaderCard leaderCard = null;
        while(leaderCardIterator.hasNext()){
            try {
                leaderCard = leaderCardIterator.next();
                activationCost = leaderCard.getEffect().getProductionEffect().getProductionPower().get(0).getResourceValue();
            } catch (ValueNotPresentException | DifferentEffectTypeException e) {
                e.printStackTrace();
                //TODO: manage better
                Server.SERVER_LOGGER.log(Level.WARNING, "The Leader card " + leaderCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
                leaderCardIterator.remove();
            }
            if (hasResourcesForThisProduction(activationCost)){
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check, given the activation Cost of a production power of a {@link Card}, if the player has the minimum
     * amount of resources to activate this production;
     * @param activationCost the {@link Map} with resources and quantity of the activation cost
     * @return true if the player has the minimum amount of resources for this production
     */
    private boolean hasResourcesForThisProduction(Map<Resource, Integer> activationCost){
        boolean executable = true;
        for (Map.Entry<Resource, Integer> entry : activationCost.entrySet()){
            try {
                executable = executable && entry.getValue() <= availableResources.get(entry.getKey());
            }catch(Exception e){
                //do nothing, it's only to easily skip a missing Resource in availableResources
            }
        }
        return executable;
    }

    @Override
    public void execute(TurnController turnController) {
        clientHandler.setCurrentAction(this);
        List<Integer> availableProductionPowers = new ArrayList<>();
        Iterator<DevelopmentCard> developmentCardIterator = availableDevelopmentCards.iterator();

        Map<Resource, Integer> activationCost = null;
        DevelopmentCard developmentCard = null;

        //add to availableProductionPowers all the Production powers from Development Cards that can be activated
        while(developmentCardIterator.hasNext()){
            developmentCard = developmentCardIterator.next();
            try {
                if(hasResourcesForThisProduction(developmentCard.getProduction().getProductionPower().get(0).getResourceValue()))
                    availableProductionPowers.add(developmentCard.getID());
            } catch (ValueNotPresentException e) {
                e.printStackTrace();
                Server.SERVER_LOGGER.log(Level.WARNING, "The Development card " + developmentCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
                developmentCardIterator.remove();
            }
        }

        Iterator<LeaderCard> leaderCardIterator = availableProductionLeaderCards.iterator();
        LeaderCard leaderCard = null;

        //add to availableProductionPowers all the Production powers from Leader Cards that can be activated
        while(leaderCardIterator.hasNext()){
            leaderCard = leaderCardIterator.next();
            try {
                if(hasResourcesForThisProduction(leaderCard.getEffect().getProductionEffect().getProductionPower().get(0).getResourceValue()))
                    availableProductionPowers.add(leaderCard.getID());
            } catch (ValueNotPresentException | DifferentEffectTypeException e) {
                e.printStackTrace();
                Server.SERVER_LOGGER.log(Level.WARNING, "The Leader card " + leaderCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
                leaderCardIterator.remove();
            }
        }


    }

    @Override
    public void handleMessage(MessageToServer message) {

    }
}
