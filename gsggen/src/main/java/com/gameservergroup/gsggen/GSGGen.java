package com.gameservergroup.gsggen;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.utils.CallBack;
import com.gameservergroup.gsggen.enums.GenMessages;
import com.gameservergroup.gsggen.generation.Generation;
import com.gameservergroup.gsggen.integration.CombatIntegration;
import com.gameservergroup.gsggen.integration.combat.impl.CombatTagPlusImpl;
import com.gameservergroup.gsggen.menu.GenMenu;
import com.gameservergroup.gsggen.units.UnitGen;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GSGGen extends Module {

    private static GSGGen instance;
    private UnitGen unitGen;
    private GenMenu genMenu;
    private boolean enableCombatTagPlusIntegration;
    private CombatIntegration combatIntegration;

    public static GSGGen getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("enable-combattagplus-integration")) {
            Plugin combatTagPlus = getServer().getPluginManager().getPlugin("CombatTagPlus");
            if (combatTagPlus != null) {
                getLogger().info("Enabled CombatTagPlus Integration");
                this.enableCombatTagPlusIntegration = getConfig().getBoolean("enable-combattagplus-integration");
                this.combatIntegration = new CombatTagPlusImpl(combatTagPlus);
            } else {
                getLogger().severe("You don't have CombatTagPlus installed, if you don't wish to use it disable the integration in GSGGen's config.yml");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        for (GenMessages genMessages : GenMessages.values()) {
            if (getConfig().isSet("messages." + genMessages.getKey())) {
                genMessages.setMessage(getConfig().getString("messages." + genMessages.getKey()));
            } else {
                getConfig().set("messages." + genMessages.getKey(), genMessages.getMessage());
            }
        }
        Generation.ASYNC = getConfig().getBoolean("async.enabled", true);
        registerUnits(unitGen = new UnitGen());
        this.genMenu = new GenMenu();
        if (Generation.ASYNC) {
            ExecutorService executorService = Executors.newFixedThreadPool(getConfig().getInt("async.threads", 2));
            getServer().getScheduler().runTaskTimer(this, () -> executorService.submit(() -> {
                for (Generation generation : unitGen.getGenerations().keySet()) {
                    BlockPosition currentBlockPosition = generation.getCurrentBlockPosition();
                    if (currentBlockPosition.isChunkLoaded()) {
                        if (generation.isVertical()) {
                            if (!generation.generateVertical()) {
                                unitGen.getGenerations().remove(generation);
                            }
                        } else {
                            doGenerationChecks(generation, currentBlockPosition);
                        }
                    } else if (generation.isVertical()) {
                        currentBlockPosition.loadChunkAsync(chunk -> {
                            if (!generation.generateVertical()) {
                                unitGen.getGenerations().remove(generation);
                            }
                        });
                    } else {
                        currentBlockPosition.loadChunkAsync(chunk -> doGenerationChecks(generation, currentBlockPosition));
                    }
                }
            }), getConfig().getLong("interval"), getConfig().getLong("interval"));
        } else {
            getServer().getScheduler().runTaskTimer(this, () -> {
                if (!unitGen.getGenerations().isEmpty()) {
                    Set<Map.Entry<Generation, Boolean>> entrySet = unitGen.getGenerations().entrySet();
                    entrySet.removeIf(entry -> entry.getKey().isVertical() ? !entry.getKey().generateVertical() : !entry.getKey().generateHorizontal(null));
                }
            }, getConfig().getLong("interval"), getConfig().getLong("interval"));
        }
            /*getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                for (Generation generation : unitGen.getGenerations().keySet()) {
                    BlockPosition currentBlockPosition = generation.getCurrentBlockPosition();
                    if (currentBlockPosition.isChunkLoaded()) {
                        if (generation.isVertical()) {
                            if (!generation.generateVertical()) {
                                unitGen.getGenerations().remove(generation);
                            }
                        } else {
                            doGenerationChecks(generation, currentBlockPosition);
                        }
                    } else if (generation.isVertical()) {
                        currentBlockPosition.loadChunkAsync(chunk -> {
                            if (!generation.generateVertical()) {
                                unitGen.getGenerations().remove(generation);
                            }
                        });
                    } else {
                        currentBlockPosition.loadChunkAsync(chunk -> doGenerationChecks(generation, currentBlockPosition));
                    }


                    /*if (!generation.getCurrentBlockPosition().isChunkLoaded()) {
                        if (generation.isVertical()) {
                            generation.getCurrentBlockPosition().loadChunkAsync(chunk -> {
                                if (generation.generateVertical()) {
                                    unitGen.getGenerations().remove(generation);
                                }
                            });
                        } else {
                            generation.getCurrentBlockPosition().loadChunkAsync(chunk -> {
                                Block relative = generation.getCurrent().getRelative(generation.getBlockFace());
                                if (relative.getType() != Material.AIR && !generation.isPatch()) {
                                    unitGen.getGenerations().remove(generation);
                                } else {
                                    BlockPosition relativeBlockPosition = BlockPosition.of(relative);
                                    if (generation.getCurrent().getWorld().isChunkLoaded(relative.getX(), relative.getZ())) {
                                        if (!generation.generateHorizontal(relativeBlockPosition)) {
                                            unitGen.getGenerations().remove(generation);
                                        }
                                    } else {
                                        relativeBlockPosition.loadChunkAsync(chunk1 -> {
                                            if (!generation.generateHorizontal(relativeBlockPosition)) {
                                                unitGen.getGenerations().remove(generation);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        if (generation.isVertical() ? !generation.generateVertical() : !generation.generateHorizontal(null)) {
                            if (generation != null) {
                                unitGen.getGenerations().remove(generation);
                            }
                        }
                    }
                }
            }, getConfig().getLong("interval"), getConfig().getLong("interval"));
        } else {
            getServer().getScheduler().runTaskTimer(this, () -> unitGen.getGenerations().keySet().removeIf(generation -> generation.isVertical() ? !generation.generateVertical() : !generation.generateHorizontal(null)), getConfig().getLong("interval"), getConfig().getLong("interval"));
        }*/
    }

    private void doGenerationChecks(Generation generation, BlockPosition currentBlockPosition) {
        BlockPosition relative = currentBlockPosition.getRelative(generation.getBlockFace());

        getTypeId(relative, new CallBack<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer != Material.AIR.getId() && !generation.isPatch()) {
                    unitGen.getGenerations().remove(generation);
                } else if (relative.isChunkLoaded()) {
                    if (!generation.generateHorizontal(relative)) {
                        unitGen.getGenerations().remove(generation);
                    }
                } else {
                    relative.loadChunkAsync(chunk2 -> {
                        if (!generation.generateHorizontal(relative)) {
                            unitGen.getGenerations().remove(generation);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void reload() {
        reloadConfig();
        unitGen.load();
        this.genMenu = new GenMenu();
    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitGen getUnitGen() {
        return unitGen;
    }

    public GenMenu getGenMenu() {
        return genMenu;
    }

    public boolean isEnableCombatTagPlusIntegration() {
        return enableCombatTagPlusIntegration;
    }

    public CombatIntegration getCombatIntegration() {
        return combatIntegration;
    }

    private void getTypeId(BlockPosition blockPosition, CallBack<Integer> callback) {
        int subX = blockPosition.getX() % 16;
        int subZ = blockPosition.getZ() % 16;

        if (subX < 0) {
            subX += 16;
        }

        if (subZ < 0) {
            subZ += 16;
        }

        if (blockPosition.isChunkLoaded()) {
            ChunkSnapshot chunkSnapshot = blockPosition.getChunk().getChunkSnapshot();
            int blockTypeId = chunkSnapshot.getBlockTypeId(subX, blockPosition.getY(), subZ);
            callback.call(blockTypeId);
        } else {
            final int x = subX, z = subZ;
            blockPosition.loadChunkAsync(chunk -> callback.call(chunk.getChunkSnapshot().getBlockTypeId(x, blockPosition.getY(), z)));
        }
    }
}
