package ru.wryuin.elysiumenderchests.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.wryuin.elysiumenderchests.ElysiumEnderChests;
import ru.wryuin.elysiumenderchests.cooldown.CooldownManager;
import ru.wryuin.elysiumenderchests.item.EnderChestItem;
import ru.wryuin.elysiumenderchests.utils.ColorUtils;

public class EnderChestListener implements Listener {
    private final ElysiumEnderChests plugin;
    private final EnderChestItem enderChestItem;
    private final CooldownManager cooldownManager;

    public EnderChestListener(ElysiumEnderChests plugin, EnderChestItem enderChestItem, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.enderChestItem = enderChestItem;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Проверяем, является ли предмет в руке эндер-сундуком
        if (item == null || !enderChestItem.isEnderChestItem(item)) {
            return;
        }

        // Проверяем, использует ли игрок ПКМ
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Отменяем стандартное действие
        event.setCancelled(true);

        // Проверяем разрешение
        if (!player.hasPermission("elysiumec.use")) {
            player.sendMessage(ColorUtils.colorize(plugin.getPluginConfig().getNoPermissionMessage()));
            return;
        }

        // Проверяем задержку
        if (plugin.getPluginConfig().isDelayEnabled() && cooldownManager.hasCooldown(player)) {
            int remainingTime = cooldownManager.getRemainingTime(player);
            String message = plugin.getPluginConfig().getDelayMessage()
                    .replace("{time}", String.valueOf(remainingTime));
            player.sendMessage(ColorUtils.colorize(message));
            return;
        }

        // Открываем эндер-сундук
        player.openInventory(player.getEnderChest());

        // Устанавливаем задержку
        if (plugin.getPluginConfig().isDelayEnabled()) {
            cooldownManager.setCooldown(player);
        }

        // Забираем предмет, если это настроено
        if (plugin.getPluginConfig().shouldRetrieve()) {
            item.setAmount(item.getAmount() - 1);
        }
    }
} 