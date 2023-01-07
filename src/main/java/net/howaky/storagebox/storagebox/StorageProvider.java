package net.howaky.storagebox.storagebox;

import net.howaky.storagebox.StorageBox;
import net.howaky.storagebox.util.HowaFileUtil;
import net.howaky.storagebox.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageProvider {
    Map<UUID, Storage> storageMap = new HashMap<>();

    public void load() {
        storageMap.clear();
        File dataDirectory = new File(StorageBox.getPlugin().getDataFolder(), "data");
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdir()) {
                Bukkit.broadcastMessage("§c[ERROR] ストレージのデータフォルダーの作成に失敗しました");
            }
        }
        for (File file : HowaFileUtil.dumpFile(dataDirectory)) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String uuidString = file.getName().replace(".yml", "");
                Storage storage = new Storage();
                storage.setUuid(UUID.fromString(uuidString));
                storage.setItemStack(data.getItemStack("ItemStack"));
                storage.setAmount(data.getInt("Amount"));
                register(storage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        storageMap.forEach((uuid, storage) -> {
            FileConfiguration configuration = new YamlConfiguration();
            configuration.set("ItemStack", storage.getItemStack());
            configuration.set("Amount", storage.getAmount());
            try {
                configuration.save(new File(new File(StorageBox.getPlugin().getDataFolder(), "data"), storage.getUuid().toString() + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Storage get(ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        final String uuidString = itemMeta.getPersistentDataContainer().get(Keys.UUID, PersistentDataType.STRING);
        if (uuidString == null) return null;
        final UUID uuid = UUID.fromString(uuidString);
        return storageMap.get(uuid);
    }

    public void register(Storage storage) {
        storageMap.put(storage.getUuid(), storage);
    }
}
