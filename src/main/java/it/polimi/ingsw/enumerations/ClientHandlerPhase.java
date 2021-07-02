package it.polimi.ingsw.enumerations;

/**
 * All the status in which a client can find himself
 */
public enum ClientHandlerPhase {
    WAITING_GAME_MODE,
    WAITING_NICKNAME,
    WAITING_NUMBER_OF_PLAYERS,
    WAITING_FOR_CLIENT_RECONNECTION,
    WAITING_IN_THE_LOBBY,
    READY_TO_START,
    WAITING_DISCARDED_LEADER_CARDS,
    WAITING_CHOOSE_RESOURCE_TYPE,
    WAITING_HIS_TURN,
    SET_UP_FINISHED,
    WAITING_CHOOSE_STORAGE_TYPE
}
