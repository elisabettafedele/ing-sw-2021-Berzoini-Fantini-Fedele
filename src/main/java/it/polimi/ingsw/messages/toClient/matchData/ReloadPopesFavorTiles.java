package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.client.PopesTileState;

/**
 * Message to reload popes tiles status
 */
public class ReloadPopesFavorTiles extends MatchDataMessage{
    PopesTileState[] popesTileStates;

    public ReloadPopesFavorTiles(String nickname, PopesTileState[] popesTileStates) {
        super(nickname);
        this.popesTileStates = popesTileStates;
    }

    public PopesTileState[] getPopesTileStates() {
        return popesTileStates;
    }
}
