package ru.wryuin.elysiumenderchests;

import org.bukkit.plugin.java.JavaPlugin;
import ru.wryuin.elysiumenderchests.commands.EnderChestCommand;
import ru.wryuin.elysiumenderchests.commands.EnderChestTabCompleter;
import ru.wryuin.elysiumenderchests.config.Config;
import ru.wryuin.elysiumenderchests.cooldown.CooldownManager;
import ru.wryuin.elysiumenderchests.item.EnderChestItem;
import ru.wryuin.elysiumenderchests.listeners.EnderChestListener;

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
        
        // Регистрируем команды
        getCommand("enderchest").setExecutor(new EnderChestCommand(this, enderChestItem));
        getCommand("enderchest").setTabCompleter(new EnderChestTabCompleter());
        
        // Регистрируем слушатели событий
        getServer().getPluginManager().registerEvents(new EnderChestListener(this, enderChestItem, cooldownManager), this);
        
        getLogger().info("ElysiumEnderChests успешно включен!");
    }

    @Override
    public void onDisable() {
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
        
        // Обновляем менеджер задержек
        if (cooldownManager != null) {
            cooldownManager = new CooldownManager(config.getDelayTime());
        }
    }
}
