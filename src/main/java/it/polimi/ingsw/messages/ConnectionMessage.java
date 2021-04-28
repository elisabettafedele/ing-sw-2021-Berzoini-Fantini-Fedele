package messages;

public enum ConnectionMessage {
    PING ("Ping"),
    TIMER_EXPIRED ("Timer has expired"),
    CONNECTION_CLOSED ("Connection closed"),
    INSERT_GAME_MODE ("Insert a game mode, multiplayer or solo mode: m | s"),
    INSERT_NICKNAME("Insert nickname"),
    INVALID_NICKNAME_ASK_AGAIN ("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)"),
    NICKNAME_ALREADY_TAKEN("Your nickname has already been taken, insert another one"),
    INSERT_NUMBER_OF_PLAYER ("Insert desired number of players"),
    INVALID_NUMBER_OF_PLAYERS("Insert a valid number of players. Numbers of player allowed are: 2, 3 or 4"),
    GAME_STARTED("Game started");

    String message;

    ConnectionMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
