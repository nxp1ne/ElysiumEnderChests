package ru.wryuin.elysiumenderchests.config;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import ru.wryuin.elysiumenderchests.ElysiumEnderChests;
import ru.wryuin.elysiumenderchests.utils.ColorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final ElysiumEnderChests plugin;
    private FileConfiguration config;
    
    // Кэш для сообщений для улучшения производительности
    private final Map<String, String> cachedMessages = new HashMap<>();

    public Config(ElysiumEnderChests plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        cachedMessages.clear(); // очистка кэша при перезагрузке
    }

    public String getName() {
        return ColorUtils.colorize(config.getString("item_enderchest.name", "&#b302fbЭндер-сундук"));
    }

    public List<String> getLore() {
        return ColorUtils.colorize(config.getStringList("item_enderchest.lore"));
    }

    public boolean isGlow() {
        return config.getBoolean("item_enderchest.glow", false);
    }

    public boolean shouldRetrieve() {
        return config.getBoolean("item_enderchest.should_retrieve", false);
    }

    public boolean isDelayEnabled() {
        return config.getBoolean("item_enderchest.delay.enable", true);
    }

    public int getDelayTime() {
        return config.getInt("item_enderchest.delay.time", 5);
    }

    public String getDelayMessage() {
        return getMessage("item_enderchest.delay.message", 
            "&#3afb00● &f- Вы недавно использовали эндер-сундук! Пожалуйста подождите &#3afb00{time} &fсекунд для повторного открытие эндер-сундука");
    }

    public boolean isSoundEnabled() {
        return config.getBoolean("item_enderchest.sound.enabled", true);
    }

    public Sound getOpenSound() {
        try {
            return Sound.valueOf(config.getString("item_enderchest.sound.open", "BLOCK_ENDER_CHEST_OPEN"));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Неверный звук в конфигурации: " + config.getString("item_enderchest.sound.open"));
            return Sound.BLOCK_ENDER_CHEST_OPEN;
        }
    }

    public float getSoundVolume() {
        return (float) config.getDouble("item_enderchest.sound.volume", 1.0);
    }

    public float getSoundPitch() {
        return (float) config.getDouble("item_enderchest.sound.pitch", 1.0);
    }

    public boolean shouldShowTitle() {
        return config.getBoolean("view_settings.show_title_on_open", true);
    }

    public String getTitleText() {
        return getMessage("view_settings.title.text", "&#b302fbЭндер-сундук");
    }

    public String getTitleSubtitle(String playerName) {
        return getMessage("view_settings.title.subtitle", "&#3afb00● &fИнвентарь игрока &e{player}")
                .replace("{player}", playerName);
    }

    public int getTitleFadeIn() {
        return config.getInt("view_settings.title.fade_in", 10);
    }

    public int getTitleStay() {
        return config.getInt("view_settings.title.stay", 40);
    }

    public int getTitleFadeOut() {
        return config.getInt("view_settings.title.fade_out", 10);
    }

    public String getOtherPlayerTitleText() {
        return getMessage("view_settings.other_player_title.text", "&#b302fbЭндер-сундук");
    }

    public String getOtherPlayerSubtitle(String targetName) {
        return getMessage("view_settings.other_player_title.subtitle", "&#3afb00● &fИгрока &e{target}")
                .replace("{target}", targetName);
    }

    public String getNoPermissionMessage() {
        return getMessage("messages.noPermission", "&#3afb00● &f- У вас нет прав!");
    }

    public String getOpenChestMessage() {
        return getMessage("messages.open_chest", "&#3afb00● &f- Вы открыли эндер-сундук.");
    }

    public String getOpenOtherChestMessage(String targetName) {
        return getMessage("messages.open_other_chest", "&#3afb00● &f- Вы открыли эндер-сундук игрока &e{target}.")
                .replace("{target}", targetName);
    }

    public String getReceivedChestMessage() {
        return getMessage("messages.received_chest", "&#3afb00● &f- Вы получили переносной эндер-сундук.");
    }

    public String getGaveChestMessage(String targetName) {
        return getMessage("messages.gave_chest", "&#3afb00● &f- Вы выдали переносной эндер-сундук игроку &e{target}.")
                .replace("{target}", targetName);
    }

    public String getGaveMultipleChestsMessage(String targetName, int amount) {
        return getMessage("messages.gave_multiple_chests", "&#3afb00● &f- Вы выдали &e{amount} &fпереносных эндер-сундуков игроку &e{target}.")
                .replace("{target}", targetName)
                .replace("{amount}", String.valueOf(amount));
    }

    public String getPlayerNotFoundMessage() {
        return getMessage("messages.player_not_found", "&#3afb00● &f- Игрок не найден.");
    }

    public String getConfigReloadedMessage() {
        return getMessage("messages.config_reloaded", "&#3afb00● &f- Конфигурация успешно перезагружена.");
    }

    public String getInvalidAmountMessage() {
        return getMessage("messages.invalid_amount", "&#3afb00● &f- Неверное количество. Используйте положительное число.");
    }

    public boolean shouldCacheItems() {
        return config.getBoolean("performance.cache_items", true);
    }

    public boolean shouldSaveCooldowns() {
        return config.getBoolean("performance.save_cooldowns_on_shutdown", true);
    }

    /**
     * Получает и кеширует сообщение из конфигурации
     * @param path путь к сообщению
     * @param defaultValue значение по умолчанию
     * @return форматированное сообщение
     */
    private String getMessage(String path, String defaultValue) {
        if (cachedMessages.containsKey(path)) {
            return cachedMessages.get(path);
        }
        
        String message = ColorUtils.colorize(config.getString(path, defaultValue));
        cachedMessages.put(path, message);
        return message;
    }
} 