package com.player.mood.moodplayer.funcmode;

/**
 * This class encapsulates the algorithm RelaxedMode to choose songs
 * using EDA values.
 *
 * @author Cortez
 * @date 2016.03.11
 */
public class RelaxedMode implements FuncMode{

    // Energy: [0,1]
    @Override
    public double songSelection(double oldEnergyLevel, double newEnergyLevel, float energy){
        double delta = (newEnergyLevel - oldEnergyLevel);
        if (delta>= 10) {
            if(energy*0.90 < 0) {
                return 0;
            } else {
                return energy*0.9;
            }
        }
        else if (delta<=-10) {
            if(energy*1.10 > 1) {
                return 1;
            } else {
                return energy*1.10;
            }
        }
        else return energy;
    }

}