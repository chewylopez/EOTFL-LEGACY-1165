package com.chewylopez.echoofthefarlands.datagen;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class WorldFarlandsSettings extends WorldSavedData {

    private static final String FARLANDS_FILE_ID = "farlands";

    private int FARLANDS_POSITION;
    private int FARLANDS_GEN_TYPE;

    private boolean isNew = false;

    public WorldFarlandsSettings() {
        super(FARLANDS_FILE_ID);
        isNew = true;
    }

    public int getFarlandsPosition() {
        return FARLANDS_POSITION;
    }

    public int getFarlandsGenType() {
        return FARLANDS_GEN_TYPE;
    }

    public void setFarlandsPosition(int val) {
        FARLANDS_POSITION = val;
        this.setDirty();
    }

    public void setFarlandsGenType(int val) {
        FARLANDS_GEN_TYPE = val;
        this.setDirty();
    }

    public static WorldFarlandsSettings create() {
        return new WorldFarlandsSettings();
    }

    @Override
    public void load(CompoundNBT tag) {
        if (tag.contains("FarlandsPosition")) FARLANDS_POSITION = tag.getInt("FarlandsPosition");
        if (tag.contains("FarlandsGenType")) FARLANDS_GEN_TYPE = tag.getInt("FarlandsGenType");

        isNew = false;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("FarlandsPosition", FARLANDS_POSITION);
        tag.putInt("FarlandsGenType", FARLANDS_GEN_TYPE);
        return tag;
    }

    public static WorldFarlandsSettings getSettings(ServerWorld level) {
        return level.getDataStorage().computeIfAbsent(WorldFarlandsSettings::create, FARLANDS_FILE_ID);
    }

    public boolean isNewWorld() {
        return isNew;
    }
}
