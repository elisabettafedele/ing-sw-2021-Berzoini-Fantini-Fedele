package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.MultiplayerPlayPhase;
import it.polimi.ingsw.controller.PlayPhase;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.MarbleInsertionPositionRequest;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.LeaderDepot;
import it.polimi.ingsw.model.depot.WarehouseDepot;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.player.PersonalBoard;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TakeResourcesFromMarketAction {

    /*
    private int insertionPosition;
    private Market market;
    private List<Resource> resourcesToStore;
    private PersonalBoard personalBoard;
    private Controller controller;*/

    private Player player;
    private ClientHandler clientHandler;
    private Market market;
    private PlayPhase playPhase;


    public TakeResourcesFromMarketAction(Player player, ClientHandler clientHandler, Market market, PlayPhase playPhase){
        this.player = player;
        this.clientHandler = clientHandler;
        this.market = market;
        this.playPhase = playPhase;
    }

    public boolean isExecutable() {
        return true;
    }

    public void execute(TurnController turnController){
        clientHandler.sendMessageToClient(new MarbleInsertionPositionRequest(false));
    }











    /*
    public TakeResourcesFromMarketAction(PersonalBoard personalBoard, Market market, int insertionPosition, Controller controller) {
        this.insertionPosition = insertionPosition;
        this.market = market;
        this.personalBoard = personalBoard;
    }

    public boolean isExecutable() {
        return true;
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
        List<Marble> conversionsAvailable = personalBoard.getAvailableConversions();

        MultiplayerPlayPhase multiplayerPlayPhase;

        // If the user has any leader card with white marble effect I ask him to choose:
        // a) whether he wants to convert it
        // b) which resource he wants to have
        if (!personalBoard.getAvailableConversions().isEmpty())
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
                if (controller.getGame().getGameMode() == GameMode.MULTI_PLAYER){
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

*/
}
