/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundcloud;

import java.util.ArrayList;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Cortez
 */
public class SoundcloudPlaylist {
    
    private JSONObject obj;
    
    public SoundcloudPlaylist(JSONObject obj){
        this.obj = obj;
    }
    
    public ArrayList<SongInfo> getSongInfo(){
        String title,artist, streamURL;
        Long duration;
        ArrayList<SongInfo> result = new ArrayList<SongInfo>();
        JSONArray tracks = (JSONArray)this.obj.get("tracks");
        JSONObject t = null;
        for(int i=0;i<tracks.size();i++){
            t = (JSONObject) tracks.get(i);
            title = filterTitle(t.get("title").toString());
            artist = ((JSONObject)t.get("user")).get("username").toString();
            streamURL = t.get("stream_url").toString();
            duration = (Long)t.get("duration");
            SongInfo si = new SongInfo(artist,title,streamURL,duration);
            result.add(si);
        }
        return result;
    }
    
    private String filterTitle (String title){
        if (title.contains("-")){
            String[] tokens = title.split("-");
            return tokens[1].trim();
        }
        return title;
    }
    
}
