package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionDefaultImpl;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionFactionsUUIDImpl;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionWorldGuardImpl;
import com.gameservergroup.gsgcore.items.UnitCustomItem;
import com.gameservergroup.gsgcore.menus.UnitMenu;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.adapters.AdapterBlockPosition;
import com.gameservergroup.gsgcore.storage.adapters.AdapterChunkPosition;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcore.units.UnitReload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

public class GSGCore extends Module {

    private static GSGCore instance;
    private CommandPostExecutor commandPostExecutor;
    private ProtectionIntegration[] protectionIntegrations;
    private Gson gson;

    public static GSGCore getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        setupVault();
        setupProtectionIntegration();
        setupJson();
        this.commandPostExecutor = new CommandPostExecutor();
        registerUnits(new UnitMenu(), new UnitCustomItem(), new UnitReload());
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setEconomy(getServer().getServicesManager().getRegistration(Economy.class).getProvider());
        }
    }

    private void setupProtectionIntegration() {
        HashSet<ProtectionIntegration> protectionIntegrations = new HashSet<>();
        if (getServer().getPluginManager().getPlugin("Factions") != null) {
            try {
                List<String> factions = getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors();
                if (factions.contains("LockedThread")) {
                    getLogger().info("Enabled LockedThread's FactionsUUID Fork Support");
                    protectionIntegrations.add(new ProtectionFactionsUUIDImpl());
                } else if (factions.contains("drtshock")) {
                    getLogger().info("Enabled FactionsUUID Support");
                    protectionIntegrations.add(new ProtectionFactionsUUIDImpl());
                } /*else if (factions.contains("MarkehMe")) {
                    getLogger().info("Enabled MassiveCraft Factions Support");
                }*/ else {
                    throw new RuntimeException("We don't support the factions plugin you're currently using, for support contact LockedThread.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            protectionIntegrations.add(new ProtectionWorldGuardImpl(getServer().getPluginManager().getPlugin("WorldGuard")));
        }
        if (protectionIntegrations.isEmpty()) {
            protectionIntegrations.add(new ProtectionDefaultImpl());
            getLogger().severe("You aren't using any supported Protection systems, all block placement and removal checks will be allowed!");
        }
        this.protectionIntegrations = protectionIntegrations.toArray(new ProtectionIntegration[0]);
    }

    @Override
    public void disable() {
        instance = null;
    }

    private void setupJson() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .disableHtmlEscaping()
                .registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> new EnumMap((Class<?>) (((ParameterizedType) type).getActualTypeArguments())[0]))
                .registerTypeAdapter(BlockPosition.class, new AdapterBlockPosition())
                .registerTypeAdapter(ChunkPosition.class, new AdapterChunkPosition())
                .create();
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }

    public Gson getGson() {
        return gson;
    }

    public ProtectionIntegration[] getProtectionIntegrations() {
        return protectionIntegrations;
    }

    public boolean canBuild(Player player, Location location) {
        return Arrays.stream(getProtectionIntegrations()).allMatch(protectionIntegration -> protectionIntegration.canBuild(player, location));
    }

    public boolean canBuild(Player player, Block block) {
        return Arrays.stream(getProtectionIntegrations()).allMatch(protectionIntegration -> protectionIntegration.canBuild(player, block));
    }

    public boolean canDestroy(Player player, Location location) {
        return Arrays.stream(getProtectionIntegrations()).allMatch(protectionIntegration -> protectionIntegration.canDestroy(player, location));
    }

    public boolean canDestroy(Player player, Block block) {
        return Arrays.stream(getProtectionIntegrations()).allMatch(protectionIntegration -> protectionIntegration.canDestroy(player, block));
    }
}
