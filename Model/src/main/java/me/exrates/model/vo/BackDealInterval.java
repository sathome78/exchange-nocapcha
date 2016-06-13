package me.exrates.model.vo;

import me.exrates.model.enums.IntervalType;
import me.exrates.model.exceptions.UnsupportedIntervalFormatException;
import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

/**
 * Created by Valk on 27.04.2016.
 * stores the interval from current DateTime
 * based on string like "5 DAY"
 */
public class BackDealInterval {
    public Integer intervalValue;
    public IntervalType intervalType;

    /*constructor*/
    public BackDealInterval(String intervalString) {
        try {
            this.intervalValue = Integer.valueOf(intervalString.split(" ")[0]);
            this.intervalType = IntervalType.convert(intervalString.split(" ")[1]);
        } catch (UnsupportedIntervalTypeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedIntervalFormatException(intervalString);
        }
    }
    /**/
    public String getInterval(){
        return intervalValue+" "+intervalType;
    }
    /*getters setters*/

}
