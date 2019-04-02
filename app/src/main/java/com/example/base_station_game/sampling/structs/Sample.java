package com.example.base_station_game.sampling.structs;

import java.io.Serializable;

public class Sample implements Serializable {
    private double timestamp;
    private BatteryDetails batteryDetails;
    private double batteryLevel;
    private String batteryState;
    private CpuStatus cpuStatus;
    private String timeZone;

    public BatteryDetails getBatteryDetails() {
        return batteryDetails;
    }

    public void setBatteryDetails(BatteryDetails batteryDetails) {
        this.batteryDetails = batteryDetails;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(String batteryState) {
        this.batteryState = batteryState;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string
                .append(batteryDetails.toString())
                .append("\n")
                .append(batteryLevel)
                .append("\n")
                .append(batteryState)
                .append("\n")
                .append(cpuStatus);
        return string.toString();
    }

    public CpuStatus getCpuStatus() {
        return cpuStatus;
    }

    public void setCpuStatus(CpuStatus cpuStatus) {
        this.cpuStatus = cpuStatus;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}