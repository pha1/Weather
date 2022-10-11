package edu.uncc.weather;

import java.util.Date;

public class Forecast {

    String dateAndTime;

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    double temp;
    double temp_max;
    double temp_min;
    int humidity;

    public String getIcon() {
        return icon;
    }

    String description;
    String icon;

    public Forecast() {}
}
