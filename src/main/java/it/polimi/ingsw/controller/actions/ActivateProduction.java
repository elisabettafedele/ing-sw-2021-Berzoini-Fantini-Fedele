package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ActivateProduction implements Action{

    private Player player;
    private PersonalBoard personalBoard;
    private Map<Resource, Integer> availableResources;
    private List<LeaderCard> availableProductionLeaderCards;
    private List<DevelopmentCard> availableDevelopmentCards;

    public ActivateProduction(Player player){
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
        boolean executable;
        Map<Resource, Integer> activationCost = null;
        Iterator<DevelopmentCard> iterator = availableDevelopmentCards.iterator();
        for (DevelopmentCard developmentCard : availableDevelopmentCards){
            try {
                activationCost = developmentCard.getProduction().getProductionPower().get(0).getResourceValue();
            } catch (ValueNotPresentException e) {
                e.printStackTrace();
                //TODO: manage better
                Server.SERVER_LOGGER.log(Level.WARNING, "The Development card " + developmentCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
                availableDevelopmentCards.remove(developmentCard);
            }
            executable = true;
            for (Map.Entry<Resource, Integer> entry : activationCost.entrySet()){
                try {
                    executable = executable && entry.getValue() <= availableResources.get(entry.getKey());
                }catch(Exception e){
                    //do nothing, it's only to easily skip a
                }
            }
            if (executable){
                return true;
            }
        }
        for(LeaderCard leaderCard : availableProductionLeaderCards){
            try {
                activationCost = leaderCard.getEffect().getProductionEffect().getProductionPower().get(0).getResourceValue();
            } catch (ValueNotPresentException | DifferentEffectTypeException e) {
                e.printStackTrace();
                //TODO: manage better
                Server.SERVER_LOGGER.log(Level.WARNING, "The Leader card " + leaderCard.toString() + "" +
                        "has no Production Effect. Removing from the list of cards present in the action");
                availableProductionLeaderCards.remove(leaderCard);
            }
        }
        return false;
    }

    @Override
    public void execute(TurnController turnController) {

    }

    @Override
    public void handleMessage(MessageToServer message) {

    }
}
