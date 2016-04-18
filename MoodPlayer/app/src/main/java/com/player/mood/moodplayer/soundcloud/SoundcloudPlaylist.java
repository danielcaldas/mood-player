/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package com.player.mood.moodplayer.soundcloud;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;

/**
 *
 * @author Cortez
 */
public class SoundcloudPlaylist {

    private JSONObject obj;

    public SoundcloudPlaylist(JSONObject obj){
        this.obj = obj;
    }

    public ArrayList<SoundcloudTrack> getSongsInfo(){
        String id, title,artist, streamURL, locationURL = "";
        Long duration;
        ArrayList<SoundcloudTrack> result = new ArrayList<SoundcloudTrack>();
        JSONArray tracks = (JSONArray)this.obj.get("tracks");
        JSONObject t = null;
        for(int i=0;i<tracks.size();i++){
            t = (JSONObject) tracks.get(i);
            id = String.valueOf(t.get("id"));
            title = filterTitle(t.get("title").toString());
            artist = ((JSONObject)t.get("user")).get("username").toString();
            streamURL = t.get("stream_url").toString();
            duration = (Long)t.get("duration");

            SoundcloudTrack si = new SoundcloudTrack(id, artist, title, streamURL, duration);

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
