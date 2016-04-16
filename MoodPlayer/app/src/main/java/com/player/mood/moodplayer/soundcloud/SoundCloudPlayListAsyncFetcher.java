package com.player.mood.moodplayer.soundcloud;

import android.os.AsyncTask;
import android.util.Log;

import com.echonest.api.v4.EchoNestException;
import com.player.mood.moodplayer.TracksManager;
import com.player.mood.moodplayer.echonest.EchoNestWrapper;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * (SUMMARY)
 *
 * @author jdc
 * @date 2016.04.15
 */
public class SoundCloudPlayListAsyncFetcher extends AsyncTask<String,Void,TracksManager> {
    public JSONObject res;
    private SoundcloudAPI api;

    private SoundcloudPlaylist playlist;
    private TracksManager tracksManager;

    public SoundCloudPlayListAsyncFetcher(SoundcloudAPI api) {
        this.api = api;
    }

    /**
     * This method accesses SoundCloud and fetches URL stream
     * location for songs in a given playlist.
     *
     * @param params, the playlist id
     * @return String containt the stream url location for streaming song in main thread.
     */
    @Override
    protected TracksManager doInBackground(String... params) {
        if(params.length > 0) {
            getEnergyForSoundcloudSongs(params[0]);
        }
        return tracksManager;
    }

    public void getEnergyForSoundcloudSongs(String playlistID) {
        tracksManager = new TracksManager();
        try {
            playlist = api.getPlaylist(playlistID);
            ArrayList<SoundcloudTrack> songsinfo = playlist.getSongsInfo();
            for (SoundcloudTrack si : songsinfo){
                try {
                    Double energy = EchoNestWrapper.getEnergy(si.getArtist(), si.getTitle());
                    // nullcheck: It is possible that echonest does not find metainfo for given song
                    if(energy!=null) {
                        tracksManager.addTrack(si,energy);
                    }
                }
                catch (EchoNestException e){
                    Log.d("EchoNestException", e.getMessage());
                }
            }
            Log.d("EchoNest", tracksManager.toString());

        }
        catch (Exception e) {
            Log.d("Soundcloud", e.getMessage());
        }
    }
}
