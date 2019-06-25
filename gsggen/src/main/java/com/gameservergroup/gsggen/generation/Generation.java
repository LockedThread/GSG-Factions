package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.utils.Utils;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Generation {

    public static boolean ASYNC = false;

    private final BlockPosition startingBlockPosition;
    private BlockPosition currentBlockPosition;
    private int length;
    private BlockFace blockFace;
    private Material material;
    private boolean patch;
    private transient FLocation startingFLocation;
    private transient Block start, current;

    public Generation(BlockPosition startingBlockPosition, Gen gen, BlockFace blockFace) {
        this.startingBlockPosition = startingBlockPosition;
        this.currentBlockPosition = startingBlockPosition;
        this.material = gen.getMaterial();
        this.patch = gen.isPatch();
        this.length = gen.getLength();
        this.blockFace = blockFace;
        this.init();
    }

    public void init() {
        this.startingFLocation = new FLocation(getStartingBlockPosition().getLocation());
        this.start = startingBlockPosition.getBlock();
        this.current = getCurrent();
    }

    public boolean isVertical() {
        return blockFace == BlockFace.UP || blockFace == BlockFace.DOWN;
    }

    public boolean generateVertical() {
        if (!getCurrentBlockPosition().isChunkLoaded()) {
            getCurrentBlockPosition().getChunk().load();
        }
        Block relative = getCurrent().getRelative(blockFace);
        if (getLength() == 0) {
            return false;
        }
        if (getStart().getType() != getMaterial()) {
            return false;
        }
        if (relative.getY() == 255) {
            return false;
        }
        if (relative.getType() != Material.AIR && !isPatch()) {
            return false;
        }
        if (isPatch() && relative.getType() != Material.WATER &&
                relative.getType() != Material.STATIONARY_WATER &&
                relative.getType() != Material.LAVA &&
                relative.getType() != Material.STATIONARY_LAVA &&
                relative.getType() != Material.OBSIDIAN &&
                relative.getType() != Material.SAND &&
                relative.getType() != Material.GRAVEL &&
                relative.getType() != Material.AIR &&
                relative.getType() != Material.COBBLESTONE) {
            return false;
        }
        setLength(getLength() - 1);
        if (ASYNC) {
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getMaterial().getId(), (byte) 0, relative.isLiquid()));
        } else {
            relative.setTypeIdAndData(getMaterial().getId(), (byte) 0, relative.isLiquid());
        }
        setCurrent(relative);
        return true;
        /*
        if (startingBlockPosition.isChunkLoaded()) {
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), startingBlockPosition.getChunk()::load);
        }
        getCurrentBlockPosition().getBlock().getRelative(blockFace);
        Block relative = getCurrentBlockPosition().getBlock().getRelative(blockFace);
        if (getLength() == 0) {
            return false;
        }
        if (getCurrentBlockPosition().getBlock().getType() != getMaterial()) {
            return false;
        }
        if (relative.getY() == 254) {
            return false;
        }
        if (relative.getType() != Material.AIR && !isPatch()) {
            return false;
        }
        if (isPatch() && relative.getType() != Material.WATER &&
                relative.getType() != Material.STATIONARY_WATER &&
                relative.getType() != Material.LAVA &&
                relative.getType() != Material.STATIONARY_LAVA &&
                relative.getType() != Material.OBSIDIAN &&
                relative.getType() != Material.SAND &&
                relative.getType() != Material.GRAVEL &&
                relative.getType() != Material.AIR &&
                relative.getType() != Material.COBBLESTONE) {
            return false;
        }
        setLength(getLength() - 1);
        GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getMaterial().getId(), (byte) 0, relative.isLiquid()));
        setCurrentBlockPosition(BlockPosition.of(relative));
        return true;*/
    }

    public boolean generateHorizontal() {
        BlockPosition blockPositionRelative = getCurrentBlockPosition().getRelative(blockFace);
        if (!startingBlockPosition.isChunkLoaded()) {
            startingBlockPosition.getChunk().load();
        }
        Block relative = blockPositionRelative.getBlock();
        if (getLength() == 0) {
            return false;
        }
        if (getStart().getType() != getMaterial()) {
            return false;
        }
        if (relative.getType() != Material.AIR && !isPatch()) {
            return false;
        }
        if (isPatch() && relative.getType() != Material.WATER &&
                relative.getType() != Material.STATIONARY_WATER &&
                relative.getType() != Material.LAVA &&
                relative.getType() != Material.STATIONARY_LAVA &&
                relative.getType() != Material.OBSIDIAN &&
                relative.getType() != Material.SAND &&
                relative.getType() != Material.GRAVEL &&
                relative.getType() != Material.AIR &&
                relative.getType() != Material.COBBLESTONE) {
            return false;
        }
        if (!Board.getInstance().getFactionAt(startingFLocation).getTag().equals(Board.getInstance().getFactionAt(new FLocation(relative)).getTag())) {
            return false;
        }
        if (Utils.isOutsideBorder(relative.getLocation())) {
            return false;
        }

        setLength(getLength() - 1);
        if (ASYNC) {
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getMaterial().getId(), (byte) 0, relative.isLiquid()));
        } else {
            relative.setTypeIdAndData(getMaterial().getId(), (byte) 0, relative.isLiquid());
        }
        setCurrent(relative);
        return true;
    }

    public Block getStart() {
        return start;
    }

    public Block getCurrent() {
        return current == null ? current = getCurrentBlockPosition().getBlock() : current;
    }

    public void setCurrent(Block block) {
        this.current = block;
        this.currentBlockPosition = BlockPosition.of(block);
    }

    public void enable() {
        GSGGen.getInstance().getUnitGen().getGenerations().add(this);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public BlockPosition getStartingBlockPosition() {
        return startingBlockPosition;
    }

    public FLocation getStartingFLocation() {
        return startingFLocation;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isPatch() {
        return patch;
    }

    public BlockPosition getCurrentBlockPosition() {
        return currentBlockPosition;
    }

    public void setCurrentBlockPosition(BlockPosition currentBlockPosition) {
        this.currentBlockPosition = currentBlockPosition;
    }
}
