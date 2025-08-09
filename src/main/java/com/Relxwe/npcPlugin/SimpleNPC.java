package com.Relxwe.npcPlugin;

import com.Relxwe.npcPlugin.API.NPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class SimpleNPC implements NPC {

    private final String id;
    private String name;
    private String skinTexture;
    private String skinSignature;
    private net.citizensnpcs.api.npc.NPC citizensNPC;

    public SimpleNPC(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
    }

    public void setSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
    }

    @Override
    public void spawn(World world, Location location) {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        citizensNPC = registry.createNPC(EntityType.PLAYER, name);

        if (skinTexture != null && !skinTexture.isEmpty()) {
            SkinTrait skinTrait = citizensNPC.getTrait(SkinTrait.class);
            if (skinTrait == null) {
                skinTrait = new SkinTrait();
                citizensNPC.addTrait(skinTrait);
            }
            skinTrait.setTexture(skinTexture, skinSignature);
        }

        citizensNPC.spawn(location);
    }

    @Override
    public void despawn() {
        if (citizensNPC != null && citizensNPC.isSpawned()) {
            citizensNPC.despawn();
        }
    }

    @Override
    public net.citizensnpcs.api.npc.NPC getCitizensNPC() {
        return citizensNPC;
    }
}

