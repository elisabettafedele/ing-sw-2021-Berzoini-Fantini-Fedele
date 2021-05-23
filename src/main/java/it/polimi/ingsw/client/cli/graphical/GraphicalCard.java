package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public abstract class GraphicalCard extends GraphicalElement{

    protected LightCard lightCard;

    String nickname;

    public GraphicalCard(LightCard ldc, String nickname) {
        super(14, 8);
        reset();
        this.lightCard = ldc;
        this.nickname = nickname;
    }

    protected void drawID() {
        int ID = lightCard.getID();
        if(ID>9){
            symbols[1][width - 4] = String.valueOf(ID/10).charAt(0);
            colours[1][width - 4] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[1][width - 3] = String.valueOf(ID%10).charAt(0);
        colours[1][width - 3] = Colour.ANSI_BRIGHT_WHITE;
    }

    protected void drawVictoryPoints() {
        int vp = lightCard.getVictoryPoints();
        int h_center = width /2;
        symbols[3][h_center-2] = 'V';
        colours[3][h_center-2] = Colour.ANSI_BRIGHT_YELLOW;
        symbols[3][h_center-1] = 'P';
        colours[3][h_center-1] = Colour.ANSI_BRIGHT_YELLOW;
        symbols[3][h_center] = ':';
        colours[3][h_center] = Colour.ANSI_BRIGHT_YELLOW;
        if(vp > 9){
            symbols[3][h_center+1] = String.valueOf(vp/10).charAt(0);
            colours[3][h_center+1] = Colour.ANSI_BRIGHT_YELLOW;
        }
        symbols[3][h_center+2] = String.valueOf(vp%10).charAt(0);
        colours[3][h_center+2] = Colour.ANSI_BRIGHT_YELLOW;
    }

    protected void drawCost(int posix, List<String> cost) {
        int center = width /2;
        int begin = center - cost.size()/2;
        for (int j = 0; j < cost.size(); j++){
            if(j%2==0){
                symbols[posix][begin+j] = cost.get(j).charAt(0);
                colours[posix][begin+j] = Colour.ANSI_BRIGHT_WHITE;
            }
            else {
                try {
                    Resource r = Resource.valueOf(cost.get(j));
                    Colour c = getResourceColor(r);

                    symbols[posix][begin+j] = r.symbol.charAt(0);
                    colours[posix][begin+j] = c;
                }catch(IllegalArgumentException e){
                    symbols[posix][begin+j] = '†';
                    colours[posix][begin+j] = Colour.ANSI_RED;
                }
            }
        }
    }

    protected Colour getResourceColor(Resource r){
        Colour c;
        if (r == Resource.COIN)
            c = Colour.ANSI_YELLOW;
        else if (r == Resource.SHIELD)
            c = Colour.ANSI_BRIGHT_BLUE;
        else if (r == Resource.STONE)
            c = Colour.ANSI_WHITE;
        else if (r == Resource.SERVANT)
            c = Colour.ANSI_BRIGHT_PURPLE;
        else
            c = Colour.ANSI_BRIGHT_GREEN;

        return c;
    }

    protected Colour getColor(String flagColor) {
        if(flagColor.equals("YELLOW")){
            return Colour.ANSI_BRIGHT_YELLOW;
        }else if(flagColor.equals("BLUE")){
            return Colour.ANSI_BRIGHT_BLUE;
        }else if(flagColor.equals("PURPLE")){
            return Colour.ANSI_BRIGHT_PURPLE;
        }else if(flagColor.equals("GREEN")){
            return Colour.ANSI_BRIGHT_GREEN;
        }
        return Colour.ANSI_BRIGHT_WHITE;
    }

    protected void drawProductionOutput() {
        drawCost(height -2, lightCard.getEffectDescription2());
    }

    protected void drawProductionCost() {
        drawCost( height -3, lightCard.getEffectDescription());
    }
}
