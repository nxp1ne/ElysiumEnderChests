package ru.wryuin.elysiumenderchests.metrics;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Класс для работы с метриками плагина
 * В будущем здесь можно добавить интеграцию с bStats или другими сервисами метрик
 */
public class MetricsHandler {
    
    private final JavaPlugin plugin;
    private int totalOpened = 0;
    private int totalGiven = 0;
    
    public MetricsHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Инициализация системы метрик...");
        
        // Здесь будет инициализация службы метрик, когда она будет добавлена
        // Например, bStats
    }
    
    /**
     * Увеличивает счетчик открытых эндер-сундуков
     */
    public void incrementOpenCount() {
        totalOpened++;
    }
    
    /**
     * Увеличивает счетчик выданных эндер-сундуков
     * @param amount количество выданных сундуков
     */
    public void incrementGivenCount(int amount) {
        totalGiven += amount;
    }
    
    /**
     * Получает общее количество открытых эндер-сундуков
     * @return количество открытых сундуков
     */
    public int getTotalOpened() {
        return totalOpened;
    }
    
    /**
     * Получает общее количество выданных эндер-сундуков
     * @return количество выданных сундуков
     */
    public int getTotalGiven() {
        return totalGiven;
    }
} 