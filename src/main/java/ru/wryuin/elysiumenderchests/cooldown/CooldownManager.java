package ru.wryuin.elysiumenderchests.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int cooldownTime;

    public CooldownManager(int cooldownTimeSeconds) {
        this.cooldownTime = cooldownTimeSeconds;
    }

    /**
     * Устанавливает задержку для игрока
     * @param player Игрок, для которого устанавливается задержка
     */
    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Проверяет, прошла ли задержка для игрока
     * @param player Игрок для проверки
     * @return true если задержка еще действует, иначе false
     */
    public boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) {
            return false;
        }
        
        long lastUsed = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();
        long cooldownInMillis = cooldownTime * 1000L;
        
        return (currentTime - lastUsed) < cooldownInMillis;
    }

    /**
     * Получает оставшееся время задержки в секундах
     * @param player Игрок для проверки
     * @return Оставшееся время в секундах или 0, если задержка прошла
     */
    public int getRemainingTime(Player player) {
        if (!hasCooldown(player)) {
            return 0;
        }
        
        UUID uuid = player.getUniqueId();
        long lastUsed = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();
        long cooldownInMillis = cooldownTime * 1000L;
        
        int remainingTimeInSeconds = (int) ((cooldownInMillis - (currentTime - lastUsed)) / 1000);
        return Math.max(remainingTimeInSeconds, 0);
    }

    /**
     * Удаляет задержку для игрока
     * @param player Игрок, для которого удаляется задержка
     */
    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    /**
     * Очищает все задержки
     */
    public void clearCooldowns() {
        cooldowns.clear();
    }
} 