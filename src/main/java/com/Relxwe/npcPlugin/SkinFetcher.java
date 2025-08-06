package com.Relxwe.npcPlugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class SkinFetcher {

    public static Property fetchSkin(String playerName) {
        try {
            // Step 1: Get UUID from Mojang API
            URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection uuidConnection = (HttpURLConnection) uuidURL.openConnection();
            uuidConnection.setReadTimeout(5000);
            uuidConnection.setConnectTimeout(5000);

            Scanner uuidScanner = new Scanner(uuidConnection.getInputStream());
            String uuidResponse = uuidScanner.nextLine();
            uuidScanner.close();

            if (!uuidResponse.contains("id")) return null;

            String uuidString = uuidResponse.split("\"id\":\"")[1].split("\"")[0];
            UUID uuid = fromTrimmed(uuidString);

            // Step 2: Get skin data from Mojang session server
            URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection skinConnection = (HttpURLConnection) skinURL.openConnection();
            skinConnection.setReadTimeout(5000);
            skinConnection.setConnectTimeout(5000);

            Scanner skinScanner = new Scanner(skinConnection.getInputStream());
            String skinResponse = skinScanner.useDelimiter("\\A").next();
            skinScanner.close();

            String value = skinResponse.split("\"value\":\"")[1].split("\"")[0];
            String signature = skinResponse.split("\"signature\":\"")[1].split("\"")[0];

            return new Property("textures", value, signature);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Converts trimmed UUID string to UUID object (without dashes)
    private static UUID fromTrimmed(String trimmedUUID) {
        return UUID.fromString(trimmedUUID.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
        ));
    }
}
