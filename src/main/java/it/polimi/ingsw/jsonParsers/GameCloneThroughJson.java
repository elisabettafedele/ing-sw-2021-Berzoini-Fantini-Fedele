package it.polimi.ingsw.jsonParsers;

import com.google.gson.Gson;
import it.polimi.ingsw.model.game.Game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;

public class GameCloneThroughJson {
    public static Game clone(Game game){
        Gson gson = new Gson();
        String json = gson.toJson(game);
        return gson.fromJson(json, game.getClass());
    }

}
