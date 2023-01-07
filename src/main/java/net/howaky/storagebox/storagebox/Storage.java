package net.howaky.storagebox.storagebox;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
public class Storage {
    private UUID uuid;
    private int amount;
    private ItemStack itemStack;

    public boolean test(ItemStack itemStack) {
        return itemStack.equals(this.itemStack);
    }
}
