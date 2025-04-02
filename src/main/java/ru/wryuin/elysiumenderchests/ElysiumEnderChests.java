package ru.wryuin.elysiumenderchests;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.wryuin.elysiumenderchests.commands.EnderChestCommand;
import ru.wryuin.elysiumenderchests.commands.EnderChestTabCompleter;
import ru.wryuin.elysiumenderchests.config.Config;
import ru.wryuin.elysiumenderchests.cooldown.CooldownManager;
import ru.wryuin.elysiumenderchests.item.EnderChestItem;
import ru.wryuin.elysiumenderchests.listeners.EnderChestListener;
import ru.wryuin.elysiumenderchests.metrics.MetricsHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ElysiumEnderChests extends JavaPlugin {

    private Config config;
    private EnderChestItem enderChestItem;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        // Сохраняем конфигурацию по умолчанию
        saveDefaultConfig();
        
        // Инициализируем компоненты
        config = new Config(this);
        enderChestItem = new EnderChestItem(this, config);
        cooldownManager = new CooldownManager(config.getDelayTime());
        
        // Пытаемся загрузить сохраненные задержки, если они существуют
        loadCooldowns();
        
        // Регистрируем команды
        getCommand("enderchest").setExecutor(new EnderChestCommand(this, enderChestItem));
        getCommand("enderchest").setTabCompleter(new EnderChestTabCompleter());
        
        // Регистрируем слушатели событий
        getServer().getPluginManager().registerEvents(new EnderChestListener(this, enderChestItem, cooldownManager), this);
        
        // Инициализируем метрики (опционально)
        new MetricsHandler(this);
        
        getLogger().info("ElysiumEnderChests успешно включен!");
    }

    @Override
    public void onDisable() {
        // Сохраняем кулдауны, если это включено
        if (config.shouldSaveCooldowns()) {
            saveCooldowns();
        }
        
        // Очищаем кулдауны
        if (cooldownManager != null) {
            cooldownManager.clearCooldowns();
        }
        
        getLogger().info("ElysiumEnderChests успешно выключен!");
    }
    
    /**
     * Получить конфигурацию плагина
     * @return Объект конфигурации
     */
    public Config getPluginConfig() {
        return config;
    }
    
    /**
     * Получить менеджер эндер-сундуков
     * @return Объект для создания эндер-сундуков
     */
    public EnderChestItem getEnderChestItem() {
        return enderChestItem;
    }
    
    /**
     * Получить менеджер задержек
     * @return Объект для управления задержками
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    /**
     * Перезагрузить конфигурацию плагина
     */
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        
        // Обновляем конфигурацию
        if (config != null) {
            config.reload();
        }
        
        // Очищаем кэш предметов
        if (enderChestItem != null) {
            enderChestItem.clearCache();
        }
        
        // Обновляем менеджер задержек
        if (cooldownManager != null) {
            cooldownManager = new CooldownManager(config.getDelayTime());
            loadCooldowns(); // Перезагружаем задержки
        }
    }
    
    /**
     * Сохраняет кулдауны в файл
     */
    private void saveCooldowns() {
        if (cooldownManager == null) return;
        
        File file = new File(getDataFolder(), "cooldowns.yml");
        YamlConfiguration yamlConfig = new YamlConfiguration();
        
        Map<UUID, Long> cooldowns = cooldownManager.getAllCooldowns();
        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            yamlConfig.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            yamlConfig.save(file);
        } catch (IOException e) {
            getLogger().warning("Не удалось сохранить кулдауны: " + e.getMessage());
        }
    }
    
    /**
     * Загружает кулдауны из файла
     */
    private void loadCooldowns() {
        if (cooldownManager == null) return;
        
        File file = new File(getDataFolder(), "cooldowns.yml");
        if (!file.exists()) return;
        
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
        Map<UUID, Long> cooldowns = new HashMap<>();
        
        if (yamlConfig.contains("cooldowns")) {
            for (String key : yamlConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long time = yamlConfig.getLong("cooldowns." + key);
                    cooldowns.put(uuid, time);
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Неверный UUID в файле кулдаунов: " + key);
                }
            }
        }
        
        cooldownManager.setCooldowns(cooldowns);
    }
}
