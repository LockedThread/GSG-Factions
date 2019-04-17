package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.GSGTrenchTools;
import com.gameservergroup.gsgcore.enums.TrenchMessages;
import com.gameservergroup.gsgcore.objs.TrenchTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UnitTrenchTools extends Unit {

    private static final Supplier<EnumSet<Material>> ENUM_SET_SUPPLIER = () -> EnumSet.noneOf(Material.class);

    private EnumSet<Material> trayMaterials, shovelMaterials, blackListedMaterials;

    @Override
    public void setup() {
        this.trayMaterials = GSGTrenchTools.getInstance().getConfig().getStringList("tray-materials")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toCollection(ENUM_SET_SUPPLIER));
        this.shovelMaterials = GSGTrenchTools.getInstance().getConfig().getStringList("shovel-materials")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toCollection(ENUM_SET_SUPPLIER));
        this.blackListedMaterials = GSGTrenchTools.getInstance().getConfig().getStringList("blacklisted-materials")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toCollection(ENUM_SET_SUPPLIER));

        ConfigurationSection section = GSGTrenchTools.getInstance().getConfig().getConfigurationSection("tools");
        for (String key : section.getKeys(false)) {
            TrenchTool trenchTool = new TrenchTool(key, section.getConfigurationSection(key + ".item"), section.getInt(key + ".radius"), section.getBoolean(key + ".traypick"), section.getBoolean(key + ".omni-tool"));
            trenchTool.setItemEdit(() -> trenchTool.buildItemStack(false));
            trenchTool.setBreakEventConsumer(event -> {
                if (GSG_CORE.canBuild(event.getPlayer(), event.getBlock())) {
                    removeBlocksInRadius(event.getPlayer(), event.getBlock().getLocation(), trenchTool.getRadius() / 2, trenchTool.getToolTrayMode(event.getPlayer().getItemInHand()));
                } else {
                    event.getPlayer().sendMessage(TrenchMessages.CANT_BREAK.toString());
                    event.setCancelled(true);
                }
            }).setInteractEventConsumer(event -> {
                if (event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    event.getPlayer().setItemInHand(trenchTool.buildItemStack(!trenchTool.getToolTrayMode(event.getPlayer().getItemInHand())));
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && trenchTool.isOmniTool() && event.getClickedBlock() != null) {
                    Material material = event.getClickedBlock().getType();

                    // TODO: Make this look nicer
                    if (isPickaxe(event.getPlayer().getItemInHand())) {
                        if (shovelMaterials.contains(material)) {
                            event.getPlayer().getItemInHand().setType(getTranslatedMaterial(event.getPlayer().getItemInHand()));
                        }
                    } else if (!shovelMaterials.contains(material)) {
                        event.getPlayer().getItemInHand().setType(getTranslatedMaterial(event.getPlayer().getItemInHand()));
                    }
                }
            });
        }
    }

    private Material getTranslatedMaterial(ItemStack itemStack) {
        return Material.valueOf(itemStack.getType().name().split("_")[0] + "_" + (isPickaxe(itemStack) ? "SPADE" : "PICKAXE"));
    }

    private boolean isPickaxe(ItemStack itemStack) {
        return itemStack.getType() == Material.DIAMOND_PICKAXE ||
                itemStack.getType() == Material.GOLD_PICKAXE ||
                itemStack.getType() == Material.IRON_PICKAXE ||
                itemStack.getType() == Material.STONE_PICKAXE ||
                itemStack.getType() == Material.WOOD_PICKAXE;
    }

    private boolean isShovel(ItemStack itemStack) {
        return itemStack.getType() == Material.DIAMOND_SPADE ||
                itemStack.getType() == Material.GOLD_SPADE ||
                itemStack.getType() == Material.IRON_SPADE ||
                itemStack.getType() == Material.STONE_SPADE ||
                itemStack.getType() == Material.WOOD_SPADE;
    }

    private void removeBlocksInRadius(Player player, Location center, int radius, boolean trayMode) {
        EnumMap<Material, Integer> amounts = null;
        if (GSGTrenchTools.getInstance().getConfig().getBoolean("drop-items")) {
            amounts = new EnumMap<>(Material.class);
        }

        for (int x = center.getBlockX() - radius; x <= radius + center.getBlockX(); x++) {
            for (int y = center.getBlockY() - radius; y <= radius + center.getBlockY(); y++) {
                for (int z = center.getBlockZ() - radius; z <= radius + center.getBlockZ(); z++) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (blackListedMaterials.contains(block.getType()) && GSG_CORE.canBuild(player, block)) {
                        if (trayMode) {
                            if (trayMaterials.contains(block.getType())) {
                                if (amounts != null) {
                                    amounts.computeIfPresent(block.getType(), (material, integer) -> integer + 1);
                                    amounts.putIfAbsent(block.getType(), 1);
                                }
                                block.setTypeIdAndData(0, (byte) 0, false);
                            }
                            continue;
                        }
                        if (amounts != null) {
                            amounts.computeIfPresent(block.getType(), (material, integer) -> integer + 1);
                            amounts.putIfAbsent(block.getType(), 1);
                        }
                        block.setTypeIdAndData(0, (byte) 0, false);
                    }
                }
            }
        }
        if (amounts != null) {
            for (Map.Entry<Material, Integer> entry : amounts.entrySet()) {
                if (entry.getValue() > 64) {
                    int remainder = entry.getValue() % 64;
                    int stacks = entry.getValue() / 64;
                    for (; stacks > 0; stacks--) {
                        center.getWorld().dropItemNaturally(center, new ItemStack(entry.getKey(), 64));
                    }
                    if (remainder > 0) {
                        center.getWorld().dropItemNaturally(center, new ItemStack(entry.getKey(), remainder));
                    }
                    continue;
                }
                center.getWorld().dropItemNaturally(center, new ItemStack(entry.getKey(), entry.getValue()));
            }
        }
    }
}
