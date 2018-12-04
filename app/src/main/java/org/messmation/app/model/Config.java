package org.messmation.app.model;

public class Config {

    public void setBreakFastTimings(String breakFastTimings) {
        this.breakFastTimings = breakFastTimings;
    }

    public void setLunchTimings(String lunchTimings) {
        this.lunchTimings = lunchTimings;
    }

    public void setEveningTeaTimings(String eveningTeaTimings) {
        this.eveningTeaTimings = eveningTeaTimings;
    }

    public void setDinnerTimings(String dinnerTimings) {
        this.dinnerTimings = dinnerTimings;
    }

    private String breakFastTimings;
    private String lunchTimings;
    private String eveningTeaTimings;
    private String dinnerTimings;


    public String getBreakFastTimings() {
        return breakFastTimings;
    }

    public String getLunchTimings() {
        return lunchTimings;
    }

    public String getEveningTeaTimings() {
        return eveningTeaTimings;
    }

    public String getDinnerTimings() {
        return dinnerTimings;
    }


}
