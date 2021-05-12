package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.model.player.Warehouse;
import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class GraphicalWarehouseTest extends TestCase {

    @Test
    public void testWarehousePrint(){
        List<Resource>[] depots = new ArrayList[3];
        for (int i = 0; i < 3; i++)
            depots[i] = new ArrayList<>();
        depots[0].add(Resource.STONE);
        depots[1].add(Resource.COIN);
        depots[1].add(Resource.SERVANT);
        depots[2].add(Resource.STONE);
        depots[2].add(Resource.STONE);
        depots[2].add(Resource.STONE);

        GraphicalWarehouse.printWarehouse(depots);

    }

}