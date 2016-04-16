package com.player.mood.moodplayer;

import android.app.ProgressDialog;
import android.media.AudioManager;
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

import com.player.mood.moodplayer.bitalino.Const;
import com.player.mood.moodplayer.bitalino.comm.BITalinoFrame;
import com.player.mood.moodplayer.bitalino.deviceandroid.BitalinoAndroidDevice;
import com.player.mood.moodplayer.funcmode.BeastMode;
import com.player.mood.moodplayer.funcmode.FuncMode;
import com.player.mood.moodplayer.funcmode.RelaxedMode;
import com.player.mood.moodplayer.soundcloud.SoundCloudLocationURLsAsyncFetcher;
import com.player.mood.moodplayer.soundcloud.SoundCloudPlayListAsyncFetcher;
import com.player.mood.moodplayer.soundcloud.SoundcloudAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * This class is a mp3 activity_player controlled by biosignals.
 *
 * The activity_player activity for moodplayer is its core functionality. Is controls music flow,
 * Bitalino sensors' events and exterior api resources management as echonest metainfo and
 * soundcloud tracks.
 *
 * @author jdc
 * @date 2016.03.11 v1 - Base version made on techathon.
 * @date 2016.04.15 v2 - Soundcloud integration, code organization, more accurate EMG controls.
 */
public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    // Layout
    private LayoutInflater inflater;
    private ImageButton playStopButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private TextView songTitle;
    private ProgressDialog progress;

    // Media Player & Logic
    private MediaPlayer mp;
    private boolean isMusicPlaying;
    private boolean isMediaPlayerPrepared;
    private int currentPosition;
    private FuncMode functioningMode;

    // Resources
    public static TracksManager tracksManager;

    // Soundcloud settings
    public static String SONG_SOURCE = "Soundcloud";

    public static final String SOUNDCLOUD_PLAYLIST = "215410113";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Set layout
        progress = ProgressDialog.show(this, "MoodPlayer is preparing everything for you",
                "Loading...", true);
        songTitle = (TextView) findViewById(R.id.song_title);
        songTitle.setText(getResources().getString(R.string.default_player_title));

        // Set activity_player mode
        if (SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.BEAST_MODE)) {
            this.functioningMode = new BeastMode();
            Const.GLOBAL_ENERGY = 0.6f;
        } else if (SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.RELAX_MODE)) {
            this.functioningMode = new RelaxedMode();
            Const.GLOBAL_ENERGY = 0.3f;
        }

        // Media Player controls
        playStopButton = (ImageButton) findViewById(R.id.btnPlay);
        playStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MoodPlayer
                try {
                    if (!isMusicPlaying) {
                        playStopButton.setImageResource(R.drawable.btn_pause);

                        if (currentPosition != -1) {
                            mp.seekTo(currentPosition);
                            currentPosition = -1;
                            mp.start();
                        } else if (!isMediaPlayerPrepared) {
                            try {
                                mp.setDataSource(tracksManager.getCurrentSongURL());
                                mp.prepare();
                                songTitle.setText(tracksManager.getCurrentTrackTitle());
                            } catch (IOException e) {
                                Log.i("MediaPlayer", e.getMessage());
                            } catch (Exception e) {
                                Log.i("MediaPlayer", e.getMessage());
                            }
                        }

                        isMusicPlaying = true;

                    } else {
                        playStopButton.setImageResource(R.drawable.btn_play);
                        mp.pause();
                        isMusicPlaying = false;
                        currentPosition = mp.getCurrentPosition();
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
        nextButton = (ImageButton) findViewById(R.id.btnNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to next Song
                try {
                    changeTrack("NEXT");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        previousButton = (ImageButton) findViewById(R.id.btnPrevious);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to previous Song
                try {
                    changeTrack("PREVIOUS");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Set up and start all components
        // Set song energy EchoNest API
        /*songsResIds = new HashMap<String,Integer>();

        ArrayList<Integer> list = new ArrayList<Integer>();
        Field[] fields = R.raw.class.getFields();
        for (Field f : fields) {
            try {
                list.add(f.getInt(null));
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {}
        }*/

        // Get songs meta-info
        /*values = new HashMap<>();
        Thread fillEnergy = new FillEnergyThread(values,getApplicationContext(),list);
        fillEnergy.start();
        try {
            fillEnergy.join();
        } catch (Exception e){}

        inflater = this.getLayoutInflater();*/

        // setUpAndStartBitalino();

        // Check if user has already downloaded songs & energy
        setUpPlayerResources();
        setUpMediaPlayer();
        isMusicPlaying = false;
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        isMediaPlayerPrepared = true;
        mp.start(); // first start is needed before play
    }

    public void setUpPlayerResources() {
        SoundcloudAPI api = new SoundcloudAPI(getResources().getString(R.string.soundcloud_client_id));

        // 1. Get songs from soundcloud and respective energy value for each song from echonest
        SoundCloudPlayListAsyncFetcher playlist = new SoundCloudPlayListAsyncFetcher(api);
        try {
            tracksManager = playlist.execute(SOUNDCLOUD_PLAYLIST).get();
        } catch (InterruptedException e) {
            Log.i("Soundcloud", e.getMessage());
        } catch (ExecutionException e) {
            Log.i("Soundcloud", e.getMessage());
        }

        // 2. For each song with a energy value, get its locationURL
        SoundCloudLocationURLsAsyncFetcher lUrls = new SoundCloudLocationURLsAsyncFetcher(api);
        try {
            HashMap<String, String> urls = lUrls.execute(tracksManager.getTracksIds()).get();
            if (urls != null) {
                tracksManager.setLocationURLs(urls);
                // Unmark to see tracks info in Log
                Log.i("SoundCloud", tracksManager.toString());
            }
        } catch (InterruptedException e) {
            Log.i("Soundcloud", e.getMessage());
        } catch (ExecutionException e) {
            Log.i("Soundcloud", e.getMessage());
        }

    }

    public void setUpMediaPlayer() {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        isMediaPlayerPrepared = false;
        currentPosition = -1;
        progress.dismiss();
    }

    public void changeTrack(String direction) throws IOException {
        mp.stop();
        setUpMediaPlayer();
        switch (direction) {
            case "NEXT":
                mp.setDataSource(tracksManager.getNextSongURL());
                break;
            case "PREVIOUS":
                mp.setDataSource(tracksManager.getPreviousSongURL());
                break;
            case "EDA_ACTION":
                mp.setDataSource(tracksManager.getCurrentSongURL());
                break;
        }
        mp.prepare();
        songTitle.setText(tracksManager.getCurrentTrackTitle());
        isMediaPlayerPrepared = true;
        isMusicPlaying = true;

    }

    /*-----------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------- BiTALINO (START) --------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/

    public void setUpAndStartBitalino() {
        try {
            Toast.makeText(getApplicationContext(), "Starting Sensors...", Toast.LENGTH_SHORT).show();
            startRecording();
        } catch (InterruptedException e) {
            Log.i("BiTalino", e.getMessage());
        }
    }

    /**
     * EMG & EDA Recording
     *
     */
    private void startRecording() throws InterruptedException {
        int[] actChan = new int[]{0,3,4};
        final BitalinoAndroidDevice bdev = new BitalinoAndroidDevice(MainActivity.MAC_ADDRESS);
        bdev.connect(Const.DEFAULT_FREQ, actChan);
        bdev.start();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                BITalinoFrame[] dataFrame = bdev.read(Const.N_FRAMES);
                int sumMuscle = 0;
                int sumZ = 0;
                // int sumEDA = 0;

                // ''O segredo esta na massa''
                for (int i = 0; i < (dataFrame.length - 1); i++) {
                    // Log.i("BITALINO", dataFrame[i].stringAnalogDigital());
                    sumMuscle += Math.abs(dataFrame[i+1].getAnalog(0) - dataFrame[i].getAnalog(0));
                    Log.i("FRAME",
                            String.valueOf(dataFrame[i+1].getAnalog(3))+
                            String.valueOf(dataFrame[i+1].getAnalog(4))
                            );
                    // sumZ += Math.abs(dataFrame[i+1].getAnalog(3) - dataFrame[i].getAnalog(3));
                    // sumEDA += Math.abs(dataFrame[i].getAnalog(1));
                }
                int meanMuscle = sumMuscle / (dataFrame.length - 1);
                // final int meanEDA = sumEDA / dataFrame.length;
                // EDA_ACCUMULATOR+=meanEDA;

                // Log.i("BITALINO MEAN MUSCLE", String.valueOf(meanMuscle));
                /*Log.i("BITALINO MEAN EDA", String.valueOf(meanEDA));
                if(PREVIOUS_MEAN_EDA==-1) {
                    PREVIOUS_MEAN_EDA = meanEDA;
                }*/
                // Log.i("EIXO Z", String.valueOf(sumZ));

                // Stop/Play Logic
                if (meanMuscle > Const.MUSCLE_PICK_MIN && meanMuscle < Const.MUSCLE_PICK_MAX) {
                    try {
                        if (!isMusicPlaying) {
                            playStopButton.setImageResource(R.drawable.btn_pause);

                            if (currentPosition != -1) {
                                mp.seekTo(currentPosition);
                                currentPosition = -1;
                                mp.start();
                            } else if (!isMediaPlayerPrepared) {
                                try {
                                    mp.setDataSource(tracksManager.getCurrentSongURL());
                                    mp.prepare();
                                    songTitle.setText(tracksManager.getCurrentTrackTitle());
                                } catch (IOException e) {
                                    Log.i("MediaPlayer", e.getMessage());
                                } catch (Exception e) {
                                    Log.i("MediaPlayer", e.getMessage());
                                }
                            }

                            isMusicPlaying = true;

                        } else {
                            playStopButton.setImageResource(R.drawable.btn_play);
                            mp.pause();
                            isMusicPlaying = false;
                            currentPosition = mp.getCurrentPosition();
                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                /*if(COUNTER_EDA==150) {
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
                } else COUNTER_EDA++;*/

                h.postDelayed(this, Const.DELAY);
            }
        }, 1);

    }

    public void findTheRightSong(double newEnergy) {
        double maxDiffNegative = 0;
        double minDiffPositive = 1000;
        String song = Const.CURRENT_SONG;

        if (SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.BEAST_MODE)) {
            for (Map.Entry<String, Double> entry : tracksManager.getTracksEnergy().entrySet()) {
                if (!entry.getKey().equals(Const.CURRENT_SONG)) {
                    double diff = newEnergy - entry.getValue();
                    if (diff < minDiffPositive) {
                        minDiffPositive = diff;
                        song = entry.getKey();
                        Log.i("BITALINO BEAST", Const.CURRENT_SONG);
                    }
                }
            }
        } else if (SelectModeActivity.PLAYER_MODE.equals(SelectModeActivity.RELAX_MODE)) {
            for (Map.Entry<String, Double> entry : tracksManager.getTracksEnergy().entrySet()) {
                if (!entry.getKey().equals(Const.CURRENT_SONG)) {
                    double diff = entry.getValue() - newEnergy;
                    if (diff > maxDiffNegative) {
                        maxDiffNegative = diff;
                        song = entry.getKey();
                        Log.i("BITALINO RELAX", Const.CURRENT_SONG);
                    }
                }
            }
        }

        if (!Const.CURRENT_SONG.equals(song)) {
            Const.CURRENT_SONG = song;
            tracksManager.setCurrentTrack(song);
            try {
                changeTrack("EDA_ACTION");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*-----------------------------------------------------------------------------------------------------------*/
    /*------------------------------------- BiTALINO (END) ------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/

}
