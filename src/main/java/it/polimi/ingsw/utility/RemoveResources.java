package it.polimi.ingsw.utility;

import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toClient.SelectStorageRequest;
import it.polimi.ingsw.model.player.Player;

import java.util.Map;
import java.util.Set;

public class RemoveResources {
    static  Player currPlayer=null;
    public static void removeResources(Map<Resource, Integer> resourcesToRemove, ClientHandler clientHandler, Player currentPlayer){
        currPlayer=currentPlayer;
        int i;
        Set<Resource> resourcesTypeToRemove = resourcesToRemove.keySet();
        for (Resource resource : resourcesTypeToRemove){
            for(i=0;i<resourcesToRemove.get(resource);i++){
                clientHandler.sendMessageToClient(new SelectStorageRequest(resource,currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.WAREHOUSE,resource,1,false),currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX,resource,1,false),currentPlayer.getPersonalBoard().isResourceAvailableAndRemove(ResourceStorageType.LEADER_DEPOT,resource,1,false)));
                clientHandler.waitSpecificMessage();
            }

        }
    }

    public static void selectedStorage(Resource resource, ResourceStorageType resourceStorageType){
        currPlayer.getPersonalBoard().isResourceAvailableAndRemove(resourceStorageType,resource,1,true);
    }
}
