/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.player.mood.moodplayer.Soundcloud;

/**
 *
 * @author Cortez
 */
public class SongInfo {
    
    private String title;
    private String artist;
    private String streamURL;
    private long duration;
    
    public SongInfo(String artist,String title, String streamURL, long duration){
        this.title = title;
        this.artist = artist;
        this.streamURL = streamURL;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration (long duration) {
        this.duration = duration;
    }
    
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(this.artist).append(" - ").append(this.title).append("\n");
        sb.append("Stream URL: ").append(this.streamURL).append("\n");
        sb.append("Duration: ").append(this.duration).append("\n");
        return sb.toString();
    }
}
