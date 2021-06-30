package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.messages.toClient.game.*;
import it.polimi.ingsw.messages.toClient.matchData.NotifyVictoryPoints;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarkerPosition;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarketView;
import it.polimi.ingsw.messages.toClient.matchData.UpdateDepotsStatus;
import it.polimi.ingsw.messages.toServer.game.*;
import it.polimi.ingsw.model.game.FaithTrack;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toServer.*;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to handle the request of a {@link Player} to take some {@link Resource} from {@link Market}
 */
public class TakeResourcesFromMarketAction implements Action {

    private Player player;
    private ClientHandler clientHandler;
    private Controller controller;
    private Market market;
    private List<Resource> resourcesToStore;
    private List<Marble> marblesToConvert;
    private TurnController turnController;
    private List<String> availableDepotsForReorganization;
    private List<Resource> availableLeaderResources;

    /**
     * Constructor of the action
     * @param turnController the {@link TurnController} of the current {@link it.polimi.ingsw.model.game.Game}
     */
    public TakeResourcesFromMarketAction(TurnController turnController) {
        this.player= turnController.getCurrentPlayer();
        this.turnController = turnController;
        this.controller = turnController.getController();
        this.resourcesToStore = new LinkedList<>();
        this.market = controller.getGame().getMarket();
    }

    /**
     * Method overriden. It checks whether the action is executable.
     * Since the player is always able to take resources from market it always returns true.
     * @return
     */
    @Override
    public boolean isExecutable() {
        return true;
    }

    /**
     * Method to reset the action from one turn to the next one
     * @param currentPlayer {@link Player} who owns the turn
     */
    @Override
    public void reset(Player currentPlayer) {
        this.player = currentPlayer;
        this.clientHandler = controller.getConnectionByNickname(currentPlayer.getNickname());
        this.market = controller.getGame().getMarket();
        resourcesToStore=new LinkedList<>();
        marblesToConvert= new LinkedList<>();
        availableLeaderResources= new LinkedList<>();
        availableDepotsForReorganization=new LinkedList<>();
    }

    /**
     * Method to start the execution of the action.
     * First, I ask the client the desired insertion position of the slide marble
     */
    public void execute() {
        clientHandler.setCurrentAction(this);
        clientHandler.sendMessageToClient(new MarbleInsertionPositionRequest());
    }


    /**
     * Method called once the client has inserted the desired insertion position
     * It checks if there are any white marble conversion available and:
     * - If no conversion is available: it converts the marbles in resources in the standard way
     * - If one conversion is available: it converts the white marbles according to the effect owned, and then it converts the other marbles in the standard way
     * - If two conversions are available: for each white marble it asks the client which conversion he wants to use
     * @param insertionPosition a number between 1 and 7. It is the insertion position of the slide marble
     */
    public void handleInsertionPositionResponse(int insertionPosition) {
        int whiteMarblesNumber = player.getPersonalBoard().getAvailableEffects(EffectType.WHITE_MARBLE).size();
        try {
            marblesToConvert = market.insertMarbleFromTheSlide(insertionPosition - 1);
            controller.sendMessageToAll(new UpdateMarketView(clientHandler.getNickname(), controller.getGame().getMarket().getMarketTray(), controller.getGame().getMarket().getSlideMarble()));
            clientHandler.sendMessageToClient(new NotifyMarbleTaken(marblesToConvert, whiteMarblesNumber > 1));
        } catch (InvalidArgumentException ignored) { }// Never thrown I check its validity before
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
     * Method to change the color of the first white marble of the list. The client has to choose between the two conversions owned
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
     * Method to get the list of the available white marble conversions of the acting player, according to the active leader cards (if any)
     * @return an {@link ArrayList} containing all the possible {@link Resource} types that a white marble can be converted to, according to the {@link LeaderCard} possessed by the acting player
     */
    public List<Resource> getWhiteMarblesConversion() {
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
                    controller.sendMessageToAll(new UpdateMarkerPosition(player.getNickname(), player.getPersonalBoard().getMarkerPosition()));
                    if (FaithTrack.changesVictoryPoints(player.getPersonalBoard().getMarkerPosition()))
                        turnController.getController().sendMessageToAll(new NotifyVictoryPoints(player.getNickname(), player.countPoints()));
                    turnController.checkFaithTrack();
                } catch (InvalidArgumentException ignored) { } //I move the marker of a not negative quantity
            } else if (marble != Marble.WHITE)
                resourcesToStore.add(Resource.valueOf(marble.getValue()));
        }
        if (resourcesToStore.isEmpty()) {
            manageEndAction();
        } else {
            clientHandler.sendMessageToClient(new NotifyResourcesToStore(resourcesToStore));
            handleChooseStorageTypeRequest();
        }
    }

    /**
     * Method used when the acting player asks to reorganize his depots
     */
    private void handleReorganizeDepotsRequest(){
        availableDepotsForReorganization = new ArrayList<>();
        if (!player.getPersonalBoard().getWarehouse().getResourceTypes().isEmpty())
            availableDepotsForReorganization = ResourceStorageType.getWarehouseDepots();
        availableLeaderResources = new ArrayList<>();
        //If the player has some leader depots, I save the Resource type of these depots in availableDepotsResources, so that I will be able to identify the depot in the client. If he does not, the list will be empty
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

    /**
     * Method used to handle the request of a swap from the client, during depots' reorganization
     * @param origin the string of the corresponding {@link ResourceStorageType} of the first depot to swap
     * @param destination the string of the corresponding {@link ResourceStorageType} of the second depot to swap
     */
    private void handleSwapRequest(String origin, String destination){
        //It is not possible to swap resources between leader depots and warehouse

        //If the client want to swap two leader depots
        if (origin.equals(destination) && origin.equals(ResourceStorageType.LEADER_DEPOT.name())){
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            return;
        }
        if (!ResourceStorageType.getWarehouseDepots().contains(origin) || !ResourceStorageType.getWarehouseDepots().contains(destination))
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
        else {
            try {
                player.getPersonalBoard().getWarehouse().switchRows(ResourceStorageType.valueOf(origin).getValue(), ResourceStorageType.valueOf(destination).getValue());
                controller.sendMessageToAll(new UpdateDepotsStatus(clientHandler.getNickname(), player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), player.getPersonalBoard().getStrongboxStatus(), player.getPersonalBoard().getLeaderStatus()));
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, false, availableLeaderResources));
            } catch (UnswitchableDepotsException | InsufficientSpaceException e) {
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to handle the move of a certain quanity of resource from a depot to another
     * @param originString
     * @param destinationString
     * @param resource must be specified only if the client has more than one leader depot and decides to select LEADER as origin or destination, otherwise it is ANY
     * @param quantity
     */
    private void handleMoveRequest(String originString, String destinationString, Resource resource, int quantity){
        ResourceStorageType origin = ResourceStorageType.valueOf(originString);
        ResourceStorageType destination = ResourceStorageType.valueOf(destinationString);
        Resource originResourceType = Resource.ANY;
        try {
            originResourceType = resource == Resource.ANY ? player.getPersonalBoard().getWarehouse().getResourceTypeOfDepot(ResourceStorageType.valueOf(originString).getValue()) : resource;
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        try {
            player.getPersonalBoard().removeResources(origin, originResourceType, quantity);
        } catch (InsufficientQuantityException| InvalidResourceTypeException e) {
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            return;
        }
        try {
            player.getPersonalBoard().addResources(destination, originResourceType, quantity);
            controller.sendMessageToAll(new UpdateDepotsStatus(clientHandler.getNickname(), player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), player.getPersonalBoard().getStrongboxStatus(), player.getPersonalBoard().getLeaderStatus()));
            clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, false, availableLeaderResources));
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
            try {
                //If the add of resources in the destination depot was not possible, I add again the resources to the origin depot
                player.getPersonalBoard().addResources(origin, originResourceType, quantity);
                clientHandler.sendMessageToClient(new SendReorganizeDepotsCommands(availableDepotsForReorganization, false, true, availableLeaderResources));
            } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException ignored) { }
        }
    }

    /**
     * Method used to end the Reorganization of the depots
     */
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
        clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resource, availableDepots, true, player.getPersonalBoard().countResourceNumber() > 0));
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
        ((PlayPhase) controller.getGamePhase()).handleResourceDiscard(player.getNickname());
        if (resourcesToStore.isEmpty())
            manageEndAction();
        else
            handleChooseStorageTypeRequest();
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
            controller.sendMessageToAll(new NotifyVictoryPoints(player.getNickname(), player.countPoints()));
            controller.sendMessageToAll(new UpdateDepotsStatus(player.getNickname(), player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), player.getPersonalBoard().getStrongboxStatus(), player.getPersonalBoard().getLeaderStatus()));
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
        //clientHandler.sendMessageToClient(new DisplayStandardView());
        turnController.setNextAction();
    }

    /**
     * Method to handle the messages received inside the action
     * @param message the message to handle
     */
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

    public String toString(){
        return ActionType.TAKE_RESOURCE_FROM_MARKET.name().replace('_', ' ');
    }
}
