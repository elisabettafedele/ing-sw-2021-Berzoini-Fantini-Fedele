package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.PlayPhase;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.*;
import it.polimi.ingsw.messages.toServer.*;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TakeResourcesFromMarketAction implements Action {

    private Player player;
    private ClientHandler clientHandler;
    private Market market;
    private PlayPhase playPhase;
    private List<Resource> resourcesToStore;
    private List<Marble> marblesToConvert;
    private TurnController turnController;

    /**
     * Constructor of the action
     *
     * @param player         the acting {@link Player}
     * @param clientHandler  the {@link ClientHandler} of the acting player
     * @param market         the market of the {@link it.polimi.ingsw.model.game.Game} the acting player is playing in
     * @param playPhase      can be {@link it.polimi.ingsw.controller.MultiplayerPlayPhase} or {@link it.polimi.ingsw.controller.SinglePlayerPlayPhase}
     * @param turnController the turn controller of the {@link it.polimi.ingsw.model.game.Game} the acting player is playing to
     */
    public TakeResourcesFromMarketAction(Player player, ClientHandler clientHandler, Market market, PlayPhase playPhase, TurnController turnController) {
        this.player = player;
        this.clientHandler = clientHandler;
        this.market = market;
        this.playPhase = playPhase;
        this.resourcesToStore = new LinkedList<>();
        this.turnController = turnController;
    }

    public boolean isExecutable() {
        return true;
    }


    public void execute(TurnController turnController) {
        clientHandler.setCurrentAction(this);
        clientHandler.sendMessageToClient(new MarbleInsertionPositionRequest(this, false));
    }

    @Override
    public void handleMessage(MessageToServer message) {
        if (message instanceof MarbleInsertionPositionResponse)
            handleInsertionPositionResponse(((MarbleInsertionPositionResponse) message).getInsertionPosition());
        if (message instanceof ChooseWhiteMarbleConversionResponse)
            handleWhiteMarblesConversionResponse(((ChooseWhiteMarbleConversionResponse) message).getResource());
        if (message instanceof ChooseStorageTypeResponse)
            storeResource(((ChooseStorageTypeResponse) message).getResource(), ((ChooseStorageTypeResponse) message).getStorageType());
        if (message instanceof DiscardResourceRequest) {
            handleDiscard(((DiscardResourceRequest) message).getResource());
        }

    }

    /**
     * Method called once that the client has inserted the desired insertion position
     *
     * @param insertionPosition a number between 1 and 7. It is the insertion position of the marble
     */
    public void handleInsertionPositionResponse(int insertionPosition) {
        int whiteMarblesNumber = player.getPersonalBoard().getAvailableEffects(EffectType.WHITE_MARBLE).size();
        try {
            marblesToConvert = market.insertMarbleFromTheSlide(insertionPosition - 1);
            clientHandler.sendMessageToClient(new NotifyMarbleTaken(marblesToConvert, whiteMarblesNumber > 1));
        } catch (InvalidArgumentException e) {
        }
        if (whiteMarblesNumber > 1 && marblesToConvert.contains(Marble.WHITE)) {
            handleWhiteMarblesConversion();
        } else if ((whiteMarblesNumber == 1 && marblesToConvert.contains(Marble.WHITE)))
            handleAutomaticConversion();
        else {
            marblesConversion();
        }
    }

    /**
     * Method to convert all the white marbles in the only available marble conversion
     */
    public void handleAutomaticConversion() {
        Marble conversion = Marble.valueOf(getWhiteMarblesConversion().get(0).getValue());
        marblesToConvert = marblesToConvert.stream().map(x -> x == Marble.WHITE ? conversion : x).collect(Collectors.toList());
        marblesConversion();
    }

    /**
     * Method to ask to a client that has more than one White Marble {@link Effect} how he wants to convert his white marbles
     */
    public void handleWhiteMarblesConversion() {
        List<Resource> availableConversion = getWhiteMarblesConversion();
        int numberOfWhiteMarbles = (int) marblesToConvert.stream().filter(x -> x == Marble.WHITE).count();
        clientHandler.sendMessageToClient(new ChooseWhiteMarbleConversionRequest(availableConversion, numberOfWhiteMarbles));
    }

    /**
     * Method to change the color of the first white marble
     *
     * @param conversion the color of the produced marble
     */
    public void handleWhiteMarblesConversionResponse(List<Resource> conversion) {
        for (int i = 0; i < marblesToConvert.size(); i++) {
            if (marblesToConvert.get(i) == Marble.WHITE) {
                marblesToConvert.set(i, Marble.valueOf(conversion.get(0).getValue()));
                conversion.remove(0);
            }
        }
        marblesConversion();
    }

    /**
     * Method to get the list of the available white marble conversions of the acting player
     *
     * @return an {@link ArrayList} containing all the possible {@link Resource} types that a white marble can be converted to, according to the {@link LeaderCard} possessed by the acting player
     */
    private List<Resource> getWhiteMarblesConversion() {
        List<Resource> availableConversions = new ArrayList<>();
        List<Effect> effectsWhiteMarble = player.getPersonalBoard().getAvailableEffects(EffectType.WHITE_MARBLE);
        for (Effect effect : effectsWhiteMarble) {
            try {
                availableConversions.add(effect.getWhiteMarbleEffectResource());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        return availableConversions;
    }

    /**
     * Method used to convert all the marbles present in marbleToConvert in the corresponding resources.
     * After the conversion, the obtained list of {@link Resource} is stored in the attribute resourcesToStore
     */
    private void marblesConversion() {
        for (Marble marble : marblesToConvert) {
            if (marble == Marble.RED) {
                try {
                    player.getPersonalBoard().moveMarker(1);
                } catch (InvalidArgumentException ignored) {
                }
            } else if (marble != Marble.WHITE)
                resourcesToStore.add(Resource.valueOf(marble.getValue()));
        }
        clientHandler.sendMessageToClient(new TextMessage("Conversion done!\nYou now have to store these resources" + resourcesToStore));
    }

    /**
     * Method used to ask the client where he wants to store a {@link Resource}
     */
    private void handleChooseStorageTypeRequest() {
        Resource resource = resourcesToStore.get(0);
        List<String> availableDepots = getAvailableDepots(resource);
        clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resource, availableDepots));
    }

    /**
     * Method used to get a {@link LinkedList} of the available depots for the {@link Resource} provided
     *
     * @param resource the {@link Resource} to store
     * @return a List of the name() of the {@link ResourceStorageType} available
     */
    private List<String> getAvailableDepots(Resource resource) {
        List<String> availableDepots = new LinkedList<>();
        //TODO
        return availableDepots;
    }

    /**
     * Method used to handle the discard of a {@link Resource} of the acting player
     *
     * @param resource the {@link Resource} the player wants to discard
     */
    private void handleDiscard(Resource resource) {
        resourcesToStore.remove(resource);
        playPhase.handleResourceDiscard(player.getNickname());
    }

    /**
     * Method used to store a specific {@link Resource} of the resourcesToStore in a specific depot
     *
     * @param resource    the {@link Resource} the acting player wants to store
     * @param storageType name() of the {@link ResourceStorageType} where te acting player wants to store the {@link Resource}
     */
    private void storeResource(Resource resource, String storageType) {
        resourcesToStore.remove(resource);
        try {
            player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storageType), resource, 1);
        } catch (InvalidDepotException | InsufficientSpaceException | InvalidResourceTypeException | InvalidArgumentException e) {
        }
        if (resourcesToStore.isEmpty())
            manageEndAction();
        else
            handleChooseStorageTypeRequest();
    }

    /**
     * Method to handle the end of the action
     */
    private void manageEndAction() {
        turnController.setStandardActionDoneToTrue();
        turnController.nextActionManager();
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
