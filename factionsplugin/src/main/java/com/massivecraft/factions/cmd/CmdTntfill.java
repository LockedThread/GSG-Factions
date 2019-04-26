package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CmdTntfill extends FCommand {

    public CmdTntfill(String... aliases) {
        super();
        this.aliases.addAll(Arrays.asList(aliases));

        this.requiredArgs.add("amount per dispenser");
        this.requiredArgs.add("radius");

        this.permission = Permission.TNTFILL.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = fme.getFaction();

        Access access = faction.getAccess(fme, PermissableAction.TNTFILL);
        if (access == Access.DENY || (access == Access.UNDEFINED && !fme.getRole().isAtLeast(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "tnt fill");
            return;
        }

        Integer amount = argAsInt(0);
        if (amount == null || amount < 1) {
            msg("<b>Amount must be an integer greater than zero!");
            return;
        }

        Integer radius = argAsInt(1);
        if (radius == null || radius < 1 || radius > 48) {
            msg("<b>Radius must be an integer between 1 and 48!");
            return;
        }

        if (faction.getTntBankBalance() < amount) {
            msg("<b>Your Faction doesn't have enough TNT in the TNT bank!");
            return;
        }

        if (!Board.getInstance().getFactionAt(new FLocation(fme)).getRelationTo(fme).isAtLeast(Relation.ALLY)) {
            msg("<b>You can only use /f tntfill in friendly territory!");
            return;
        }

        int radiusSq = radius * radius;

        Map<Dispenser, Integer> toFill = new HashMap<>();
        Chunk centre = me.getLocation().getChunk();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Chunk chunk = me.getWorld().getChunkAt(centre.getX() + x, centre.getZ() + z);
                Faction factionAt = Board.getInstance().getFactionAt(new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ()));
                if (factionAt == null || !faction.getRelationTo(factionAt).isAtLeast(Relation.ALLY)) {
                    continue;
                }

                main:
                for (BlockState state : chunk.getTileEntities()) {
                    if (state instanceof Dispenser) {
                        if (state.getLocation().distanceSquared(me.getLocation()) > radiusSq) {
                            continue;
                        }
                        Dispenser dispenser = (Dispenser) state;
                        Inventory inventory = dispenser.getInventory();

                        int existing = 0;
                        int space = 0;
                        for (ItemStack stack : inventory) {
                            if (stack != null && stack.getType().equals(Material.TNT)) {
                                existing += stack.getAmount();
                                if (existing >= amount) {
                                    continue main;
                                }
                            } else if (stack == null || stack.getType().equals(Material.AIR)) {
                                space++;
                            }
                        }

                        if (space > 0 || existing > 0) {
                            toFill.put(dispenser, amount - existing);
                        }
                    }
                }
            }
        }

        if (toFill.size() < 1) {
            msg("<b>No dispensers found within " + radius + " blocks that need filling!");
            return;
        }

        int dispensers = 0;
        int totalFilled = 0;
        boolean canAfford = true;
        main:
        for (Map.Entry<Dispenser, Integer> entry : toFill.entrySet()) {
            Integer target = entry.getValue();
            int thisDispenser = 0;
            Inventory inventory = entry.getKey().getInventory();
            for (int k = 0; k < inventory.getSize(); k++) {
                ItemStack item = inventory.getItem(k);
                if (item == null || item.getType().equals(Material.AIR)) {
                    int localAmount = Math.min(target - thisDispenser, 64);
                    if (totalFilled + localAmount > faction.getTntBankBalance()) {
                        // can't afford to fill any more, exit
                        canAfford = false;
                        break main;
                    }
                    thisDispenser += localAmount;
                    totalFilled += localAmount;
                    inventory.setItem(k, new ItemStack(Material.TNT, localAmount));
                } else if (item.getType().equals(Material.TNT)) {
                    int remainder = Math.min(target - thisDispenser, 64);
                    if (totalFilled + remainder > faction.getTntBankBalance()) {
                        // can't afford to fill any more, exit
                        canAfford = false;
                        break main;
                    }

                    int newTotal = item.getAmount() + remainder;
                    if (newTotal <= 64) {
                        item.setAmount(newTotal);
                        thisDispenser += remainder;
                        totalFilled += remainder;
                    } else {
                        int added = 64 - item.getAmount();
                        item.setAmount(64);
                        thisDispenser += added;
                        totalFilled += added;
                    }
                }
                if (thisDispenser >= target) {
                    break;
                }
            }
            if (thisDispenser > 0) {
                dispensers++;
            }
        }

        if (!canAfford) {
            msg("<b>You don't have enough TNT in your Faction TNT bank to fill all nearby dispensers!");
            if (totalFilled == 0) return;
        }
        if (totalFilled == 0) {
            msg("<i>No nearby dispensers need filling");
        } else {
            // remove from tnt balance
            faction.setTntBankBalance(faction.getTntBankBalance() - totalFilled);
            msg("<i>Successfully filled " + dispensers + " dispensers with " + String.format("%,d", totalFilled) + " TNT");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNTFILL_DESCRIPTION;
    }
}
