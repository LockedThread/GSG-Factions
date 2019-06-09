package dev.lockedthread.frontierfactions.frontierhub.bukkit.units;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.SoundBuilder;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.FrontierHubBukkit;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.menus.MenuServerSelector;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class UnitHub extends Unit {

    private static final FrontierHubBukkit FRONTIER_HUB_BUKKIT = FrontierHubBukkit.getInstance();

    @Override
    public void setup() {
        MenuServerSelector menuServerSelector = new MenuServerSelector();
        CustomItem customItem = CustomItem.of(FRONTIER_HUB_BUKKIT, FRONTIER_HUB_BUKKIT.getConfig().getConfigurationSection("server-selector.item"), "server-selector")
                .setInteractEventConsumer(event -> {
                    event.getPlayer().openInventory(menuServerSelector.getInventory());
                    event.setCancelled(true);
                });

        EventPost.of(PlayerJoinEvent.class, EventPriority.LOWEST)
                .handle(event -> {
                    Player player = event.getPlayer();
                    player.getInventory().clear();
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().setItem(4, customItem.getItemStack());
                    Triple<Sound, Float, Float> sound = SoundBuilder.of(FRONTIER_HUB_BUKKIT.getConfig().getConfigurationSection("join-sound")).build();
                    player.playSound(player.getLocation(), sound.getLeft(), sound.getMiddle(), sound.getRight());
                    event.setJoinMessage(null);

                    for (Player other : FRONTIER_HUB_BUKKIT.getServer().getOnlinePlayers()) {
                        other.hidePlayer(player);
                    }
                }).post(FRONTIER_HUB_BUKKIT);

        EventPost.of(PlayerQuitEvent.class, EventPriority.LOWEST)
                .handle(event -> event.setQuitMessage(null))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(BlockBreakEvent.class)
                .filter(event -> !event.getPlayer().isOp())
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(BlockPlaceEvent.class)
                .filter(event -> !event.getPlayer().isOp())
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(PlayerToggleFlightEvent.class)
                .handle(event -> {
                    Player player = event.getPlayer();
                    player.setVelocity(player.getEyeLocation().getDirection().multiply(2));
                    event.setCancelled(true);
                }).post(FRONTIER_HUB_BUKKIT);

        EventPost.of(EntityDamageEvent.class)
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(PlayerInteractAtEntityEvent.class)
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(PlayerDropItemEvent.class)
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);

        EventPost.of(PlayerPickupItemEvent.class)
                .handle(event -> event.setCancelled(true))
                .post(FRONTIER_HUB_BUKKIT);
    }
}
