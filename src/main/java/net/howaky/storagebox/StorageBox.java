package net.howaky.storagebox;

import lombok.Getter;
import net.howaky.storagebox.listener.StorageBoxListener;
import net.howaky.storagebox.storagebox.RecipeManager;
import net.howaky.storagebox.storagebox.StorageProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class StorageBox extends JavaPlugin {

    @Getter
    private static StorageBox plugin;
    @Getter
    private StorageProvider storageProvider;

    @Override
    public void onEnable() {
        plugin = this;
        storageProvider = new StorageProvider();
        storageProvider.load();
        RecipeManager.createRecipe();
        getServer().getPluginManager().registerEvents(new StorageBoxListener(), this);
    }

    @Override
    public void onDisable() {
        storageProvider.save();
    }
}
