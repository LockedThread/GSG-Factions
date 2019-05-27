package com.gameservergroup.gsgoutpost.items;

import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

public class SerializableItem {

    private Material material;
    private String name;
    private List<String> lore;
    private boolean glowing;

    public SerializableItem(Material material, String name, List<String> lore, boolean glowing) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.glowing = glowing;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableItem that = (SerializableItem) o;

        return glowing == that.glowing && material == that.material && Objects.equals(name, that.name) && Objects.equals(lore, that.lore);

    }

    @Override
    public int hashCode() {
        int result = material != null ? material.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lore != null ? lore.hashCode() : 0);
        result = 31 * result + (glowing ? 1 : 0);
        return result;
    }
}
