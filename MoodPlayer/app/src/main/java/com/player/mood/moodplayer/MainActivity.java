package com.player.mood.moodplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

                String macAdd = macAddress.getText().toString();
                boolean isValid = isMacAddressValid(macAdd);

                if(isValid) {
                    /*Start MoodPlayer*/
                    Intent i = new Intent(MainActivity.this, SelectModeActivity.class);
                    startActivity(i);
                    MAC_ADDRESS = macAdd;
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Mac Address!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}
