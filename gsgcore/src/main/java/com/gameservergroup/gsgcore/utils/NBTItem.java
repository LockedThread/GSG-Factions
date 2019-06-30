package com.gameservergroup.gsgcore.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;

public class NBTItem {

    private net.minecraft.server.v1_8_R3.ItemStack nmsItemStack;
    private NBTTagCompound rootCompound;

    public NBTItem(ItemStack startItemStack) {
        Objects.requireNonNull(startItemStack, "startItemStack can't be null in NBTItem Constructor");
        this.nmsItemStack = CraftItemStack.asNMSCopy(startItemStack);
        this.rootCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
    }

    public Set<String> getKeys() {
        return rootCompound.c();
    }

    public boolean hasKey(String key) {
        return rootCompound.hasKey(key);
    }

    public boolean getBoolean(String key) {
        return rootCompound.getBoolean(key);
    }

    public String getString(String key) {
        return rootCompound.getString(key);
    }

    public double getDouble(String key) {
        return rootCompound.getDouble(key);
    }

    public int getInt(String key) {
        return rootCompound.getInt(key);
    }

    public long getLong(String key) {
        return rootCompound.getLong(key);
    }

    public short getShort(String key) {
        return rootCompound.getShort(key);
    }

    public short getByte(String key) {
        return rootCompound.getByte(key);
    }

    public NBTItem set(String key, Object value) {
        PrimitiveClass primitiveClass = PrimitiveClass.get(value.getClass());
        Objects.requireNonNull(primitiveClass, "Unable to find primitive class \"" + value.toString() + "\"");

        switch (primitiveClass) {
            case BOOLEAN:
                rootCompound.setBoolean(key, (boolean) value);
                break;
            case BYTE:
                rootCompound.setByte(key, (byte) value);
                break;
            case DOUBLE:
                rootCompound.setDouble(key, (double) value);
                break;
            case INT:
                rootCompound.setInt(key, (int) value);
                break;
            case LONG:
                rootCompound.setLong(key, (long) value);
                break;
            case STRING:
                rootCompound.setString(key, (String) value);
                break;
            case SHORT:
                rootCompound.setShort(key, (short) value);
                break;
        }
        nmsItemStack.setTag(rootCompound);
        return this;
    }

    public ItemStack buildItemStack() {
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
}
