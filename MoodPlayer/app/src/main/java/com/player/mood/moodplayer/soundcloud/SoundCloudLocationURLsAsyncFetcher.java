package com.player.mood.moodplayer.soundcloud;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * This class provides an async task to fetch location URLs
 * for a set of Soundcloud tracks.
 *
 * @author daniel
 * @date 2016.04.15
 */
public class SoundCloudLocationURLsAsyncFetcher extends AsyncTask<String,Void,HashMap<String,String>> {

    private SoundcloudAPI api;

    public SoundCloudLocationURLsAsyncFetcher(SoundcloudAPI api) {
        this.api = api;
    }

    /**
     * Fetch location urls for given Soundcloud tracks, so that
     * other component in the app can play the tracks.
     *
     * @param params, the Soundcloud tracks ids.
     * @return songsLocationUrls a map where the key is the
     * song id and the value is the locationURL for the song
     */
    @Override
    protected HashMap<String,String> doInBackground(String... params) {
        HashMap<String,String> songsLocationUrls = new HashMap<>();

        for(int i=0; i < params.length; i++) {
            try {
                String ilocation = api.getStreamLocation(params[i]);
                songsLocationUrls.put(params[i],ilocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return songsLocationUrls;
    }
}
