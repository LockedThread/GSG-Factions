package com.gameservergroup.gsgskyblock.commands;

import com.gameservergroup.gsgskyblock.GSGSkyBlock;
import com.gameservergroup.gsgskyblock.commands.annotations.KommandData;
import com.gameservergroup.gsgskyblock.exceptions.UnableToFindAnnotationException;
import com.gameservergroup.gsgskyblock.objs.IslandPlayer;
import org.bukkit.entity.Player;

public abstract class Kommand {

    private KommandData kommandData;

    public Kommand() {

    }

    public KommandData getKommandData() {
        if (kommandData != null) {
            return kommandData;
        }
        if (this.getClass().getAnnotations().length > 0) {
            if ((kommandData = this.getClass().getDeclaredAnnotation(KommandData.class)) != null) {
                return kommandData;
            }
        }
        throw new UnableToFindAnnotationException("Unable to find KommandData annotation. Contact LockedThread#5691 on discord");
    }

    public void execute(Player player, String command, String[] arguments) {
        execute(GSGSkyBlock.get().getDB().getIslandPlayerData().get(player.getUniqueId()), command, arguments);
    }

    public void execute(IslandPlayer islandPlayer, String command, String[] arguments) {
        if (islandPlayer.getPlayer().hasPermission(getKommandData().permission())) {

        } else {

        }
    }

    public abstract void perform();
}
