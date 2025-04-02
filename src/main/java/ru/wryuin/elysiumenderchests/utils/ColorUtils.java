package ru.wryuin.elysiumenderchests.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final boolean SUPPORTS_HEX = VersionUtil.supportsHexColors();
    
    // Кэшируем метод of() для рефлексии, чтобы избежать его поиска при каждом вызове
    private static Method OF_METHOD;
    
    static {
        if (SUPPORTS_HEX) {
            try {
                OF_METHOD = ChatColor.class.getMethod("of", String.class);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to get ChatColor.of method: " + e.getMessage());
            }
        }
    }

    /**
     * Преобразует строку с цветовыми кодами в цветной текст
     * Поддерживает как стандартные цветовые коды &, так и HEX цвета (&#RRGGBB) для версий 1.16+
     * 
     * @param text текст для раскрашивания
     * @return раскрашенный текст
     */
    public static String colorize(String text) {
        if (text == null) return "";
        
        String result = text;
        
        // Применение HEX цветов для 1.16+
        if (SUPPORTS_HEX && OF_METHOD != null) {
            try {
                Matcher matcher = HEX_PATTERN.matcher(result);
                StringBuffer buffer = new StringBuffer();
    
                while (matcher.find()) {
                    String hex = matcher.group(1);
                    // Используем рефлексию безопасно
                    Object chatColor = OF_METHOD.invoke(null, "#" + hex);
                    matcher.appendReplacement(buffer, chatColor.toString());
                }
                matcher.appendTail(buffer);
                result = buffer.toString();
            } catch (Throwable e) {
                // Если произошла ошибка при применении HEX цветов, просто игнорируем
                // и продолжаем с обычными цветами (может произойти если метод не найден)
                Bukkit.getLogger().warning("Failed to apply HEX colors: " + e.getMessage());
            }
        }
        
        // Применение стандартных цветовых кодов
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * Преобразует список строк с цветовыми кодами в список с цветным текстом
     * 
     * @param list список строк для раскрашивания
     * @return список раскрашенных строк
     */
    public static List<String> colorize(List<String> list) {
        return list.stream().map(ColorUtils::colorize).collect(Collectors.toList());
    }
    
    /**
     * Преобразует HEX цвет в ближайший стандартный цвет для старых версий
     * Используется для совместимости со старыми версиями (до 1.16)
     * 
     * @param hexColor HEX цвет в формате #RRGGBB
     * @return ближайший стандартный цвет
     */
    private static ChatColor getNearestColor(String hexColor) {
        // Удаляем символ # и префикс &# если они есть
        hexColor = hexColor.replace("&#", "").replace("#", "");
        
        // Преобразуем HEX в RGB
        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);
        
        ChatColor nearestColor = ChatColor.WHITE;
        double minDistance = Double.MAX_VALUE;
        
        // Находим ближайший стандартный цвет - используем только базовые цвета
        ChatColor[] colors = {
            ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA,
            ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY,
            ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA,
            ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE
        };
        
        for (ChatColor color : colors) {
            // Получаем RGB для стандартного цвета (приблизительные значения)
            int[] rgb = getRGBFromChatColor(color);
            int colorR = rgb[0];
            int colorG = rgb[1];
            int colorB = rgb[2];
            
            // Вычисляем расстояние в RGB пространстве
            double distance = Math.sqrt(
                Math.pow(colorR - r, 2) + 
                Math.pow(colorG - g, 2) + 
                Math.pow(colorB - b, 2)
            );
            
            if (distance < minDistance) {
                minDistance = distance;
                nearestColor = color;
            }
        }
        
        return nearestColor;
    }
    
    /**
     * Получает приблизительные RGB значения для стандартного цвета
     * 
     * @param color стандартный цвет
     * @return массив RGB значений
     */
    private static int[] getRGBFromChatColor(ChatColor color) {
        // Приблизительные RGB значения для стандартных цветов
        if (color == ChatColor.BLACK) return new int[]{0, 0, 0};
        else if (color == ChatColor.DARK_BLUE) return new int[]{0, 0, 170};
        else if (color == ChatColor.DARK_GREEN) return new int[]{0, 170, 0};
        else if (color == ChatColor.DARK_AQUA) return new int[]{0, 170, 170};
        else if (color == ChatColor.DARK_RED) return new int[]{170, 0, 0};
        else if (color == ChatColor.DARK_PURPLE) return new int[]{170, 0, 170};
        else if (color == ChatColor.GOLD) return new int[]{255, 170, 0};
        else if (color == ChatColor.GRAY) return new int[]{170, 170, 170};
        else if (color == ChatColor.DARK_GRAY) return new int[]{85, 85, 85};
        else if (color == ChatColor.BLUE) return new int[]{85, 85, 255};
        else if (color == ChatColor.GREEN) return new int[]{85, 255, 85};
        else if (color == ChatColor.AQUA) return new int[]{85, 255, 255};
        else if (color == ChatColor.RED) return new int[]{255, 85, 85};
        else if (color == ChatColor.LIGHT_PURPLE) return new int[]{255, 85, 255};
        else if (color == ChatColor.YELLOW) return new int[]{255, 255, 85};
        else if (color == ChatColor.WHITE) return new int[]{255, 255, 255};
        else return new int[]{255, 255, 255};
    }
} 