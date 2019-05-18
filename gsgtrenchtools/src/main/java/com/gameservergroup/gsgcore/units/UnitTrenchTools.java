package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.GSGTrenchTools;
import com.gameservergroup.gsgcore.enums.TrenchMessages;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.objs.TrenchTool;
import com.gameservergroup.gsgcore.utils.Utils;
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

public class UnitTrenchTools extends Unit {

    private EnumSet<Material> trayMaterials, shovelMaterials, blackListedMaterials;

    @Override
    public void setup() {
        this.trayMaterials = Utils.parseStringListAsEnumSet(GSGTrenchTools.getInstance().getConfig().getStringList("tray-materials"));
        this.shovelMaterials = Utils.parseStringListAsEnumSet(GSGTrenchTools.getInstance().getConfig().getStringList("shovel-materials"));
        this.blackListedMaterials = Utils.parseStringListAsEnumSet(GSGTrenchTools.getInstance().getConfig().getStringList("blacklisted-materials"));

        ConfigurationSection section = GSGTrenchTools.getInstance().getConfig().getConfigurationSection("tools");
        for (String key : section.getKeys(false)) {
            TrenchTool trenchTool = new TrenchTool(key, section.getConfigurationSection(key + ".item"), section.getInt(key + ".radius"), section.getBoolean(key + ".traymode"), section.getBoolean(key + ".omni-tool"));
            trenchTool.setItemEdit(() -> trenchTool.buildItemStack(false));
            trenchTool.setBreakEventConsumer(event -> {
                if (GSG_CORE.canBuild(event.getPlayer(), event.getBlock())) {
                    removeBlocksInRadius(event.getPlayer(), event.getBlock().getLocation(), trenchTool.getRadius() / 2, trenchTool.getToolTrayMode(event.getPlayer().getItemInHand()));
                } else {
                    event.getPlayer().sendMessage(TrenchMessages.CANT_BREAK.toString());
                    event.setCancelled(true);
                }
            }).setInteractEventConsumer(event -> {
                Player player = event.getPlayer();
                ItemStack hand = player.getItemInHand();
                if (player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    if (trenchTool.isTrayMode()) {
                        final boolean toolTrayMode = trenchTool.getToolTrayMode(hand);
                        /*ItemStack itemStack = player.getItemInHand().clone();
                        ItemMeta meta = itemStack.getItemMeta();
                        List<String> trayModeLore = trenchTool.getTrayModeLore(!toolTrayMode);
                        meta.setLore(trayModeLore);
                        itemStack.setItemMeta(meta);*/
                        player.setItemInHand(trenchTool.setToolTrayMode(ItemStackBuilder.of(player.getItemInHand())
                                .setLore(trenchTool.getTrayModeLore(!toolTrayMode))
                                .build(), !toolTrayMode));
                        //player.updateInventory();
                    }
                } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && trenchTool.isOmniTool() && event.getClickedBlock() != null) {
                    final boolean pickaxe = isPickaxe(hand);
                    if (GSGTrenchTools.getInstance().getMcMMOIntegration() != null) {
                        if (pickaxe) {
                            if (GSGTrenchTools.getInstance().getMcMMOIntegration().isSuperBreakerEnabled(player)) {
                                player.sendMessage(TrenchMessages.YOU_CANT_HAVE_SUPER_BREAKER_ENABLED.toString());
                                event.setCancelled(true);
                                return;
                            }
                        } else if (GSGTrenchTools.getInstance().getMcMMOIntegration().isGigaDrillEnabled(player)) {
                            player.sendMessage(TrenchMessages.YOU_CANT_HAVE_GIGADRILL_ENABLED.toString());
                            event.setCancelled(true);
                            return;
                        }
                    }

                    Material material = event.getClickedBlock().getType();

                    // TODO: Make this look nicer
                    if (pickaxe) {
                        if (shovelMaterials.contains(material)) {
                            final short durability = hand.getDurability();
                            hand.setType(getTranslatedMaterial(hand));
                            if (!hand.getItemMeta().spigot().isUnbreakable()) {
                                hand.setDurability(durability);
                            }
                        }
                    } else if (!shovelMaterials.contains(material)) {
                        final short durability = hand.getDurability();
                        hand.setType(getTranslatedMaterial(hand));
                        if (!hand.getItemMeta().spigot().isUnbreakable()) {
                            hand.setDurability(durability);
                        }
                    }
                }
            });
        }
    }

    private Material getTranslatedMaterial(ItemStack itemStack) {
        try {
            return Material.valueOf(itemStack.getType().name().split("_")[0] + "_" + (isPickaxe(itemStack) ? "SPADE" : "PICKAXE"));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Please report error to LockedThread.", ex);
        }
    }

    private boolean isPickaxe(ItemStack itemStack) {
        return itemStack.getType() == Material.DIAMOND_PICKAXE ||
                itemStack.getType() == Material.GOLD_PICKAXE ||
                itemStack.getType() == Material.IRON_PICKAXE ||
                itemStack.getType() == Material.STONE_PICKAXE ||
                itemStack.getType() == Material.WOOD_PICKAXE;
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
                    if (!blackListedMaterials.contains(block.getType()) && GSG_CORE.canBuild(player, block)) {
                        if (trayMode) {
                            if (trayMaterials.contains(block.getType())) {
                                if (amounts != null) {
                                    amounts.computeIfPresent(block.getType(), (material, integer) -> integer + 1);
                                    amounts.putIfAbsent(block.getType(), 1);
                                }
                                block.setTypeIdAndData(0, (byte) 0, true);
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
