package dev.lockedthread.frontierfactions.frontierhub.bukkit.items;

import com.gameservergroup.gsgcore.menus.MenuItem;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.FrontierHubBukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerMenuItem extends MenuItem {

    public ServerMenuItem(ItemStack itemStack, String server) {
        super(itemStack);
        this.setInventoryClickEventConsumer(event -> {
            send((Player) event.getWhoClicked(), server);
            event.setCancelled(true);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void send(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("frontier-hub-commands");
        out.writeUTF(player.getName() + " " + server);
        player.sendPluginMessage(FrontierHubBukkit.getInstance(), "BungeeCord", out.toByteArray());
    }
}
