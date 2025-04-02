package ru.wryuin.elysiumenderchests.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.wryuin.elysiumenderchests.ElysiumEnderChests;
import ru.wryuin.elysiumenderchests.config.Config;

import java.util.UUID;

public class EnderChestItem {
    private final ElysiumEnderChests plugin;
    private final Config config;
    private final NamespacedKey enderChestKey;

    public EnderChestItem(ElysiumEnderChests plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.enderChestKey = new NamespacedKey(plugin, "enderchest");
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Настройка имени и описания
            meta.setDisplayName(config.getName());
            meta.setLore(config.getLore());
            
            // Добавление метки для идентификации предмета
            meta.getPersistentDataContainer().set(enderChestKey, PersistentDataType.BYTE, (byte) 1);
            
            // Настройка свечения
            if (config.isGlow()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            // Добавляем все флаги для скрытия атрибутов
            meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_POTION_EFFECTS
            );
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    public boolean isEnderChestItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(enderChestKey, PersistentDataType.BYTE);
    }
} 