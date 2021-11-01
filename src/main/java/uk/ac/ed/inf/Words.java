package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Words {
    private static final HttpClient client = HttpClient.newHttpClient();

    public final String name;
    public final String port;

    /**
     * Class constructor.
     *
     * @param name the name of the machine
     * @param port the port where the webserver is running
     */
    public Words(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public LongLat getLongLatFromWords(String first, String second, String third) {
        String urlString = "http://"+this.name+":"+this.port+"/words/"+first+"/"+second+"/"+third+"/details.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            W3WDetails details = new Gson().fromJson(response.body(), new TypeToken<W3WDetails>(){}.getType());
            return new LongLat(details.getCoordinates().getLng(),details.getCoordinates().getLat());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.err.print("Error in What3Words: no location found");
        return null;
    }
}
