package com.massivecraft.factions.zcore.factionshop;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.utils.Text;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ShopItem extends MenuItem {

    private final String name;
    private final List<String> commands;
    private final ShopType menuToOpen;
    private final int points, hashCode;
    private final long cooldownMs;

    public ShopItem(ConfigurationSection section) {
        super(ItemStackBuilder.of(section).build());
        this.menuToOpen = Shop.getShopType(section.getString("options.on-click.menu-to-open"));
        this.commands = section.getStringList("options.on-click.commands");
        this.points = section.getInt("options.on-click.point-price");
        this.cooldownMs = section.getInt("options.on-click.cooldown-ms");
        this.name = section.getName();

        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (commands != null ? commands.hashCode() : 0);
        result = 31 * result + (menuToOpen != null ? menuToOpen.hashCode() : 0);
        result = 31 * result + points;
        result = 31 * result + (int) (cooldownMs ^ (cooldownMs >>> 32));
        this.hashCode = result;

    }

    public void execute(Faction faction, Player player) {
        for (String command : commands) {
            String replace = command.replace("{player}", player.getName());
            if (replace.startsWith("@broadcast")) {
                Bukkit.broadcastMessage(Text.toColor(replace.substring(11)));
            } else if (replace.startsWith("@message")) {
                player.sendMessage(Text.toColor(replace.substring(8)));
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replace);
            }
        }
        if (menuToOpen != null) {
            player.closeInventory();
            Shop.getShop(menuToOpen).ifPresent(shop -> player.openInventory(shop.getInventory()));
        }
        faction.setPoints(faction.getPoints() - points);
        faction.getShopCooldown().put(name, System.currentTimeMillis() + cooldownMs);
    }

    public List<String> getCommands() {
        return commands;
    }

    public ShopType getMenuToOpen() {
        return menuToOpen;
    }

    public int getPoints() {
        return points;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShopItem shopItem = (ShopItem) o;

        return shopItem.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "name='" + name + '\'' +
                ", hashCode=" + hashCode +
                ", commands=" + commands +
                ", menuToOpen=" + menuToOpen +
                ", points=" + points +
                ", cooldownMs=" + cooldownMs +
                '}';
    }
}
