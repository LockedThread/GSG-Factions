package com.gameservergroup.gsgcore.menus.fill;

import com.gameservergroup.gsgcore.menus.fill.enums.FillMode;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class FillOptions {

    private final FillMode fillMode;
    private List<DyeColor> dyeColorsList;
    private DyeColor solidDyeColor;


    public FillOptions(ConfigurationSection section) {
        switch (this.fillMode = FillMode.valueOf(section.getString("fill-mode").toUpperCase())) {
            case CHECKERED:
            case RANDOM:
                this.dyeColorsList = section.getStringList("colors").stream().map(s -> DyeColor.valueOf(s.toUpperCase())).collect(Collectors.toList());
                break;
            case SOLID:
                this.solidDyeColor = DyeColor.valueOf(section.getString("color"));
                break;
        }
    }

    public FillMode getFillMode() {
        return fillMode;
    }

    public List<DyeColor> getDyeColorsList() {
        return dyeColorsList;
    }

    public DyeColor getDyeColor() {
        return solidDyeColor;
    }
}
