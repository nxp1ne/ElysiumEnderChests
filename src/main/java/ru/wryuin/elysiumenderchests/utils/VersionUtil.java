package ru.wryuin.elysiumenderchests.utils;

import org.bukkit.Bukkit;

/**
 * Утилита для определения версии сервера и совместимости
 */
public class VersionUtil {
    private static final int CURRENT_VERSION = getCurrentVersion();
    private static final boolean IS_LEGACY = CURRENT_VERSION < 13;
    private static final boolean HAS_TITLES = CURRENT_VERSION >= 9;
    private static final boolean SUPPORTS_HEX = CURRENT_VERSION >= 16;
    
    /**
     * Получает текущую основную версию сервера
     * @return номер версии (например, 16 для 1.16.x)
     */
    public static int getCurrentVersion() {
        String version = Bukkit.getBukkitVersion();
        // Формат обычно server-version, например: 1.16.5-R0.1-SNAPSHOT
        String[] parts = version.split("-")[0].split("\\.");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Проверяет, поддерживает ли текущая версия HEX цвета
     * @return true если поддерживает (1.16+)
     */
    public static boolean supportsHexColors() {
        return SUPPORTS_HEX;
    }
    
    /**
     * Проверяет, поддерживает ли текущая версия заголовки
     * @return true если поддерживает (1.9+)
     */
    public static boolean supportsTitles() {
        return HAS_TITLES;
    }
    
    /**
     * Проверяет, является ли текущая версия устаревшей
     * @return true если это старая версия (до 1.13)
     */
    public static boolean isLegacy() {
        return IS_LEGACY;
    }
    
    /**
     * Получает строку с версией сервера в формате "1.X.Y"
     * @return строка версии
     */
    public static String getVersionString() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }
    
    /**
     * Проверяет, поддерживается ли текущая версия плагином
     * @return true если версия поддерживается (1.13 - 1.20+)
     */
    public static boolean isSupported() {
        return CURRENT_VERSION >= 13;
    }
} 