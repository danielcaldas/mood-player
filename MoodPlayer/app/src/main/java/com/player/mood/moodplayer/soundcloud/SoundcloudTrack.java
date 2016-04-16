/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.player.mood.moodplayer.soundcloud;

/**
 *
 * @author Cortez
 */
public class SoundcloudTrack {

    private String id;
    private String title;
    private String artist;
    private String streamURL;
    private String locationURL;
    private long duration;
    
    public SoundcloudTrack(String id, String artist, String title, String streamURL, long duration){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.streamURL = streamURL;
        this.locationURL = ""; // default
        this.duration = duration;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public String getLocationURL() { return locationURL; }

    public void setLocationURL(String locationURL) { this.locationURL = locationURL; }

    public long getDuration() { return duration; }

    public void setDuration (long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "SoundcloudTrack{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", streamURL='" + streamURL + '\'' +
                ", locationURL='" + locationURL + '\'' +
                ", duration=" + duration +
                '}';
    }
}
