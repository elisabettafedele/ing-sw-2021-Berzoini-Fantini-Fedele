package it.polimi.ingsw.messages.toServer.lobby;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.toClient.lobby.NicknameRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

public class GameModeResponse implements MessageToServer {
    private final GameMode gameMode;

    public GameModeResponse(GameMode gameMode){
        this.gameMode = gameMode;
    }

    public GameMode getGameMode(){
        return gameMode;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_GAME_MODE) {
            clientHandler.setGameMode(gameMode);
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
            clientHandler.sendMessageToClient(new NicknameRequest(false, false));
        }
    }

    public String toString(){
        return "received game mode: " + gameMode.name().replace("_", " ");
    }
}
