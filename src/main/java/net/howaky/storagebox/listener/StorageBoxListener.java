package net.howaky.storagebox.listener;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import net.howaky.storagebox.StorageBox;
import net.howaky.storagebox.storagebox.Storage;
import net.howaky.storagebox.util.Keys;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class StorageBoxListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (!isStorage(event.getItem())) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Storage storage = StorageBox.getPlugin().getStorageProvider().get(itemStack);
            String[] guiSetup = {
                    "  i  "
            };
            InventoryGui gui = new InventoryGui(StorageBox.getPlugin(), event.getPlayer(), "§lストレージボックス", guiSetup);
            gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
            gui.addElement(new DynamicGuiElement(
                    'i',
                    viewer -> new StaticGuiElement(
                            'i',
                            storage.getItemStack().clone(),
                            1,
                            click -> {
                                if (click.getType() == ClickType.LEFT) {
                                    if (storage.getAmount() == 0) {
                                        if (click.getCursor() != null) {
                                            storage.setItemStack(click.getCursor());
                                            storage.setAmount(click.getCursor().getAmount());
                                            click.setCursor(new ItemStack(Material.AIR));
                                            click.getGui().draw();
                                        }
                                    } else {
                                        if (!(click.getWhoClicked() instanceof Player)) return true;
                                        ItemStack giveItem = storage.getItemStack().clone();
                                        giveItem.setAmount(1);
                                        giveItem((Player) click.getWhoClicked(), giveItem);
                                        storage.setAmount(storage.getAmount() - 1);
                                        click.getGui().draw();
                                    }
                                }
                                if (click.getType() == ClickType.RIGHT) {
                                    if (storage.getAmount() == 0) {
                                        if (click.getCursor() != null) {
                                            storage.setItemStack(click.getCursor());
                                            storage.setAmount(click.getCursor().getAmount());
                                            click.setCursor(new ItemStack(Material.AIR));
                                            click.getGui().draw();
                                        }
                                    } else {
                                        if (!(click.getWhoClicked() instanceof Player)) return true;
                                        int amount = Math.min(storage.getAmount(), storage.getItemStack().getMaxStackSize());
                                        ItemStack giveItem = storage.getItemStack().clone();
                                        giveItem.setAmount(amount);
                                        storage.setAmount(storage.getAmount() - amount);
                                        giveItem((Player) click.getWhoClicked(), giveItem);
                                        click.getGui().draw();
                                    }
                                }
                                return true;
                            },
                            "§e残り: " + storage.getAmount() + "個"
                    )
            ));
            gui.setCloseAction(close -> {
                close.getGui().destroy();
                return false;
            });
            gui.show(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Inventory inventory = player.getInventory();
        for (ItemStack content : inventory.getContents()) {
            if (isStorage(content)) {
                Storage storage = StorageBox.getPlugin().getStorageProvider().get(content);
                if (event.getItem().getItemStack().getItemMeta() == null) continue;
                if (event.getItem().getItemStack().getItemMeta().equals(storage.getItemStack().getItemMeta())) {
                    event.setCancelled(true);
                    storage.setAmount(storage.getAmount() + event.getItem().getItemStack().getAmount());
                    event.getItem().remove();
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 2.0f);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onCreateStorage(CraftItemEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (isStorage(itemStack)) {
            Storage storage = new Storage();
            UUID uuid = UUID.randomUUID();
            storage.setUuid(uuid);
            storage.setAmount(0);
            storage.setItemStack(new ItemStack(Material.AIR));
            ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta.getPersistentDataContainer().set(Keys.UUID, PersistentDataType.STRING, uuid.toString());
            itemStack.setItemMeta(itemMeta);
            StorageBox.getPlugin().getStorageProvider().register(storage);
        }
    }

    public static void giveItem(Player player, ItemStack itemStack) {
        if (itemStack == null) return;
        Inventory inventory = player.getInventory();
        World world = player.getWorld();
        if (inventory.firstEmpty() == -1) {
            Item item = world.spawn(player.getLocation(), Item.class);
            item.setItemStack(itemStack);
        } else {
            inventory.addItem(itemStack);
        }
    }

    public static boolean isStorage(ItemStack itemStack) {
        if (itemStack == null) return false;

        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;

        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        final String itemId = dataContainer.get(Keys.ITEM_ID, PersistentDataType.STRING);
        if (itemId == null) return false;
        return itemId.equals("storage_box");
    }
}
