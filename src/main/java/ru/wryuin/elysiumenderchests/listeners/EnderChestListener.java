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
import ru.wryuin.elysiumenderchests.utils.VersionUtil;

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
        
        // Отправляем сообщение об открытии
        player.sendMessage(ColorUtils.colorize(plugin.getPluginConfig().getOpenChestMessage()));
        
        // Воспроизводим звук, если включено
        if (plugin.getPluginConfig().isSoundEnabled()) {
            try {
                player.playSound(player.getLocation(), 
                    plugin.getPluginConfig().getOpenSound(), 
                    plugin.getPluginConfig().getSoundVolume(), 
                    plugin.getPluginConfig().getSoundPitch());
            } catch (Exception e) {
                // Безопасное воспроизведение звука (на случай если звук не существует в этой версии)
                plugin.getLogger().warning("Ошибка при воспроизведении звука: " + e.getMessage());
            }
        }
        
        // Показываем заголовок, если включено и поддерживается в этой версии
        if (plugin.getPluginConfig().shouldShowTitle() && VersionUtil.supportsTitles()) {
            try {
                // Для версий 1.16+
                sendTitle(
                    player,
                    plugin.getPluginConfig().getTitleText(),
                    plugin.getPluginConfig().getTitleSubtitle(player.getName()),
                    plugin.getPluginConfig().getTitleFadeIn(),
                    plugin.getPluginConfig().getTitleStay(),
                    plugin.getPluginConfig().getTitleFadeOut()
                );
            } catch (Exception e) {
                // В случае ошибки просто логируем
                plugin.getLogger().warning("Ошибка при показе заголовка: " + e.getMessage());
            }
        }

        // Устанавливаем задержку
        if (plugin.getPluginConfig().isDelayEnabled()) {
            cooldownManager.setCooldown(player);
        }

        // Забираем предмет, если это настроено
        if (plugin.getPluginConfig().shouldRetrieve()) {
            item.setAmount(item.getAmount() - 1);
        }
        
        // Увеличиваем счетчик метрик
        if (plugin.getMetricsHandler() != null) {
            plugin.getMetricsHandler().incrementOpenCount();
        }
    }
    
    /**
     * Безопасно отправляет заголовок игроку с учетом разных версий API
     * 
     * @param player игрок
     * @param title заголовок
     * @param subtitle подзаголовок
     * @param fadeIn время появления
     * @param stay время отображения
     * @param fadeOut время исчезновения
     */
    private void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            // Пытаемся использовать стандартный метод (для 1.13+)
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } catch (NoSuchMethodError e) {
            // Для более старых версий, где метод с разными параметрами
            try {
                player.sendTitle(title, subtitle);
            } catch (Exception ex) {
                // Если и это не работает, просто логируем
                plugin.getLogger().warning("Не удалось отправить заголовок: " + ex.getMessage());
            }
        }
    }
} 