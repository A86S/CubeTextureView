package edu.a45.cubetextureview.utils;

/**
 * Created by w1 on 12/11/2014.
 */

/**
 * Handle cube drag configuration
 */
public class DragConfig {
    /**
     * threshold before consider touch as drag in pixel
     */
    public final int threshold = 5;
    /**
     * pixel to theta multiplier in y direction
     */
    public float yMultiplier = 0.4f;
    /**
     * pixel to theta multiplier in x direction
     */
    public float xMultiplier = 0.4f;
    /**
     * flag set to true if config already calibrated
     */
    public boolean calibrated = false;

    /**
     * calibrate config
     * @param width width of view
     * @param height height of view
     */
    public void calibrate(int width, int height){
        if(height > 0) yMultiplier = 90f/height;
        if(width>0) xMultiplier = 90f/width;
        calibrated = true;
    }
}
