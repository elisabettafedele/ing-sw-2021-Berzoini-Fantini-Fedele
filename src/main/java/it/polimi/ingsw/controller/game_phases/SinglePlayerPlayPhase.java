package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toClient.matchData.TurnMessage;
import it.polimi.ingsw.messages.toClient.game.NotifyLorenzoAction;
import it.polimi.ingsw.messages.toClient.matchData.LoadDevelopmentCardGrid;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarkerPosition;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.jsonParsers.GameHistory;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhaseSingle;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.jsonParsers.SoloActionTokenParser;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to manage the play phase in single player matches
 */
public class SinglePlayerPlayPhase extends PlayPhase {
    private int blackCrossPosition;
    private Queue<SoloActionToken> tokens;
    public static String LORENZO = "Lorenzo";
    private String lastPlayer;
    private boolean endTriggered = false;

    /**
     * Standard class constructor
     * @param controller the {@link Controller} of the {@link it.polimi.ingsw.model.game.Game}
     */
    public SinglePlayerPlayPhase(Controller controller){
        setController(controller);
        this.blackCrossPosition = 0;
        setPlayer(controller.getPlayers().get(0));
        this.tokens = SoloActionTokenParser.parseTokens();
        shuffleTokens();
        this.lastPlayer = LORENZO;
    }

    /**
     * Class constructor after a {@link it.polimi.ingsw.server.Server} or {@link it.polimi.ingsw.client.Client} disconnection
     * @param controller the {@link Controller} that has been retrieved from the {@link PersistentControllerPlayPhaseSingle} saved in the file backupOfGames.json
     * @param lastPlayer it represents the last player that has performed a valid turn. It can be LORENZO or the nickname of the single player
     * @param isEndTriggered true if the game was end triggered before the disconnection
     * @param blackCrossPosition the position of the black cross
     * @param tokens the queue of tokens
     */
    public SinglePlayerPlayPhase(Controller controller, String lastPlayer, boolean isEndTriggered, int blackCrossPosition, List<Integer> tokens){
        setController(controller);
        this.lastPlayer = lastPlayer;
        this.blackCrossPosition = blackCrossPosition;
        setActionTokens(tokens);
        this.endTriggered = isEndTriggered;
        setPlayer(controller.getPlayers().get(0));
    }

    /**
     * Method to execute the play phase in a single player game
     * It starts the turn og the player
     * @param controller the controller of the game
     */
    @Override
    public void executePhase(Controller controller) {
        setTurnController(new TurnController(controller,getPlayer()));
        getTurnController().start(getPlayer());
    }

    /**
     * Method to pick a solo action token from the queue and to handle its effects
     */
    private void useActionToken() {
        getController().sendMessageToAll(new TurnMessage(LORENZO, true));
        SoloActionToken token = tokens.remove(); //removing the first of the queue;
        tokens.add(token); //saving the used token at the end of the queue;
        getController().sendMessageToAll(new NotifyLorenzoAction(token.getId()));
        token.useActionToken(this);
        lastPlayer = LORENZO;
        saveGame();
        getController().sendMessageToAll(new TurnMessage(getPlayer().getNickname(), true));
        getTurnController().start(getPlayer());
    }

    /**
     * Method to shuffle action tokens
     */
    public void shuffleTokens() {
        Collections.shuffle((List<SoloActionToken>) this.tokens);
    }

    /**
     * method to handle an action token which involves the discard of development cards
     * @param numOfCard2Remove the number of cards to be removed
     * @param flagColor the color of the flag of the cards to remove
     */
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
                getPlayer().setWinner(false); //useless cause is false by default, leave here just to remember
                getController().sendMessageToAll(new LoadDevelopmentCardGrid(getPlayer().getNickname(),getController().getGame().getDevelopmentCardGrid().getAvailableCards().stream().map(Card:: getID).collect(Collectors.toList())));
                getController().endMatch();
                return;
            }

        }
        getController().sendMessageToAll(new LoadDevelopmentCardGrid(getPlayer().getNickname(),getController().getGame().getDevelopmentCardGrid().getAvailableCards().stream().map(Card:: getID).collect(Collectors.toList())));

    }

    /**
     * //TODO  Raffa finire
     * Method to get the right card to remove (the one with the lowest level)
     * @param availableCards
     * @return
     */
    private DevelopmentCard getLowerCard(List<DevelopmentCard> availableCards) {
        DevelopmentCard lowerDevelopmentCard = availableCards.get(0);
        for(DevelopmentCard dc : availableCards){
            if(dc.getFlag().getFlagLevel().getValue() < lowerDevelopmentCard.getFlag().getFlagLevel().getValue()){
                lowerDevelopmentCard = dc;
            }
        }
        return lowerDevelopmentCard;
    }

    /**
     * Method to move the black cross position of a certain number of steps
     * @param step how many position the black cross must move forward
     */
    public void moveBlackCross(int step){
        blackCrossPosition = blackCrossPosition + step;
        if (blackCrossPosition > getController().getGame().getFaithTrack().getLength())
            blackCrossPosition = getController().getGame().getFaithTrack().getLength();
        getTurnController().checkFaithTrack();
    }

    @Override
    public void nextTurn() {
        lastPlayer = getPlayer().getNickname();
        useActionToken();
    }

    /**
     * Method to handle the discard of resources.
     * The black cross gain one position in the faith track
     * @param nickname the nickname of the player that has discarded the resource
     */
    @Override
    public void handleResourceDiscard(String nickname) {
        moveBlackCross(1);
        getController().getConnectionByNickname(getPlayer().getNickname()).sendMessageToClient(new UpdateMarkerPosition(LORENZO, blackCrossPosition));
        getTurnController().checkFaithTrack();
    }


    public int getBlackCrossPosition() {
        return blackCrossPosition;
    }

    /**
     * Common method in Play Phase called when the end is trigger.
     * In single player play phase, the game ends immediately
     */
    @Override
    public void handleEndTriggered() {
        getPlayer().setWinner(!getController().getGame().getDevelopmentCardGrid().checkEmptyColumn() && blackCrossPosition < getController().getGame().getFaithTrack().getLength());
        getController().endMatch();
    }

    /**
     * Method used to save the game in the json file after each turn and after a player's disconnection
     */
    @Override
    public void saveGame() {
        GameHistory.saveGame(new PersistentControllerPlayPhaseSingle(new PersistentGame(getController().getGame()), lastPlayer, getController().getControllerID(), getTurnController().isEndTriggered(), getActionTokenIds(), blackCrossPosition));
    }

    /**
     * Method used to restart the game from the right turn, when retrieved from the json file
     */
    @Override
    public void restartLastTurn() {
        if (endTriggered) {
            handleEndTriggered();
            return;
        }
        setTurnController(new TurnController(getController(), getController().getPlayers().get(0)));
        if (lastPlayer.equals(LORENZO))
            getTurnController().start(getController().getPlayers().get(0));
        else
            useActionToken();
    }

    public void setLastPlayer(String lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public List<Integer> getActionTokenIds(){
        return tokens.stream().map(SoloActionToken::getId).collect(Collectors.toList());
    }

    /**
     * Method to set the order of the action tokens, after a game is retrieved from the json file
     * @param tokens the list of tokens
     */
    public void setActionTokens (List<Integer> tokens){
        Queue<SoloActionToken> tokenParser = SoloActionTokenParser.parseTokens();
        this.tokens = new LinkedList<>();
        for (Integer token : tokens){
            this.tokens.add(tokenParser.stream().filter(x -> x.getId() == token).collect(Collectors.toList()).get(0));
        }
    }

    /**
     * Method to check whether the end was end triggered when restarting a game.
     * It is just an additional check since this information should be saved in the json file
     * @return true only if the end was end trigger
     */
    public boolean wasEndTriggered(){
        return  (endTriggered || getController().getGame().getDevelopmentCardGrid().checkEmptyColumn() || blackCrossPosition == getController().getGame().getFaithTrack().getLength() || getController().getPlayers().get(0).getPersonalBoard().getDevelopmentCards().size() >= 7);
    }
}
