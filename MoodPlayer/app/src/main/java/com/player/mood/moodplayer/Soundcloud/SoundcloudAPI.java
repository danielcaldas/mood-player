/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundcloud;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Cortez
 */
public class SoundcloudAPI {
    
    private String APIKEY;
    
    public SoundcloudAPI (String key){
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
    
        public JSONObject getPlaylist(int playlistID) throws Exception{
            return GET("http://api.soundcloud.com/playlists/"+playlistID+"?client_id="+this.APIKEY);
        }
    
        public JSONObject getTrack(int trackID) throws Exception {
            return GET("http://api.soundcloud.com/tracks/"+trackID+"?client_id="+this.APIKEY);
        }
}
