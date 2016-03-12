package com.player.mood.moodplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by daniel on 11-music_03-2016.
 */
public class PlayerActivity extends AppCompatActivity {

    private static int MAX_MUSIC_INDEX = 2;
    private static int MIN_MUSIC_INDEX = 0;

    private static String DEFAULT_MUSIC_TITLE = "MoodPlayer!";
    private static String MUSIC_TITLE = "Miguel Araújo - Fizz Limão";

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
        /* TOOLBAR??
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
