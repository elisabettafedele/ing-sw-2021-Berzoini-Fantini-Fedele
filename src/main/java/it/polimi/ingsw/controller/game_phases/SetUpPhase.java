package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.messages.toClient.game.ChooseLeaderCardsRequest;
import it.polimi.ingsw.messages.toClient.game.ChooseResourceTypeRequest;
import it.polimi.ingsw.messages.toClient.game.ChooseStorageTypeRequest;
import it.polimi.ingsw.messages.toClient.game.NotifyResourcesToStore;
import it.polimi.ingsw.messages.toClient.matchData.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.persistency.GameHistory;
import it.polimi.ingsw.model.persistency.PersistentControllerSetUpPhase;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.*;
import it.polimi.ingsw.messages.toServer.game.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.game.ChooseResourceTypeResponse;
import it.polimi.ingsw.messages.toServer.game.ChooseStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import java.util.*;
import java.util.stream.Collectors;

public class SetUpPhase implements GamePhase {
    Controller controller;
    Map<String, Integer> initialResourceByNickname;
    Map<String, List<Resource>> resourcesToStoreByNickname;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        resourcesToStoreByNickname = new HashMap<>();
        controller.sendLightCards();
        setUpLeaderCards();
        controller.sendMessageToAll(new LoadDevelopmentCardGrid(null, controller.getGame().getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
    }

    /**
     * Constructor of the SetUpPhase used when a game is reloaded from the json file
     * @param resourcesToStoreByNickname
     * @param controller
     */
    public SetUpPhase(Map<String, List<Resource>> resourcesToStoreByNickname, Controller controller){
        this.resourcesToStoreByNickname = resourcesToStoreByNickname;
        this.controller = controller;
    }

    /**
     * Standard constructor of the Set Up phase. It is used when a new game is created.
     */
    public SetUpPhase(){ }

    /**
     * Method to reload the phase retrieving the needed data from the {@link PersistentControllerSetUpPhase}
     */
    public void reloadPhase(){
        List<String> nicknames = controller.getNicknames();
        initialResourceByNickname = new HashMap<>();
        nicknames.forEach(x -> initialResourceByNickname.put(x, getNumberOfInitialResourcesByIndex(nicknames.indexOf(x))));
        controller.sendLightCards();
        controller.sendMatchData(controller.getGame(), false);
        for (String nickname : nicknames){
            if (controller.getPlayerByNickname(nickname).getPersonalBoard().getLeaderCards().size() == 4){
                controller.getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS);
                controller.getConnectionByNickname(nickname).sendMessageToClient(new ChooseLeaderCardsRequest(controller.getPlayerByNickname(nickname).getPersonalBoard().getLeaderCards().stream().map(Card::getID).collect(Collectors.toList())));
            }
            else {
                if (initialResourceByNickname.get(nickname) == 0){
                    controller.getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
                    endPhaseManager(controller.getConnectionByNickname(nickname));
                }
                else if (initialResourceByNickname.get(nickname) == controller.getPlayerByNickname(nickname).getPersonalBoard().countResourceNumber()){
                    controller.getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
                    endPhaseManager(controller.getConnectionByNickname(nickname));
                }
                else if (resourcesToStoreByNickname.get(nickname).isEmpty()){
                    assignResources(controller.getConnectionByNickname(nickname));
                }
                else {
                    Resource resourceType = resourcesToStoreByNickname.get(nickname).get(0);
                    List<String> availableStorage = controller.getPlayerByNickname(nickname).getPersonalBoard().getWarehouse().getAvailableWarehouseDepotsForResourceType(resourceType).stream().map(x -> x.name()).collect(Collectors.toList());
                    controller.getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE);
                    List<Resource> resourcesList= resourcesToStoreByNickname.get(nickname);
                    controller.getConnectionByNickname(nickname).sendMessageToClient(new NotifyResourcesToStore(resourcesList));
                    controller.getConnectionByNickname(nickname).sendMessageToClient(new ChooseStorageTypeRequest(resourceType, availableStorage, false, false));
                }
            }
        }
    }


    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (!clientHandler.isGameStarted())
            return;
        if (message instanceof ChooseLeaderCardsResponse && clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS)
            removeLeaderCards(((ChooseLeaderCardsResponse) message).getDiscardedLeaderCards(), clientHandler);

        if (message instanceof ChooseResourceTypeResponse && clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE)
            setInitialResourcesByNickname(((ChooseResourceTypeResponse) message).getResources(), clientHandler);

        if (message instanceof ChooseStorageTypeResponse && clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE)
            storeResource(((ChooseStorageTypeResponse) message).getResource(), ((ChooseStorageTypeResponse) message).getStorageType(), clientHandler);
    }

    /**
     * Method to assign 4 leader cards to each players and to randomly set the order in which the player will play
     */
    private void setUpLeaderCards() {
        List<LeaderCard> leaderCards = LeaderCardParser.parseCards();
        Collections.shuffle(leaderCards);

        //Shuffle the order of the nicknames
        List<String> nicknames = controller.getNicknames();
        Collections.shuffle(nicknames);

        //I set the number of resources for each player
        initialResourceByNickname = new HashMap<>();

        for (int i = 0; i < nicknames.size(); i++) {

            // 1. I assign leader cards and add the player to the game
            List<LeaderCard> leaderCardsAssigned = assignLeaderCards(leaderCards, 4 * i, 4 * i + 4);
            addPlayerToTheGame(nicknames.get(i), leaderCardsAssigned, i);

            // 2. I set the number of resources for each player
            initialResourceByNickname.put(nicknames.get(i), getNumberOfInitialResourcesByIndex(i));
        }

        //I send to everyone the view with the leader Cards to choose
        GameHistory.saveSetupPhase(new PersistentControllerSetUpPhase(new PersistentGame(controller.getGame()), controller.getControllerID(), resourcesToStoreByNickname));
        controller.sendMatchData(controller.getGame(), false);

        for (int i = 0; i < nicknames.size(); i++) {
            // 3. I send to the client the leader cards assigned
            ClientHandler connection = controller.getConnectionByNickname(nicknames.get(i));
            connection.setClientHandlerPhase(ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS);
            connection.sendMessageToClient(new ChooseLeaderCardsRequest(controller.getPlayerByNickname(nicknames.get(i)).getPersonalBoard().getLeaderCards().stream().map(Card::getID).collect(Collectors.toList())));
        }

    }

    /**
     * Method to remove the two discarded {@link LeaderCard} from the {@link it.polimi.ingsw.model.player.PersonalBoard} of the {@link Player}
     * @param discardedCards the two cards to be removed
     * @param clientHandler the connection of the player who provided the cards
     */
    private void removeLeaderCards(List<Integer> discardedCards, ClientHandler clientHandler) {
        String nickname = clientHandler.getNickname();
        Player player = controller.getPlayerByNickname(nickname);
        for (Integer id : discardedCards)
            player.getPersonalBoard().removeLeaderCard(id);
        GameHistory.saveSetupPhase(new PersistentControllerSetUpPhase(new PersistentGame(controller.getGame()), controller.getControllerID(), resourcesToStoreByNickname));
        controller.sendMessageToAll(new ReloadLeaderCardsOwned(nickname, player.getPersonalBoard().getLeaderCardsMap()));
        if (getNumberOfInitialResourcesByNickname(nickname) == 0) {
            endPhaseManager(clientHandler);
        } else {
            assignResources(clientHandler);
        }
    }

    /**
     * Method to assign the right number of resources to a specific player, depending on his position in the turn round
     * @param clientHandler the player I want to assign the resources to
     */
    public void assignResources(ClientHandler clientHandler) {
        List<Resource> resourceTypes = Resource.realValues();
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
        clientHandler.sendMessageToClient(new ChooseResourceTypeRequest(resourceTypes, getNumberOfInitialResourcesByNickname(clientHandler.getNickname())));
    }

    /**
     * Method to ask where a specific player wants to store the resources he has taken
     * @param resources resources to store
     * @param clientHandler the connection of the player
     */
    private void setInitialResourcesByNickname(List<Resource> resources, ClientHandler clientHandler){
        resourcesToStoreByNickname.put(clientHandler.getNickname(), resources);
        GameHistory.saveSetupPhase(new PersistentControllerSetUpPhase(new PersistentGame(controller.getGame()), controller.getControllerID(), resourcesToStoreByNickname));
        List<String> availableDepots = ResourceStorageType.getWarehouseDepots();
        if (resources.size() == 2 && resources.get(0) == resources.get(1))
            availableDepots.remove(ResourceStorageType.WAREHOUSE_FIRST_DEPOT.name());
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE);
        clientHandler.sendMessageToClient(new NotifyResourcesToStore(resources));
        clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resources.get(0), availableDepots, false, false));
    }

    /**
     * Method to handle the response of the client, regarding the placement of his resources
     * @param resource the {@link Resource} to be stored
     * @param storageType where the {@link Resource} should be stored
     * @param clientHandler the connection of the player who has asked to store his {@link Resource}
     */
    private void storeResource(Resource resource, String storageType, ClientHandler clientHandler){
        Player player = controller.getPlayerByNickname(clientHandler.getNickname());
        resourcesToStoreByNickname.get(player.getNickname()).remove(resource);
        try {
            player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storageType), resource, 1);
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
            e.printStackTrace();
        }
        GameHistory.saveSetupPhase(new PersistentControllerSetUpPhase(new PersistentGame(controller.getGame()), controller.getControllerID(), resourcesToStoreByNickname));
        if (resourcesToStoreByNickname.get(player.getNickname()).isEmpty()) {
            controller.sendMessageToAll(new UpdateDepotsStatus(player.getNickname(), player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), player.getPersonalBoard().getStrongboxStatus(), player.getPersonalBoard().getLeaderStatus()));
            endPhaseManager(clientHandler);
        } else {
            Resource resourceType = resourcesToStoreByNickname.get(player.getNickname()).get(0);
            List<String> availableStorage = player.getPersonalBoard().getWarehouse().getAvailableWarehouseDepotsForResourceType(resourceType).stream().map(x -> x.name()).collect(Collectors.toList());
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE);
            controller.sendMessageToAll(new UpdateDepotsStatus(player.getNickname(), player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), player.getPersonalBoard().getStrongboxStatus(), player.getPersonalBoard().getLeaderStatus()));
            clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resourceType, availableStorage, false, false));
        }

    }

    /**
     * Method that checks whether all the players has finished the set up.
     * If they are all ready to start, it makes the game start!
     * @param clientHandler the connection of the client who has just finished the set up
     */
    public synchronized void endPhaseManager(ClientHandler clientHandler) {
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
        if (!(controller.getGamePhase() instanceof SetUpPhase))
            return;
        List<String> nicknames = controller.getClientHandlers().stream().map(ClientHandler::getNickname).collect(Collectors.toList());
        for (String nickname : nicknames) {
            if (controller.getConnectionByNickname(nickname).getClientHandlerPhase() != ClientHandlerPhase.SET_UP_FINISHED) {
                clientHandler.sendMessageToClient(new TextMessage("Waiting the other players, the game will start as soon as they all be ready..."));
                return;
            }
        }
        controller.setGamePhase(controller.getGame().getGameMode() == GameMode.MULTI_PLAYER ? new MultiplayerPlayPhase(controller) : new SinglePlayerPlayPhase(controller));
    }

    /**
     * Method to assign the {@link LeaderCard} of the list contained between the index start and end
     * @param cards a list of {@link LeaderCard}
     * @param start the starting index to assign the cards (included)
     * @param end the finishing index to assign the cards (excluded)
     * @return a {@link LinkedList} with the selected cards
     */
    private List<LeaderCard> assignLeaderCards(List<LeaderCard> cards, int start, int end) {
        List<LeaderCard> IDs = new LinkedList<>();
        for (int i = start; i < end; i++)
            IDs.add(cards.get(i));
        return IDs;
    }

    /**
     * Method to add a specific {@link Player} to the {@link it.polimi.ingsw.model.game.Game}
     * @param nickname the nickname of the player
     * @param leaderCardsAssigned the four {@link LeaderCard} assigned to the {@link Player}
     * @param index the player's position in the round order
     */
    private void addPlayerToTheGame(String nickname, List<LeaderCard> leaderCardsAssigned, int index) {
        try {
            controller.getGame().addPlayer(nickname, leaderCardsAssigned, getInitialFaithPoints(index), hasInkwell(index));
        } catch (InvalidArgumentException | InvalidPlayerAddException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param index the player's position in the round order
     * @return true iff the player has the inkwell (the player is the first of the round)
     */
    private boolean hasInkwell(int index) {
        return index == 0;
    }

    /**
     * @param index the player's position in the round order
     * @return the number of initial faith points to assign to the player
     */
    private int getInitialFaithPoints(int index) {
        return index > 1 ? 1 : 0;
    }

    /**
     * @param index the player's position in the round order
     * @return the number of initial resources to assign to that {@link Player}
     */
    private int getNumberOfInitialResourcesByIndex(int index) {
        if (index == 0)
            return 0;
        if (index < 3)
            return 1;
        return 2;
    }

    /**
     * @param nickname the player's nickname
     * @return the number of initial resources to assign to that {@link Player}
     */
    public int getNumberOfInitialResourcesByNickname(String nickname) {
        return initialResourceByNickname.get(nickname);
    }

    public String toString() {
        return "Set Up Phase";
    }
}
