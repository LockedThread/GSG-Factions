package com.massivecraft.factions.zcore.factionshields.menus;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.menus.fill.FillOptions;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsgcore.utils.TimeUtil;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.configuration.ConfigurationSection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FactionShieldMainMenu extends Menu {

    private static final ConfigurationSection MAIN_MENU_SECTION = P.p.getConfig().getConfigurationSection("faction-shields.main-menu");
    private static final boolean PLAYERS_CAN_CONFIGURE = P.p.getConfig().getBoolean("faction-shields.players-can-configure");

    private final FPlayer fPlayer;

    public FactionShieldMainMenu(FPlayer fPlayer) {
        super(MAIN_MENU_SECTION.getString("name"), MAIN_MENU_SECTION.getInt("size"));
        this.fPlayer = fPlayer;
        ConfigurationSection fillSection = MAIN_MENU_SECTION.getConfigurationSection("fill");
        if (fillSection.getBoolean("enabled")) {
            setFillOptions(new FillOptions(fillSection));
        }
        initialize();
    }

    @Override
    public void initialize() {
        setItem(MAIN_MENU_SECTION.getInt("configure-item.slot"), MenuItem.of(ItemStackBuilder.of(MAIN_MENU_SECTION.getConfigurationSection("configure-item")).build())
                .setInventoryClickEventConsumer(event -> {
                    event.setCancelled(true);
                    if (PLAYERS_CAN_CONFIGURE) {
                        Faction faction = fPlayer.getFaction();
                        if (faction.getFPlayerAdmin().getAccountId().equals(fPlayer.getAccountId())) {
                            event.getWhoClicked().openInventory(FactionShieldConfigureMenu.getInstance().getInventory());
                        }
                    }
                }));


        SimpleDateFormat lastShieldChangeFormat = new SimpleDateFormat(MAIN_MENU_SECTION.getString("information-item.format.time"));
        setItem(MAIN_MENU_SECTION.getInt("information-item.slot"), MenuItem.of(ItemStackBuilder.of(MAIN_MENU_SECTION.getConfigurationSection("information-item"))
                .consumeItemMeta(itemMeta -> {
                    // TODO: COMPLETE THIS
                    Faction faction = fPlayer.getFaction();
                    String lastShieldChange, timeRemaining;
                    if (faction.getLastShieldChange() == 0) {
                        lastShieldChange = MAIN_MENU_SECTION.getString("information-item.format.never");
                        timeRemaining = MAIN_MENU_SECTION.getString("information-item.format.inactive");
                    } else {
                        lastShieldChange = lastShieldChangeFormat.format(faction.getLastShieldChange());
                        if (faction.getFactionShield() != null && faction.getFactionShieldCachedValue()) {
                            long timeLeft = faction.getFactionShield().getTimeLeft();
                            if (timeLeft != -1) {
                                if (timeLeft < 60) {
                                    timeLeft = Math.abs(timeLeft - 60) * 60;
                                } else {
                                    timeLeft *= 60;
                                }
                            }
                            timeRemaining = TimeUtil.toLongForm(timeLeft);
                        } else {
                            timeRemaining = MAIN_MENU_SECTION.getString("information-item.format.inactive");
                        }
                    }
                    List<String> list = new ArrayList<>();
                    for (String s : itemMeta.getLore()) {
                        String replace = s.replace("{last-shield-change}", lastShieldChange).replace("{time-remaining}", timeRemaining);
                        list.add(Text.toColor(replace));
                    }
                    itemMeta.setLore(list);
                }).build()));
        fill();
    }
}
