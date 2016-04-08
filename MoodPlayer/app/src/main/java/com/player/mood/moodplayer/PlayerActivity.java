package com.player.mood.moodplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.player.mood.moodplayer.bitalino.comm.BITalinoFrame;
import com.player.mood.moodplayer.bitalino.deviceandroid.BitalinoAndroidDevice;
import com.player.mood.moodplayer.funcmode.BeastMode;
import com.player.mood.moodplayer.funcmode.FuncMode;
import com.player.mood.moodplayer.funcmode.RelaxedMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by daniel on 11-music_03-2016.
 *
 * This class is a mp3 player controlled by biosignals.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final int N_FRAMES = 100;
    private static final int DEFAULT_FREQ = 1000;
    private static final int MUSCLE_MEAN = 500;
    private static float GLOBAL_ENERGY;
    private static int PREVIOUS_MEAN_EDA=-1;

    private FuncMode functioningMode;

    private static final int MUSCLE_PICK = 150;
    private static int EDA_PICK = 900;

    private static int DELAY = 60;
    private static int COUNTER_EDA=0;
    private static int EDA_ACCUMULATOR=0;

    private static String DEFAULT_MUSIC_TITLE = "Just squeeze your arm!";
    private static String CURRENT_SONG;

    // View
    private ImageButton playStopButton;
    private ImageButton nextButton;
    private ImageButton previousButton;

    private LayoutInflater inflater;

    private MediaPlayer mp;
    private TextView songTitle;

    // Logic
    private boolean isMusicPlaying;
    private int currentPosition;

    // Songs collection
    private HashMap<String,Double> values;

    private HashMap<String,Integer> songsResIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        // Set player mode
        if(SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.BEAST_MODE)) {
            this.functioningMode = new BeastMode();
            GLOBAL_ENERGY = 0.6f;
        } else if(SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.RELAX_MODE)) {
            this.functioningMode = new RelaxedMode();
            GLOBAL_ENERGY = 0.3f;
        }

        // Set song energy EchoNest API
        songsResIds = new HashMap<String,Integer>();

        ArrayList<Integer> list = new ArrayList<Integer>();
        Field[] fields = R.raw.class.getFields();
        for (Field f : fields) {
            try {
                list.add(f.getInt(null));
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {}
        }


        // Get songs meta-info
        values = new HashMap<>();
        Thread fillEnergy = new FillEnergyThread(values,getApplicationContext(),list);
        fillEnergy.start();
        try {
            fillEnergy.join();
        } catch (Exception e){}

        inflater = this.getLayoutInflater();
        setUpBitalino();

        mp = new MediaPlayer();
        isMusicPlaying = false;
        songTitle = (TextView) findViewById(R.id.songTitle);
        // CURRENT_SONG = "Me Gustas Tu";
        mp = MediaPlayer.create(getApplicationContext(), songsResIds.get(CURRENT_SONG));

        playStopButton = (ImageButton) findViewById(R.id.btnPlay);
        playStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
                /*Start MoodPlayer*/
                try{
                    if(!isMusicPlaying) {
                        playStopButton.setImageResource(R.drawable.btn_pause);

                        if(currentPosition!=-1) {
                            // forward or backward to certain seconds
                            mp.seekTo(currentPosition);
                        }
                        mp.start();

                        songTitle.setText(CURRENT_SONG);
                        isMusicPlaying=true;

                    } else {
                        playStopButton.setImageResource(R.drawable.btn_play);
                        mp.pause();
                        songTitle.setText(DEFAULT_MUSIC_TITLE);
                        isMusicPlaying=false;

                        currentPosition = mp.getCurrentPosition();
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUpBitalino() {
        try {
            boolean r = startRecording();
            if(r==true){
                Toast.makeText(getApplicationContext(), "Starting Sensors...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){}
    }

    /**
     * EMG & EDA Recording
     * @return true if Bitalino starts collecting data.
     */
    private boolean startRecording() throws InterruptedException {
        final BitalinoAndroidDevice bdev = new BitalinoAndroidDevice(MainActivity.MAC_ADDRESS);
        int[] actChan = new int[]{0,1};
        bdev.connect(DEFAULT_FREQ, actChan);
        Thread.sleep(DEFAULT_FREQ); // Danger Zone!
        bdev.start();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                BITalinoFrame[] dataFrame = bdev.read(N_FRAMES);
                int sumMuscle = 0;
                int sumEDA = 0;
                for (int i = 0; i < dataFrame.length; i++) {
                    // Log.i("BITALINO", dataFrame[i].stringAnalogDigital());
                    sumMuscle += Math.abs(dataFrame[i].getAnalog(0) - MUSCLE_MEAN);
                    sumEDA += Math.abs(dataFrame[i].getAnalog(1));
                }
                int meanMuscle = sumMuscle / dataFrame.length;
                final int meanEDA = sumEDA / dataFrame.length;
                EDA_ACCUMULATOR+=meanEDA;

                Log.i("BITALINO MEAN MUSCLE", String.valueOf(meanMuscle));
                Log.i("BITALINO MEAN EDA", String.valueOf(meanEDA));
                if(PREVIOUS_MEAN_EDA==-1) {
                    PREVIOUS_MEAN_EDA = meanEDA;
                }

                // Stop/Play Logic
                if (meanMuscle > MUSCLE_PICK) {
                    try {
                        if (!isMusicPlaying) {
                            playStopButton.setImageResource(R.drawable.btn_pause);

                            if (currentPosition != -1) {
                                // forward or backward to certain seconds
                                mp.seekTo(currentPosition);
                            }
                            mp.start();

                            songTitle.setText(CURRENT_SONG);
                            isMusicPlaying = true;

                        } else {
                            playStopButton.setImageResource(R.drawable.btn_play);
                            mp.pause();
                            songTitle.setText(DEFAULT_MUSIC_TITLE);
                            isMusicPlaying = false;

                            currentPosition = mp.getCurrentPosition();
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                if(COUNTER_EDA==150) {
                    EDA_ACCUMULATOR = EDA_ACCUMULATOR / 150;
                    double newEnergy = functioningMode.songSelection(EDA_ACCUMULATOR, PREVIOUS_MEAN_EDA, GLOBAL_ENERGY);
                    GLOBAL_ENERGY = (float)newEnergy;
                    if(isMusicPlaying) {
                        Log.d("ENERGY", "newEnergy: "+newEnergy);
                        findTheRightSong(newEnergy);
                        PREVIOUS_MEAN_EDA = meanEDA;
                        COUNTER_EDA = 0;
                        EDA_ACCUMULATOR = 0;
                    } else {
                        COUNTER_EDA=0; EDA_ACCUMULATOR=0;
                    }
                } else COUNTER_EDA++;

                h.postDelayed(this, DELAY);
            }
        }, 1);

        return true;
    }

    private void changeSong() {
        mp.stop();
        mp = MediaPlayer.create(getApplicationContext(), songsResIds.get(CURRENT_SONG));
        mp.start();
        songTitle.setText(CURRENT_SONG);
        isMusicPlaying = true;
    }

    public void findTheRightSong(double newEnergy) {
        double maxDiffNegative = 0;
        double minDiffPositive = 1000;
        String song=CURRENT_SONG;

        if(SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.BEAST_MODE)) {
            for (Map.Entry<String, Double> entry : values.entrySet()) {
                if (!entry.getKey().equals(CURRENT_SONG)) {
                    double diff = newEnergy - entry.getValue();
                    if(diff < minDiffPositive) {
                        minDiffPositive = diff;
                        song = entry.getKey();
                        Log.i("BITALINO BEAST", CURRENT_SONG);
                    }
                }
            }
        } else if(SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.RELAX_MODE)) {
            for (Map.Entry<String, Double> entry : values.entrySet()) {
                if (!entry.getKey().equals(CURRENT_SONG)) {
                    double diff = entry.getValue() - newEnergy;
                    if(diff > maxDiffNegative) {
                        maxDiffNegative = diff;
                        song = entry.getKey();
                        Log.i("BITALINO RELAX", CURRENT_SONG);
                    }
                }
            }
        }

        if(!CURRENT_SONG.equals(song)) {
            CURRENT_SONG = song;
            changeSong();
        }
    }

    float volume = 1;
    float speed = 0.05f;

    public void FadeOut(float deltaTime) {
        mp.setVolume(volume, volume);
        volume -= speed* deltaTime;
    }

    public void FadeIn(float deltaTime) {
        mp.setVolume(volume, volume);
        volume += speed* deltaTime;
    }

    /*-----------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------- ECHO NEST -----------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/

    public static Double getTempo(String artistName, String title) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI("WNCVO3LISNWOOHCDS");
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

    class FillEnergyThread extends Thread {

        private Map<String,Double> energyValues;
        private List<Integer> fields;
        private Context context;

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
                int diff = (int) Math.abs(tempo - GLOBAL_ENERGY);

                if(diff < min) {
                    min = diff;
                    CURRENT_SONG = songTitle;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
