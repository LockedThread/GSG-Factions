package com.gameservergroup.gsgcrophopper.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcrophopper.GSGCropHopper;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;

public class UnitCropHopper extends Unit {

    private static final GSGCropHopper GSG_CROP_HOPPER = GSGCropHopper.getInstance();
    private CustomItem crophopper;

    @Override
    public void setup() {
        EventPost.of(BlockGrowEvent.class, EventPriority.HIGH)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getNewState().getType() == Material.CACTUS)
                .filter(event -> canGrow(event.getBlock()))
                .handle(event -> {
                    BlockPosition cropHopper = GSG_CROP_HOPPER.findCropHopper(event.getBlock().getChunk());
                    if (cropHopper != null) {
                        event.setCancelled(true);
                        ((Hopper) cropHopper.getBlock().getState()).getInventory().addItem(new ItemStack(Material.CACTUS));
                    }
                }).post(GSG_CROP_HOPPER);

        crophopper = CustomItem.of(GSG_CROP_HOPPER.getConfig().getConfigurationSection("crophopper"))
                .setPlaceEventConsumer(event -> GSG_CROP_HOPPER.create(event.getBlockPlaced().getLocation()))
                .setBreakEventConsumer(event -> {
                    GSG_CROP_HOPPER.remove(event.getBlock().getChunk(), true);
                    event.setCancelled(true);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), crophopper.getItemStack());
                });
    }

    private boolean canGrow(Block bukkitBlock) {
        net.minecraft.server.v1_8_R3.BlockPosition blockPosition = new net.minecraft.server.v1_8_R3.BlockPosition(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) bukkitBlock.getWorld()).getHandle();

        for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (nmsWorld.getType(blockPosition.shift(enumDirection)).getBlock().getMaterial().isBuildable()) {
                return false;
            }
        }

        final net.minecraft.server.v1_8_R3.Block block = nmsWorld.getType(blockPosition.down()).getBlock();
        return block == Blocks.CACTUS || block == Blocks.SAND;
    }
}
