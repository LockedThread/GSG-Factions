package com.gameservergroup.gsgcore;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionDefaultImpl;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionFactionsUUIDImpl;
import com.gameservergroup.gsgcore.integration.impl.protection.ProtectionWorldGuardImpl;
import com.gameservergroup.gsgcore.items.UnitCustomItem;
import com.gameservergroup.gsgcore.menus.UnitMenu;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.deserializer.BlockPositionDeserializer;
import com.gameservergroup.gsgcore.storage.deserializer.ChunkPositionDeserializer;
import com.gameservergroup.gsgcore.storage.deserializer.LocationDeserializer;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcore.storage.serializers.BlockPositionSerializer;
import com.gameservergroup.gsgcore.storage.serializers.ChunkPositionSerializer;
import com.gameservergroup.gsgcore.storage.serializers.LocationSerializer;
import com.gameservergroup.gsgcore.units.UnitReload;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GSGCore extends Module {

    private static GSGCore instance;
    private CommandPostExecutor commandPostExecutor;
    private ObjectMapper jsonObjectMapper;
    private ProtectionIntegration[] protectionIntegrations;

    public static GSGCore getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
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
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule serializers = new SimpleModule("Serializers", new Version(1, 0, 0, null));

        //Location
        serializers.addSerializer(new LocationSerializer()).addDeserializer(Location.class, new LocationDeserializer());
        //BlockPosition
        serializers.addSerializer(new BlockPositionSerializer()).addDeserializer(BlockPosition.class, new BlockPositionDeserializer());
        //ChunkPosition
        serializers.addSerializer(new ChunkPositionSerializer()).addDeserializer(ChunkPosition.class, new ChunkPositionDeserializer());

        objectMapper.registerModule(serializers);

        this.jsonObjectMapper = objectMapper;
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }

    public ObjectMapper getJsonObjectMapper() {
        return jsonObjectMapper;
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
