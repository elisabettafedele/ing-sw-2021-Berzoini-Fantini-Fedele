package it.polimi.ingsw.client.cli.graphical;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphicalFaithTrackTest {

    GraphicalFaithTrack gft;

    @Before
    public void setUp() throws Exception {
        gft = new GraphicalFaithTrack();
    }

    @Test
    public void displayFaithTrack() {
        gft.drawFaithTrack();
        gft.displayFaithTrack();
    }
}