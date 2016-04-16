package com.player.mood.moodplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.player.mood.moodplayer.bitalino.Const;
import com.player.mood.moodplayer.bitalino.comm.BITalinoFrame;
import com.player.mood.moodplayer.bitalino.deviceandroid.BitalinoAndroidDevice;

/**
 * Created by daniel on 12-03-2016.
 */
public class SelectModeActivity extends AppCompatActivity {

    public static final String BEAST_MODE = "SET_PLAYER_BEAST_MODE";
    public static final String RELAX_MODE = "SET_PLAYER_RELAX_MODE";

    public static String PLAYER_MODE;

    private Button beastButton;
    private Button relaxButton;
    private Button calibrate;

    private boolean calibrateSuccessfull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_mode_activity);

        beastButton = (Button) findViewById(R.id.play_beast_mode);
        beastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!calibrateSuccessfull) {
                    Toast.makeText(getApplicationContext(), "Please calibrate sensors before proceed.", Toast.LENGTH_LONG).show();
                } else {
                    PLAYER_MODE = BEAST_MODE;
                    Intent i = new Intent(SelectModeActivity.this, PlayerActivity.class);
                    startActivity(i);
                }
            }
        });

        relaxButton = (Button) findViewById(R.id.play_relax_mode);
        relaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!calibrateSuccessfull) {
                    Toast.makeText(getApplicationContext(), "Please calibrate sensors before proceed.", Toast.LENGTH_LONG).show();
                } else {
                    PLAYER_MODE = RELAX_MODE;
                    Intent i = new Intent(SelectModeActivity.this, PlayerActivity.class);
                    startActivity(i);
                }
            }
        });

        calibrate = (Button) findViewById(R.id.calibrate);
        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Start calibrating...", Toast.LENGTH_SHORT).show();
                try {
                    startCalibration();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        calibrateSuccessfull = false;
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


    /**
     * EMG & EDA Calibration
     * Different users and different sensors set up require, mean and pick calibration
     * for more efficient event handling
     * @return true if Bitalino calibrates successfully
     */
    public boolean startCalibration() throws InterruptedException {
        int[] actChan = new int[]{0};
        final BitalinoAndroidDevice bdev = new BitalinoAndroidDevice(MainActivity.MAC_ADDRESS);
        bdev.connect(Const.DEFAULT_FREQ, actChan);
        bdev.start();

        final Handler h = new Handler();

        h.postDelayed(new Runnable() {
            public void run() {
                BITalinoFrame[] dataFrame = bdev.read(Const.N_FRAMES);
                int sumMuscle = 0;
                for (int i = 0; i < (dataFrame.length - 1); i++) {
                    sumMuscle += Math.abs(dataFrame[i+1].getAnalog(0) - dataFrame[i].getAnalog(0));
                }
                int meanMuscle = sumMuscle / (dataFrame.length - 1);
                Const.MUSCLE_MEAN = meanMuscle;
                Const.CALIBRATION_COUNTER++;

                Log.i("CALIBRATE MEAN MUSCLE", String.valueOf(Const.MUSCLE_MEAN));

                if(Const.MUSCLE_MEAN > Const.MUSCLE_PICK_MAX) {
                    Const.MUSCLE_PICK_MAX = Const.MUSCLE_MEAN;
                }

                if(Const.CALIBRATION_COUNTER < Const.CALIBRATION_CYCLES) {
                    h.postDelayed(this, Const.DELAY);
                } else {
                    Thread.interrupted();

                    Const.MUSCLE_PICK_MIN = Const.MUSCLE_PICK_MAX - (int)(Const.MUSCLE_PICK_MIN_PER_DEVIATION * Const.MUSCLE_PICK_MAX);
                    Const.MUSCLE_PICK_MAX = Const.MUSCLE_PICK_MAX - (int)(Const.MUSCLE_PICK_MAX_PER_DEVIATION * Const.MUSCLE_PICK_MAX);

                    Log.i("FINAL MEAN MUSCLE", String.valueOf(Const.MUSCLE_MEAN));
                    Log.i("FINAL PICK INTERVAL", "["+String.valueOf(Const.MUSCLE_PICK_MIN)+","+String.valueOf(Const.MUSCLE_PICK_MAX)+"]");

                    if(Const.MUSCLE_PICK_MIN <= ( Const.MUSCLE_MEAN * 1.10) ) {
                        Toast.makeText(getApplicationContext(), "Sorry, calibration failed, please try again :(", Toast.LENGTH_LONG).show();
                        calibrateSuccessfull=false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Calibration success!", Toast.LENGTH_SHORT).show();
                        calibrateSuccessfull=true;
                        beastButton.setClickable(true);
                        relaxButton.setClickable(true);
                    }

                    bdev.stop();
                    return;
                }
            }
        }, 1);

        return true;
    }
}