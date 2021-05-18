package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public abstract class GraphicalCard {
    public static final int CardWidth = 14;
    public static final int CardHeight = 8;

    protected LightCard lightCard;

    protected final char[][] symbols = new char[CardHeight][CardWidth];
    protected final Colour[][] colours = new Colour[CardHeight][CardWidth];
    protected final BackColour[][] backGroundColours = new BackColour[CardHeight][CardWidth];

    String nickname;

    public GraphicalCard(LightCard ldc, String nickname) {
        reset();
        this.lightCard = ldc;
        this.nickname = nickname;
    }

    public void displayCard(){
        for(int i = 0; i < CardHeight; i++){
            for(int j = 0; j < CardWidth; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j] + Colour.ANSI_RESET);
            }
            System.out.print("\n");
        }
    }

    protected void reset(){
        for(int i = 0; i < CardHeight; i++) {
            for (int j = 0; j < CardWidth; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }

    protected void drawEdges(){
        for (int i = 0; i < CardHeight; i++) {
            for (int j = 0; j < CardWidth; j++) {
                if (i == 0 && j == 0) {
                    symbols[i][j] = '╔';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == 0 && j == CardWidth - 1) {
                    symbols[i][j] = '╗';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if ((i == 0 || i == CardHeight - 1) && j > 0 && j < CardWidth -1)
                {
                    symbols[i][j] = '═';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == CardHeight - 1 && j == 0) {
                    symbols[i][j] = '╚';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i == CardHeight - 1 && j == CardWidth - 1) {
                    symbols[i][j] = '╝';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
                else if (i > 0 && i < CardHeight-1 && (j == 0 || j == CardWidth - 1)) {
                    symbols[i][j] = '║';
                    colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                }
            }
        }
    }

    protected void drawID() {
        int ID = lightCard.getID();
        if(ID>9){
            symbols[1][CardWidth - 4] = String.valueOf(ID/10).charAt(0);
            colours[1][CardWidth - 4] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[1][CardWidth - 3] = String.valueOf(ID%10).charAt(0);
        colours[1][CardWidth - 3] = Colour.ANSI_BRIGHT_WHITE;
    }

    protected void drawVictoryPoints() {
        int vp = lightCard.getVictoryPoints();
        int h_center = CardWidth/2;
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
        int center = CardWidth/2;
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
        drawCost(CardHeight-2, lightCard.getEffectDescription());
    }

    protected void drawProductionCost() {
        drawCost( CardHeight-3, lightCard.getEffectDescription2());
    }

    char[][] getSymbols() {
        return symbols;
    }

    Colour[][] getColours() {
        return colours;
    }

    BackColour[][] getBackGroundColours() {
        return backGroundColours;
    }
}
