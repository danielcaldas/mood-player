package com.player.mood.moodplayer.funcmode;

/**
 * This interface hides and unifies the access to the EDA song selection
 * algorithms.
 *
 * @author Cortez
 * @date 2016.03.11
 */
public interface FuncMode {
    double songSelection(double oldEnergyLevel, double newEnergyLevel, float energy);
}
