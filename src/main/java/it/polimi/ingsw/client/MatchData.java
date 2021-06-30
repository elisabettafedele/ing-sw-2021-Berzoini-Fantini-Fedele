package it.polimi.ingsw.client;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toClient.matchData.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a light version of the Model. It contains all the information that are in common
 * among all the players
 */
public class MatchData {

    List<LightDevelopmentCard> lightDevelopmentCards;
    List<LightLeaderCard> lightLeaderCards;
    LightClient thisClient;
    List<LightClient> otherClients;
    private Marble[][] marketTray;
    private Marble slideMarble;
    private List<Integer> developmentCardGrid;
    public static final int EMPTY_SLOT = -1;
    public static final String LORENZO = "Lorenzo";
    private int blackCrossPosition;
    private String currentViewNickname;
    private String turnOwnerNickname;
    private View view;

    public boolean isReloading() {
        return isReloading;
    }

    private boolean isReloading;
    private GameMode gameMode;

    private static MatchData instance;

    public static MatchData getInstance(){
        if (instance == null){
            instance = new MatchData();
        }
        return instance;
    }

    private MatchData(){
        this.blackCrossPosition = 0;
        this.isReloading = false;
        this.lightLeaderCards = new ArrayList<>();
        this.thisClient = new LightClient();
        this.otherClients = new ArrayList<>();
    }

    /**
     * Method to reset other clients when a new list of nicknames is received (the {@link MatchData} will be reloaded}
     */
    public void resetOtherClients(){
        this.otherClients = new ArrayList<>();
    }

    /**
     * Set the nickname of the LightClient representing the player himself
     * @param nickname the nickname chosen by the player
     */
    public void setThisClient(String nickname){
        thisClient.setNickname(nickname);
        currentViewNickname = nickname;
    }

    /**
     * Set the {@link View} of the ongoing match
     * @param view the {@link it.polimi.ingsw.client.cli.CLI} or the {@link it.polimi.ingsw.client.gui.GUI} used
     */
    public void setView(View view){
        this.view = view;
    }

    /**
     * Set the nickname of other players of the game, if any
     * @param nickname the nickname of the player
     */
    public void addLightClient(String nickname){
        LightClient lc = new LightClient();
        lc.setNickname(nickname);
        otherClients.add(lc);

    }

    /**
     * Return the {@link LightClient} object corresponding to a nickname of the players in game
     * @param nickname String containing the nickname of the player
     * @return {@link LightClient}
     */
    public LightClient getLightClientByNickname(String nickname) {
        for(LightClient lc : otherClients){
            if(lc.getNickname().equals(nickname))
                return lc;
        }
        return thisClient;
    }

    /**
     * Setter to set the boolean isReloading to "inform" the view to not display any scene when isReloading is True
     * @param reloading True if the reloading of all the information of the match is going on, false if it's not
     */
    public void setReloading(boolean reloading) {
        isReloading = reloading;
        display();
    }

    /**
     * Add to thisClient the {@link it.polimi.ingsw.model.cards.LeaderCard}
     * @param ID
     * @param active
     */
    public void addChosenLeaderCard(Integer ID, boolean active){
        thisClient.addLeaderCard(ID, active);
    }

    public void setAllLeaderCards(List<LightLeaderCard> allLeaderCards){
        this.lightLeaderCards = allLeaderCards;
    }

    /**
     * Set all the {@link LightDevelopmentCard} present in the game
     * @param lightDevelopmentCards the list of light version of
     * {@link it.polimi.ingsw.model.cards.DevelopmentCard}
     */
    public void setAllDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {
        this.lightDevelopmentCards = lightDevelopmentCards;
    }

    /**
     * Load the IDs of the {@link LightDevelopmentCard} to show in the developmentCardGrid
     * @param developmentCardGrid the List of IDs of the Cards
     */
    public void loadDevelopmentCardGrid(List<Integer> developmentCardGrid){
        this.developmentCardGrid = developmentCardGrid;
    }

    /**
     * Return a {@link LightDevelopmentCard} given an ID
     * @param ID the ID that identifies the Card
     * @return the {@link LightDevelopmentCard} corresponfing of a given ID, null if the ID is not correct
     */
    public LightDevelopmentCard getDevelopmentCardByID(Integer ID){
        for (LightDevelopmentCard ldc : lightDevelopmentCards){
            if(ldc.getID() == ID){
                return ldc;
            }
        }
        return null;
    }

    /**
     * Return a {@link LightLeaderCard} given an ID
     * @param ID the ID that identifies the Card
     * @return the {@link LightLeaderCard} corresponfing of a given ID, null if the ID is not correct
     */
    public LightLeaderCard getLeaderCardByID(int ID){
        for (LightLeaderCard lc : lightLeaderCards){
            if(lc.getID() == ID){
                return lc;
            }
        }
        return null;
    }

    /**
     * Update the informations of a specific {@link LightClient} or of {@link MatchData}
     * @param message the message containing the informations to update and the nickname of the target
     * {@link LightClient}
     */
    public void update(MatchDataMessage message){

        if (message instanceof LoadDevelopmentCardGrid){
            this.developmentCardGrid = ((LoadDevelopmentCardGrid) message).getAvailableCardsIds();
            display();
        }

        if (message instanceof UpdateDepotsStatus) {
            getLightClientByNickname(message.getNickname()).updateDepotStatus(((UpdateDepotsStatus) message).getWarehouseDepots(), ((UpdateDepotsStatus) message).getStrongboxDepots(), ((UpdateDepotsStatus) message).getLeaderDepots());
            display(message.getNickname());
        }
        if (message instanceof UpdateMarkerPosition) {
            if (message.getNickname().equals(LORENZO)){
                this.blackCrossPosition = ((UpdateMarkerPosition) message).getMarkerPosition();
            } else {
                getLightClientByNickname(message.getNickname()).updateMarkerPosition(((UpdateMarkerPosition) message).getMarkerPosition());
            }
            display();
        }
        if (message instanceof NotifyLeaderAction) {
            //I remove the card only if it is my card
            if (((NotifyLeaderAction) message).isDiscard() && thisClient.getNickname().equals(message.getNickname())) {
                thisClient.removeLeaderCard(((NotifyLeaderAction) message).getId());
                display(message.getNickname());
            }
            else if (((NotifyLeaderAction) message).isDiscard() && !thisClient.getNickname().equals(message.getNickname())){
                getLightClientByNickname(message.getNickname()).removeLeaderCard(((NotifyLeaderAction) message).getId());
                display(message.getNickname());
            }
            else if (!((NotifyLeaderAction) message).isDiscard()) {
                getLightClientByNickname(message.getNickname()).activateLeader(((NotifyLeaderAction) message).getId());
                display(message.getNickname());
            }
        }

        if (message instanceof NotifyDevelopmentCardBought){
            developmentCardGrid.remove(Integer.valueOf(((NotifyDevelopmentCardBought) message).getCardBought()));
            if (((NotifyDevelopmentCardBought) message).getNewCardOnGrid() != EMPTY_SLOT)
                developmentCardGrid.add(((NotifyDevelopmentCardBought) message).getNewCardOnGrid());
            getLightClientByNickname(message.getNickname()).addDevelopmentCard(((NotifyDevelopmentCardBought) message).getCardBought(), ((NotifyDevelopmentCardBought) message).getSlot(), ((NotifyDevelopmentCardBought) message).getVictoryPoints());
            display();
        }
        if (message instanceof UpdateMarketView){
            marketTray = ((UpdateMarketView) message).getMarbles();
            slideMarble = ((UpdateMarketView) message).getSideMarble();
            display();
        }

        if (message instanceof NotifyTakenPopesFavorTile) {
            getLightClientByNickname(message.getNickname()).updatePopeFavorTilesStatus(((NotifyTakenPopesFavorTile) message).getNumber(), ((NotifyTakenPopesFavorTile) message).isTaken());
            display();
        }
        if (message instanceof ReloadLeaderCardsOwned) {
            getLightClientByNickname(message.getNickname()).reloadLeaderCards(((ReloadLeaderCardsOwned) message).getCards());
            display(message.getNickname());
        }
        if (message instanceof ReloadPopesFavorTiles) {
            getLightClientByNickname(message.getNickname()).setPopesTileStates(((ReloadPopesFavorTiles) message).getPopesTileStates());
            display();
        }
        if (message instanceof ReloadDevelopmentCardsVictoryPoints) {
            getLightClientByNickname(message.getNickname()).setVictoryPointsDevelopmentCardSlots(((ReloadDevelopmentCardsVictoryPoints) message).getDevelopmentCardsVictoryPoints());
        }
        if (message instanceof LoadDevelopmentCardSlots){
            getLightClientByNickname(message.getNickname()).setDevelopmentCardSlots(((LoadDevelopmentCardSlots) message).getSlots());
            display();
        }

        if (message instanceof TurnMessage){
            if (((TurnMessage) message).isStarted()) {
                this.turnOwnerNickname = message.getNickname();
                if(this.turnOwnerNickname.equals(getThisClientNickname()) && !currentViewNickname.equals(getThisClientNickname())){
                    setCurrentViewNickname(getThisClientNickname());
                    display();
                }
            }
        }
        if (message instanceof NotifyVictoryPoints){
            getLightClientByNickname(message.getNickname()).setVictoryPoints(((NotifyVictoryPoints) message).getVictoryPoints());
            display();
        }

    }

    /**
     * Method to update the view when a {@link MatchDataMessage} is received and the message contains update information
     * about the elements that are in common with all the players (e.g. Market Tray, DevelopmentCardGrid etc.)
     */
    private void display(){
        if(!isReloading){
            view.displayStandardView();
        }
    }

    /**
     * Method to update the view when a {@link MatchDataMessage} is received and the message contains update information
     * about the elements of a specific player, so the view is updated only if thisClient is watching that player's view
     * @param nicknameMessage The nickname of the player whose information was updated
     */
    private void display(String nicknameMessage){
        if((currentViewNickname.equals(nicknameMessage) || LORENZO.equals(nicknameMessage)) && !isReloading){
            view.displayStandardView();
        }
    }

    /**
     * Return the nicknames of all the players if the {@link GameMode} is MULTI_PLAYER or the nickname of thisClient
     * and Lorenzo's nickname if teh {@link GameMode} is SINGLE_PLAYER
     * @return a List containing the nicknames
     */
    public List<String> getAllNicknames(){
        List<String> nicknames = new ArrayList<>();
        nicknames.add(thisClient.getNickname());
        if(gameMode == GameMode.SINGLE_PLAYER){
            nicknames.add(LORENZO);
        }else{
            for(LightClient lc : otherClients){
                nicknames.add(lc.getNickname());
            }
        }
        return nicknames;
    }

    /**
     * Return the market Tray
     * @return a bi-dimensional array of {@link Marble} representing the Market Tray
     */
    public Marble[][] getMarketTray() {
        return marketTray;
    }

    /**
     * Return the slide {@link Marble}
     * @return the slide {@link Marble}
     */
    public Marble getSlideMarble() {
        return slideMarble;
    }

    public List<Integer> getDevelopmentCardGrid() {
        return developmentCardGrid;
    }

    public String getThisClientNickname(){
        return thisClient.getNickname();
    }

    public List<String> getOtherClientsNicknames(){
        return otherClients.stream().map(LightClient::getNickname).collect(Collectors.toList());
    }

    public void setCurrentViewNickname(String nickname){
        this.currentViewNickname = nickname;
    }

    public String getCurrentViewNickname(){
        return currentViewNickname;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getBlackCrossPosition() {
        return blackCrossPosition;
    }
}
