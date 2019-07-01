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

import java.util.function.BiConsumer;

public class Generation {

    private static final BiConsumer<Block, Material> BLOCK_CONSUMER = (block, material) -> block.setTypeIdAndData(material.getId(), (byte) 0, block.isLiquid());
    public static boolean ASYNC = false;
    private final BlockFace blockFace;
    private final Material material;
    private final BlockPosition startingBlockPosition;
    private final boolean patch;
    private int length;
    private BlockPosition currentBlockPosition;
    private transient FLocation startingFLocation;
    private transient Block start, current;
    private transient int hash;

    public Generation(BlockPosition startingBlockPosition, Gen gen, BlockFace blockFace) {
        this.startingBlockPosition = startingBlockPosition;
        this.currentBlockPosition = startingBlockPosition;
        this.material = gen.getMaterial();
        this.patch = gen.isPatch();
        this.length = gen.getLength();
        this.blockFace = blockFace;
        this.init();
        recalcHash();
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
        if (getLength() == 0) {
            return false;
        }
        if (getStart().getType() != getMaterial()) {
            return false;
        }
        Block relative = getCurrent().getRelative(blockFace);
        if (relative.getY() >= 255) {
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
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> BLOCK_CONSUMER.accept(relative, getMaterial()));
        } else {
            BLOCK_CONSUMER.accept(relative, getMaterial());
        }
        setCurrent(relative);
        return true;
    }

    public boolean generateHorizontal() {
        if (getLength() == 0) {
            return false;
        }
        Block relative = getCurrentBlockPosition().getRelative(blockFace).getBlock();
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
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> BLOCK_CONSUMER.accept(relative, getMaterial()));
        } else {
            BLOCK_CONSUMER.accept(relative, getMaterial());
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

    private void recalcHash() {
        int result = startingBlockPosition != null ? startingBlockPosition.hashCode() : 0;
        result = 31 * result + (blockFace != null ? blockFace.hashCode() : 0);
        result = 31 * result + (material != null ? material.hashCode() : 0);
        result = 31 * result + (patch ? 1 : 0);
        this.hash = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Generation that = (Generation) o;

        return that.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            recalcHash();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Generation{" +
                "blockFace=" + blockFace +
                ", material=" + material +
                ", startingBlockPosition=" + startingBlockPosition +
                ", patch=" + patch +
                ", length=" + length +
                ", currentBlockPosition=" + currentBlockPosition +
                ", startingFLocation=" + startingFLocation +
                ", start=" + start +
                ", current=" + current +
                '}';
    }
}
