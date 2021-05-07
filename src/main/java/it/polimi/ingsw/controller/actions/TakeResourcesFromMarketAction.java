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
    private List<String> availableDepotsForReorganization;
    private List<Resource> availableLeaderResources;

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
        if (message instanceof DiscardResourceRequest)
            handleDiscard(((DiscardResourceRequest) message).getResource());
        if (message instanceof ReorganizeDepotRequest)
            handleReorganizeDepotsRequest();
        if (message instanceof SwapWarehouseDepotsRequest)
            handleSwapRequest(((SwapWarehouseDepotsRequest) message).getOriginDepot(), ((SwapWarehouseDepotsRequest) message).getDestinationDepot());
        if (message instanceof MoveResourcesRequest)
            handleMoveRequest(((MoveResourcesRequest) message).getOriginDepot(), ((MoveResourcesRequest) message).getDestinationDepot(), ((MoveResourcesRequest) message).getResource(), ((MoveResourcesRequest) message).getQuantity());
        if (message instanceof NotifyEndDepotsReorganization)
            handleEndDepotsOrganization();
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

    private void handleReorganizeDepotsRequest(){
        availableDepotsForReorganization = ResourceStorageType.getWarehouseDepots();
        availableLeaderResources = new ArrayList<>();
        for (Effect effect : player.getPersonalBoard().getAvailableEffects(EffectType.EXTRA_DEPOT)){
            try{
                availableLeaderResources.add(effect.getExtraDepotEffect().getLeaderDepot().getResourceType());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        if (!player.getPersonalBoard().getAvailableEffects(EffectType.EXTRA_DEPOT).isEmpty())
            availableDepotsForReorganization.add(ResourceStorageType.LEADER_DEPOT.name());
        clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, true, false, availableLeaderResources));
    }

    private void handleSwapRequest(String origin, String destination){
        if (!ResourceStorageType.getWarehouseDepots().contains(origin) || !ResourceStorageType.getWarehouseDepots().contains(destination))
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
        else {
            try {
                player.getPersonalBoard().getWarehouse().switchRows(ResourceStorageType.valueOf(origin).getValue(), ResourceStorageType.valueOf(destination).getValue());
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, false, availableLeaderResources));
            } catch (UnswitchableDepotsException | InsufficientSpaceException e) {
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMoveRequest(String originString, String destinationString, Resource resource, int quantity){
        ResourceStorageType origin = ResourceStorageType.valueOf(originString);
        ResourceStorageType destination = ResourceStorageType.valueOf(destinationString);
        try {
            Resource originResourceType = resource == Resource.ANY ? player.getPersonalBoard().getWarehouse().getResourceTypeOfDepot(ResourceStorageType.valueOf(originString).getValue()) : resource;
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        try {
            player.getPersonalBoard().removeResources(origin, resource, quantity);
        } catch (InsufficientQuantityException e) {
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
        } catch (InvalidResourceTypeException e) {
            e.printStackTrace();
        }
        try {
            player.getPersonalBoard().addResources(destination, resource, quantity);
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, false, availableLeaderResources));
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException e) {
            e.printStackTrace();
        } catch (InsufficientSpaceException e) {
            try {
                player.getPersonalBoard().addResources(origin, resource, quantity);
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException ignored) { }
        }
    }

    void handleEndDepotsOrganization(){
        if (resourcesToStore.isEmpty())
            manageEndAction();
        else
            handleChooseStorageTypeRequest();
    }

    /**
     * Method used to ask the client where he wants to store a {@link Resource}
     */
    private void handleChooseStorageTypeRequest() {
        Resource resource = resourcesToStore.get(0);
        List<String> availableDepots = getAvailableDepots(resource);
        clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resource, availableDepots, false));
    }

    /**
     * Method used to get a {@link LinkedList} of the available depots for the {@link Resource} provided
     *
     * @param resource the {@link Resource} to store
     * @return a List of the name() of the {@link ResourceStorageType} available
     */
    private List<String> getAvailableDepots(Resource resource) {
        //Warehouse depots
        List<String> availableDepots = player.getPersonalBoard().getWarehouse().getAvailableWarehouseDepotsForResourceType(resource).stream().map(Enum::name).collect(Collectors.toList());
        if (player.getPersonalBoard().isLeaderDepotAvailable(resource, 1))
            availableDepots.add(ResourceStorageType.LEADER_DEPOT.name());
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
     * @param resource the {@link Resource} the acting player wants to store
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

}
