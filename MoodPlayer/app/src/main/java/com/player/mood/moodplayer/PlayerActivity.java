package com.player.mood.moodplayer;

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

import com.player.mood.moodplayer.bitalino.comm.BITalinoFrame;
import com.player.mood.moodplayer.bitalino.deviceandroid.BitalinoAndroidDevice;

import java.util.ArrayList;


/**
 * Created by daniel on 11-music_03-2016.
 *
 * This class is a mp3 player controlled by biosignals.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final int N_FRAMES = 100;
    private static final int DEFAULT_FREQ = 1000;
    private static final int MUSCLE_MEAN = 500;


    private static final int MUSCLE_PICK = 150;
    private static int EDA_PICK = 900;

    private static int DELAY = 60;

    private static int MAX_MUSIC_INDEX = 2;
    private static int MIN_MUSIC_INDEX = 0;

    private static String DEFAULT_MUSIC_TITLE = "Just squeeze your arm!";

    private static ArrayList<String> songsTitles;
    private static int[] songsList;

    // View
    private ImageButton playStopButton;
    private ImageButton nextButton;
    private ImageButton previousButton;

    private LayoutInflater inflater;

    private MediaPlayer mp;
    private TextView songTitle;

    // Logic
    private boolean isMusicPlaying;
    private int currentMusic;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        inflater = this.getLayoutInflater();
        setUpBitalino();

        // Test musics List
        currentMusic=0;
        currentPosition=-1;
        songsList = new int[10];
        songsList[0] = R.raw.music_01;
        songsList[1] = R.raw.music_02;
        songsList[2] = R.raw.music_03;

        songsTitles = new ArrayList<>();
        songsTitles.add("Miguel Araújo - Fizz Limão");
        songsTitles.add("Pendulum - Crush");
        songsTitles.add("The Lumineers - Stuborn Love");

        mp = new MediaPlayer();
        isMusicPlaying = false;
        songTitle = (TextView) findViewById(R.id.songTitle);
        mp = MediaPlayer.create(getApplicationContext(), songsList[currentMusic]);

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

                        /*Implement here
                        * - Time of Song
                        * - Start progress Bar
                        * */

                        songTitle.setText(songsTitles.get(currentMusic));
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

        nextButton = (ImageButton) findViewById(R.id.btnNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Go to next Song*/
                mp.stop();
                nextSong();
                mp = MediaPlayer.create(getApplicationContext(), songsList[currentMusic]);
                mp.start();
                songTitle.setText(songsTitles.get(currentMusic));
                isMusicPlaying=true;
            }
        });

        previousButton = (ImageButton) findViewById(R.id.btnPrevious);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Go to previous Song*/
                mp.stop();
                previousSong();
                mp = MediaPlayer.create(getApplicationContext(), songsList[currentMusic]);
                mp.start();
                songTitle.setText(songsTitles.get(currentMusic));
                isMusicPlaying=true;
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
                int meanEDA = sumEDA / dataFrame.length;
                Log.i("BITALINO MEAN MUSCLE", String.valueOf(meanMuscle));
                Log.i("BITALINO MEAN EDA", String.valueOf(meanEDA));

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

                            /*Implement here
                            * - Time of Song
                            * - Start progress Bar
                            * */

                            songTitle.setText(songsTitles.get(currentMusic));
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

                // Song manager logic
                if(meanEDA > EDA_PICK) {
                    EDA_PICK+=10;
                    /*Go to next Song*/
                    mp.stop();
                    nextSong();
                    mp = MediaPlayer.create(getApplicationContext(), songsList[currentMusic]);
                    mp.start();
                    songTitle.setText(songsTitles.get(currentMusic));
                    isMusicPlaying=true;
                }

                h.postDelayed(this, DELAY);
            }
        }, 1);

        return true;
    }

    private void nextSong() {
        if(currentMusic < MAX_MUSIC_INDEX) {
            currentMusic++;
        } else {
            currentMusic=0;
        }
    }

    private void previousSong() {
        if(currentMusic != MIN_MUSIC_INDEX) {
            currentMusic--;
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
