package it.polimi.ingsw.jsonParsers;

import com.google.gson.Gson;
import it.polimi.ingsw.model.game.Game;

//TODO
public class GameCloneThroughJson {
    public static Game clone(Game game){
        Gson gson = new Gson();
        String json = gson.toJson(game);
        return gson.fromJson(json, game.getClass());
    }

}
