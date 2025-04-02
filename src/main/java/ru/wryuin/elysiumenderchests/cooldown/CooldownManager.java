package ru.wryuin.elysiumenderchests.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
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
     * Устанавливает задержку для UUID
     * @param uuid UUID игрока
     * @param timestamp Временная метка начала задержки
     */
    public void setCooldown(UUID uuid, long timestamp) {
        cooldowns.put(uuid, timestamp);
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
        
        // Если время действия задержки истекло, удаляем запись
        if ((currentTime - lastUsed) >= cooldownInMillis) {
            cooldowns.remove(uuid);
            return false;
        }
        
        return true;
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
    
    /**
     * Устанавливает все задержки из сохраненного файла
     * @param savedCooldowns Карта сохраненных задержек
     */
    public void setCooldowns(Map<UUID, Long> savedCooldowns) {
        cooldowns.clear();
        
        long currentTime = System.currentTimeMillis();
        long cooldownInMillis = cooldownTime * 1000L;
        
        // Загружаем только актуальные задержки
        for (Map.Entry<UUID, Long> entry : savedCooldowns.entrySet()) {
            if ((currentTime - entry.getValue()) < cooldownInMillis) {
                cooldowns.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Получает карту всех текущих задержек для сохранения
     * @return Копия карты задержек
     */
    public Map<UUID, Long> getAllCooldowns() {
        return new HashMap<>(cooldowns);
    }
    
    /**
     * Очищает просроченные задержки
     */
    public void cleanupExpiredCooldowns() {
        long currentTime = System.currentTimeMillis();
        long cooldownInMillis = cooldownTime * 1000L;
        
        cooldowns.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) >= cooldownInMillis);
    }
} 