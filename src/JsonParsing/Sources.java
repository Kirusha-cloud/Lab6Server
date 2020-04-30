package JsonParsing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Sources {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String path_out = System.getenv().get("coloutput");
}
