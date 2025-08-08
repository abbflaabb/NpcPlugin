package com.Relxwe.npcPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinFetcher {
    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SESSION_SERVER_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final Map<String, SkinData> skinCache = new HashMap<>();

    public static class SkinData {
        private final String texture;
        private final String signature;

        public SkinData(String texture, String signature) {
            this.texture = texture;
            this.signature = signature;
        }

        public String getTexture() {
            return texture;
        }

        public String getSignature() {
            return signature;
        }
    }

    public static SkinData getSkinData(String playerName) {
        if (skinCache.containsKey(playerName)) {
            return skinCache.get(playerName);
        }

        try {
            // Get UUID from player name
            String uuid = getUUIDFromName(playerName);
            if (uuid == null) {
                return getDefaultSkin();
            }

            // Get skin data from UUID
            SkinData skinData = getSkinDataFromUUID(uuid);
            if (skinData != null) {
                skinCache.put(playerName, skinData);
                return skinData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getDefaultSkin();
    }

    private static String getUUIDFromName(String playerName) throws IOException {
        URL url = new URL(MOJANG_API_URL + playerName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        return json.get("id").getAsString();
    }

    private static SkinData getSkinDataFromUUID(String uuid) throws IOException {
        URL url = new URL(SESSION_SERVER_URL + uuid + "?unsigned=false");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();

        String texture = properties.get("value").getAsString();
        String signature = properties.has("signature") ? properties.get("signature").getAsString() : "";

        return new SkinData(texture, signature);
    }

    private static SkinData getDefaultSkin() {
        // Default Steve skin texture (base64 encoded)
        String defaultTexture = "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzY5NzI3MywKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NTc5Nzc5YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpeGVsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZiYWNhNjlkZjEwZjQ5NzMwZjJmNGY3YmE2MzVkYTQ0OWY3YjI5YzI5ZjI5YzI5ZjI5YzI5ZjI5YzI5ZjI5YzI5IgogICAgfQogIH0KfQ==";
        return new SkinData(defaultTexture, "");
    }

    public static void clearCache() {
        skinCache.clear();
    }
}

