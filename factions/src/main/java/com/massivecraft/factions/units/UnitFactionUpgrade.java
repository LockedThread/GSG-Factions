package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.triple.Triple;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.SoundBuilder;
import com.google.common.base.Joiner;
import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgrade;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.logging.Level;

public class UnitFactionUpgrade extends Unit {

    public static ItemStack fillItemStack;
    public static Triple<Sound, Float, Float> cantAfford, upgraded;

    @Override
    public void setup() {
        if (P.p.getConfig().getBoolean("faction-upgrades.enabled")) {
            P.p.log("Enabling FactionUpgrades...");

            upgraded = SoundBuilder.of(P.p.getConfig().getConfigurationSection("faction-upgrades.gui.sounds.upgraded")).build();

            cantAfford = SoundBuilder.of(P.p.getConfig().getConfigurationSection("faction-upgrades.gui.sounds.cant-afford")).build();

            fillItemStack = P.p.getConfig().getBoolean("faction-upgrades.gui.fill.enchanted") ? ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(P.p.getConfig().getString("faction-upgrades.gui.fill.color")))
                    .setDisplayName(" ")
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    .addEnchant(Enchantment.DURABILITY, 1)
                    .build() : ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(P.p.getConfig().getString("faction-upgrades.gui.fill.color")))
                    .setDisplayName(" ")
                    .build();

            ConfigurationSection root = P.p.getConfig().getConfigurationSection("faction-upgrades");
            ConfigurationSection types = root.getConfigurationSection("types");
            for (String key : types.getKeys(false)) {
                FactionUpgrade factionUpgrade = FactionUpgrade.parse(key);
                if (factionUpgrade != null) {
                    factionUpgrade.loadValues(types.getConfigurationSection(key));
                } else {
                    throw new RuntimeException("Unable to find FactionUpgrade called \"" + key + "\". Upgrades: " + Joiner.on(", ")
                            .skipNulls()
                            .join(Arrays.stream(FactionUpgrade.values())
                                    .map(upgrade -> upgrade.name().toLowerCase().replace("_", "-"))
                                    .toArray(String[]::new)));
                }
            }
            if (FactionUpgrade.CHUNK_SPAWNER_LIMIT.isEnabled()) {
                EventPost.of(BlockPlaceEvent.class, EventPriority.HIGHEST)
                        .filter(EventFilters.getIgnoreCancelled())
                        .filter(event -> event.getBlockPlaced().getType() == Material.MOB_SPAWNER)
                        .handle(event -> {
                            Player player = event.getPlayer();
                            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                            if (fPlayer.hasFaction()) {
                                Integer level = fPlayer.getFaction().getUpgrades().get(FactionUpgrade.CHUNK_SPAWNER_LIMIT);
                                if (getSpawnerCountInChunk(event.getBlockPlaced().getChunk()) > FactionUpgrade.CHUNK_SPAWNER_LIMIT.getMetaInteger((level == null ? "default" : level) + "-amount")) {
                                    player.sendMessage(TL.FACTION_UPGRADES_CHUNK_SPAWNER_LIMIT_DENY.toString());
                                    event.setCancelled(true);
                                }
                            }
                        }).post(P.p);
            }
        } else {
            P.p.log(Level.WARNING, "Disabling FactionUpgrades because it is disabled in the config.");
        }

        if (FactionUpgrade.SPAWNER_SPAWN_RATE.isEnabled()) {
            EventPost.of(SpawnerSpawnEvent.class)
                    .handle(event -> {
                        FLocation fLocation = new FLocation(event.getLocation());
                        Faction faction = Board.getInstance().getFactionAt(fLocation);
                        if (!faction.isWilderness()) {
                            Integer level = faction.getUpgrades().get(FactionUpgrade.SPAWNER_SPAWN_RATE);
                            double divisor = FactionUpgrade.SPAWNER_SPAWN_RATE.getMetaDouble((level == null ? "default" : level) + "-divisor");
                            event.getSpawner().setDelay((int) (event.getSpawner().getDelay() * divisor));
                        }
                    }).post(P.p);
        }
    }

    private int getSpawnerCountInChunk(Chunk chunk) {
        int count = 0;
        for (BlockState tile : chunk.getTileEntities()) {
            if (tile instanceof CreatureSpawner) {
                count++;
            }
        }
        return count;
    }
}
