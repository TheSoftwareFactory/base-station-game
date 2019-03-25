package com.example.base_station_game.sampling.structs;

public class BatteryDetails {
    private String batteryCharger;
    private String batteryHealth;
    private double batteryVoltage;
    private double batteryTemperature;
    private String batteryTechnology;
    private double batteryCapacity;

    public String getBatteryCharger() {
        return batteryCharger;
    }

    public void setBatteryCharger(String batteryCharger) {
        this.batteryCharger = batteryCharger;
    }

    public String getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(String batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getBatteryTemperature() {
        return batteryTemperature;
    }

    public void setBatteryTemperature(double batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    public String getBatteryTechnology() {
        return batteryTechnology;
    }

    public void setBatteryTechnology(String batteryTechnology) {
        this.batteryTechnology = batteryTechnology;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }
}
