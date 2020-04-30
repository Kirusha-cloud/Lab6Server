package JsonParsing;

import Lab6Client.Flat;

import java.util.PriorityQueue;

public class Parser implements Sources {
    public static char[] parsToJson(PriorityQueue<Flat> flats) {
        String json = gson.toJson(flats);
        return json.toCharArray();
    }
}
