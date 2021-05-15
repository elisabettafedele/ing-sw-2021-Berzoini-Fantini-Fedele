package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;

public class GraphicalCard {
    public static final int CardWidth = 14;
    public static final int CardHeight = 8;

    private GraphicalDevelopmentCardGrid graphicalGrid;
    private int ID;
    private LightDevelopmentCard lightDevelopmentCard;

    public GraphicalCard(GraphicalDevelopmentCardGrid graphicalGrid, LightDevelopmentCard ldc) {
        this.graphicalGrid = graphicalGrid;
        this.lightDevelopmentCard = ldc;
    }

    public void drawOnScreen(int x_coord, int y_coord) {
        for (int i = 0; i < CardHeight; i++) {
            for (int j = 0; j < CardWidth; j++) {
                if (i == 0 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╔');
                else if (i == 0 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╗');
                else if ((i == 0 || i == CardHeight - 1) && j > 0 && j < CardWidth -1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '═');
                else if (i == CardHeight - 1 && j == 0) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╚');
                else if (i == CardHeight - 1 && j == CardWidth - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '╝');
                else if (i > 0 && i < CardHeight-1 && (j == 0 || j == CardWidth - 1)) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_WHITE, '║');
                //else if (i == 0 || i == CardHeight - 1) graphicalGrid.addPixel(i+x_coord, j+y_coord, Colour.ANSI_BRIGHT_RED, "\u02C9");
            }
        }
        drawFlag(x_coord, y_coord);
        drawCost(x_coord, y_coord, 1, lightDevelopmentCard.getCost());
        drawID(x_coord, y_coord);
        drawVictoryPoints(x_coord, y_coord);
        drawProductionCost(x_coord, y_coord);
        drawProductionOutput(x_coord, y_coord);

    }

    private void drawProductionOutput(int x_coord, int y_coord) {
        drawCost(x_coord, y_coord, CardHeight-2, lightDevelopmentCard.getProductionOutput());
    }

    private void drawProductionCost(int x_coord, int y_coord) {
        drawCost(x_coord, y_coord, CardHeight-3, lightDevelopmentCard.getProductionCost());

    }

    private void drawVictoryPoints(int x_coord, int y_coord) {
        int vp = lightDevelopmentCard.getVictoryPoints();
        int h_center = CardWidth/2;
        graphicalGrid.addPixel(x_coord+3, y_coord+h_center-2, Colour.ANSI_BRIGHT_YELLOW, 'V');
        graphicalGrid.addPixel(x_coord+3, y_coord+h_center-1, Colour.ANSI_BRIGHT_YELLOW, 'P');
        graphicalGrid.addPixel(x_coord+3, y_coord+h_center, Colour.ANSI_BRIGHT_WHITE, ':');
        if(vp > 9)
            graphicalGrid.addPixel(x_coord+3, y_coord+h_center+1, Colour.ANSI_BRIGHT_YELLOW, String.valueOf(vp/10).charAt(0));
        graphicalGrid.addPixel(x_coord+3, y_coord+h_center+2, Colour.ANSI_BRIGHT_YELLOW, String.valueOf(vp%10).charAt(0));
    }

    private void drawID(int x_coord, int y_coord) {
        int ID = lightDevelopmentCard.getID();
        if(ID>9)
            graphicalGrid.addPixel(x_coord + 1, y_coord + CardWidth - 4, Colour.ANSI_BRIGHT_WHITE, String.valueOf(ID/10).charAt(0));
        graphicalGrid.addPixel(x_coord+1, y_coord+CardWidth-3, Colour.ANSI_BRIGHT_WHITE, String.valueOf(ID%10).charAt(0));
    }

    private void drawCost(int x_coord, int y_coord, int posix, List<String> cost) {
        int center = CardWidth/2;
        int begin = center - cost.size()/2;
        for (int j = 0; j < cost.size(); j++){
            if(j%2==0)
                graphicalGrid.addPixel(x_coord+posix, y_coord+j+begin, Colour.ANSI_BRIGHT_WHITE, cost.get(j).charAt(0));
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

                    graphicalGrid.addPixel(x_coord + posix, y_coord + j + begin, c, r.symbol.charAt(0));
                }catch(IllegalArgumentException e){
                    graphicalGrid.addPixel(x_coord+posix, y_coord+j+begin, Colour.ANSI_RED, '†');
                }
            }
            if(posix == 3){
                //graphicalGrid.addPixel(x_coord + posix, y_coord + cost.size()+6, Colour.ANSI_BRIGHT_WHITE, "\u21B4".charAt(0));
            }
        }
    }

    private void drawFlag(int x_coord, int y_coord) {
        Colour flagColor = getColor(lightDevelopmentCard.getFlagColor());
        int level = getLevel(lightDevelopmentCard);
        for (int j = 0; j <= level; j++){
            graphicalGrid.addPixel(x_coord+1+j, y_coord+2, flagColor,'\u25CF');
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
