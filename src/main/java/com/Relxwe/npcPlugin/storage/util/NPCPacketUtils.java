package com.Relxwe.npcPlugin.storage.util;

import com.Relxwe.npcPlugin.SimpleNPC;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCPacketUtils {

    public static void spawnNPCPacket(Player viewer, SimpleNPC npc) {
        EntityPlayer npcEntity = (EntityPlayer) npc.getEntity();

        if (npcEntity == null) return;

        PlayerConnection connection = ((CraftPlayer) viewer).getHandle().playerConnection;

        // 1. Add player info (to show skin)
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                npcEntity
        );
        connection.sendPacket(addInfo);

        // 2. Spawn the NPC in the world
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(npcEntity);
        connection.sendPacket(spawn);

        // 3. Head rotation
        float yaw = npcEntity.yaw;
        float pitch = npcEntity.pitch;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npcEntity, (byte) (yaw * 256.0F / 360.0F)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npcEntity.getId(), (byte) (yaw * 256.0F / 360.0F), (byte) (pitch * 256.0F / 360.0F), true));

        // 4. Remove player info (to hide from tablist)
        new Thread(() -> {
            try {
                Thread.sleep(500);
                PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                        npcEntity
                );
                connection.sendPacket(removeInfo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
