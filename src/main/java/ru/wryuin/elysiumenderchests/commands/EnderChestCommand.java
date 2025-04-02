package ru.wryuin.elysiumenderchests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.wryuin.elysiumenderchests.ElysiumEnderChests;
import ru.wryuin.elysiumenderchests.config.Config;
import ru.wryuin.elysiumenderchests.item.EnderChestItem;
import ru.wryuin.elysiumenderchests.utils.ColorUtils;

public class EnderChestCommand implements CommandExecutor {
    private final ElysiumEnderChests plugin;
    private final EnderChestItem enderChestItem;
    private final Config config;

    public EnderChestCommand(ElysiumEnderChests plugin, EnderChestItem enderChestItem) {
        this.plugin = plugin;
        this.enderChestItem = enderChestItem;
        this.config = plugin.getPluginConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Если команда вызвана без аргументов
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Эта команда может быть использована только игроком."));
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("elysiumec.admin")) {
                player.sendMessage(ColorUtils.colorize(config.getNoPermissionMessage()));
                return true;
            }
            
            // Открыть эндер-сундук
            player.openInventory(player.getEnderChest());
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            // Проверка прав на выдачу предмета
            if (!sender.hasPermission("elysiumec.admin")) {
                sender.sendMessage(ColorUtils.colorize(config.getNoPermissionMessage()));
                return true;
            }
            
            // Определение получателя
            Player target;
            if (args.length >= 2) {
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Игрок не найден."));
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Пожалуйста, укажите игрока."));
                return true;
            }
            
            // Создание и выдача предмета
            ItemStack enderChest = enderChestItem.create();
            target.getInventory().addItem(enderChest);
            
            // Сообщения
            if (sender != target) {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Вы выдали эндер-сундук игроку " + target.getName() + "."));
            }
            target.sendMessage(ColorUtils.colorize("&#3afb00● &f- Вы получили эндер-сундук."));
            
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            // Проверка прав на перезагрузку конфига
            if (!sender.hasPermission("elysiumec.admin")) {
                sender.sendMessage(ColorUtils.colorize(config.getNoPermissionMessage()));
                return true;
            }
            
            // Перезагрузка конфига
            plugin.reloadConfig();
            sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Конфигурация перезагружена."));
            return true;
        }
        
        // Если аргументы не распознаны, выводим справку
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Использование:"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest - открыть свой эндер-сундук"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest give <ник> - выдать эндер-сундук игроку"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest reload - перезагрузить конфигурацию"));
        
        return true;
    }
} 