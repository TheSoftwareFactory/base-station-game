package com.example.base_station_game.sampling;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.example.base_station_game.sampling.structs.BatteryDetails;
import com.example.base_station_game.sampling.structs.Sample;
import com.example.base_station_game.sampling.utils.BatteryUtils;

/**
 * Created by Jonatan Hamberg on 2.2.2017.
 */
public class Sampler {
    private static String TAG = Sampler.class.getSimpleName();

//    public static boolean sample(Context context, String trigger){
//        Logger.d(Constants.SF, "Sample called by " + trigger + " " +
//                "in process " + ProcessUtil.getCurrentProcessName(context));
//
//        PrefsManager.MultiPrefs preferences = PrefsManager.getPreferences(context);
//        if(preferences.getString(Keys.registeredUUID, null) == null){
//            Logger.i(TAG, "Not registered yet, skipping");
//            return false;
//        }
//
//        boolean success = false;
//        SampleDB db = SampleDB.getInstance(context);
//        long monthAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30);
//        long lastSampleTime =  preferences.getLong(Keys.lastSampleTimestamp, monthAgo);
//
//        Intent batteryIntent = SamplingLibrary.getLastBatteryIntent(context);
//        Sample lastSample = db.getLastSample(context);
//        if(checkIdentical(context, batteryIntent, lastSample)){
//            Logger.d(TAG, "Pre-check failed, sample would be essentially identical");
//            return false;
//        }
//
//        Sample sample = constructSample(context, batteryIntent, trigger, lastSampleTime, true);
//        if(sample != null){
//            long id = db.putSample(sample);
//            Logger.i(TAG, "Stored sample " + id + " for " + trigger + ":\n" + sample.toString());
//            preferences.edit().putLong(Keys.lastSampleTimestamp, System.currentTimeMillis()).commit();
//            success = true;
//        }
//        int sampleCount = SampleDB.getInstance(context).countSamples();
//        if(sampleCount >= Constants.SAMPLES_MAX_BACKLOG /* 250 */) {
//            CaratApplication.postSamplesNotification(sampleCount);
//        }
//        return success;
//    }
//
//    private static boolean checkIdentical(Context context, Intent batteryIntent, Sample last){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//
//        // Create a dummy sample with less overhead
//        Sample dummy = new Sample();
//        dummy.setTimestamp(System.currentTimeMillis()/1000.0);
//        dummy.setBatteryLevel(BatteryUtils.getBatteryLevel(batteryIntent)/100.0);
//        dummy.setBatteryState(getBatteryStatusString(prefs, batteryIntent));
//        dummy.setTimeZone(SamplingLibrary.getTimeZone(context));
//        dummy.setBatteryDetails(getBatteryDetails(context, batteryIntent));
//
//        return isEssentiallyIdentical(dummy, last);
//    }
//
//    public static Sample constructSample(Context context, Intent batteryIntent, String trigger, long lastSampleTime, boolean resetDistance){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SystemLoadPoint load1 = SamplingLibrary.getSystemLoad();
//
//        Sample sample = new Sample();
//        sample.setUuId(prefs.getString(Keys.registeredUUID, null));
//        sample.setTriggeredBy(trigger);
//
//        sample.setBatteryLevel(BatteryUtils.getBatteryLevel(batteryIntent)/100.0);
//        sample.setBatteryDetails(getBatteryDetails(context, batteryIntent));
//        sample.setBatteryState(getBatteryStatusString(prefs, batteryIntent));
//
//        sample.setTimestamp(System.currentTimeMillis()/1000.0);
//        sample.setPiList(SamplingLibrary.getRunningProcesses(context, lastSampleTime, true));
//        sample.setScreenBrightness(SamplingLibrary.getScreenBrightness(context));
//        sample.setLocationProviders(SamplingLibrary.getEnabledLocationProviders(context));
//        sample.setDistanceTraveled(SamplingLibrary.getDistanceTraveled(context, resetDistance));
//        sample.setNetworkStatus(SamplingLibrary.getNetworkStatusForSample(context));
//        sample.setNetworkDetails(constructNetworkDetails(context));
//
//        sample.setStorageDetails(SamplingLibrary.getStorageDetails());
//        sample.setSettings(constructSettings(context));
//        sample.setDeveloperMode(SamplingLibrary.isDeveloperModeOn(context));
//        sample.setUnknownSources(SamplingLibrary.allowUnknownSources(context));
//        sample.setScreenOn(SamplingLibrary.isScreenOn(context));
//        sample.setTimeZone(SamplingLibrary.getTimeZone(context));
//        sample.setCountryCode(SamplingLibrary.getCountryCode(context));
//        sample.setExtra(SamplingLibrary.getExtras(context));
//        sample.setUsageStatsEnabled(SamplingLibrary.isUsageAccessGranted(context));
//        sample.setLightIdleEnabled(PowerUtils.isLightDoze(context));
//        sample.setDeepIdleEnabled(PowerUtils.isDeepDoze(context));
//        sample.setThermalZones(FsUtils.THERMAL.getThermalZones());
//        sample.setThermalZoneNames(FsUtils.THERMAL.getThermalZoneNames());
//
//        int[] memoryInfo = SamplingLibrary.readMeminfo();
//        if(memoryInfo.length == 4){
//            sample.setMemoryUser(memoryInfo[0]);
//            sample.setMemoryFree(memoryInfo[1]);
//            sample.setMemoryActive(memoryInfo[2]);
//            sample.setMemoryInactive(memoryInfo[3]);
//        }
//
//        // Take as much time between cpu measurements as possible
//        // TODO: Use the time between samples instead.
//        SystemLoadPoint load2 = SamplingLibrary.getSystemLoad();
//        sample.setCpuStatus(constructCpuStatus(load1, load2));
//        return sample;
//    }
//
    private static Sample createSample(Context context, Intent batteryIntent) {
        Sample sample = new Sample();
        sample.setBatteryDetails(getBatteryDetails(context, batteryIntent));
        sample.setBatteryLevel(BatteryUtils.getBatteryLevel(batteryIntent));
        sample.setBatteryState(getBatteryStatusString(batteryIntent));
        return sample;
    }

    private static BatteryDetails getBatteryDetails(Context context, Intent intent){
        if(intent == null) return null;

        BatteryDetails details = new BatteryDetails();
        details.setBatteryHealth(getHealthString(intent));
        details.setBatteryTechnology(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
        details.setBatteryTemperature(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0);
        details.setBatteryVoltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000.0);
        details.setBatteryCharger(getChargerString(intent));
        details.setBatteryCapacity(SamplingLibrary.getBatteryCapacity(context));
        return details;
    }

    private static String getBatteryStatusString(Intent intent){
        if(intent != null){
            int id = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            String status;
            switch(id){
                case BatteryManager.BATTERY_STATUS_CHARGING: status =  "Charging"; break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING: status = "Discharging"; break;
                case BatteryManager.BATTERY_STATUS_FULL: status = "Full"; break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING: status = "Not charging"; break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN: status = "Unknown"; break;
                default: status = null;
            }
            return status;
        }
        return null;
    }
//
//    private static NetworkDetails constructNetworkDetails(Context context){
//        NetworkDetails details = new NetworkDetails();
//        details.setNetworkType(SamplingLibrary.getNetworkType(context));
//        details.setMobileNetworkType(SamplingLibrary.getMobileNetworkType(context));
//
//        details.setMobileDataStatus(SamplingLibrary.getDataState(context));
//        details.setMobileDataActivity(SamplingLibrary.getDataActivity(context));
//        details.setRoamingEnabled(SamplingLibrary.getRoamingStatus(context));
//
//        details.setWifiStatus(SamplingLibrary.getWifiState(context));
//        details.setWifiSignalStrength(SamplingLibrary.getWifiSignalStrength(context));
//        details.setWifiLinkSpeed(SamplingLibrary.getWifiLinkSpeed(context));
//        details.setWifiApStatus(SamplingLibrary.getWifiHotspotState(context));
//
//        details.setNetworkOperator(SamplingLibrary.getNetworkOperator(context));
//        details.setSimOperator(SamplingLibrary.getSIMOperator(context));
//        details.setMcc(SamplingLibrary.getMcc(context));
//        details.setMnc(SamplingLibrary.getMnc(context));
//        details.setMobileSignalStrength(SamplingLibrary.getMobileSignalStrength(context));
//        return details;
//    }
//
//    private static CpuStatus constructCpuStatus(SystemLoadPoint load1, SystemLoadPoint load2){
//        CpuStatus cpuStatus = new CpuStatus();
//        if(load1 == null || load2 == null){
//            Logger.d(TAG, "CPU usage was null when constructing sample!"); // Typical on O
//            if(Build.VERSION.SDK_INT >= 26 /* Hardcoded Android O */){
//                cpuStatus.setCpuUsage(SamplingLibrary.getCpuUsageEstimate());
//            }
//        } else {
//            cpuStatus.setCpuUsage(SamplingLibrary.getCpuUsage(load1, load2));
//        }
//        cpuStatus.setUptime(SamplingLibrary.getUptime());
//        cpuStatus.setSleeptime(SamplingLibrary.getSleepTime());
//
//        cpuStatus.setCurrentFrequencies(FsUtils.CPU.getCurrentFrequencies());
//        cpuStatus.setMinFrequencies(FsUtils.CPU.getMinimumFrequencies());
//        cpuStatus.setMaxFrequencies(FsUtils.CPU.getMaximumFrequencies());
//        return cpuStatus;
//    }
//
//    private static Settings constructSettings(Context context){
//        Settings settings = new Settings();
//        settings.setBluetoothEnabled(SamplingLibrary.getBluetoothEnabled());
//        settings.setPowersaverEnabled(PowerUtils.isPowerSaving(context));
//        settings.setRotationEnabled(SamplingLibrary.getRotationEnabled(context));
//        return settings;
//    }
//
    private static String getChargerString(Intent intent){
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        switch(plugged){
            case BatteryManager.BATTERY_PLUGGED_AC: return "ac";
            case BatteryManager.BATTERY_PLUGGED_USB: return "usb";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: return "wireless";
            default: return "unplugged";
        }
    }
//
    private static String getHealthString(Intent intent){
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        switch(health){
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over voltage";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN: return "Unknown";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Unspecified failure";
            default: return "Unknown";
        }
    }
//
//    public static boolean isEssentiallyIdentical(Sample s1, Sample s2){
//        if(s2 != null){
//            if(s1.getTimestamp() - s2.getTimestamp() < Constants.DUPLICATE_INTERVAL){
//
//                Logger.d(TAG, "Sample was triggered within five minutes " +
//                        "(diff: " + (s1.getTimestamp() - s2.getTimestamp()) + "s) " +
//                        "of the last one. Checking if it's a duplicate..");
//
//                BatteryDetails bd1 = s1.getBatteryDetails();
//                BatteryDetails bd2 = s2.getBatteryDetails();
//                boolean isDuplicate =
//                        s1.getBatteryLevel() == s2.getBatteryLevel()
//                                && 	s1.getBatteryState().equals(s2.getBatteryState())
//                                && 	s1.getTimeZone().equals(s2.getTimeZone())
//                                // && 	bd1.getBatteryTemperature()  - bd2.getBatteryTemperature()
//                                && 	bd1.getBatteryCapacity() == bd2.getBatteryCapacity()
//                                // This seems to change too often
//                                // && 	bd1.getBatteryVoltage() == bd2.getBatteryVoltage()
//                                && 	bd1.getBatteryTechnology().equals(bd2.getBatteryTechnology())
//                                && 	bd1.getBatteryCharger().equals(bd2.getBatteryCharger())
//                                && 	bd1.getBatteryHealth().equals(bd2.getBatteryHealth());
//
//                if(s1.isSetPiList() && !Util.isNullOrEmpty(s1.getPiList())){
//                    for(ProcessInfo pi : s1.getPiList()){
//                        if(pi.importance.equals(Constants.IMPORTANCE_INSTALLED)
//                                || pi.importance.equals(Constants.IMPORTANCE_DISABLED)
//                                || pi.importance.equals(Constants.IMPORTANCE_REPLACED)
//                                || pi.importance.equals(Constants.IMPORTANCE_UNINSTALLED)){
//                            Logger.i(TAG, "Installs changes, cannot discard as duplicate");
//                            return false;
//                        }
//                    }
//                }
//                return isDuplicate;
//            }
//        }
//        return false;
//    }

}
