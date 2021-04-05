package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * The class can represents all the possible special ability of {@link LeaderCard}
 */
public class Effect {

    private Resource discountEffect;
    private Marble whiteMarbleEffect;
    private ExtraDepot extraDepotEffect;
    private Production productionEffect;
    private EffectType effectType;

    /**
     * Constructs a discount effect
     * @param discountEffect the {@link Resource} to be subtracted from the {@link DevelopmentCard} cost when buying from the {@link DevelopmentCardGrid}.
     * @throws InvalidArgumentException
     */
    public Effect(Resource discountEffect) throws InvalidArgumentException {
        if(discountEffect == null){
            throw new InvalidArgumentException();
        }
        this.effectType = EffectType.DISCOUNT;
        this.discountEffect = discountEffect;
        this.whiteMarbleEffect = null;
        this.extraDepotEffect = null;
        this.productionEffect = null;
    }
    /**
     * Constructs a white marble conversion effect
     * @param whiteMarbleEffect the {@link Marble} color to be converted to when picking a white {@link Marble} from the {@link Market}
     * @throws InvalidArgumentException
     */
    public Effect(Marble whiteMarbleEffect) throws InvalidArgumentException {
        if(whiteMarbleEffect == null || whiteMarbleEffect == Marble.WHITE || whiteMarbleEffect == Marble.RED){
            throw new InvalidArgumentException();
        }
        this.effectType = EffectType.WHITE_MARBLE;
        this.discountEffect = null;
        this.whiteMarbleEffect = whiteMarbleEffect;
        this.extraDepotEffect = null;
        this.productionEffect = null;
    }

    /**
     * Constructs an extra depot effect
     * @param extraDepotEffect the extra storage capability of some {@link LeaderCard}
     * @throws InvalidArgumentException
     */
    public Effect(ExtraDepot extraDepotEffect) throws InvalidArgumentException {
        if(extraDepotEffect == null){
            throw new InvalidArgumentException();
        }
        this.effectType = EffectType.EXTRA_DEPOT;
        this.discountEffect = null;
        this.whiteMarbleEffect = null;
        this.extraDepotEffect = extraDepotEffect;
        this.productionEffect = null;
    }
    /**
     * Constructs a production effect
     * @param productionEffect the {@link Production} power of some {@link LeaderCard}
     * @throws InvalidArgumentException
     */
    public Effect(Production productionEffect) throws InvalidArgumentException {
        if(productionEffect == null){
            throw new InvalidArgumentException();
        }
        this.effectType = EffectType.PRODUCTION;
        this.discountEffect = null;
        this.whiteMarbleEffect = null;
        this.extraDepotEffect = null;
        this.productionEffect = productionEffect;
    }


    public Resource getDiscountEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.DISCOUNT){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return discountEffect;
    }

    public Marble getWhiteMarbleEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.WHITE_MARBLE){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return whiteMarbleEffect;
    }

    public ExtraDepot getExtraDepotEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.EXTRA_DEPOT){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return extraDepotEffect;
    }

    public Production getProductionEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.PRODUCTION){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return productionEffect;
    }

    public EffectType getEffectType() {
        return effectType;
    }

}
