package com.Relxwe.npcPlugin.utils;

import org.bukkit.Bukkit;

public class VersionUtils {
    private static String version;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isVersionSupported() {
        // Add supported versions here
        return version.startsWith("v1_8_R") ||
                version.startsWith("v1_9_R") ||
                version.startsWith("v1_10_R") ||
                version.startsWith("v1_11_R") ||
                version.startsWith("v1_12_R") ||
                version.startsWith("v1_13_R") ||
                version.startsWith("v1_14_R") ||
                version.startsWith("v1_15_R") ||
                version.startsWith("v1_16_R") ||
                version.startsWith("v1_17_R") ||
                version.startsWith("v1_18_R") ||
                version.startsWith("v1_19_R") ||
                version.startsWith("v1_20_R") ||
                version.startsWith("v1_21_R");
    }

    public static int getMajorVersion() {
        String[] parts = version.split("_");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return 8; // Default to 1.8
            }
        }
        return 8;
    }

    public static int getMinorVersion() {
        String[] parts = version.split("_");
        if (parts.length >= 3) {
            try {
                return Integer.parseInt(parts[2].substring(1)); // Remove 'R' prefix
            } catch (NumberFormatException e) {
                return 1; // Default to R1
            }
        }
        return 1;
    }
}

