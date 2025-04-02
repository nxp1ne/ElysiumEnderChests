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
            // Команда /ec - открыть свой эндер-сундук
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Эта команда может быть использована только игроком."));
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("elysiumec.enderchest.see.own")) {
                player.sendMessage(ColorUtils.colorize(config.getNoPermissionMessage()));
                return true;
            }
            
            // Открыть свой эндер-сундук
            player.openInventory(player.getEnderChest());
            
            // Отправляем сообщение об открытии
            player.sendMessage(config.getOpenChestMessage());
            
            // Воспроизводим звук, если включено
            if (config.isSoundEnabled()) {
                player.playSound(player.getLocation(), 
                    config.getOpenSound(), 
                    config.getSoundVolume(), 
                    config.getSoundPitch());
            }
            
            // Показываем заголовок, если включено
            if (config.shouldShowTitle()) {
                player.sendTitle(
                    config.getTitleText(),
                    config.getTitleSubtitle(player.getName()),
                    config.getTitleFadeIn(),
                    config.getTitleStay(),
                    config.getTitleFadeOut()
                );
            }
            
            return true;
        } else if (args.length == 1 && isNotCommand(args[0])) {
            // Команда /ec <ник> - открыть чужой эндер-сундук
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Эта команда может быть использована только игроком."));
                return true;
            }
            
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
            
            if (target == null) {
                player.sendMessage(config.getPlayerNotFoundMessage());
                return true;
            }
            
            // Проверяем права на просмотр чужого эндер-сундука
            if (!player.hasPermission("elysiumec.enderchest.see.*") && 
                !player.hasPermission("elysiumec.enderchest.see." + target.getName())) {
                player.sendMessage(config.getNoPermissionMessage());
                return true;
            }
            
            // Открыть чужой эндер-сундук
            player.openInventory(target.getEnderChest());
            
            // Отправляем сообщение об открытии
            player.sendMessage(config.getOpenOtherChestMessage(target.getName()));
            
            // Воспроизводим звук, если включено
            if (config.isSoundEnabled()) {
                player.playSound(player.getLocation(), 
                    config.getOpenSound(), 
                    config.getSoundVolume(), 
                    config.getSoundPitch());
            }
            
            // Показываем заголовок для чужого сундука, если включено
            if (config.shouldShowTitle()) {
                player.sendTitle(
                    config.getOtherPlayerTitleText(),
                    config.getOtherPlayerSubtitle(target.getName()),
                    config.getTitleFadeIn(),
                    config.getTitleStay(),
                    config.getTitleFadeOut()
                );
            }
            
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            // Проверка прав на выдачу предмета
            if (!sender.hasPermission("elysiumec.admin")) {
                sender.sendMessage(config.getNoPermissionMessage());
                return true;
            }
            
            // Определение получателя
            Player target;
            if (args.length >= 2) {
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(config.getPlayerNotFoundMessage());
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Пожалуйста, укажите игрока."));
                return true;
            }
            
            // Определение количества предметов
            int amount = 1;
            if (args.length >= 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        sender.sendMessage(config.getInvalidAmountMessage());
                        return true;
                    }
                    if (amount > 64) {
                        amount = 64; // Максимальное количество предметов в одном стаке
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(config.getInvalidAmountMessage());
                    return true;
                }
            }
            
            // Создание и выдача предмета
            ItemStack enderChest = enderChestItem.create();
            enderChest.setAmount(amount);
            target.getInventory().addItem(enderChest);
            
            // Сообщения
            if (sender != target) {
                if (amount == 1) {
                    sender.sendMessage(config.getGaveChestMessage(target.getName()));
                } else {
                    sender.sendMessage(config.getGaveMultipleChestsMessage(target.getName(), amount));
                }
            }
            target.sendMessage(config.getReceivedChestMessage());
            
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            // Проверка прав на перезагрузку конфига
            if (!sender.hasPermission("elysiumec.admin")) {
                sender.sendMessage(config.getNoPermissionMessage());
                return true;
            }
            
            // Перезагрузка конфига
            plugin.reloadConfig();
            sender.sendMessage(config.getConfigReloadedMessage());
            return true;
        }
        
        // Если аргументы не распознаны, выводим справку
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- Использование:"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest - открыть свой эндер-сундук"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest <ник> - открыть эндер-сундук указанного игрока"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest give <ник> [количество] - выдать эндер-сундук игроку"));
        sender.sendMessage(ColorUtils.colorize("&#3afb00● &f- /enderchest reload - перезагрузить конфигурацию"));
        
        return true;
    }
    
    /**
     * Проверяет, не является ли аргумент командой
     * @param arg проверяемый аргумент
     * @return true если аргумент не является командой
     */
    private boolean isNotCommand(String arg) {
        return !arg.equalsIgnoreCase("give") && !arg.equalsIgnoreCase("reload");
    }
} 