package com.player.mood.moodplayer.echonest;

import android.util.Log;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.player.mood.moodplayer.TracksManager;
import com.player.mood.moodplayer.bitalino.Const;
import com.player.mood.moodplayer.soundcloud.SoundcloudAPI;

import java.util.List;
import java.util.Map;

/**
 * An echonest API wrapper to perform some information fetching operations on songs.
 *
 * @author Cortez
 * @date 2016.15.04
 */
public class EchoNestWrapper {

    private static String API_KEY = "WNCVO3LISNWOOHCDS";

    public static Double getTempo(String artistName, String title) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI(API_KEY);
        SongParams p = new SongParams();
        p.setArtist(artistName);
        p.setTitle(title);
        p.setResults(1);
        p.includeAudioSummary();

        List<Song> songs = echoNest.searchSongs(p);
        if (songs.size() > 0) {
            double tempo = songs.get(0).getTempo();
            return Double.valueOf(tempo);
        } else {
            return null;
        }
    }

    public static Double getEnergy(String artistName, String title) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI(API_KEY);
        SongParams p = new SongParams();
        p.setArtist(artistName);
        p.setTitle(title);
        p.setResults(1);
        p.includeAudioSummary();

        List<Song> songs = echoNest.searchSongs(p);
        if (songs.size() > 0) {
            double energy = songs.get(0).getEnergy();
            return Double.valueOf(energy);
        } else {
            return null;
        }
    }
}
