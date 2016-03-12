package com.player.mood.moodplayer.funcmode;

/**
 *
 * @author Cortez
 */
public class BeastMode implements FuncMode {

    // Energy: [0,1]
    @Override
    public double songSelection(double oldEnergyLevel, double newEnergyLevel, float energy){
        double delta = (newEnergyLevel - oldEnergyLevel);
        if (delta>= 10) {
            if(energy*1.10 < 1) {
                return energy*1.10;
            } else return 1;
        }
        else if (delta<=-10) {
            if(energy*1.20 < 1) {
                return energy * 1.20;
            } else return 1;
        }
        else {
            if(energy*1.05 < 1) {
                return energy * 1.05;
            } else return 1;
        }
    }
}
