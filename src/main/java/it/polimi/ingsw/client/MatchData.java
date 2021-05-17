package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.graphical.GraphicalMarket;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toClient.matchData.*;

import java.util.*;

public class MatchData {

    List<LightDevelopmentCard> lightDevelopmentCards;
    List<LightLeaderCard> lightLeaderCards;
    LightClient thisClient;
    List<LightClient> otherClients;
    private Marble[][] marketTray;
    private Marble slideMarble;
    private List<Integer> developmentCardGrid;

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
    }

    public void addLightClient(String nickname){
        LightClient lc = new LightClient();
        lc.setNickname(nickname);
        otherClients.add(lc);

    }

    public void updateInfo(String nickname, int steps){
        LightClient lc = getLightClientByNickname(nickname);
        lc.faithTrackAdvancement(steps);
    }

    private LightClient getLightClientByNickname(String nickname) {
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

        if (message instanceof UpdateDepotsStatus)
            getLightClientByNickname(message.getNickname()).updateDepotStatus(((UpdateDepotsStatus) message).getWarehouseDepots(), ((UpdateDepotsStatus) message).getStrongboxDepots(), ((UpdateDepotsStatus) message).getLeaderDepots());

        if (message instanceof UpdateMarkerPosition)
            getLightClientByNickname(message.getNickname()).updateMarkerPosition(((UpdateMarkerPosition) message).getMarkerPosition());

        if (message instanceof NotifyLeaderActivation)
            getLightClientByNickname(message.getNickname()).activateLeader(((NotifyLeaderActivation) message).getId());

        if (message instanceof UpdateOwnedDevelopmentCards)
            getLightClientByNickname(message.getNickname()).updateOwnedDevelopmentCards(((UpdateOwnedDevelopmentCards) message).getIds(), ((UpdateOwnedDevelopmentCards) message).getVictoryPoints());

        if (message instanceof NotifyDevelopmentCardBought){
            Collections.replaceAll(developmentCardGrid, ((NotifyDevelopmentCardBought) message).getCardBought(), ((NotifyDevelopmentCardBought) message).getNewCardOnGrid());
            getLightClientByNickname(message.getNickname()).addDevelopmentCard(((NotifyDevelopmentCardBought) message).getCardBought(), ((NotifyDevelopmentCardBought) message).getSlot(), ((NotifyDevelopmentCardBought) message).getVictoryPoints());
        }
        if (message instanceof UpdateMarketView){
            marketTray = ((UpdateMarketView) message).getMarbles();
            slideMarble = ((UpdateMarketView) message).getSideMarble();
            //TODO just temporary, decide when to show
            GraphicalMarket.printMarket(((UpdateMarketView) message).getMarbles(), ((UpdateMarketView) message).getSideMarble());
        }
    }
}
