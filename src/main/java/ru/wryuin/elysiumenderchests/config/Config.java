package ru.wryuin.elysiumenderchests.config;

import org.bukkit.configuration.file.FileConfiguration;
import ru.wryuin.elysiumenderchests.ElysiumEnderChests;
import ru.wryuin.elysiumenderchests.utils.ColorUtils;

import java.util.List;

public class Config {
    private final ElysiumEnderChests plugin;
    private FileConfiguration config;

    public Config(ElysiumEnderChests plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getName() {
        return ColorUtils.colorize(config.getString("item_enderchest.name"));
    }

    public List<String> getLore() {
        return ColorUtils.colorize(config.getStringList("item_enderchest.lore"));
    }

    public boolean isGlow() {
        return config.getBoolean("item_enderchest.glow");
    }

    public boolean shouldRetrieve() {
        return config.getBoolean("item_enderchest.should_retrieve");
    }

    public boolean isDelayEnabled() {
        return config.getBoolean("item_enderchest.delay.enable");
    }

    public int getDelayTime() {
        return config.getInt("item_enderchest.delay.time");
    }

    public String getDelayMessage() {
        return ColorUtils.colorize(config.getString("item_enderchest.delay.message"));
    }

    public String getNoPermissionMessage() {
        return ColorUtils.colorize(config.getString("messages.noPermission"));
    }
} 