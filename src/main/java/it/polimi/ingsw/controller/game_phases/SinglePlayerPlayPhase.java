package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toClient.TurnMessage;
import it.polimi.ingsw.messages.toClient.game.NotifyLorenzoAction;
import it.polimi.ingsw.messages.toClient.matchData.LoadDevelopmentCardGrid;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarkerPosition;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.persistency.GameHistory;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhaseSingle;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.jsonParsers.SoloActionTokenParser;
import java.util.*;
import java.util.stream.Collectors;

public class SinglePlayerPlayPhase extends PlayPhase {
    private int blackCrossPosition;
    private Queue<SoloActionToken> tokens;
    public static String LORENZO = "Lorenzo";
    private String lastPlayer;
    private boolean endTriggered = false;

    public SinglePlayerPlayPhase(Controller controller){
        setController(controller);
        this.blackCrossPosition = 0;
        setPlayer(controller.getPlayers().get(0));
        this.tokens = SoloActionTokenParser.parseTokens();
        shuffleTokens();
        this.lastPlayer = LORENZO;
    }

    public SinglePlayerPlayPhase(Controller controller, String lastPlayer, boolean isEndTriggered, int blackCrossPosition, List<Integer> tokens){
        setController(controller);
        this.lastPlayer = lastPlayer;
        this.blackCrossPosition = blackCrossPosition;
        setActionTokens(tokens);
        this.endTriggered = isEndTriggered;
        setPlayer(controller.getPlayers().get(0));
    }

    @Override
    public void executePhase(Controller controller) {
        setTurnController(new TurnController(controller,getPlayer()));
        getTurnController().start(getPlayer());
    }

    private void useActionToken() {
        getController().sendMessageToAll(new TurnMessage(LORENZO, true));
        SoloActionToken token = tokens.remove(); //removing the first of the queue;
        tokens.add(token); //saving the used token at the end of the queue;
        token.useActionToken(this);
        getController().sendMessageToAll(new NotifyLorenzoAction(token.getId()));
        lastPlayer = LORENZO;
        getController().sendMessageToAll(new TurnMessage(getPlayer().getNickname(), true));
        getTurnController().start(getPlayer());
    }

    public void shuffleTokens() {
        Collections.shuffle((List<SoloActionToken>) this.tokens);
    }

    public void discardDevelopmentCards(int numOfCard2Remove, FlagColor flagColor){
        for(int i = 0; i < numOfCard2Remove; i++){
            List<DevelopmentCard> availableCards = getController().getGame().getDevelopmentCardGrid().getAvailableCards();
            List<DevelopmentCard> rightColorCards = availableCards.stream().filter(dc -> dc.getFlag().getFlagColor().equals(flagColor)).collect(Collectors.toList());

            if(rightColorCards.size() > 0){
                DevelopmentCard card2Remove = getLowerCard(rightColorCards);
                try {
                    getController().getGame().getDevelopmentCardGrid().removeCard(card2Remove);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }else{
                getTurnController().setEndTrigger(true);
                getPlayer().setWinner(false); //useless cause is false by default, leave here just to remember
                //TODO: lost the game communication
                break;
            }

        }
        getController().sendMessageToAll(new LoadDevelopmentCardGrid(getPlayer().getNickname(),getController().getGame().getDevelopmentCardGrid().getAvailableCards().stream().map(Card:: getID).collect(Collectors.toList())));

    }

    private DevelopmentCard getLowerCard(List<DevelopmentCard> availableCards) {
        DevelopmentCard lowerDevelopmentCard = availableCards.get(0);
        for(DevelopmentCard dc : availableCards){
            if(dc.getFlag().getFlagLevel().getValue() < lowerDevelopmentCard.getFlag().getFlagLevel().getValue()){
                lowerDevelopmentCard = dc;
            }
        }
        return lowerDevelopmentCard;
    }

    public void moveBlackCross(int step){
        blackCrossPosition = blackCrossPosition + step;
        getTurnController().checkFaithTrack();
        //TODO, manage eventual Lorenzo's victory.
    }

    @Override
    public void nextTurn() {
        lastPlayer = getPlayer().getNickname();
        useActionToken();
    }

    @Override
    public void handleResourceDiscard(String nickname) {
        moveBlackCross(1);
        getController().getConnectionByNickname(getPlayer().getNickname()).sendMessageToClient(new UpdateMarkerPosition(LORENZO, blackCrossPosition));
    }


    public int getBlackCrossPosition() {
        return blackCrossPosition;
    }

    @Override
    public void handleEndTriggered() {
        getController().endMatch();
    }

    @Override
    public void saveGame() {
        GameHistory.saveGame(new PersistentControllerPlayPhaseSingle(new PersistentGame(getController().getGame()), lastPlayer, getController().getControllerID(), getTurnController().isEndTriggered(), getActionTokenIds(), blackCrossPosition));
    }

    @Override
    public void restartLastTurn() {
        if (endTriggered) {
            handleEndTriggered();
            return;
        }
        setTurnController(new TurnController(getController(), getController().getPlayers().get(0)));
        if (lastPlayer.equals(LORENZO))
            useActionToken();
        else
            getTurnController().start(getController().getPlayers().get(0));
    }

    public void setLastPlayer(String lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public List<Integer> getActionTokenIds(){
        return tokens.stream().map(SoloActionToken::getId).collect(Collectors.toList());
    }

    public void setActionTokens (List<Integer> tokens){
        Queue<SoloActionToken> tokenParser = SoloActionTokenParser.parseTokens();
        this.tokens = new LinkedList<>();
        for (Integer token : tokens){
            this.tokens.add(tokenParser.stream().filter(x -> x.getId() == token).collect(Collectors.toList()).get(0));
        }
    }

    public void handleReconnection(){

    }

    public String getLastPlayer() {
        return lastPlayer;
    }
}
