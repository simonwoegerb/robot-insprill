package me.Insprill.RI.storage;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.custom.codedred.AdvertisingCooldown;
import me.Insprill.RI.featues.Suggestions;

public class SaveAll {

    public static void saveAll() {

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/serverPrefixes.ser"), ServerSettings.serverPrefix);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/serverModRoles.ser"), ServerSettings.serverModRoles);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/suggestionChannels.ser"), ServerSettings.suggestionChannels);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/suggestionMinChars.ser"), ServerSettings.suggestionMinChars);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/antiSpam.ser"), ServerSettings.antiSpam);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/auditLog.ser"), ServerSettings.auditLog);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/global/suggestions.ser"), Suggestions.suggestions);

        HashMaps.saveHashMap(RobotInsprill.formatPath("data/codedred/cooldown.ser"), AdvertisingCooldown.cooldown);

    }

}