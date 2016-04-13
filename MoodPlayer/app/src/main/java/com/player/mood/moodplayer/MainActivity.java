package com.player.mood.moodplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static String MAC_ADDRESS;

    // Control Buttons
    private Button playButton;
    private EditText macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        macAddress = (EditText) findViewById(R.id.mac_address);

        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UNMARK FOR CONNECT TO BITALINO
                String macAdd = macAddress.getText().toString();
                boolean isValid = isMacAddressValid(macAdd);

                if(isValid) {
                    // Start MoodPlayer
                    Intent i = new Intent(MainActivity.this, SelectModeActivity.class);
                    startActivity(i);
                    MAC_ADDRESS = macAdd;
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Mac Address!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        SoundCloudManager job = new SoundCloudManager();
        job.execute("");

        // job has res with sound cloud content



    }

    /**
     *
     * @param macAddres, mac address for bitalino board
     * @return true if it is a valid MAC, false otherwise
     */
    public boolean isMacAddressValid(String macAddres) {
        boolean validated = true;

        Pattern p = Pattern.compile("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$");
        Matcher m = p.matcher(macAddres);

        if (macAddress == null || macAddress.equals("") || !m.find()) {
            validated = false;
        }

        return validated;
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

    private class SoundCloudManager extends AsyncTask<String, Void, String> implements MediaPlayer.OnPreparedListener {

        public JSONObject res;
        public MediaPlayer mMediaPlayer;

        @Override
        protected String doInBackground(String[] params) {
            // do above Server call here
            String clientId = "a2a51bf7a7f1451dbb021d0c5b6672c9";
            Log.i("SoundCloud", "Trying GET...");

            URL url = null;
            try {
                url = new URL("http://api.soundcloud.com/tracks/13158665?client_id=" + clientId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            //connection.setDoOutput(true);
            InputStream content = null;
            try {
                content = (InputStream) connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            JSONParser parser = new JSONParser();
            JSONObject obj = new JSONObject();
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = in.readLine()) != null) {
                    // System.out.println(line);
                    // Log.i("SoundCloud", line);
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("SoundCloud", sb.toString());
            try {
                obj = (JSONObject) parser.parse(sb.toString());
                Log.i("SoundCloud", obj.toString());
            } catch (Exception e) {}
            this.res = obj;

            String stream ="nadia";
            try {
                stream = (String)res.get("stream_url");
            } catch (Exception e) {
                Log.i("SoundCloud", e.toString());
            }
            Log.i("SoundCloud", stream);

            // Music MTF!!

            // GET PARA OBTER O LOCATION!!!!
            // stream = stream + "?client_id=" + clientId; ...


            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try{
                mMediaPlayer.setDataSource("https://cf-media.sndcdn.com/1AfP5wDGGFRK.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMUFmUDV3REdHRlJLLjEyOC5tcDMiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE0NjA1NDc0NjV9fX1dfQ__&Signature=Zm9aKeqeB8n3Z0FvulT9jaunouN0o6h~NGFXryEwThsL0rl-Iygu7m4x7qacq0i1pq1g3CIhmPAQv3va68Ydob8GP-YgnVkRHpCuQbv5ZpE48Oi1Ah6mu9Mswa00sgV5Xwabr8SWG0XspQ~LKPouoNgguJXQw3kGDoKTy~UJ6oTygivdRnyjvvKFoNxyDDfYsG5ufYxhrLgNQTA7l7KKr7WwxTQS7tsen~o1WlXwI03FqFp7B8LjQPFl4r6l~F5hHxVU0BpaPh~ER0adKmRsWUK3WKTY4XGHfxEdxTRfdhqqiykP6Fj2SiyPR4k-JapTXfogZpupNBZsteE53TKL1A__&Key-Pair-Id=APKAJAGZ7VMH2PFPW6UQ");
                mMediaPlayer.prepare();
            }catch (IllegalArgumentException e){
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }


            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    }
}
