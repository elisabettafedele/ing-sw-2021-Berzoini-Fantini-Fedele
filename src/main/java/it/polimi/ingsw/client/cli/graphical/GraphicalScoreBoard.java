package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.enumerations.GameMode;

import java.util.List;

/**
 * Class to draw the scoreboard with names, position on the faith track and victory points
 * of the players
 */
public class GraphicalScoreBoard extends GraphicalElement{

    public GraphicalScoreBoard() {
        super(29, 11);
        reset();
    }

    /**
     * Method to draw all the elements of the scoreboard
     * @return the length of the longest nickname
     */
    public int drawScoreBoard(){
        List<String> nicknames = MatchData.getInstance().getAllNicknames();
        int maxLength = getMaxLength(nicknames);
        drawTable(maxLength, nicknames.size());
        fillNamesAndScores(nicknames, maxLength);
        return maxLength;
    }

    /**
     * Puts the names and the points of each player in the scoreboard
     * @param nicknames all the player nicknames
     * @param max_length the lenght of the longest nickname (aesthetic needs)
     */
    private void fillNamesAndScores(List<String> nicknames, int max_length) {
        int x_begin = 3;
        for(String nickname : nicknames) {
            for (int i = 0; i < nickname.length(); i++) {
                symbols[x_begin][i+1] = nickname.charAt(i);
            }

            int faithTrackPosition;
            if(MatchData.getInstance().getGameMode() == GameMode.SINGLE_PLAYER && nickname.equals(MatchData.LORENZO)){
                faithTrackPosition = MatchData.getInstance().getBlackCrossPosition();
            }else{
                faithTrackPosition = MatchData.getInstance().getLightClientByNickname(nickname).getFaithTrackPosition();
            }
            if(faithTrackPosition > 9){
                symbols[x_begin][max_length + 2] = String.valueOf(faithTrackPosition/10).charAt(0);
            }
            symbols[x_begin][max_length + 3] = String.valueOf(faithTrackPosition%10).charAt(0);
            int victoryPoints = nickname.equals(MatchData.LORENZO)? 0: MatchData.getInstance().getLightClientByNickname(nickname).getVictoryPoints();
            if(victoryPoints > 9){
                symbols[x_begin][max_length + 6] = String.valueOf(victoryPoints /10).charAt(0);
            }
            symbols[x_begin][max_length + 7] = String.valueOf(victoryPoints %10).charAt(0);

            x_begin += 2;
        }
    }

    /**
     * Draw edges and separator of the table
     * @param maxLength the length of the longest nickname
     * @param size the number of players
     */
    private void drawTable(int maxLength, int size) {
        drawEdges(3+size*2, maxLength+1+8);
        drawSeparators(size, maxLength);
        fillHeader(maxLength);
    }

    private void fillHeader(int maxLength) {
        symbols[1][maxLength+2] = 'P';
        symbols[1][maxLength+3] = 'O';
        symbols[1][maxLength+4] = 'S';
        symbols[1][maxLength+6] = 'V';
        symbols[1][maxLength+7] = 'P';
    }

    private void drawSeparators(int size, int maxLength) {
        for(int i = 0; i < size; i++){
            for(int j = 0; j < maxLength + 1 + 8; j++){
                symbols[i*2+2][j] = '═';
            }
            symbols[i*2+2][0] = '╠';
            symbols[i*2+2][maxLength+8] = '╣';
        }
        for(int i = 0; i < 3+size*2; i++){
            symbols[i][maxLength+1] = '║';
            symbols[i][maxLength+5] = '║';
            if(i==0){
                symbols[i][maxLength+1] = '╦';
                symbols[i][maxLength+5] = '╦';
            }else if(i == 2+size*2){
                symbols[i][maxLength+1] = '╩';
                symbols[i][maxLength+5] = '╩';
            }else if(i%2 == 0){
                symbols[i][maxLength+1] = '╬';
                symbols[i][maxLength+5] = '╬';
            }
        }
    }

    private int getMaxLength(List<String> nicknames) {
        int max_length = 0;
        for(String nickname : nicknames){
            if(nickname.length() > max_length)
                max_length = nickname.length();
        }
        return max_length;
    }
}
