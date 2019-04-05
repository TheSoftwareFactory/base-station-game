package com.example.base_station_game.sampling.structs;

import java.io.Serializable;
import java.util.List;

public class CpuStatus implements Serializable {
    private double cpuUsage;
    private double uptime;
    private double sleeptime;
    private List<Long> currentFrequencies;
    private List<Long> minFrequencies;
    private List<Long> maxFrequencies;

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(double sleepTime) {
        this.sleeptime = sleepTime;
    }

    public List<Long> getCurrentFrequencies() {
        return currentFrequencies;
    }

    public void setCurrentFrequencies(List<Long> currentFrequencies) {
        this.currentFrequencies = currentFrequencies;
    }

    public List<Long> getMinFrequencies() {
        return minFrequencies;
    }

    public void setMinFrequencies(List<Long> minFrequencies) {
        this.minFrequencies = minFrequencies;
    }

    public List<Long> getMaxFrequencies() {
        return maxFrequencies;
    }

    public void setMaxFrequencies(List<Long> maxFrequencies) {
        this.maxFrequencies = maxFrequencies;
    }

    public double getUptime() {
        return uptime;
    }

    public void setUptime(double upTime) {
        this.uptime = upTime;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string
                .append(cpuUsage)
                .append("\n")
                .append(uptime)
                .append("\n")
                .append(sleeptime)
                .append("\n");
        for (long i : currentFrequencies) {
            string
                    .append(i)
                    .append(" ");
        }
        string.append("\n");
        for (long i : minFrequencies) {
            string
                    .append(i)
                    .append(" ");
        }
        string.append("\n");
        for (long i : maxFrequencies) {
            string
                    .append(i)
                    .append(" ");
        }

        return string.toString();
    }
}
