package it.polimi.ingsw.jsonParsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ValueType;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse the {@link Value} from a json file
 */
public class ValueParser {

    private ValueParser(){
        throw new IllegalStateException("Utility class");
    }

    public static Value parseValue(JsonArray jsonValues) throws InvalidArgumentException {
        Map<Flag, Integer> flagValue = new HashMap<Flag, Integer>();
        Map<Resource, Integer> resourceValue = new HashMap<Resource,Integer>();
        int faithValue=0;
        for (JsonElement jsonElement : jsonValues){
            JsonArray value= jsonElement.getAsJsonArray();
            ValueType valueType = ValueType.valueOf(value.get(0).getAsString());
            if (valueType.equals(ValueType.RESOURCE)){
                resourceValue.put(Resource.valueOf(value.get(1).getAsString()), value.get(2).getAsInt());
            }
            else if (valueType.equals(ValueType.FAITH)){
                faithValue = value.get(1).getAsInt();
            }
            else if (valueType.equals(ValueType.FLAG)){
                Flag flag = new Flag(FlagColor.valueOf(value.get(1).getAsString()), Level.valueOf(value.get(2).getAsString()));
                flagValue.put(flag, value.get(3).getAsInt());
            }
        }
        return new Value(flagValue, resourceValue, faithValue);
    }
}
