package com.player.mood.moodplayer;

import com.player.mood.moodplayer.soundcloud.SoundcloudTrack;

import java.util.HashMap;
import java.util.Map;

/**
 * (SUMMARY)
 *
 * @author daniel
 * @date 15-04-2016.
 */
public class TracksManager {
    private HashMap<String,SoundcloudTrack> tracks; // soundcloudid, SoundcloudTrack
    private HashMap<String,Double> tracksEnergy; // soundcloudid, energy
    private String[] tracksIDs;
    private int currentTrackIndex;

    public TracksManager() {
        this.tracks = new HashMap<>();
        this.tracksEnergy = new HashMap<>();
        this.tracksIDs=null;
        this.currentTrackIndex=0;
    }

    public void addTrack(SoundcloudTrack sctrack, double energy) {
        this.tracks.put(sctrack.getId(),sctrack);
        this.tracksEnergy.put(sctrack.getId(),energy);
    }

    public HashMap<String,SoundcloudTrack> getTracks() { return this.tracks; }

    public HashMap<String,Double> getTracksEnergy() { return this.tracksEnergy; }

    public String[] getTracksIds() {
        String[] r = new String[tracksEnergy.size()];
        int i=0;
        for(String k : tracksEnergy.keySet()){
            r[i] = k;
            i++;
        }
        return r;
    }

    public void setLocationURLs(HashMap<String,String> urls) {
        for(Map.Entry<String,String> entry : urls.entrySet()) {
            this.tracks.get(entry.getKey()).setLocationURL(entry.getValue());
        }
    }

    public String getLocationUrlForTrackId(String id) {
        return this.tracks.get(id).getLocationURL();
    }

    public String getCurrentTrackTitle() {
        return this.tracks.get(this.tracksIDs[this.currentTrackIndex]).getTitle();
    }

    public String getCurrentTrackID() {
        return this.tracksIDs[this.currentTrackIndex];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Tracks ---\n");
        for(Map.Entry<String,SoundcloudTrack> entry : this.tracks.entrySet()) {
            sb.append(entry.toString());
        }
        sb.append("--- Energy ---\n");
        for(Map.Entry<String,Double> entry : tracksEnergy.entrySet()) {
            sb.append(entry.getKey() + " - " + entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    // Media controller methods

    public String getCurrentSongURL() {
        if(this.tracksIDs==null) {
            this.tracksIDs = this.getTracksIds();
            this.currentTrackIndex = 0;
            return this.getLocationUrlForTrackId(this.tracksIDs[this.currentTrackIndex]);
        } else {
            return this.getLocationUrlForTrackId(this.tracksIDs[this.currentTrackIndex]);
        }
    }

    public String getNextSongURL() {
        if( (this.currentTrackIndex + 1) >= this.tracksIDs.length) {
            this.currentTrackIndex = 0;
        } else {
            this.currentTrackIndex++;
        }
        return this.getLocationUrlForTrackId(this.tracksIDs[this.currentTrackIndex]);
    }

    public String getPreviousSongURL() {
        if( (this.currentTrackIndex - 1) < 0) {
            this.currentTrackIndex = (this.tracksIDs.length - 1);
        } else {
            this.currentTrackIndex--;
        }
        return this.getLocationUrlForTrackId(this.tracksIDs[this.currentTrackIndex]);
    }

    public void setCurrentTrack(String trackID) {
        for(int i=0; i < tracksIDs.length; i++) {
            if(tracksIDs[i].equals(trackID)) {
                this.currentTrackIndex = i;
                break;
            }
        }
    }
}
