package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.MultiplayerPlayPhase;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.GameType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.LeaderDepot;
import it.polimi.ingsw.model.depot.WarehouseDepot;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.player.PersonalBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TakeResourcesFromMarketAction {
    private int insertionPosition;
    private Market market;
    private List<Resource> resourcesToStore;
    private PersonalBoard personalBoard;
    private Controller controller;

    public TakeResourcesFromMarketAction(PersonalBoard personalBoard, Market market, int insertionPosition, Controller controller) {
        this.insertionPosition = insertionPosition;
        this.market = market;
        this.personalBoard = personalBoard;
    }

    public boolean isExecutable() {
        return true;
    }

    public void reset(){
        insertionPosition = -1;
        resourcesToStore = null;
        personalBoard = null;
    }

    private void marblesConversion(List<Marble> marbles) throws InvalidArgumentException {
        marbles.stream().filter(x -> x == Marble.RED).forEach(x -> {
            try {
                personalBoard.moveMarker(1);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        });
        resourcesToStore = marbles.stream().filter(x -> (x != Marble.WHITE && x != Marble.RED))
                .map(x -> Resource.valueOf(x.getValue())).collect(Collectors.toList());
    }

    private List<Marble> getAvailableConversions(){
        List<Marble> availableConversions = new ArrayList<>();

        //I get the available leader cards' effects
        List<Effect> availableWhiteMarbleEffects = personalBoard.getAvailableEffects(EffectType.WHITE_MARBLE);

        //I add the available marble conversions to the list of the available ones (if any)
        for (Effect effect : availableWhiteMarbleEffects) {
            try {
                availableConversions.add(effect.getWhiteMarbleEffect());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        return availableConversions;
    }

    private List<Depot> availableDepotsForResourceType(Resource resource) throws DifferentEffectTypeException {
        List<Depot> depots = personalBoard.getWarehouse().getAvailableWarehouseDepotsForResourceType(resource);
        List<LeaderCard> cards = personalBoard.availableLeaderCards();
        LeaderDepot depot;
        for (LeaderCard card : cards) {
            if (card.getEffect().getEffectType() == EffectType.EXTRA_DEPOT) {
                depot = card.getEffect().getExtraDepotEffect().getLeaderDepot();
                if (depot.getResourceType() == resource && depot.spaceAvailable() > 1)
                    depots.add(depot);
            }
        }
        return depots;
    }


    public void execute(TurnController turnController) throws InvalidArgumentException, DifferentEffectTypeException, InvalidMethodException, ZeroPlayerException, InvalidDepotException, InsufficientSpaceException {
        List<Marble> marbles = market.insertMarbleFromTheSlide(insertionPosition);
        Depot depotChosen;
        LeaderDepot leaderDepot;
        WarehouseDepot warehouseDepot;
        List<Marble> conversionsAvailable = getAvailableConversions();

        MultiplayerPlayPhase multiplayerPlayPhase;

        // If the user has any leader card with white marble effect I ask him to choose:
        // a) whether he wants to convert it
        // b) which resource he wants to have
        if (!getAvailableConversions().isEmpty())
            for (Marble marble : marbles) {
                if (marble == Marble.WHITE) {
                    //TODO parte in cui l'utente sceglie come convertire le biglie bianche marble = Marble.scelta_utente
                }
            }

        //I convert the marbles to the respective resources
        marblesConversion(marbles);

        //For each resource I ask to the client where he wants to store it
        while (!resourcesToStore.isEmpty()){
            //getStorageFromClient
            List<Depot> availableDepots = availableDepotsForResourceType(resourcesToStore.get(0));
            if (availableDepots.isEmpty()){
                if (controller.getGame().getGameType() == GameType.MULTI_PLAYER){
                    controller.getGamePhase().handleResourceDiscard();
                }
            }
            else{
                //TODO the client tells where he wants to store the resource, this is just an example where I choose the first one
                depotChosen = availableDepots.get(0);
                if (depotChosen instanceof LeaderDepot) {
                    leaderDepot = (LeaderDepot) depotChosen;
                    leaderDepot.addResources(1);
                }
                else if (depotChosen instanceof WarehouseDepot) {
                    warehouseDepot = (WarehouseDepot) depotChosen;
                    warehouseDepot.addResources(1);
                }

            }
            resourcesToStore.remove(0);
        }
    }


}
