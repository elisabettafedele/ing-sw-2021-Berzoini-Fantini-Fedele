package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class GraphicalDevelopmentCard {
    public static final int CardWidth = 14;
    public static final int CardHeight = 8;

    private int ID;
    private LightDevelopmentCard lightDevelopmentCard;

    private final char[][] symbols = new char[CardHeight][CardWidth];
    private final Colour[][] colours = new Colour[CardHeight][CardWidth];

    public GraphicalDevelopmentCard(LightDevelopmentCard ldc) {
        reset();
        this.lightDevelopmentCard = ldc;
    }

    public void displayDevelopmentCard(){
        for(int i = 0; i < CardHeight; i++){
            for(int j = 0; j < CardWidth; j++){
                System.out.print(colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    private void reset(){
        for(int i = 0; i < CardHeight; i++) {
            for (int j = 0; j < CardWidth; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
            }
        }
    }

    //draw itsSelf
    public void drawCard(){
        reset();
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
        drawFlag();
        drawCost(1, lightDevelopmentCard.getCost());
        drawID();
        drawVictoryPoints();
        drawProductionCost();
        drawProductionOutput();
    }

    private void drawProductionOutput() {
        drawCost(CardHeight-2, lightDevelopmentCard.getProductionOutput());
    }

    private void drawProductionCost() {
        drawCost( CardHeight-3, lightDevelopmentCard.getProductionCost());

    }

    private void drawVictoryPoints() {
        int vp = lightDevelopmentCard.getVictoryPoints();
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

    private void drawID() {
        int ID = lightDevelopmentCard.getID();
        if(ID>9){
            symbols[1][CardWidth - 4] = String.valueOf(ID/10).charAt(0);
            colours[1][CardWidth - 4] = Colour.ANSI_BRIGHT_WHITE;
        }
        symbols[1][CardWidth - 3] = String.valueOf(ID%10).charAt(0);
        colours[1][CardWidth - 3] = Colour.ANSI_BRIGHT_WHITE;
    }

    private void drawCost(int posix, List<String> cost) {
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
                    Colour c;
                    if (r == Resource.COIN)
                        c = Colour.ANSI_YELLOW;
                    else if (r == Resource.SHIELD)
                        c = Colour.ANSI_BRIGHT_BLUE;
                    else if (r == Resource.STONE)
                        c = Colour.ANSI_WHITE;
                    else
                        c = Colour.ANSI_BRIGHT_PURPLE;

                    symbols[posix][begin+j] = r.symbol.charAt(0);
                    colours[posix][begin+j] = c;
                }catch(IllegalArgumentException e){
                    symbols[posix][begin+j] = '†';
                    colours[posix][begin+j] = Colour.ANSI_RED;
                }
            }
        }
    }

    private void drawFlag() {
        Colour flagColor = getColor(lightDevelopmentCard.getFlagColor());
        int level = getLevel(lightDevelopmentCard);
        for (int j = 0; j <= level; j++){
            symbols[1+j][2] = '\u25CF';
            colours[1+j][2] = flagColor;
        }
    }

    private int getLevel(LightDevelopmentCard ldc){
        return Level.valueOf(ldc.getFlagLevel()).getValue();
    }

    private Colour getColor(String flagColor) {
        if(flagColor == "YELLOW"){
            return Colour.ANSI_BRIGHT_YELLOW;
        }else if(flagColor == "BLUE"){
            return Colour.ANSI_BRIGHT_BLUE;
        }else if(flagColor == "PURPLE"){
            return Colour.ANSI_BRIGHT_PURPLE;
        }else if(flagColor == "GREEN"){
            return Colour.ANSI_BRIGHT_GREEN;
        }
        return Colour.ANSI_BRIGHT_WHITE;
    }

}
