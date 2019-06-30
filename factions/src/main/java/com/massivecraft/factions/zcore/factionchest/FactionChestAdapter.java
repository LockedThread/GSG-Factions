package com.massivecraft.factions.zcore.factionchest;

import com.gameservergroup.gsgcore.utils.Base64Serializers;
import com.gameservergroup.gsgcore.utils.Text;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class FactionChestAdapter extends TypeAdapter<FactionChest> {

    @Override
    public void write(JsonWriter out, FactionChest value) throws IOException {
        out.beginObject();
        out.name("rows").value(value.getRows());
        out.name("contents").value(Base64Serializers.toBase64ItemStacks(value.getInventory().getContents()));
        out.endObject();
    }

    @Override
    public FactionChest read(JsonReader in) throws IOException {
        in.beginObject();
        FactionChest factionChest = new FactionChest();
        int rows = 3;
        ItemStack[] itemStacks = new ItemStack[0];
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equalsIgnoreCase("rows")) {
                rows = in.nextInt();
            } else if (fieldName.equalsIgnoreCase("contents")) {
                itemStacks = Base64Serializers.fromBase64ItemStacks(in.nextString());
            }
        }
        Inventory inventory = Bukkit.createInventory(factionChest, rows * 9, Text.toColor(P.p.getConfig().getString("faction-chest-title", "&eFaction Chest")));
        inventory.setContents(itemStacks);
        factionChest.setInventory(inventory);
        factionChest.setRows(rows);
        in.endObject();
        return factionChest;
    }
}
