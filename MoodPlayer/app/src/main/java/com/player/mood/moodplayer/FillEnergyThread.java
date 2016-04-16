package com.player.mood.moodplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.player.mood.moodplayer.bitalino.Const;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 15-04-2016.
 */
class FillEnergyThread extends Thread {

    private Map<String,Double> energyValues;
    private List<Integer> fields;
    private Context context;

    private HashMap<String,Integer> songsResIds;

    public FillEnergyThread(Map<String,Double> BPMs, Context context, List<Integer> fields){
        this.energyValues = BPMs;
        this.context = context;
        this.fields = fields;
    }

    public void run (){
        double tempo;
        int min = 10000;
        for(int i : fields) {
            Resources res = this.context.getResources();
            AssetFileDescriptor afd = res.openRawResourceFd(i);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            Log.d("ECHO", albumName);
            Log.d("ECHO", songTitle);

            try {
                tempo = getEnergy(albumName, songTitle);
                songsResIds.put(songTitle,i);
            } catch (EchoNestException e) {
                Log.d("TAG", e.getMessage());
                tempo = 0;
            }
            energyValues.put(songTitle, tempo);

            // Find the first song to play with!
            /**
             * Simple Algorithm finds best song to start with
             */
            int diff = (int) Math.abs(tempo - Const.GLOBAL_ENERGY);

            if(diff < min) {
                min = diff;
                Const.CURRENT_SONG = songTitle;
            }
                /*-----------------------------------------------*/

        }
        for (Map.Entry<String,Double> entry: energyValues.entrySet())
            Log.d("tos", entry.getKey()+" - "+entry.getValue());
    }

    public Double getEnergy(String artistName, String title) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI("WNCVO3LISNWOOHCDS");
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

    public synchronized Map<String,Double> getEnergyValues() {
        return energyValues;
    }
}
