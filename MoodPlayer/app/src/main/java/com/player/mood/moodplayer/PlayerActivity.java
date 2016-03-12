package com.player.mood.moodplayer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by daniel on 11-music_03-2016.
 *
 * EMG A1 (Port 0)
 * ECG A3 (Port 2)
 */
public class PlayerActivity extends AppCompatActivity {

    // Bitalino default values
    private static final int RECEPTION_FREQ_MAX = 1000;
    private static final int RECEPTION_FREQ_MIN = 1;
    private static final int DEFAULT_RECEPTION_FREQ = 100;
    private static final int SAMPLING_FREQ_MAX = 100;
    private static final int SAMPLING_FREQ_MIN = 1;
    private static final int DEFAULT_SAMPLING_FREQ = 50;
    private static final int DEFAULT_NUMBER_OF_BITS = 12;

    private static final int EMG_MIN_FREQ = 1000;
    private static final int ECG_MIN_FREQ = 1000;

    // ERROR VARIABLES
    private int bpErrorCode = 0;
    private boolean serviceError = false;
    private boolean connectionError = false;


    private static final int N_FRAMES = 100;
    private static final int MUSCLE_MEAN = 500;
    private static int DELAY = 60;

    private static int MAX_MUSIC_INDEX = 2;
    private static int MIN_MUSIC_INDEX = 0;

    private static String DEFAULT_MUSIC_TITLE = "MoodPlayer!";
    private static String MUSIC_TITLE = "Miguel Araújo - Fizz Limão";

    private LayoutInflater inflater;

    private static int[] songsList;

    // View
    private ImageButton playStopButton;
    private ImageButton nextButton;
    private ImageButton previousButton;

    private MediaPlayer mp; // Media Player, that manages audio

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
        setUpBitalino(MainActivity.MAC_ADDRESS);

        // Test musics List
        currentMusic=0;
        currentPosition=-1;
        songsList = new int[10];
        songsList[0] = R.raw.music_01;
        songsList[1] = R.raw.music_02;
        songsList[2] = R.raw.music_03;

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

                        songTitle.setText(MUSIC_TITLE);
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
                songTitle.setText(MUSIC_TITLE);
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
                songTitle.setText(MUSIC_TITLE);
                isMusicPlaying=true;
            }
        });

    }

    public void setUpBitalino(String macAddress) {
        boolean r = startRecordingEMG();
        if(r==true){
            Toast.makeText(getApplicationContext(), "Starting EMG...", Toast.LENGTH_SHORT).show();
        }

        r = startRecordingECG();
        if(r==true){
            Toast.makeText(getApplicationContext(), "Starting ECG...", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean startRecordingECG() {
        final BitalinoAndroidDevice bdev = new BitalinoAndroidDevice(MainActivity.MAC_ADDRESS);
        int[] actChan = new int[]{1};
        bdev.connect(ECG_MIN_FREQ, actChan);
        bdev.start();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                BITalinoFrame[] dataFrame = bdev.read(N_FRAMES);
                int sum = 0;
                for (int i = 0; i < dataFrame.length; i++) {
                    // Log.i("BITALINO", dataFrame[i].stringAnalogDigital());
                    sum += Math.abs(dataFrame[i].getAnalog(0) - MUSCLE_MEAN);
                }
                int mean = sum / dataFrame.length;
                Log.i("MEAN!!!!!!", String.valueOf(mean));

                if (mean > 40) {
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

                            songTitle.setText(MUSIC_TITLE);
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
                h.postDelayed(this, DELAY);
            }
        }, 1);

        return true;
    }

    private boolean startRecordingEMG() {
        final BitalinoAndroidDevice bdev = new BitalinoAndroidDevice(MainActivity.MAC_ADDRESS);
        int[] actChan = new int[]{0};
        bdev.connect(EMG_MIN_FREQ, actChan);
        bdev.start();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                BITalinoFrame[] dataFrame = bdev.read(N_FRAMES);
                int sum = 0;
                for (int i = 0; i < dataFrame.length; i++) {
                    // Log.i("BITALINO", dataFrame[i].stringAnalogDigital());
                    sum += Math.abs(dataFrame[i].getAnalog(0) - MUSCLE_MEAN);
                }
                int mean = sum / dataFrame.length;
                Log.i("MEAN!!!!!!", String.valueOf(mean));

                if (mean > 40) {
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

                            songTitle.setText(MUSIC_TITLE);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
