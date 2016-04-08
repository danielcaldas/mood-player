package com.player.mood.moodplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daniel on 12-03-2016.
 */
public class SelectModeActivity extends AppCompatActivity {

    public static final String BEAST_MODE = "SET_PLAYER_BEAST_MODE";
    public static final String RELAX_MODE = "SET_PLAYER_RELAX_MODE";

    public static String PLAYER_MODE;

    private Button beastButton;
    private Button relaxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode_activity);

        beastButton = (Button) findViewById(R.id.play_beast_mode);
        beastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLAYER_MODE = BEAST_MODE;
                Intent i = new Intent(SelectModeActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });

        relaxButton = (Button) findViewById(R.id.play_relax_mode);
        relaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLAYER_MODE = RELAX_MODE;
                Intent i = new Intent(SelectModeActivity.this, PlayerActivity.class);
                startActivity(i);
            }
        });

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