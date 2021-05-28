package it.polimi.ingsw.client;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toClient.TurnMessage;
import it.polimi.ingsw.messages.toClient.matchData.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private String currentViewNickname;
    private String turnOwnerNickname;
    private View view;

    private static MatchData instance;

    public static MatchData getInstance(){
        if (instance == null){
            instance = new MatchData();
        }
        return instance;
    }

    private MatchData(){
        this.lightLeaderCards = new ArrayList<>();
        this.thisClient = new LightClient();
        this.otherClients = new ArrayList<>();
    }

    public void setThisClient(String nickname){
        thisClient.setNickname(nickname);
        //Just temporary, later the user will be able to choose which view he wants to see
        currentViewNickname = nickname;
    }

    public void setView(View view){
        this.view = view;
    }

    public void addLightClient(String nickname){
        LightClient lc = new LightClient();
        lc.setNickname(nickname);
        otherClients.add(lc);

    }

    public LightClient getLightClientByNickname(String nickname) {
        for(LightClient lc : otherClients){
            if(lc.getNickname().equals(nickname))
                return lc;
        }
        return thisClient;
    }

    public void addChosenLeaderCard(Integer ID, boolean active){
        thisClient.addLeaderCard(ID, active);
    }

    public void setAllLeaderCards(List<LightLeaderCard> allLeaderCards){
        this.lightLeaderCards = allLeaderCards;
    }


    public void setAllDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {
        this.lightDevelopmentCards = lightDevelopmentCards;
    }

    public void loadDevelopmentCardGrid(List<Integer> developmentCardGrid){
        this.developmentCardGrid = developmentCardGrid;
    }

    public LightDevelopmentCard getDevelopmentCardByID(Integer ID){
        for (LightDevelopmentCard ldc : lightDevelopmentCards){
            if(ldc.getID() == ID){
                return ldc;
            }
        }
        return null;
    }

    public LightLeaderCard getLeaderCardByID(int ID){
        for (LightLeaderCard lc : lightLeaderCards){
            if(lc.getID() == ID){
                return lc;
            }
        }
        return null;
    }

    public void update(MatchDataMessage message){

        if (message instanceof UpdateDepotsStatus) {
            getLightClientByNickname(message.getNickname()).updateDepotStatus(((UpdateDepotsStatus) message).getWarehouseDepots(), ((UpdateDepotsStatus) message).getStrongboxDepots(), ((UpdateDepotsStatus) message).getLeaderDepots());
            //if (message.getNickname().equals(currentViewNickname))
                //view.displayStandardView();

            //TODO just temporary, decide when to show. Qua sarà qualcosa del tipo "se è la view selezionata dal client, ristampala"
        }
        if (message instanceof UpdateMarkerPosition)
            getLightClientByNickname(message.getNickname()).updateMarkerPosition(((UpdateMarkerPosition) message).getMarkerPosition());

        if (message instanceof NotifyLeaderAction) {
            //I remove the card only if it is my card
            if (((NotifyLeaderAction) message).isDiscard() && thisClient.getNickname().equals(message.getNickname()))
                thisClient.removeLeaderCard(((NotifyLeaderAction) message).getId());
            else if (((NotifyLeaderAction) message).isDiscard() && !thisClient.getNickname().equals(message.getNickname())){
                return; //TODO maybe we can show that a specific player has discarded a specific card...
            }
            else if (!((NotifyLeaderAction) message).isDiscard())
                getLightClientByNickname(message.getNickname()).activateLeader(((NotifyLeaderAction) message).getId());
        }

        if (message instanceof NotifyDevelopmentCardBought){
            Collections.replaceAll(developmentCardGrid, ((NotifyDevelopmentCardBought) message).getCardBought(), ((NotifyDevelopmentCardBought) message).getNewCardOnGrid());
            getLightClientByNickname(message.getNickname()).addDevelopmentCard(((NotifyDevelopmentCardBought) message).getCardBought(), ((NotifyDevelopmentCardBought) message).getSlot(), ((NotifyDevelopmentCardBought) message).getVictoryPoints());
        }
        if (message instanceof UpdateMarketView){
            marketTray = ((UpdateMarketView) message).getMarbles();
            slideMarble = ((UpdateMarketView) message).getSideMarble();
            //TODO just temporary, decide when to show...stesso discorso di prima
            //if (message.getNickname().equals(thisClient.getNickname()) || message.getNickname().equals("SETUP"))
            //    GraphicalMarket.printMarket(((UpdateMarketView) message).getMarbles(), ((UpdateMarketView) message).getSideMarble());
        }

        if (message instanceof NotifyTakenPopesFavorTile)
            getLightClientByNickname(message.getNickname()).updatePopeFavorTilesStatus(((NotifyTakenPopesFavorTile) message).getNumber(), ((NotifyTakenPopesFavorTile) message).isTaken());

        if (message instanceof ReloadLeaderCardsOwned)
            getLightClientByNickname(message.getNickname()).reloadLeaderCards(((ReloadLeaderCardsOwned) message).getCards());

        if (message instanceof ReloadPopesFavorTiles)
            getLightClientByNickname(message.getNickname()).setPopesTileStates(((ReloadPopesFavorTiles) message).getPopesTileStates());

        if (message instanceof ReloadDevelopmentCardsVictoryPoints)
            getLightClientByNickname(message.getNickname()).setVictoryPointsDevelopmentCardSlots(((ReloadDevelopmentCardsVictoryPoints) message).getDevelopmentCardsVictoryPoints());

        if (message instanceof LoadDevelopmentCardSlots){
            getLightClientByNickname(message.getNickname()).setDevelopmentCardSlots(((LoadDevelopmentCardSlots) message).getSlots());
        }

        if (message instanceof TurnMessage){
            if (((TurnMessage) message).isStarted())
                this.turnOwnerNickname = message.getNickname();
        }

    }

    public List<String> getAllNicknames(){
        List<String> nicknames = new ArrayList<>();
        nicknames.add(thisClient.getNickname());
        for(LightClient lc : otherClients){
            nicknames.add(lc.getNickname());
        }
        return nicknames;
    }

    public Marble[][] getMarketTray() {
        return marketTray;
    }

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
}
