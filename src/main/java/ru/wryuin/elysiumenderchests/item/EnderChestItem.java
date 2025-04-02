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
import ru.wryuin.elysiumenderchests.utils.VersionUtil;

import java.lang.reflect.Method;
import java.util.UUID;

public class EnderChestItem {
    private final ElysiumEnderChests plugin;
    private final Config config;
    private final NamespacedKey enderChestKey;
    
    // Проверка наличия PersistentDataContainer API (1.14+)
    private static final boolean HAS_PDC = VersionUtil.getCurrentVersion() >= 14;
    
    // Кэширование методов для работы с PersistentDataContainer через рефлексию
    private static Method GET_PDC_METHOD;
    private static Method PDC_SET_METHOD;
    private static Method PDC_HAS_METHOD;
    
    // Кэш для предмета эндер-сундука
    private ItemStack cachedItem;

    public EnderChestItem(ElysiumEnderChests plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.enderChestKey = new NamespacedKey(plugin, "enderchest");
        
        // Инициализация методов через рефлексию, если они доступны
        if (HAS_PDC) {
            try {
                GET_PDC_METHOD = Class.forName("org.bukkit.inventory.meta.ItemMeta").getMethod("getPersistentDataContainer");
                Class<?> pdcClass = Class.forName("org.bukkit.persistence.PersistentDataContainer");
                PDC_SET_METHOD = pdcClass.getMethod("set", NamespacedKey.class, Class.forName("org.bukkit.persistence.PersistentDataType"), Object.class);
                PDC_HAS_METHOD = pdcClass.getMethod("has", NamespacedKey.class, Class.forName("org.bukkit.persistence.PersistentDataType"));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to initialize PersistentDataContainer methods: " + e.getMessage());
            }
        }
    }

    /**
     * Создает или возвращает из кэша предмет эндер-сундук
     * @return Предмет эндер-сундук
     */
    public ItemStack create() {
        // Если предмет уже есть в кэше и кэширование включено, возвращаем копию предмета
        if (cachedItem != null && config.shouldCacheItems()) {
            return cachedItem.clone();
        }
        
        // Иначе создаем новый предмет
        ItemStack item = createNewItem();
        
        // Если включено кэширование, сохраняем предмет в кэш
        if (config.shouldCacheItems()) {
            cachedItem = item.clone();
        }
        
        return item;
    }
    
    /**
     * Создает новый предмет эндер-сундук
     * @return Созданный предмет
     */
    private ItemStack createNewItem() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Настройка имени и описания
            meta.setDisplayName(config.getName());
            meta.setLore(config.getLore());
            
            // Добавление метки для идентификации предмета
            setEnderChestTag(meta, enderChestKey);
            
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

    /**
     * Очищает кэш предмета при перезагрузке конфигурации
     */
    public void clearCache() {
        cachedItem = null;
    }

    /**
     * Проверяет, является ли предмет эндер-сундуком
     * @param item Проверяемый предмет
     * @return true если предмет является эндер-сундуком
     */
    public boolean isEnderChestItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && hasEnderChestTag(meta, enderChestKey);
    }
    
    /**
     * Безопасно устанавливает тег в meta для совместимости с разными версиями
     * @param meta ItemMeta объект
     * @param key NamespacedKey для тега
     */
    private void setEnderChestTag(ItemMeta meta, NamespacedKey key) {
        // Если у нас есть PersistentDataContainer API (1.14+)
        if (HAS_PDC && GET_PDC_METHOD != null && PDC_SET_METHOD != null) {
            try {
                Object pdc = GET_PDC_METHOD.invoke(meta);
                PDC_SET_METHOD.invoke(pdc, key, PersistentDataType.BYTE, (byte) 1);
            } catch (Exception e) {
                // В случае ошибки используем fallback метод - простая запись имени
                plugin.getLogger().warning("Error using PersistentDataContainer: " + e.getMessage());
                useFallbackMethod(meta);
            }
        } else {
            // Для старых версий - используем fallback метод
            useFallbackMethod(meta);
        }
    }
    
    /**
     * Безопасно проверяет наличие тега в meta для совместимости с разными версиями
     * @param meta ItemMeta объект
     * @param key NamespacedKey для тега
     * @return true если тег найден
     */
    private boolean hasEnderChestTag(ItemMeta meta, NamespacedKey key) {
        // Если у нас есть PersistentDataContainer API (1.14+)
        if (HAS_PDC && GET_PDC_METHOD != null && PDC_HAS_METHOD != null) {
            try {
                Object pdc = GET_PDC_METHOD.invoke(meta);
                return (boolean) PDC_HAS_METHOD.invoke(pdc, key, PersistentDataType.BYTE);
            } catch (Exception e) {
                // В случае ошибки используем fallback метод
                plugin.getLogger().warning("Error using PersistentDataContainer: " + e.getMessage());
                return checkFallbackMethod(meta);
            }
        } else {
            // Для старых версий - используем fallback метод
            return checkFallbackMethod(meta);
        }
    }
    
    /**
     * Запасной метод для отметки предмета, если PDC недоступен
     * @param meta ItemMeta для маркировки
     */
    private void useFallbackMethod(ItemMeta meta) {
        // Используем локализованное имя для хранения дополнительной информации
        if (meta.hasLocalizedName()) {
            meta.setLocalizedName(meta.getLocalizedName() + ":enderchest");
        } else {
            meta.setLocalizedName("enderchest");
        }
    }
    
    /**
     * Запасной метод для проверки, является ли предмет эндер-сундуком
     * @param meta ItemMeta для проверки
     * @return true если это эндер-сундук
     */
    private boolean checkFallbackMethod(ItemMeta meta) {
        return meta.hasLocalizedName() && meta.getLocalizedName().contains("enderchest");
    }
} 