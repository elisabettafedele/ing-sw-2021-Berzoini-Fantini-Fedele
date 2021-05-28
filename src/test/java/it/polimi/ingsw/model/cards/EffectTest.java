package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.depot.LeaderDepot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class EffectTest {

    Resource discountEffect;
    Marble whiteMarbleEffect;
    ExtraDepot extraDepotEffect;
    Production productionEffect;
    Effect effect;
    @Before
    public void setUp() throws Exception {
        discountEffect = Resource.COIN;
        whiteMarbleEffect = Marble.PURPLE;
        extraDepotEffect = new ExtraDepot(new LeaderDepot(Resource.STONE));
        productionEffect = new Production(new Value(new HashMap<>(), new HashMap<>(), 5), new Value(new HashMap<>(), new HashMap<>(), 5));
    }

    @After
    public void tearDown() {
        effect = null;
    }

    @Test
    public void testGetWhiteMarbleEffectResource() throws InvalidArgumentException, DifferentEffectTypeException {
        effect = new Effect(whiteMarbleEffect);
        assertEquals(effect.getWhiteMarbleEffectResource(), Resource.SERVANT);
    }

    @Test
    public void getDiscountEffect_returnCorrectResource() throws InvalidArgumentException, DifferentEffectTypeException {
        effect = new Effect(discountEffect);
        assertEquals(discountEffect, effect.getDiscountEffect());
    }

    @Test
    public void getWhiteMarbleEffect_returnCorrectMarble() throws InvalidArgumentException, DifferentEffectTypeException {
        effect = new Effect(whiteMarbleEffect);
        assertEquals(whiteMarbleEffect, effect.getWhiteMarbleEffect());
    }

    @Test
    public void getExtraDepotEffect_returnCorrectDepot() throws InvalidArgumentException, DifferentEffectTypeException {
        effect = new Effect(extraDepotEffect);
        assertEquals(extraDepotEffect, effect.getExtraDepotEffect());
    }

    @Test
    public void getProductionEffect_returnCorrectProduction() throws InvalidArgumentException, DifferentEffectTypeException {
        effect = new Effect(productionEffect);
        assertEquals(productionEffect, effect.getProductionEffect());
    }

    @Test (expected = InvalidArgumentException.class)
    public void constructor_ProductionNull_InvalidArgumentException() throws InvalidArgumentException {
        Production p = null;
        Effect invalidEffect = new Effect(p);
    }

    @Test (expected = InvalidArgumentException.class)
    public void constructor_MarbleNull_InvalidArgumentException() throws InvalidArgumentException {
        Marble p = null;
        Effect invalidEffect = new Effect(p);
    }
    @Test (expected = InvalidArgumentException.class)
    public void constructor_MarbleRED_InvalidArgumentException() throws InvalidArgumentException {
        Marble p = Marble.RED;
        Effect invalidEffect = new Effect(p);
    }

    @Test (expected = InvalidArgumentException.class)
    public void constructor_MarbleWHITE_InvalidArgumentException() throws InvalidArgumentException {
        Marble p = Marble.WHITE;
        Effect invalidEffect = new Effect(p);
    }

    @Test (expected = InvalidArgumentException.class)
    public void constructor_ResourceNull_InvalidArgumentException() throws InvalidArgumentException {
        Resource p = null;
        Effect invalidEffect = new Effect(p);
    }

    @Test (expected = InvalidArgumentException.class)
    public void constructor_ExtraDepotNull_InvalidArgumentException() throws InvalidArgumentException {
        ExtraDepot p = null;
        Effect invalidEffect = new Effect(p);
    }

    @Test (expected = DifferentEffectTypeException.class)
    public void getDiscountEffect_DifferentEffectTypeException() throws InvalidArgumentException, DifferentEffectTypeException {
        Effect exceptionEffect = new Effect(productionEffect);
        exceptionEffect.getDiscountEffect();
    }

    @Test (expected = DifferentEffectTypeException.class)
    public void getWhiteMarbleEffect_DifferentEffectTypeException() throws InvalidArgumentException, DifferentEffectTypeException {
        Effect exceptionEffect = new Effect(productionEffect);
        exceptionEffect.getWhiteMarbleEffect();
    }

    @Test (expected = DifferentEffectTypeException.class)
    public void getExtraDepotEffect_DifferentEffectTypeException() throws InvalidArgumentException, DifferentEffectTypeException {
        Effect exceptionEffect = new Effect(productionEffect);
        exceptionEffect.getExtraDepotEffect();
    }

    @Test (expected = DifferentEffectTypeException.class)
    public void getProductionEffect_DifferentEffectTypeException() throws InvalidArgumentException, DifferentEffectTypeException {
        Effect exceptionEffect = new Effect(extraDepotEffect);
        exceptionEffect.getProductionEffect();
    }

    @Test
    public void getEffectType_returnCorrectType() throws InvalidArgumentException {
        effect = new Effect(productionEffect);
        assertEquals(EffectType.PRODUCTION, effect.getEffectType());
        effect = new Effect(whiteMarbleEffect);
        assertEquals(EffectType.WHITE_MARBLE, effect.getEffectType());
        effect = new Effect(extraDepotEffect);
        assertEquals(EffectType.EXTRA_DEPOT, effect.getEffectType());
        effect = new Effect(discountEffect);
        assertEquals(EffectType.DISCOUNT, effect.getEffectType());
    }

    @Test
    public void testEqualsExtraDepot() throws InvalidArgumentException {
        Effect effect1 = new Effect(new ExtraDepot(new LeaderDepot(Resource.COIN)));
        Effect effect2 = new Effect(new ExtraDepot(new LeaderDepot(Resource.COIN)));
        assertEquals(effect1, effect2);
        Effect effect3 = new Effect(new ExtraDepot(new LeaderDepot(Resource.SHIELD)));
        assertNotEquals(effect1, effect3);
    }

    @Test
    public void testEqualsWhiteMarble() throws InvalidArgumentException {
        Effect effect1 = new Effect(Marble.BLUE);
        Effect effect2 = new Effect(Marble.BLUE);
        assertEquals(effect1, effect2);
        Effect effect3 = new Effect(Marble.PURPLE);
        assertNotEquals(effect1, effect3);
    }

    @Test
    public void testEqualsDiscount() throws InvalidArgumentException {
        Effect effect1 = new Effect(Resource.COIN);
        Effect effect2 = new Effect(Resource.COIN);
        assertEquals(effect1, effect2);
        Effect effect3 = new Effect(Resource.SHIELD);
        assertNotEquals(effect1, effect3);
    }

    @Test
    public void testEqualsProduction() throws InvalidArgumentException {
        Production production1 = new Production(new Value(null, null, 0), new Value(null, null, 2));
        Effect effect1 = new Effect(production1);
        Production production2 = new Production(new Value(null, null, 0), new Value(null, null, 2));
        Effect effect2 = new Effect(production2);
        assertEquals(effect1, effect2);
        Production production3 = new Production(new Value(null, null, 1), new Value(null, null, 3));
        Effect effect3 = new Effect(production3);
        assertNotEquals(effect1, effect3);
    }
}