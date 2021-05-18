package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;

import java.util.List;

public class GraphicalScoreBoard {

    private final int width = 36;
    private final int height = 4;

    private final char[][] symbols = new char[height][width];
    private final Colour[][] colours = new Colour[height][width];
    private final BackColour[][] backGroundColours = new BackColour[height][width];

    public GraphicalScoreBoard() {
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

    public void displayScoreBoard() {
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(backGroundColours[i][j].getCode() + colours[i][j].getCode() + symbols[i][j]); //+ Colour.ANSI_RESET
            }
            System.out.print("\n");
        }
    }

    public void drawScoreBoard(){
        drawPlayersPositions();
    }

    private void drawPlayersPositions() {
        MatchData matchData = MatchData.getInstance();
        List<String> nicknames = matchData.getAllNicknames();
        int x_begin = 0;
        int y_begin = 0;
        int max_length = 0;
        for(String nickname : nicknames){
            if(nickname.length() > max_length)
                max_length = nickname.length();
        }
        for(String nickname : nicknames){
            for(int i = 0; i < nickname.length(); i++){
                symbols[x_begin][y_begin + i] = nickname.charAt(i);
            }
            symbols[x_begin][y_begin + max_length + 2] = 'P';
            symbols[x_begin][y_begin + max_length + 3] = 'O';
            symbols[x_begin][y_begin + max_length + 4] = 'S';
            symbols[x_begin][y_begin + max_length + 5] = ':';
            int faithTrackPosition = matchData.getLightClientByNickname(nickname).getFaithTrackPosition();
            if(faithTrackPosition > 9){
                symbols[x_begin][y_begin + max_length + 7] = String.valueOf(faithTrackPosition/10).charAt(0);
            }
            symbols[x_begin][y_begin + max_length + 8] = String.valueOf(faithTrackPosition%10).charAt(0);

            symbols[x_begin][y_begin + max_length + 10] = 'V';
            symbols[x_begin][y_begin + max_length + 11] = 'P';
            symbols[x_begin][y_begin + max_length + 12] = ':';

            int victoryPoints = matchData.getLightClientByNickname(nickname).getVictoryPoints();
            if(victoryPoints > 9){
                symbols[x_begin][y_begin + max_length + 13] = String.valueOf(victoryPoints /10).charAt(0);
            }
            symbols[x_begin][y_begin + max_length + 14] = String.valueOf(victoryPoints %10).charAt(0);


            x_begin++;

        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
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
