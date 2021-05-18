package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.LightClient;
import it.polimi.ingsw.client.MatchData;

import java.util.List;

public class GraphicalFaithTrack {

    private final int width = 58;
    private final int height = 9;
    private final int trackWidth = 58;

    private final int squareHeight = 3;
    private final int squareWidth = 4;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];
    private final BackColour[][] backGroundColours = new BackColour[height][width];
    String nickname;

    public GraphicalFaithTrack(String nickname) {
        this.nickname = nickname;
        reset();
    }

    private void reset(){
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                symbols[i][j] = ' ';
                colours[i][j] = Colour.ANSI_BRIGHT_WHITE;
                backGroundColours[i][j] = BackColour.ANSI_DEFAULT;
            }
        }
    }

    public void displayFaithTrack() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    public void drawFaithTrack(){
        reset();
        int xStep = height/3 - 1;
        int yStep = trackWidth/19;
        int cellNumber = 0;
        for(int i = 0; i < 3; i++){
            drawSquare(cellNumber++, xStep*2+1, i*yStep);
        }

        drawSquare(cellNumber++, xStep*1+1, yStep*2);

        for(int i = 0; i < 6; i++){
            drawSquare(cellNumber++, 0+1, yStep*i+2*yStep);
        }

        drawSquare(cellNumber++, xStep*1+1, yStep*7);

        for(int i = 0; i < 6; i++){
            drawSquare(cellNumber++, xStep*2+1, yStep*i+7*yStep);
        }

        drawSquare(cellNumber++, xStep*1+1, yStep*12);

        for(int i = 0; i < 7; i++){
            drawSquare(cellNumber++, 0+1, yStep*i+12*yStep);
        }
        drawPopesFavorTiles();
    }



    private void drawPopesFavorTiles() {
        int xStep = height/3 - 1;
        int yStep = trackWidth/19;
        int x_begin = xStep+2;
        int y_begin = yStep*4;
        LightClient lc = MatchData.getInstance().getLightClientByNickname(this.nickname);
        boolean taken = lc.hasTakenPopesFavorTile(0);
        drawLowerTiles(x_begin, y_begin, xStep, yStep, taken, 2);
        taken = lc.hasTakenPopesFavorTile(2);
        drawLowerTiles(x_begin, y_begin+yStep*11, xStep, yStep, taken, 4);
        taken = lc.hasTakenPopesFavorTile(1);
        drawUpperTile(x_begin - 1, y_begin+yStep*5, xStep, yStep, taken, 3);
    }

    private void drawUpperTile(int x_begin, int y_begin, int xStep, int yStep, boolean taken, int vps) {
        for(int i = 0; i < xStep; i++) {
            for (int j = 0; j < yStep * 2 + 1; j++) {
                if(i==0 && j == 0){
                    symbols[i + x_begin][j + y_begin] = '┌';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }else if(i == 0 && j == yStep*2){
                    symbols[i + x_begin][j + y_begin] = '┐';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }else if(i == 0){
                    symbols[i + x_begin][j + y_begin] = '─';
                    colours[i + x_begin][j + y_begin] = taken  ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }else if(j == 0 || j == yStep*2){
                    symbols[i + x_begin][j + y_begin] = '│';
                    colours[i + x_begin][j + y_begin] = taken  ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }
            }
        }
        symbols[x_begin + xStep][y_begin] = '┤';
        symbols[x_begin + xStep][y_begin+yStep*2] = '├';

        symbols[x_begin + 1][y_begin+2] = String.valueOf(vps).charAt(0);
        symbols[x_begin + 1][y_begin+3] = 'V';
        symbols[x_begin + 1][y_begin+4] = 'P';
        colours[x_begin + 1][y_begin+2] = Colour.ANSI_BRIGHT_YELLOW;
        colours[x_begin + 1][y_begin+3] = Colour.ANSI_BRIGHT_YELLOW;
        colours[x_begin + 1][y_begin+4] = Colour.ANSI_BRIGHT_YELLOW;
    }

    private void drawLowerTiles(int x_begin, int y_begin, int xStep, int yStep, boolean taken, int vps) {
        for(int i = 0; i < xStep; i++){
            for(int j = 0; j < yStep*2+1; j++){
                if(i == xStep - 1 && j == 0) {
                    symbols[i + x_begin][j + y_begin] = '└';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }
                else if(i == xStep - 1 && j == yStep * 2) {
                    symbols[i + x_begin][j + y_begin] = '┘';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }
                else if(j==0 || j == yStep*2) {
                    symbols[i + x_begin][j + y_begin] = '│';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }
                else if(i == xStep-1) {
                    symbols[i + x_begin][j + y_begin] = '─';
                    colours[i + x_begin][j + y_begin] = taken ? Colour.ANSI_BRIGHT_GREEN : Colour.ANSI_BRIGHT_RED;
                }
            }
        }
        symbols[x_begin - 1][y_begin] = '┤';
        symbols[x_begin - 1][y_begin+yStep*2] = '├';

        symbols[x_begin][y_begin+2] = String.valueOf(vps).charAt(0);
        symbols[x_begin][y_begin+3] = 'V';
        symbols[x_begin][y_begin+4] = 'P';
        colours[x_begin][y_begin+2] = Colour.ANSI_BRIGHT_YELLOW;
        colours[x_begin][y_begin+3] = Colour.ANSI_BRIGHT_YELLOW;
        colours[x_begin][y_begin+4] = Colour.ANSI_BRIGHT_YELLOW;

    }

    private void drawSquare(int number, int x, int y) {
        int tens = number/10;
        int units = number%10;

        char[] boxChars = getCharsByNumber(number);
        char upLeft = boxChars[0];
        char upRight = boxChars[1];
        char bottomLeft = boxChars[2];
        char bottomRight = boxChars[3];

        for(int i = 0; i < squareHeight; i++){
            for(int j = 0; j < squareWidth; j++){
                if(i == 0 && j == 0)
                    symbols[i+x][j+y] = upLeft;
                else if(i == 0 && j == squareWidth - 1)
                    symbols[i+x][j+y] = upRight;
                else if(i == squareHeight - 1 && j == 0)
                    symbols[i+x][j+y] = bottomLeft;
                else if(i == squareHeight - 1 && j == squareWidth - 1)
                    symbols[i+x][j+y] = bottomRight;
                else if(i == 0 || i == squareHeight - 1)
                    symbols[i+x][j+y] = '─';
                else if(j == 0 || j == squareWidth - 1)
                    symbols[i+x][j+y] = '│';
            }
        }
        symbols[1+x][1+y] = String.valueOf(tens).charAt(0);
        symbols[1+x][2+y] = String.valueOf(units).charAt(0);

        paintSquares(number, x, y); //۩

    }

    private void paintSquares(int number, int x, int y) {
        if(number == 8 || number == 15 || number == 24)
            drawPopeSpaces(x, y);
        if(number == 3 || number == 12 || number == 15)
            drawVictoryPoints(number, x, y, false);
        if(number == 6 || number == 9 || number == 18 || number == 21 || number == 24)
            drawVictoryPoints(number, x, y, true);
        if(number == 5 || number == 8|| number == 12 || number == 16 || number == 19 || number == 24){
            paintEdges(x, y, false);
            colours[x+1][y+3] = Colour.ANSI_RED;
            if(number == 16)
                colours[x][y] = Colour.ANSI_BRIGHT_WHITE;
        }
        if(number == 6 || number == 7 || (number > 12 && number < 16) || (number > 19 && number < 24)){
            paintEdges(x, y, true);
        }
    }

    private void drawPopeSpaces(int x, int y) {
        symbols[x+1][y+1] = '۩';
        symbols[x+1][y+2] = '۩';

    }

    private void drawVictoryPoints(int number, int x, int y, boolean above) {

        int vps = getVPsByNumber(number);

        colours[1+x][1+y] = Colour.ANSI_BRIGHT_YELLOW;
        colours[1+x][2+y] = Colour.ANSI_BRIGHT_YELLOW;

        if(number == 3) {
            symbols[1 + x][y - 3] = String.valueOf(vps).charAt(0);
            colours[1 + x][y - 3] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[1 + x][y - 2] = 'V';
            colours[1 + x][y - 2] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[1 + x][y - 1] = 'P';
            colours[1 + x][y - 1] = Colour.ANSI_BRIGHT_YELLOW;
            return;
        }
        if(above){
            if(vps>9){
                symbols[x-1][y] = String.valueOf(vps).charAt(0);
                colours[x-1][y] = Colour.ANSI_BRIGHT_YELLOW;
                symbols[x-1][y+1] = String.valueOf(vps).charAt(1);
            }else{
                symbols[x-1][y+1] = String.valueOf(vps).charAt(0);
            }
            colours[x-1][y+1] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[x-1][y+2] = 'V';
            colours[x-1][y+2] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[x-1][y+3] = 'P';
            colours[x-1][y+3] = Colour.ANSI_BRIGHT_YELLOW;
        }
        else{
            symbols[x+3][y+1] = String.valueOf(vps).charAt(0);
            colours[x+3][y+1] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[x+3][y+2] = 'V';
            colours[x+3][y+2] = Colour.ANSI_BRIGHT_YELLOW;
            symbols[x+3][y+3] = 'P';
            colours[x+3][y+3] = Colour.ANSI_BRIGHT_YELLOW;
        }
    }

    private int getVPsByNumber(int number) {
        if(number == 3)
            return 1;
        if(number == 6)
            return 2;
        if(number == 9)
            return 4;
        if(number == 12)
            return 6;
        if(number == 15)
            return 9;
        if(number == 18)
            return 12;
        if(number == 21)
            return 16;
        if(number == 24)
            return 20;
        return 0;
    }

    private void paintEdges(int x, int y, boolean corners) {
        colours[x+1][y] = Colour.ANSI_RED;
        colours[x][y+1] = Colour.ANSI_RED;
        colours[x][y+2] = Colour.ANSI_RED;
        colours[x+2][y+1] = Colour.ANSI_RED;
        colours[x+2][y+2] = Colour.ANSI_RED;
        if(corners){
            colours[x][y] = Colour.ANSI_RED;
            colours[x+2][y] = Colour.ANSI_RED;
            colours[x][y+3] = Colour.ANSI_RED;
            colours[x+2][y+3] = Colour.ANSI_RED;
        }
    }

    private char[] getCharsByNumber(int number) {
        char [] boxChars = new char[4];
        boxChars[0] = '┬';
        boxChars[1] = '┐';
        boxChars[2] = '┴';
        boxChars[3] = '┘';

        if(number == 0 || number == 4 || number == 18)
            boxChars[0] = '┌';
        if(number == 0 || number == 11)
            boxChars[2] = '└';
        if(number == 3 || number == 17) {
            boxChars[2] = '┼';
            boxChars[3] = '┤';
        }
        if(number == 4 || number == 18)
            boxChars[2] = '├';
        if(number == 5 || number == 19)
            boxChars[2] = '┼';
        if(number == 10) {
            boxChars[0] = '┼';
            boxChars[1] = '┤';
        }
        if (number == 11)
            boxChars[0] = '├';
        if (number == 12)
            boxChars[0] = '┼';

        return boxChars;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    char[][] getSymbols() {
        return symbols;
    }

    Colour[][] getColours() {
        return colours;
    }

    public BackColour[][] getBackGroundColours() {
        return backGroundColours;
    }
}
