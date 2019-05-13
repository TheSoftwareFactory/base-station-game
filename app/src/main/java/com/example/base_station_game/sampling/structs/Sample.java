package com.example.base_station_game.sampling.structs;

import java.io.Serializable;

public class Sample implements Serializable {
    private double timestamp;
    private BatteryDetails batteryDetails;
    private double batteryLevel;
    private String batteryState;
    private CpuStatus cpuStatus;
    private String timeZone;
    private int memoryUser;
    private int memoryFree;
    private int memoryActive;
    private int memoryInactive;
    private CellDetails cellDetails;

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
                .append(timestamp)
                .append("\n")
                .append(timeZone)
                .append("\n")
                .append(batteryDetails.toString())
                .append("\n")
                .append(batteryLevel)
                .append("\n")
                .append(batteryState)
                .append("\n")
                .append(cpuStatus)
                .append("\n")
                .append(memoryUser)
                .append("\n")
                .append(memoryFree)
                .append("\n")
                .append(memoryActive)
                .append("\n")
                .append(memoryInactive)
                .append("\n")
                .append(cellDetails);
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

    public int getMemoryUser() {
        return memoryUser;
    }

    public void setMemoryUser(int memoryUser) {
        this.memoryUser = memoryUser;
    }

    public int getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(int memoryFree) {
        this.memoryFree = memoryFree;
    }

    public int getMemoryActive() {
        return memoryActive;
    }

    public void setMemoryActive(int memoryActive) {
        this.memoryActive = memoryActive;
    }

    public int getMemoryInactive() {
        return memoryInactive;
    }

    public void setMemoryInactive(int memoryInactive) {
        this.memoryInactive = memoryInactive;
    }

    public void setCellDetails(CellDetails cellDetails) {
        this.cellDetails = cellDetails;
    }

    public CellDetails getCellDetails() {
        return cellDetails;
    }
}