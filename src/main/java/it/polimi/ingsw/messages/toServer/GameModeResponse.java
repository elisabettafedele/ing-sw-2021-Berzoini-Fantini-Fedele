package messages.toServer;

import Server.ClientHandler;
import Server.Server;
import enumerations.ClientHandlerPhase;
import enumerations.GameMode;
import messages.toClient.NicknameRequest;

import java.io.Serializable;
import java.util.logging.Level;

public class GameModeResponse implements Serializable, MessageToServer {
    private final GameMode gameMode;

    public GameModeResponse(GameMode gameMode){
        this.gameMode = gameMode;
    }

    public GameMode getGameMode(){
        return gameMode;
    }

    @Override
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (clientHandler.getClientHandlerPhase() == ClientHandlerPhase.WAITING_GAME_MODE) {
            Server.SERVER_LOGGER.log(Level.INFO, "New message from client that has chosen the game mode");
            clientHandler.setGameMode(gameMode);
            clientHandler.sendMessageToClient(new NicknameRequest(false, false));
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
        }
    }
}
