package com.player.mood.moodplayer.soundcloud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * SoundCloud API wrapper to perform some operations on SoundCloud REST end points.
 * @author Cortez
 * @date 2016.04.13
 */
public class SoundcloudAPI {

    private String APIKEY;

    public SoundcloudAPI(String key) {
        this.APIKEY = key;
    }

    private static JSONObject GET(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());
        return json;
    }

    public SoundcloudPlaylist getPlaylist(String playlistID) throws Exception {
        return new SoundcloudPlaylist(GET("http://api.soundcloud.com/playlists/" + playlistID + "?client_id=" + this.APIKEY));
    }

    public JSONObject getTrack(String trackID) throws Exception {
        return GET("http://api.soundcloud.com/tracks/" + trackID + "?client_id=" + this.APIKEY);
    }

    public String getStreamLocation(String trackID) throws Exception {
        JSONObject obj = GET("http://api.soundcloud.com/tracks/" + trackID + "/stream?client_id=" + this.APIKEY);
        return obj.get("location").toString();
    }
}
