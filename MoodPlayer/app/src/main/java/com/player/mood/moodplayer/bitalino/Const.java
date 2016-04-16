package com.player.mood.moodplayer.bitalino;

import com.player.mood.moodplayer.MainActivity;
import com.player.mood.moodplayer.bitalino.deviceandroid.BitalinoAndroidDevice;

/**
 * This class holds some constants for proper and customized
 * configuration of the bitalino device.
 *
 * @author jdc
 * @date 2016.04.08
 */
public class Const {
    public static final int DEFAULT_FREQ = 1000;
    public static final int N_FRAMES = 200;
    public static int MUSCLE_MEAN = 500;
    public static int MUSCLE_PICK_MIN = 0;
    public static int MUSCLE_PICK_MAX = 0;
    public static final int DELAY = 60;
    public static int CALIBRATION_COUNTER = 0;

    public static final int CALIBRATION_CYCLES = 50;

    public static final double MUSCLE_PICK_MIN_PER_DEVIATION = 0.35;
    public static final double MUSCLE_PICK_MAX_PER_DEVIATION = 0.15;

    // Bitalino environment configuration
    public static float GLOBAL_ENERGY;
    private static int PREVIOUS_MEAN_EDA=-1;
    private static int EDA_PICK = 900;
    private static int COUNTER_EDA=0;
    private static int EDA_ACCUMULATOR=0;
    public static String CURRENT_SONG;
}
