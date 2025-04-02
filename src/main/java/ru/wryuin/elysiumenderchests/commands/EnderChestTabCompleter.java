package ru.wryuin.elysiumenderchests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnderChestTabCompleter implements TabCompleter {
    private static final List<String> COMMANDS = Arrays.asList("give", "reload");
    private static final List<String> AMOUNT_SUGGESTIONS = Arrays.asList("1", "5", "10", "32", "64");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Если у отправителя есть права на просмотр чужих сундуков, добавляем имена игроков
            if (hasAnyPermission(sender, "elysiumec.enderchest.see.*", "elysiumec.admin")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            
            // Если у отправителя есть права на администрирование, добавляем команды
            if (sender.hasPermission("elysiumec.admin")) {
                completions.addAll(COMMANDS);
            }
            
            // Фильтруем результаты
            String input = args[0].toLowerCase();
            return completions.stream()
                    .filter(suggestion -> suggestion.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Второй аргумент
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("elysiumec.admin")) {
                // Для команды give предлагаем имена игроков
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
                
                String input = args[1].toLowerCase();
                return completions.stream()
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            // Третий аргумент
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("elysiumec.admin")) {
                // Для команды give предлагаем варианты количества
                String input = args[2].toLowerCase();
                return AMOUNT_SUGGESTIONS.stream()
                        .filter(amount -> amount.startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
    
    /**
     * Проверяет, имеет ли отправитель хотя бы одно из указанных разрешений
     * @param sender отправитель
     * @param permissions разрешения для проверки
     * @return true если есть хотя бы одно разрешение
     */
    private boolean hasAnyPermission(CommandSender sender, String... permissions) {
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
} 