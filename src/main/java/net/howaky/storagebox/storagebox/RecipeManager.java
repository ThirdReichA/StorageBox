package net.howaky.storagebox.storagebox;

import net.howaky.storagebox.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RecipeManager {
    public static void createRecipe() {
        ItemStack result = new ItemStack(Material.CHEST_MINECART);
        ItemMeta itemMeta = result.getItemMeta();
        if (itemMeta == null) return;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(Keys.ITEM_ID, PersistentDataType.STRING, "storage_box");
        result.setItemMeta(itemMeta);
        ShapedRecipe shapedRecipe = new ShapedRecipe(Keys.STORAGE_BOX, result);
        shapedRecipe.shape("WWW", "W W", "WWW");
        shapedRecipe.setIngredient('W', Material.OAK_LOG);
        Bukkit.addRecipe(shapedRecipe);
    }
}
