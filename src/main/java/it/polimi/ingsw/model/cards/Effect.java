package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Market;

import java.util.Objects;

//import it.polimi.ingsw.model.game.DevelopmentCardGrid;

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
     * @param discountEffect the {@link Resource} to be subtracted from the {@link DevelopmentCard} cost when buying from the DevelopmentCardGrid {@link it.polimi.ingsw.model.game.DevelopmentCardGrid}
     * @throws InvalidArgumentException if discountEffect is null
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
     * @param whiteMarbleEffect the {@link Marble} color to be converted to when picking a white {@link Marble} from the {@link Market}.
     * @throws InvalidArgumentException if whiteMarble effect is null, WHITE or RED
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
     * @throws InvalidArgumentException if extraDepotEffect is null
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
     * @throws InvalidArgumentException if productionEffect is null
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

    /**
     * Get the {@link Resource} to be subtracted from the {@link DevelopmentCard} cost when buying from the {@link it.polimi.ingsw.model.game.DevelopmentCardGrid}
     * @return the {@link Resource} to be subtracted from the {@link DevelopmentCard} cost when buying from {@link it.polimi.ingsw.model.game.DevelopmentCardGrid}
     * @throws DifferentEffectTypeException if the {@link LeaderCard} has a different effect
     */
    public Resource getDiscountEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.DISCOUNT){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return discountEffect;
    }

    /**
     * Get the {@link Marble} color to be converted to when picking a white {@link Marble} from the {@link Market}.
     * @return the {@link Marble} color to be converted to when picking a white {@link Marble} from the {@link Market}.
     * @throws DifferentEffectTypeException if the {@link LeaderCard} has a different effect
     */
    public Marble getWhiteMarbleEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.WHITE_MARBLE){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return whiteMarbleEffect;
    }

    /**
     * Get the extra storage capability of the {@link LeaderCard}
     * @return the extra storage capability of the {@link LeaderCard}
     * @throws DifferentEffectTypeException if the {@link LeaderCard} has a different effect
     */
    public ExtraDepot getExtraDepotEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.EXTRA_DEPOT){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return extraDepotEffect;
    }

    /**
     * Get the {@link Production} power of the {@link LeaderCard}
     * @return the {@link Production} power of the {@link LeaderCard}
     * @throws DifferentEffectTypeException if the {@link LeaderCard} has a different effect
     */
    public Production getProductionEffect() throws DifferentEffectTypeException {
        if (this.effectType != EffectType.PRODUCTION){
            throw new DifferentEffectTypeException(this.effectType, EffectType.DISCOUNT);
        }
        return productionEffect;
    }

    /**
     * Get the type of the {@link Effect} of the {@link LeaderCard}
     * @return the type of the {@link Effect} of the {@link LeaderCard}
     */
    public EffectType getEffectType() {
        return effectType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return discountEffect == effect.discountEffect && whiteMarbleEffect == effect.whiteMarbleEffect && ((extraDepotEffect==null && effect.extraDepotEffect == null) ||Objects.equals(extraDepotEffect, effect.extraDepotEffect)) && ((productionEffect == null && effect.productionEffect == null) || Objects.equals(productionEffect, effect.productionEffect)) && effectType == effect.effectType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountEffect, whiteMarbleEffect, extraDepotEffect, productionEffect, effectType);
    }
}
