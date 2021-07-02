package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.enumerations.Resource;
import java.util.List;
import java.util.Map;

/**
 * Message to update the status of the various depots
 */
public class UpdateDepotsStatus extends MatchDataMessage {
    private List<Resource>[] warehouseDepots;
    private int[] strongboxDepots;
    private Map<Integer, Integer> leaderDepots;
    public UpdateDepotsStatus(String nickname, List<Resource>[] warehouseDepots, int[] strongboxDepots, Map<Integer, Integer> leaderDepots){
        super(nickname);
        this.warehouseDepots = warehouseDepots;
        this.strongboxDepots = strongboxDepots;
        this.leaderDepots = leaderDepots;
    }

    public List<Resource>[] getWarehouseDepots() {
        return warehouseDepots;
    }

    public int[] getStrongboxDepots() {
        return strongboxDepots;
    }

    public Map<Integer, Integer> getLeaderDepots() {
        return leaderDepots;
    }
}
