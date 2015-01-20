package edu.a45.cubetextureview.utils;

/**
 * Created by w1 on 12/12/2014.
 */

/**
 * Handle tween of a float value
 */
public class TweenFloat {
    /**
     * the supposed start value
     */
    float start = 0;
    /**
     * target end value
     */
    float end = 0;
    /**
     * parameter @t from 0 to 1 corresponds to @start to @end
     */
    float t = 0;

    /**
     * Tween constructor
     * Tween from @current to @end by assuming it start
     * at @start
     * @param start the supposed start value
     * @param end the future end value
     * @param current the current value
     */
    public TweenFloat(float start, float end, float current){
        this.start = start;
        this.end = end;
        t = (current-start)/(end-start);
    }

    /**
     * step t
     * @param delta_t increment of t
     * @return false on end
     */
    public boolean step(float delta_t){
        t += delta_t;
        if(t>=1.0f){
            t = 1.0f;
            return false;
        }
        return true;
    }

    /**
     * check if tween is done
     * @return true on done
     */
    public boolean isDone(){
        return t>=1f;
    }

    /**
     * get the float value
     * @return float value
     */
    public float getValue(){
        return start + (end-start)*t;
    }
}
