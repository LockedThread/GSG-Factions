package frontierfactions.rewards;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public enum RewardType {

    MINING, FISHING, TRIVIA;

    public String toPrettyName() {
        return StringUtils.capitalize(name().toLowerCase());
    }

    public double getNominalChance(Player player) {
        double chance = 0.0;
        for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            if (permissionAttachmentInfo.getPermission().startsWith("frontiercore." + name().toLowerCase() + ".")) {
                return Double.parseDouble(permissionAttachmentInfo.getPermission().substring(14 + name().length()));
            }
        }
        return chance;
    }
}
